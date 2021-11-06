package org.zy.dichroite.fluorite.session;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zy.dichroite.fluorite.annotation.BeanMapping;
import org.zy.dichroite.fluorite.annotation.InsertValue;
import org.zy.dichroite.fluorite.annotation.MapKey;
import org.zy.dichroite.fluorite.annotation.Mapper;
import org.zy.dichroite.fluorite.annotation.MapperMethod;
import org.zy.dichroite.fluorite.annotation.QueryTemplate;
import org.zy.dichroite.fluorite.annotation.QueryWhere;
import org.zy.dichroite.fluorite.annotation.UpdateSet;
import org.zy.dichroite.fluorite.binding.DefaultSqlSource;
import org.zy.dichroite.fluorite.binding.DynamicSqlSource;
import org.zy.dichroite.fluorite.interfaces.SqlSource;
import org.zy.dichroite.fluorite.mapping.MappedStatement;
import org.zy.dichroite.fluorite.mapping.ParameterMap;
import org.zy.dichroite.fluorite.mapping.ParameterMapping;
import org.zy.dichroite.fluorite.mapping.ParameterMode;
import org.zy.dichroite.fluorite.mapping.ResultMap;
import org.zy.dichroite.fluorite.mapping.ResultMap.Builder;
import org.zy.dichroite.fluorite.mapping.ResultMapping;
import org.zy.dichroite.fluorite.mapping.SqlCommandType;
import org.zy.fluorite.core.convert.ResolvableType;
import org.zy.fluorite.core.interfaces.AnnotationMetadata;
import org.zy.fluorite.core.subject.AnnotationAttributes;
import org.zy.fluorite.core.utils.ClassUtils;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 解析Mapper接口类中的注解
 */
public class MapperAnnotationParser {
	private Configuration configuration;
	private AnnotationMetadata annotationMetadata;
	private Class<?> mapperInterface;

	/**
	 * 创建 MapperAnnotationParser
	 * @param config
	 * @param annotationMetadata Maper接口的注解对象
	 * @param mapperInterface - mapper接口Class对象
	 */
	public MapperAnnotationParser(Configuration config, AnnotationMetadata annotationMetadata, Class<?> mapperInterface) {
		this.configuration = config;
		this.annotationMetadata = annotationMetadata;
		this.mapperInterface = mapperInterface;
	}

	/**
	 * 解析Mapper接口类中的注解,并填充Configuration的相关属性
	 * 创建 SqlSource
	 * 创建 MappedStatement
	 */
	public void parse() {
		Logger logger = LoggerFactory.getLogger(mapperInterface);
		Method[] methods = this.mapperInterface.getMethods();
		for (Method method : methods) {
			paraseMethod(method,logger);
		}
	}

	private void paraseMethod(Method method,Logger logger) {
		String id = ClassUtils.getFullyQualifiedName(method);
		// 解析注解
		DefaultMapperAnnotation defaultMapperAnnotation = paraseMethodAnnotation(method, logger);

		// 解析入参
		ParameterMap parameterMap = null;
		if (method.getParameterCount() == 0) {
			parameterMap = ParameterMap.EMPYT_PARAMETER_MAP;
		} else {
			ParameterMap.Builder parameterMapBuilder = null;
			if (configuration.hasParameterMap(id)) {
				parameterMapBuilder = new ParameterMap.Builder(configuration.getParamterMap(id));
			} else {
				parameterMapBuilder = paraseMethodParameter(method,logger);
			}
			parameterMapBuilder.id(id);
			parameterMapBuilder.type(mapperInterface);
			parameterMap = parameterMapBuilder.build(configuration);
		}
		
		ResultMap resultMap = null;
		ResolvableType returnType = ResolvableType.forClass(method.getGenericReturnType());
		Class<?> returnTypeResolve = returnType.resolve();
		if (Void.class == returnTypeResolve) {
			resultMap = ResultMap.EMPTY_RESULT_MAP;
		} else {
			Builder builder = null;
			if (configuration.hasResultMappings(returnTypeResolve)) { 	// 首先尝试从缓存中获取 ResultMapping
				builder = new ResultMap.Builder(id, returnType, configuration.getResultMappings(returnTypeResolve));
			} else if (parameterMap.isResultParameter()) { // 复用之前已解析的Mapping
				List<ResultMapping> resultMappings = new ArrayList<>();
				for (ParameterMapping multiplexMapping : parameterMap.getParameterMappings()) {
					ResultMapping resultMapping = new ResultMapping(multiplexMapping.getProperty(), multiplexMapping.getJavaType(), multiplexMapping.getColumnName());
					resultMappings.add(resultMapping);
				}
				builder = new ResultMap.Builder(id, returnType, resultMappings);
			} else {
				builder = new ResultMap.Builder(id, returnType);
			}
			builder.mapKey(method.getAnnotation(MapKey.class));
			resultMap = builder.build(configuration);
		}
	
		SqlSource sqlsource = null;
		if (defaultMapperAnnotation == null) {
			return ;
		} else if (defaultMapperAnnotation.isDynamicQuery() || defaultMapperAnnotation.isDynamicAddOrModify()) {
			sqlsource = new DynamicSqlSource(configuration, parameterMap, defaultMapperAnnotation);
		} else {
			sqlsource = new DefaultSqlSource(configuration, parameterMap, defaultMapperAnnotation);
		}

		MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, id, sqlsource, defaultMapperAnnotation.getSqlCommandType(), this.mapperInterface)
				.databaseId(configuration.getDatabaseId())
				.statementLogger(logger)
				.resultMap(resultMap)
				.parameterMap(parameterMap);
		MappedStatement mappedStatement = statementBuilder.build();
		configuration.addMappedStatement(mappedStatement);
		configuration.addParamterMap(parameterMap);
		configuration.addResultMap(resultMap);
	}

	/**
	 * 解析入参参数
	 * @param method
	 * @param logger
	 */
	private ParameterMap.Builder paraseMethodParameter(Method method, Logger logger) {
		List<ParameterMapping> list = new ArrayList<>();
		ParameterMap.Builder parameterMapBuilder = new ParameterMap.Builder(list, false);

		Class<?> returnType = method.getReturnType();
		Parameter[] parameters = method.getParameters();
		Parameter parameter = null;
		if (parameters.length == 1) {
			parameterMapBuilder.onlyParameter();
		}
		for (int i = 0; i < parameters.length; i++) {
			parameter = parameters[i];
			Class<?> paramterType = parameter.getType();
			ParameterMapping parameterMapping = null;
			// 因为运行时无法获取Method对象的参数名，所以此处忽略参数名，先掷为null;
			if (returnType.equals(paramterType)) {
				parameterMapping = new ParameterMapping(null, paramterType, ParameterMode.INOUT, null);
				if (paramterType.getAnnotation(BeanMapping.class) != null)  {
					parameterMapBuilder.resultParameter();
				}
			} else {
				parameterMapping = new ParameterMapping(null, paramterType, null);
			}
			list.add(parameterMapping);
		}
		return parameterMapBuilder;
	}

	/**
	 * 解析注解
	 * @param method
	 * @param logger
	 * @return
	 */
	private DefaultMapperAnnotation paraseMethodAnnotation(Method method,Logger logger) {
		DefaultMapperAnnotation mapperAnnotation = new DefaultMapperAnnotation();

		AnnotationAttributes attributesForClass = annotationMetadata.getAnnotationAttributesForClass();
		AnnotationAttributes attributesForMethod = annotationMetadata.getAnnotationAttributesForMethod(method);

		MapperMethod mapperMethod = attributesForMethod.getAnnotation(MapperMethod.class);
		if (mapperMethod == null) {
			logger.info("被忽视的Mapper方法，因为它未标注@MapperMethod注解，by method：" + ClassUtils.getFullyQualifiedName(method));
			return null;
		}

		mapperAnnotation.setSql(mapperMethod.value());
		mapperAnnotation.setSqlCommandType(mapperMethod.sqlCommandType());
		if (attributesForMethod.containsKey(InsertValue.class)) {
			mapperAnnotation.setDynamicAddOrModify(true);
			InsertValue insertValue = attributesForMethod.getAnnotation(InsertValue.class);
//			mapperAnnotation.setInputParameter(insertValue.parame());
			mapperAnnotation.setInputParameterLocationIndex(insertValue.value());
			mapperAnnotation.setInsertPropretyNameLocationIndex(insertValue.name());
		} else if (attributesForMethod.containsKey(UpdateSet.class)) {
			mapperAnnotation.setDynamicAddOrModify(true);
			UpdateSet updateSet = attributesForMethod.getAnnotation(UpdateSet.class);
//			mapperAnnotation.setInputParameter(updateSet.parame());
			mapperAnnotation.setInputParameterLocationIndex(updateSet.name());
		}

		if (attributesForMethod.containsKey(QueryWhere.class)) {
			mapperAnnotation.setDynamicQuery(true);
			QueryWhere querWhere = attributesForMethod.getAnnotation(QueryWhere.class);
			mapperAnnotation.setDefaultQuerySql(querWhere.defaultQuerySql());
			mapperAnnotation.setMode(querWhere.mode());
			mapperAnnotation.setQueryWhereName(querWhere.name());
//			mapperAnnotation.setQueryParameClz(querWhere.parame());
		}

		Mapper annotation = attributesForClass.getAnnotation(Mapper.class);
		if (annotation != null) {
			mapperAnnotation.setCacheNameSpace(annotation.cacheNameSpace());
			mapperAnnotation.setNameSpaceActive(annotation.nameSpaceActive());
		}
		return mapperAnnotation;
	}

	/**
	 * mapper接口注解的模型对象
	 * @author Azurite-Y
	 */
	public class MapperAnnotation {
		/**
		 * 命名空间缓存名
		 * @see Mapper#cacheNameSpace()
		 */
		protected String cacheNameSpace;

		/**
		 * 是否使用命名空间缓存
		 * @see Mapper#nameSpaceActive()
		 */
		protected boolean nameSpaceActive;

		public String getCacheNameSpace() {
			return cacheNameSpace;
		}
		public void setCacheNameSpace(String cacheNameSpace) {
			this.cacheNameSpace = cacheNameSpace;
		}
		public boolean isNameSpaceActive() {
			return nameSpaceActive;
		}
		public void setNameSpaceActive(boolean nameSpaceActive) {
			this.nameSpaceActive = nameSpaceActive;
		}
	}

	/**
	 * 封装sql的基础属性
	 * @author Azurite-Y
	 *
	 */
	public class DefaultMapperAnnotation extends MapperAnnotation{
		/**
		 * 基础sql
		 * @see MapperMethod#value()
		 */
		private String sql;

		/**
		 * 查询模板，指定在SQL拼接的过程中的逻辑,默认为并行
		 * @see QueryWhere#mode()
		 */
		private SqlCommandType sqlCommandType;

		/**
		 * 在目标sql为insert语句时此属性视作废弃
		 * @see QueryWhere#name()
		 */
		private String queryWhereName;

		/**
		 * 在目标sql为insert语句时此属性视作废弃
		 * @see QueryWhere#defaultQuerySql()
		 */
		private String defaultQuerySql;

		/**
		 * 查询模板，指定在SQL拼接的过程中的逻辑,默认为并行</br>
		 * 在目标sql为insert语句时此属性视作废弃
		 * @see QueryWhere#mode()
		 */
		private QueryTemplate mode;

		/**
		 * 查询参数类型，区分于 {@code inputParameter }.</br>
		 * see QueryWhere#parame()
		 */
		private Class<?> QueryParameClz;

		/**
		 * 入参参数类型
		 * @see InsertValue#parame()
		 * @see UpdateSet#parame()
		 */
		private Class<?> inputParameter;

		/**
		 * 入参参数占位符
		 * @see InsertValue#name()
		 * @see UpdateSet#name()
		 */
		private String inputParameterLocationIndex;

		/**
		 * @see InsertValue#name()
		 */
		private String insertPropretyNameLocationIndex;
		
		/**
		 * 是否是动态查询SQL
		 */
		private boolean dynamicQuery;

		/**
		 * 是否是动态插入或修改SQL
		 */
		private boolean dynamicAddOrModify;

		public String getSql() {
			return sql;
		}
		public void setSql(String sql) {
			this.sql = sql;
		}
		public SqlCommandType getSqlCommandType() {
			return sqlCommandType;
		}
		public void setSqlCommandType(SqlCommandType sqlCommandType) {
			this.sqlCommandType = sqlCommandType;
		}
		public String getQueryWhereName() {
			return queryWhereName;
		}
		public void setQueryWhereName(String queryWhereName) {
			this.queryWhereName = queryWhereName;
		}
		public String getDefaultQuerySql() {
			return defaultQuerySql;
		}
		public void setDefaultQuerySql(String defaultQuerySql) {
			this.defaultQuerySql = defaultQuerySql;
		}
		public QueryTemplate getMode() {
			return mode;
		}
		public void setMode(QueryTemplate mode) {
			this.mode = mode;
		}
		public Class<?> getQueryParameClz() {
			return QueryParameClz;
		}
		public void setQueryParameClz(Class<?> queryParameClz) {
			QueryParameClz = queryParameClz;
		}
		public Class<?> getInputParameter() {
			return inputParameter;
		}
		public void setInputParameter(Class<?> inputParameter) {
			this.inputParameter = inputParameter;
		}
		public String getInputParameterLocationIndex() {
			return inputParameterLocationIndex;
		}
		public void setInputParameterLocationIndex(String inputParameterLocationIndex) {
			this.inputParameterLocationIndex = inputParameterLocationIndex;
		}
		public boolean isDynamicQuery() {
			return dynamicQuery;
		}
		public void setDynamicQuery(boolean dynamicQuery) {
			this.dynamicQuery = dynamicQuery;
		}
		public boolean isDynamicAddOrModify() {
			return dynamicAddOrModify;
		}
		public void setDynamicAddOrModify(boolean dynamicAddOrModify) {
			this.dynamicAddOrModify = dynamicAddOrModify;
		}
		public String getInsertPropretyNameLocationIndex() {
			return insertPropretyNameLocationIndex;
		}
		public void setInsertPropretyNameLocationIndex(String insertPropretyNameLocationIndex) {
			this.insertPropretyNameLocationIndex = insertPropretyNameLocationIndex;
		}
	}

	public Configuration getConfiguration() {
		return configuration;
	}
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	public AnnotationMetadata getAnnotationMetadata() {
		return annotationMetadata;
	}
	public void setAnnotationMetadata(AnnotationMetadata annotationMetadata) {
		this.annotationMetadata = annotationMetadata;
	}
}
