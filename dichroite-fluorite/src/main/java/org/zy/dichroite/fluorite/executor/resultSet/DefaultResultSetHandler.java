package org.zy.dichroite.fluorite.executor.resultSet;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.zy.dichroite.fluorite.binding.BoundSql;
import org.zy.dichroite.fluorite.executor.ErrorContext;
import org.zy.dichroite.fluorite.interfaces.Executor;
import org.zy.dichroite.fluorite.interfaces.ObjectFactory;
import org.zy.dichroite.fluorite.interfaces.ParameterHandler;
import org.zy.dichroite.fluorite.interfaces.ReflectorFactory;
import org.zy.dichroite.fluorite.interfaces.ResultHandler;
import org.zy.dichroite.fluorite.interfaces.ResultSetHandler;
import org.zy.dichroite.fluorite.mapping.MappedStatement;
import org.zy.dichroite.fluorite.mapping.ResultMap;
import org.zy.dichroite.fluorite.session.Configuration;

/**
 * @DateTime 2021年9月9日;
 * @author zy(azurite-Y);
 * @Description 
 */
public class DefaultResultSetHandler implements ResultSetHandler {
	private final Executor executor;
	private final Configuration configuration;
	private final MappedStatement mappedStatement;
	private final ParameterHandler parameterHandler;
	private final BoundSql boundSql;
	private final ObjectFactory objectFactory;
	private final ReflectorFactory reflectorFactory;
	private ResultHandler resultHandler;

	public DefaultResultSetHandler(Executor executor, Configuration configuration, MappedStatement mappedStatement,
			ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql) {
		super();
		this.executor = executor;
		this.configuration = configuration;
		this.mappedStatement = mappedStatement;
		this.parameterHandler = parameterHandler;
		this.resultHandler = resultHandler;
		this.boundSql = boundSql;
		this.objectFactory = configuration.getObjectFactory();
		this.reflectorFactory = configuration.getReflectorFactory();
	}

	@Override
	public List<Object> handleResultSets(Statement stmt) throws SQLException {
		ErrorContext.instance().activity("结果集处理").object(mappedStatement.getId());
		ResultSetWrapper rsw = createResultSetWrapper(stmt);

		ResultMap resultMap = mappedStatement.getResultMap();
		while (rsw != null) {
			handleResultSet(rsw, resultMap);
			rsw = getNextResultSet(stmt);
		}
		return resultMap.getResultObject();
	}

	private void handleResultSet(ResultSetWrapper rsw, ResultMap resultMap) throws SQLException {
		try {
			resultHandler = resultMap.isManyMapResult() ? new MultipleMapResulthandler(mappedStatement,configuration, objectFactory, reflectorFactory, resultMap) 
					: new DefaultResultHandler(mappedStatement,configuration, objectFactory, reflectorFactory, resultMap);
			resultHandler.handleResultSet(rsw);
		} finally {
			closeResultSet(rsw.getResultSet());
		}
	}

	/**
	 * 尝试获取下一个结果集
	 * @param stmt
	 * @return
	 * @throws SQLException
	 */
	private ResultSetWrapper getNextResultSet(Statement stmt) throws SQLException {
		try {
			// 检索该数据库是否支持从对方法execute的单个调用中获取多个ResultSet对象
			if (stmt.getConnection().getMetaData().supportsMultipleResultSets()) {
				// 判断是否有更多结果的标准JDBC方法
				if (!((!stmt.getMoreResults()) && (stmt.getUpdateCount() == -1))) {
					ResultSet rs = stmt.getResultSet();
					return rs != null ? new ResultSetWrapper(rs, configuration) : null;
				}
			}
		} catch (Exception e) {}
		return null;
	}

	/**
	 * 从 {@code ResultSet }中获得列表信息创建 {@code ResultSetWrapper }
	 * @param stmt
	 * @return
	 * @throws SQLException
	 */
	private ResultSetWrapper createResultSetWrapper(Statement stmt) throws SQLException {
		ResultSet rs = stmt.getResultSet();
		while (rs == null) {
			if (stmt.getMoreResults()) {
				rs = stmt.getResultSet();
			} else {
				if (stmt.getUpdateCount() == -1) {
					break;
				}
			}
		}
		return rs != null ? new ResultSetWrapper(rs, configuration) : null;
	}

	private void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {}
	}

	@Override
	public void handleOutputParameters(CallableStatement cs) throws SQLException {
	}

	public Executor getExecutor() {
		return executor;
	}
	public Configuration getConfiguration() {
		return configuration;
	}
	public MappedStatement getMappedStatement() {
		return mappedStatement;
	}
	public ParameterHandler getParameterHandler() {
		return parameterHandler;
	}
	public ResultHandler getResultHandler() {
		return resultHandler;
	}
	public BoundSql getBoundSql() {
		return boundSql;
	}
}
