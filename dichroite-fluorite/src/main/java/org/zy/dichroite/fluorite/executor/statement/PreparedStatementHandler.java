package org.zy.dichroite.fluorite.executor.statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.zy.dichroite.fluorite.binding.BoundSql;
import org.zy.dichroite.fluorite.interfaces.Executor;
import org.zy.dichroite.fluorite.interfaces.ParameterHandler;
import org.zy.dichroite.fluorite.interfaces.ResultSetHandler;
import org.zy.dichroite.fluorite.mapping.MappedStatement;
import org.zy.dichroite.fluorite.session.Configuration;

/**
 * @DateTime 2021年9月9日;
 * @author zy(azurite-Y);
 * @Description PreparedStatement处理器
 */
public class PreparedStatementHandler extends BaseStatementHandler{

	public PreparedStatementHandler(Executor executor, MappedStatement mappedStatement, Map<Integer,Object> args, BoundSql boundSql) {
		super(executor, mappedStatement, args, boundSql);
	}

	@Override
	public int update(Statement statement) throws SQLException {
		PreparedStatement ps = (PreparedStatement) statement;
		ps.execute();
		int rows = ps.getUpdateCount();
		return rows;
	}

	@Override
	public <E> List<E> query(Statement statement) throws SQLException {
		PreparedStatement ps = (PreparedStatement) statement;
		ps.execute();
		return resultSetHandler.<E> handleResultSets(ps);
	}

	@Override
	protected Statement instantiateStatement(Connection connection) throws SQLException {
		String sql = boundSql.getSql();
		return connection.prepareStatement(sql);
	}

	//-------------------------------------------getter-------------------------------------------
	@Override
	public ParameterHandler getParameterHandler() {
		return this.parameterHandler;
	}
	@Override
	public BoundSql getBoundSql() {
		return this.boundSql;
	}
	@Override
	public Configuration getConfiguration() {
		return configuration;
	}
	@Override
	public ResultSetHandler getResultSetHandler() {
		return resultSetHandler;
	}
	@Override
	public Executor getExecutor() {
		return executor;
	}
	@Override
	public MappedStatement getMappedStatement() {
		return mappedStatement;
	}

	@Override
	public void parameterize(Statement statement) {
		parameterHandler.setParameters((PreparedStatement) statement);		
	}
}
