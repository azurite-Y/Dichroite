package org.zy.dichroite.fluorite.executor.resultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.zy.dichroite.fluorite.exception.ExecutorException;
import org.zy.dichroite.fluorite.interfaces.ObjectFactory;
import org.zy.dichroite.fluorite.interfaces.ReflectorFactory;
import org.zy.dichroite.fluorite.interfaces.ResultContext;
import org.zy.dichroite.fluorite.interfaces.ResultHandler;
import org.zy.dichroite.fluorite.interfaces.TypeHandler;
import org.zy.dichroite.fluorite.mapping.MappedStatement;
import org.zy.dichroite.fluorite.mapping.ResultMap;
import org.zy.dichroite.fluorite.reflection.MetaObject;
import org.zy.dichroite.fluorite.reflection.Reflector;
import org.zy.dichroite.fluorite.session.Configuration;
import org.zy.fluorite.core.utils.TypeUtils;

/**
 * @DateTime 2021年10月27日;
 * @author zy(azurite-Y);
 * @Description
 */
public abstract class AbstractResultHandler implements ResultHandler {
	protected final Configuration configuration;
	protected final ObjectFactory objectFactory;
	protected final ReflectorFactory reflectorFactory;
	protected final Class<?> resultObjectType;
	protected final Class<?> resultRowObjectType;
	protected ResultMap resultMap;
	protected MappedStatement mappedStatement;
	
	protected List<Object> list = new ArrayList<>();

	public AbstractResultHandler(MappedStatement mappedStatement, Configuration configuration, ObjectFactory objectFactory,
			ReflectorFactory reflectorFactory, ResultMap resultMap) {
		super();
		this.mappedStatement = mappedStatement;
		this.configuration = configuration;
		this.objectFactory = objectFactory;
		this.reflectorFactory = reflectorFactory;
		this.resultObjectType = resultMap.getResultObjectType();
		this.resultRowObjectType = resultMap.getResultRowObjectType();
		this.resultMap = resultMap;
	}

	private final Map<String, List<UnMappedColumnAutoMapping>> autoMappingsCache = new HashMap<String, List<UnMappedColumnAutoMapping>>();
	
	protected static class UnMappedColumnAutoMapping {
		private final String column;   
		private final String property;    
		private final TypeHandler<?> typeHandler;
		private final boolean primitive;
		public UnMappedColumnAutoMapping(String column, String property, TypeHandler<?> typeHandler, boolean primitive) {
			this.column = column;
			this.property = property;
			this.typeHandler = typeHandler;
			this.primitive = primitive;
		}
	} 
	
	@Override
	public void handleResultSet(ResultSetWrapper rsw) throws SQLException {
		preReserve(rsw);
		ResultContext resultContext = new DefaultResultContext();
		while (rsw.getResultSet().next()) {
			MetaObject metaObject = getRowValue(rsw,resultRowObjectType);
			resultContext.nextResultMetaObject(metaObject);
			storeObject(resultContext);
		}
		Logger statementLogger = mappedStatement.getStatementLogger();
		statementLogger.info("<== Total\t: " + resultContext.getResultCount());
		resultMap.setResultObject(result());
	}
	
	/**
	 * 前置预留方法，以在处理结果集之前有机会进行准备
	 * @param rsw 
	 * @throws SQLException 
	 */
	protected void preReserve(ResultSetWrapper rsw) throws SQLException {}
	
	/**
	 * 保存当前单行数据
	 * @param resultContext - 存储单行数据的上下文对象
	 */
	protected abstract void storeObject(ResultContext resultContext);

	protected MetaObject getRowValue(ResultSetWrapper rsw, Class<?> installClass) throws SQLException {
		Object resultObject = createResultObject(rsw, installClass);
		boolean foundValues = false;
		MetaObject metaObject = null;
		if (resultObject != null) {
			metaObject = configuration.newMetaObject(resultMap, resultObject);
			foundValues = applyAutomaticMappings(rsw, metaObject);
		}
		return foundValues ? metaObject : null;
	}
	
	/**
	 * 创建返回值对象
	 * @param rsw
	 * @param resultMap
	 * @return
	 * @throws SQLException 
	 */
	protected Object createResultObject(ResultSetWrapper rsw, Class<?> installClass) throws SQLException {
		Reflector reflector = reflectorFactory.findForClass(installClass);
		if (TypeUtils.isDefaultType(installClass)) { 
			// 若返回值类型本身或返回的List中元素类型为"基础数据类型"则默认使用结果集中第一字段值作为返回值
			return createPrimitiveResultObject(rsw, resultMap);
		} else if (installClass.isInterface() || reflector.hasDefaultConstructor()) {
			return objectFactory.create(installClass);
		}
		throw new ExecutorException("不知道如何创建的实例： " + installClass);
	}
	
	/**
	 * 创建基础类型的返回值对象
	 * @param rsw
	 * @param resultMap
	 * @return
	 * @throws SQLException 
	 * @see TypeUtils#isDefaultType
	 */
	private Object createPrimitiveResultObject(ResultSetWrapper rsw, ResultMap resultMap) throws SQLException {
		final Class<?> resultType = resultMap.getResultType().resolve();
		String columnName = rsw.getColumnNames().get(0);
		final TypeHandler<?> typeHandler = rsw.getTypeHandler(resultType, columnName);
		return typeHandler.getResult(rsw.getResultSet(), columnName);
	}
	
	/**
	 * 
	 * @param rsw
	 * @param resultMap
	 * @param metaObject
	 * @return
	 */
	private List<UnMappedColumnAutoMapping> createAutomaticMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject) {
		final String mapKey = resultMap.getId();
		List<UnMappedColumnAutoMapping> autoMapping = autoMappingsCache.get(mapKey);
		if (autoMapping == null) {
			autoMapping = new ArrayList<UnMappedColumnAutoMapping>();
			// 获得未映射的字段名集合
			final List<String> unmappedColumnNames = rsw.getUnmappedColumnNames(resultMap);
			for (String columnName : unmappedColumnNames) {
				String propertyName = columnName;
				// 交由 ObjectWrapper 确定属性名
				final String property = metaObject.findProperty(propertyName, configuration.isMapUnderscoreToCamelCase());
				if (property != null && metaObject.hasSetter(property)) {
					// 通过属性名获得对应的java类型
					final Class<?> propertyType = metaObject.getSetterType(property);
					// 通过java类型获得可用于处理字段值的 TypeHandler
					final TypeHandler<?> typeHandler = rsw.getTypeHandler(propertyType, columnName);
					autoMapping.add(new UnMappedColumnAutoMapping(columnName, property, typeHandler, propertyType.isPrimitive()));
				}
			}
			autoMappingsCache.put(mapKey, autoMapping);
		}
		return autoMapping;
	}
	
	private boolean applyAutomaticMappings(ResultSetWrapper rsw, MetaObject metaObject) throws SQLException {
		List<UnMappedColumnAutoMapping> autoMapping = createAutomaticMappings(rsw, resultMap, metaObject);
		boolean foundValues = false;
		if (autoMapping.size() > 0) {
			for (UnMappedColumnAutoMapping mapping : autoMapping) {
				// 处理得到对应类型的字段值
				final Object value = mapping.typeHandler.getResult(rsw.getResultSet(), mapping.column);
				if (value != null || configuration.isCallSettersOnNulls()) {
					if (value != null || !mapping.primitive) {
						// 设置字段值为返回值属性
						metaObject.setValue(mapping.property, value);
					}
					foundValues = true;
				}
			}
			metaObject.resultsReconstructed();
		}
		return foundValues;
	}
}
