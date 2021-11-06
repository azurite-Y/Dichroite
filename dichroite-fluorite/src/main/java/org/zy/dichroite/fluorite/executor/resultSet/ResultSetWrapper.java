package org.zy.dichroite.fluorite.executor.resultSet;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.zy.dichroite.fluorite.interfaces.TypeHandler;
import org.zy.dichroite.fluorite.mapping.ResultMap;
import org.zy.dichroite.fluorite.session.Configuration;
import org.zy.dichroite.fluorite.type.JdbcType;
import org.zy.dichroite.fluorite.type.TypeHandlerRegistry;
import org.zy.dichroite.fluorite.type.handler.ObjectTypeHandler;
import org.zy.fluorite.core.utils.ReflectionUtils;

/**
 * @DateTime 2021年10月19日;
 * @author zy(azurite-Y);
 * @Description
 */
public class ResultSetWrapper {
	private final ResultSet resultSet;
	private final TypeHandlerRegistry typeHandlerRegistry;
	/**
	 * 字段名集合
	 */
	private final List<String> columnNames = new ArrayList<String>();
	/**
	 * 字段值对应的java类型全限定名集合
	 */
	private final List<String> classNames = new ArrayList<String>();
	/**
	 * 字段值类型名称
	 */
	private final List<JdbcType> jdbcTypes = new ArrayList<JdbcType>();
	/**
	 * 字段名和类型处理器的映射
	 */
	private final Map<String, Map<Class<?>, TypeHandler<?>>> typeHandlerMap = new HashMap<String, Map<Class<?>, TypeHandler<?>>>();
	/**
	 * 已映射-方法全限定名：[字段名集合]
	 */
	private Map<String, List<String>> mappedColumnNamesMap = new HashMap<String, List<String>>();
	/**
	 * 未映射-方法全限定名：[字段名集合]
	 */
	private Map<String, List<String>> unMappedColumnNamesMap = new HashMap<String, List<String>>();

	public ResultSetWrapper(ResultSet rs, Configuration configuration) throws SQLException {
		super();
		this.typeHandlerRegistry = configuration.getTypeHandlerRegistry();
		this.resultSet = rs;
		final ResultSetMetaData metaData = rs.getMetaData();
		final int columnCount = metaData.getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			// 默认优先使用字段别名作为字段名
			columnNames.add(configuration.isUseColumnLabel() ? metaData.getColumnLabel(i) : metaData.getColumnName(i));
			jdbcTypes.add(JdbcType.forCode(metaData.getColumnType(i)));
			classNames.add(metaData.getColumnClassName(i));
		}
	}
	public ResultSet getResultSet() {
		return resultSet;
	}
	public List<String> getColumnNames() {
		return this.columnNames;
	}
	public List<String> getClassNames() {
		return Collections.unmodifiableList(classNames);
	}
	
	public List<String> getMappedColumnNames(ResultMap resultMap) {
		List<String> mappedColumnNames = mappedColumnNamesMap.get(resultMap.getId());
		if (mappedColumnNames == null) {
			loadMappedAndUnmappedColumnNames(resultMap);
			mappedColumnNames = mappedColumnNamesMap.get(resultMap.getId());
		}
		return mappedColumnNames;
	}

	public List<String> getUnmappedColumnNames(ResultMap resultMap) {
		List<String> unMappedColumnNames = unMappedColumnNamesMap.get(resultMap.getId());
		if (unMappedColumnNames == null) {
			loadMappedAndUnmappedColumnNames(resultMap);
			unMappedColumnNames = unMappedColumnNamesMap.get(resultMap.getId());
		}
		return unMappedColumnNames;
	}

	private void loadMappedAndUnmappedColumnNames(ResultMap resultMap) {
		List<String> mappedColumnNames = new ArrayList<String>();
		List<String> unmappedColumnNames = new ArrayList<String>();
		final Set<String> mappedColumns = resultMap.getMappedColumns();
		for (String columnName : columnNames) {
			final String upperColumnName = columnName.toUpperCase(Locale.ENGLISH);
			if (mappedColumns.contains(upperColumnName)) {
				mappedColumnNames.add(upperColumnName);
			} else {
				unmappedColumnNames.add(columnName);
			}
		}
		mappedColumnNamesMap.put(resultMap.getId(), mappedColumnNames);
		unMappedColumnNamesMap.put(resultMap.getId(), unmappedColumnNames);
	}

	public JdbcType getJdbcType(String columnName) {
		for (int i = 0 ; i < columnNames.size(); i++) {
			if (columnNames.get(i).equalsIgnoreCase(columnName)) {
				return jdbcTypes.get(i);
			}
		}
		return null;
	}
	

	/**
	 * 通过入参类型和参数名尝试获得对应的 {@code TypeHandler } 实现
	 * @param propertyType - 入参类型
	 * @param columnName - 参数名称
	 * @return
	 */
	public TypeHandler<?> getTypeHandler(Class<?> propertyType, String columnName) {
		TypeHandler<?> handler = null;
		Map<Class<?>, TypeHandler<?>> columnHandlers = typeHandlerMap.get(columnName);
		if (columnHandlers == null) {
			columnHandlers = new HashMap<Class<?>, TypeHandler<?>>();
			typeHandlerMap.put(columnName, columnHandlers);
		} else {
			handler = columnHandlers.get(propertyType);
		}
		if (handler == null) {
			JdbcType jdbcType = getJdbcType(columnName);
			handler = typeHandlerRegistry.getTypeHandler(propertyType, jdbcType);
			if (handler == null || handler instanceof ObjectTypeHandler) {
				final int index = columnNames.indexOf(columnName);
				final Class<?> javaType = ReflectionUtils.forName(classNames.get(index));
				handler = typeHandlerRegistry.getTypeHandler(javaType, jdbcType);
			}
			if (handler == null || handler instanceof ObjectTypeHandler) {
				handler = new ObjectTypeHandler();
			}
			columnHandlers.put(propertyType, handler);
		}
		return handler;
	}
}
