package org.zy.dichroite.fluorite.session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.zy.dichroite.fluorite.binding.BoundSql;
import org.zy.dichroite.fluorite.binding.MapperRegistry;
import org.zy.dichroite.fluorite.exception.MapperBindingException;
import org.zy.dichroite.fluorite.executor.CachingExecutor;
import org.zy.dichroite.fluorite.executor.SimpleExecutor;
import org.zy.dichroite.fluorite.executor.parameter.DefaultParameterHandler;
import org.zy.dichroite.fluorite.executor.resultSet.DefaultResultSetHandler;
import org.zy.dichroite.fluorite.executor.statement.PreparedStatementHandler;
import org.zy.dichroite.fluorite.interfaces.Executor;
import org.zy.dichroite.fluorite.interfaces.ObjectFactory;
import org.zy.dichroite.fluorite.interfaces.ParameterHandler;
import org.zy.dichroite.fluorite.interfaces.ReflectorFactory;
import org.zy.dichroite.fluorite.interfaces.ResultSetHandler;
import org.zy.dichroite.fluorite.interfaces.SqlSession;
import org.zy.dichroite.fluorite.interfaces.StatementHandler;
import org.zy.dichroite.fluorite.interfaces.Transaction;
import org.zy.dichroite.fluorite.interfaces.TransactionFactory;
import org.zy.dichroite.fluorite.mapping.MappedStatement;
import org.zy.dichroite.fluorite.mapping.ParameterMap;
import org.zy.dichroite.fluorite.mapping.ParameterMapping;
import org.zy.dichroite.fluorite.mapping.ResultMap;
import org.zy.dichroite.fluorite.mapping.ResultMapping;
import org.zy.dichroite.fluorite.reflection.DefaultObjectFactory;
import org.zy.dichroite.fluorite.reflection.DefaultReflectorFactory;
import org.zy.dichroite.fluorite.reflection.MetaObject;
import org.zy.dichroite.fluorite.type.TypeHandlerRegistry;
import org.zy.fluorite.core.environment.interfaces.PropertyResolver;
import org.zy.fluorite.core.interfaces.AnnotationMetadata;
import org.zy.fluorite.core.utils.Assert;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 描述框架所需配置属性的集合
 */
public class Configuration {
	/** 数据库标识  */
	protected String databaseId;

	private final TransactionFactory transactionFactory;

	private final DataSource dataSource;

	private boolean useColumnLabel;

	protected Integer defaultStatementTimeout = 0;

	/** id:MappedStatement [id-每一个方法的全限定签名] */
	protected final Map<String, MappedStatement> mappedStatements = new HashMap<>();

	//	protected final PluginChain interceptorChain = new PluginChain();

	/** 是否自动提交 */
	private boolean autoCommit;

	/**	是否启用缓存 */
	protected boolean cacheEnabled = true;
	/**
	 * ParameterMap 缓存,在同一方法上复用
	 * [方法全 限定名:ParameterMap]
	 */
	protected final Map<String, ParameterMap> parameterMaps = new HashMap<>();
	/**
	 * ParameterMapping 缓存,在同一参数上复用
	 * [beanMappingClz:[ParameterMapping]]
	 */
	protected final Map<Class<?>, List<ParameterMapping>> parameterMappings = new HashMap<>();
	/**
	 * ResultMap 缓存,在同一方法上复用
	 * [方法全 限定名:ResultMap]
	 */
	protected final Map<String, ResultMap> resultMaps = new HashMap<>();
	/**
	 * ResultMapping 缓存,在同一返回值上复用
	 * [beanMappingClz:[ResultMapping]]
	 */
	protected final Map<Class<?>, List<ResultMapping>> resultMappings = new HashMap<>();

	protected MapperRegistry mapperRegistry = new MapperRegistry(this);

	protected ObjectFactory objectFactory = new DefaultObjectFactory();
	protected ReflectorFactory reflectorFactory = new DefaultReflectorFactory();

	/**
	 * 配置属性获取入口
	 */
	private PropertyResolver propertyResolver;
	/**
	 * 是否将下划线映射为大小写
	 */
	private boolean mapUnderscoreToCamelCase = true;
	/**
	 * 是否可设置返回值属性为 {@code null }
	 */
	private boolean callSettersOnNulls;

	private TypeHandlerRegistry typeHandlerRegistry;

	public Configuration(String databaseId, TransactionFactory transactionFactory, DataSource dataSource) {
		Assert.notNull(databaseId, "参数'databaseId'不能为空");
		Assert.notNull(transactionFactory, "参数'transactionFactory'不能为空");
		Assert.notNull(dataSource, "参数'dataSource'不能为空");
		this.databaseId = databaseId;
		this.transactionFactory = transactionFactory;
		this.dataSource = dataSource;
	}

	/**
	 * 判断此mapper接口是否已注册到mapperRegistry的容器中
	 * @param type
	 * @return
	 */
	public boolean hasMapper(Class<?> type) {
		return mapperRegistry.hasMapper(type);
	}

	public <T> void addMapper(Class<T> mapperInterface, AnnotationMetadata annotationMetadata) throws MapperBindingException {
		mapperRegistry.addMapper(mapperInterface,annotationMetadata);
	}

	public <T> T getMapper(Class<T> type, SqlSession sqlSession) throws MapperBindingException {
		return mapperRegistry.getMapper(type, sqlSession);
	}


	public boolean isAutoCommit() {
		return autoCommit;
	}
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}
	public String getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}
	public TransactionFactory getTransactionFactory() {
		return transactionFactory;
	}
	public Integer getDefaultStatementTimeout() {
		return defaultStatementTimeout;
	}
	public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
		this.defaultStatementTimeout = defaultStatementTimeout;
	}
	public MapperRegistry getMapperRegistry() {
		return mapperRegistry;
	}
	public void setMapperRegistry(MapperRegistry mapperRegistry) {
		this.mapperRegistry = mapperRegistry;
	}
	public DataSource getDataSource() {
		return dataSource;
	}
	public Map<String, MappedStatement> getMappedStatements() {
		return mappedStatements;
	}

	/**
	 * 创建Executor
	 * @param transaction
	 * @return
	 */
	public Executor newExecutor(Transaction transaction) {
		Executor executor = new SimpleExecutor(this, transaction);
		if (cacheEnabled) {
			executor = new CachingExecutor(executor);
		}
		return executor;
	}

	/**
	 * 创建StatementHandler
	 * @param executor
	 * @param mappedStatement
	 * @param parameterObject
	 * @param boundSql
	 * @return
	 */
	public StatementHandler newStatementHandler(Executor executor, MappedStatement mappedStatement, Map<Integer,Object> args, BoundSql boundSql) {
		StatementHandler statementHandler = new PreparedStatementHandler(executor, mappedStatement, args, boundSql);
		return statementHandler;
	}

	/**
	 * 创建ParameterHandler
	 * @param mappedStatement
	 * @param parameterObject
	 * @param boundSql
	 * @return
	 */
	public ParameterHandler newParameterHandler(MappedStatement mappedStatement, Map<Integer,Object> args, BoundSql boundSql) {
		ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, args, boundSql);
		return parameterHandler;
	}

	/**
	 * 创建ResultSetHandler
	 * @param executor
	 * @param mappedStatement
	 * @param parameterHandler
	 * @param resultHandler
	 * @param boundSql
	 * @return
	 */
	public ResultSetHandler newResultSetHandler(Executor executor, MappedStatement mappedStatement,
			ParameterHandler parameterHandler, BoundSql boundSql) {
		ResultSetHandler resultSetHandler = new DefaultResultSetHandler(executor, mappedStatement.getConfiguration(), mappedStatement, parameterHandler, null, boundSql);
		return resultSetHandler;
	}

	public MetaObject newMetaObject(ResultMap resultMap, Object resultObject) {
		return MetaObject.forObject(resultMap, resultObject, objectFactory, reflectorFactory);
	}

	// MappedStatements
	public void addMappedStatement(MappedStatement ms) {
		mappedStatements.put(ms.getId(), ms);
	}
	public MappedStatement getMappedStatement(String statement) {
		return mappedStatements.get(statement);
	}
	public boolean hasStatement(String statementName) {
		return mappedStatements.containsKey(statementName);
	}

	// ParameterMaps
	public void addParamterMap(ParameterMap parameterMap) {
		this.parameterMaps.put(parameterMap.getId(), parameterMap);
	}
	public ParameterMap getParamterMap(String id) {
		return parameterMaps.get(id);
	}
	public boolean hasParameterMap(String id) {
		return parameterMaps.containsKey(id);
	}

	// ResultMaps
	public void addResultMap(ResultMap resultMap) {
		this.resultMaps.put(resultMap.getId(), resultMap);
	}
	public ResultMap getResultMap(String id) {
		return resultMaps.get(id);
	}
	public boolean hasResultMap(String id) {
		return resultMaps.containsKey(id);
	}

	// ResultMappings
	public void addResultMappings(Class<?> beanMappingClz , List<ResultMapping> resultMappings) {
		this.resultMappings.put(beanMappingClz, resultMappings);
	}
	public List<ResultMapping> getResultMappings(Class<?> returnType) {
		return resultMappings.get(returnType);
	}
	public boolean hasResultMappings(Class<?> returnType) {
		return resultMappings.containsKey(returnType);
	}

	// ParameterMappings
	public void addParameterMappings(Class<?> beanMappingClz , List<ParameterMapping> parameterMapping) {
		this.parameterMappings.put(beanMappingClz, parameterMapping);
	}
	public List<ParameterMapping> getParameterMappings(Class<?> beanMappingClz) {
		return parameterMappings.get(beanMappingClz);
	}
	public boolean hasParameterMappings(Class<?> beanMappingClz) {
		return parameterMappings.containsKey(beanMappingClz);
	}

	//------------------------------------------------getter、setter---------------------------------------
	public boolean isCacheEnabled() {
		return cacheEnabled;
	}
	public void setCacheEnabled(boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}
	public PropertyResolver getPropertyResolver() {
		return propertyResolver;
	}
	public void setPropertyResolver(PropertyResolver propertyResolver) {
		this.propertyResolver = propertyResolver;
	}
	/**
	 * 是否使用字段别名
	 * @return
	 */
	public boolean isUseColumnLabel() {
		return useColumnLabel;
	}
	public ObjectFactory getObjectFactory() {
		return this.objectFactory;
	}
	public ReflectorFactory getReflectorFactory() {
		return this.reflectorFactory;
	}
	public TypeHandlerRegistry getTypeHandlerRegistry() {
		return typeHandlerRegistry;
	}
	public void setTypeHandlerRegistry(TypeHandlerRegistry typeHandlerRegistry) {
		this.typeHandlerRegistry = typeHandlerRegistry;
	}
	public void setMapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase) {
		this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
	}
	public boolean isMapUnderscoreToCamelCase() {
		return mapUnderscoreToCamelCase;
	}
	public boolean isCallSettersOnNulls() {
		return callSettersOnNulls;
	}
}
