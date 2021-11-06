package org.zy.dichroite.fluorite.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.zy.dichroite.fluorite.binding.BoundSql;
import org.zy.dichroite.fluorite.mapping.MappedStatement;
import org.zy.dichroite.fluorite.session.Configuration;


/**
 * @DateTime 2021年9月8日;
 * @author zy(azurite-Y);
 * @Description 执行语句句柄处理重新
 */
public interface StatementHandler {
	/**
	 *  执行语句句柄预配置
	 * @param connection
	 * @param transactionTimeout
	 * @return
	 * @throws SQLException
	 */
	Statement prepare(Connection connection, Integer transactionTimeout) throws SQLException;

	/**
	 * 执行更新语句
	 * @param statement
	 * @return
	 * @throws SQLException
	 */
	int update(Statement statement) throws SQLException;

	/**
	 * 执行查询语句
	 * @param <E>
	 * @param statement
	 * @param resultHandler
	 * @return
	 * @throws SQLException
	 */
	<E> List<E> query(Statement statement) throws SQLException;

	/**
	 * 配置sql执行参数
	 * @param stmt
	 */
	void parameterize(Statement stmt);
	
	ParameterHandler getParameterHandler();

	BoundSql getBoundSql();

	Configuration getConfiguration();
	
	ResultSetHandler getResultSetHandler();
	
	Executor getExecutor();
	
	MappedStatement getMappedStatement();
}
