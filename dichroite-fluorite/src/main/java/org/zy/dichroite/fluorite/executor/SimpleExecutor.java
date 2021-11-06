package org.zy.dichroite.fluorite.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.zy.dichroite.fluorite.binding.BoundSql;
import org.zy.dichroite.fluorite.interfaces.StatementHandler;
import org.zy.dichroite.fluorite.interfaces.Transaction;
import org.zy.dichroite.fluorite.mapping.MappedStatement;
import org.zy.dichroite.fluorite.session.Configuration;

/**
 * @DateTime 2021年9月8日;
 * @author zy(azurite-Y);
 * @Description 基础的一级缓存执行器
 */
public class SimpleExecutor extends BaseExecutor {

	public SimpleExecutor(Configuration configuration, Transaction transaction) {
		super(transaction, configuration);
	}

	/**
	 * 配置Statement
	 * @param handler
	 * @param statementLog
	 * @return
	 * @throws SQLException
	 */
	private Statement prepareStatement(StatementHandler handler, Logger statementLog) throws SQLException {
		Statement stmt;
		statementLog.info("==> Preparing\t: " + handler.getBoundSql().getSql());
		Connection connection = getConnection(statementLog);
		stmt = handler.prepare(connection, transaction.getTimeout());
		// 配置sql执行参数
		handler.parameterize(stmt);
		return stmt;
	}
	
	@Override
	protected int doUpdate(MappedStatement ms, Map<Integer,Object> args) throws SQLException {
		Statement stmt = null;
		try {
			Configuration configuration = ms.getConfiguration();
			StatementHandler handler = configuration.newStatementHandler(this, ms, args, null);
			stmt = prepareStatement(handler, ms.getStatementLogger());
			return handler.update(stmt);
		} finally {
			closeStatement(stmt);
		}
	}

	@Override
	protected <E> List<E> doQuery(MappedStatement ms, Map<Integer,Object> args, BoundSql boundSql)	throws SQLException {
		Statement stmt = null;
		try {
			Configuration configuration = ms.getConfiguration();
			StatementHandler handler = configuration.newStatementHandler(this, ms, args, boundSql);
			stmt = prepareStatement(handler, ms.getStatementLogger());
			return handler.<E>query(stmt);
		} finally {
			closeStatement(stmt);
		}
	}
	
	protected void closeStatement(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {}
		}
	}
}
