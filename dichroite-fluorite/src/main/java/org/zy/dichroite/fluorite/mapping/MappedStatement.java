package org.zy.dichroite.fluorite.mapping;

import java.util.Map;

import org.slf4j.Logger;
import org.zy.dichroite.fluorite.binding.BoundSql;
import org.zy.dichroite.fluorite.interfaces.Cache;
import org.zy.dichroite.fluorite.interfaces.SqlSource;
import org.zy.dichroite.fluorite.session.Configuration;

/**
 * @DateTime 2021年9月27日;
 * @author zy(azurite-Y);
 * @Description 封装在运行mapper方法时所需的相关参数
 */
public final class MappedStatement {
	/** 每一个方法的全限定签名 */
	private String id;
	private String databaseId;
	private Class<?> mapperClz;
	private Configuration configuration;
	private Integer timeout;
	private SqlSource sqlSource;
	private ParameterMap parameterMap;
	private ResultMap resultMap;
	private Cache cache;
	private boolean useCache;
	/** 是否需要刷新缓存 */
	private boolean flushCacheRequired;
	private SqlCommandType sqlCommandType;
	private String[] keyProperties;
	private String[] keyColumns;
	private boolean hasNestedResultMaps;
	private String[] resultSets;
	private Logger statementLogger; 

	MappedStatement() {}

	public static class Builder {
		private MappedStatement mappedStatement = new MappedStatement();

		public Builder(Configuration configuration, String id, SqlSource sqlSource, SqlCommandType sqlCommandType, Class<?> mapperClz) {
			mappedStatement.configuration = configuration;
			mappedStatement.id = id;
			mappedStatement.sqlSource = sqlSource;
			mappedStatement.sqlCommandType = sqlCommandType;
			mappedStatement.mapperClz = mapperClz;
		}

		public Builder id(String id) {
			mappedStatement.id = id;
			return this;
		}

		public Builder statementLogger(Logger statementLogger) {
			mappedStatement.statementLogger = statementLogger;
			return this;
		}

		public Builder timeout(Integer timeout) {
			mappedStatement.timeout = timeout;
			return this;
		}

		public Builder cache(Cache cache) {
			mappedStatement.cache = cache;
			return this;
		}

		public Builder flushCacheRequired(boolean flushCacheRequired) {
			mappedStatement.flushCacheRequired = flushCacheRequired;
			return this;
		}

		public Builder useCache(boolean useCache) {
			mappedStatement.useCache = useCache;
			return this;
		}

		public Builder resultMap(ResultMap resultMap) {
			mappedStatement.resultMap = resultMap;
			return this;
		}
		
		public Builder parameterMap(ParameterMap parameterMap) {
			mappedStatement.parameterMap = parameterMap;
			return this;
		}
		
		public Builder databaseId(String databaseId) {
			mappedStatement.databaseId = databaseId;
			return this;
		}

		public MappedStatement build() {
			assert mappedStatement.configuration != null;
			assert mappedStatement.id != null;
			assert mappedStatement.sqlSource != null;
			assert mappedStatement.statementLogger != null;
			assert mappedStatement.parameterMap != null;
			assert mappedStatement.resultMap != null;
			return mappedStatement;
		}
	}

	public BoundSql getBoundSql(Map<Integer,Object> args) {
		BoundSql boundSql = sqlSource.getBoundSql(args);
		return boundSql;
	}

	public Configuration getConfiguration() {
		return configuration;
	}
	public String getId() {
		return id;
	}
	public Integer getTimeout() {
		return timeout;
	}
	public SqlSource getSqlSource() {
		return sqlSource;
	}
	public ParameterMap getParameterMap() {
		return parameterMap;
	}
	public ResultMap getResultMap() {
		return resultMap;
	}
	public boolean isUseCache() {
		return useCache;
	}
	public SqlCommandType getSqlCommandType() {
		return sqlCommandType;
	}
	public String[] getKeyProperties() {
		return keyProperties;
	}
	public String[] getKeyColumns() {
		return keyColumns;
	}
	public boolean isHasNestedResultMaps() {
		return hasNestedResultMaps;
	}
	public String getDatabaseId() {
		return databaseId;
	}
	public String[] getResultSets() {
		return resultSets;
	}
	public Cache getCache() {
		return cache;
	}
	public boolean isFlushCacheRequired() {
		return flushCacheRequired;
	}
	public Class<?> getMapperClz() {
		return mapperClz;
	}
	public Logger getStatementLogger() {
		return statementLogger;
	}
}
