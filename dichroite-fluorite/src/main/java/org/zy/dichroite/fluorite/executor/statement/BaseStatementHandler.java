package org.zy.dichroite.fluorite.executor.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.zy.dichroite.fluorite.binding.BoundSql;
import org.zy.dichroite.fluorite.exception.ExecutorException;
import org.zy.dichroite.fluorite.executor.ErrorContext;
import org.zy.dichroite.fluorite.interfaces.Executor;
import org.zy.dichroite.fluorite.interfaces.ParameterHandler;
import org.zy.dichroite.fluorite.interfaces.ResultSetHandler;
import org.zy.dichroite.fluorite.interfaces.StatementHandler;
import org.zy.dichroite.fluorite.mapping.MappedStatement;
import org.zy.dichroite.fluorite.session.Configuration;

/**
 * @DateTime 2021年9月9日;
 * @author zy(azurite-Y);
 * @Description
 */
public abstract class BaseStatementHandler implements StatementHandler {
	protected final Configuration configuration;

	protected final ResultSetHandler resultSetHandler;

	protected final ParameterHandler parameterHandler;

	protected final Executor executor;

	protected final MappedStatement mappedStatement;

	protected BoundSql boundSql;

	public BaseStatementHandler(Executor executor, MappedStatement mappedStatement, Map<Integer,Object> args, BoundSql boundSql) {
		this.configuration = mappedStatement.getConfiguration();
		this.executor = executor;
		this.mappedStatement = mappedStatement;

		if (boundSql == null) {
			boundSql = mappedStatement.getBoundSql(args);
		}
		this.boundSql = boundSql;

		this.parameterHandler = configuration.newParameterHandler(mappedStatement, args, boundSql);
		this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement, parameterHandler, boundSql);
	}

	@Override
	public Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException {
		ErrorContext.instance().sql(boundSql.getSql());
		Statement statement = null;
		try {
			statement = instantiateStatement(connection);
			statement.setQueryTimeout(transactionTimeout == 0 ? this.configuration.getDefaultStatementTimeout() : transactionTimeout);
			return statement;
		} catch (SQLException e) {
			closeStatement(statement);
			throw e;
		} catch (Exception e) {
			closeStatement(statement);
			throw new ExecutorException("预配置statement出错。  Cause: " + e, e);
		}
	}

	protected void closeStatement(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {}
	}

	/**
	 * 子类根据jdbc连接创建Statement(PreparedStatement)的自定义逻辑
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
	protected abstract Statement instantiateStatement(Connection connection) throws SQLException;
}
