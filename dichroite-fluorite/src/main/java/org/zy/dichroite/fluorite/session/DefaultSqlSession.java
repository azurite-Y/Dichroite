package org.zy.dichroite.fluorite.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zy.dichroite.fluorite.exception.DichroiteException;
import org.zy.dichroite.fluorite.exception.ExecutorException;
import org.zy.dichroite.fluorite.exception.MapperBindingException;
import org.zy.dichroite.fluorite.exception.TooManyResultsException;
import org.zy.dichroite.fluorite.executor.ErrorContext;
import org.zy.dichroite.fluorite.interfaces.Executor;
import org.zy.dichroite.fluorite.interfaces.SqlSession;
import org.zy.dichroite.fluorite.mapping.MappedStatement;

/**
 * @DateTime 2021年9月8日;
 * @author zy(azurite-Y);
 * @Description
 */
public class DefaultSqlSession implements SqlSession {
	public final Logger logger = LoggerFactory.getLogger(DefaultSqlSession.class);

	private Configuration configuration;
	private Executor executor;
	private boolean autoCommit;
	private boolean dirty;

	public DefaultSqlSession(Configuration configuration, Executor executor) {
		this(configuration, executor, false);
	}

	public DefaultSqlSession(Configuration configuration, Executor executor, boolean autoCommit) {
		super();
		this.configuration = configuration;
		this.executor = executor;
		this.autoCommit = autoCommit;
	}

	/**
	 * 判断是否需要提交或回滚操作
	 * @param force
	 * @return
	 */
	private boolean isCommitOrRollbackRequired(boolean force) {
		return (!autoCommit && dirty) || force;
	}

	@Override
	public void close() {
		try {
			executor.close(isCommitOrRollbackRequired(false));
			dirty = false;
		} finally {
			ErrorContext.instance().reset();
		}
	}

	@Override
	public void clearCache() {
	}

	@Override
	public void rollback() {
		rollback(false);
	}

	@Override
	public void rollback(boolean force) {
		try {
			executor.rollback(isCommitOrRollbackRequired(force));
			dirty = false;
		} catch (Exception e) {
			logger.error("回滚事务异常，Cause: ", e);
			e.printStackTrace();;
		} finally {
			ErrorContext.instance().reset();
		}
	}

	@Override
	public void commit() {
		commit(false);
	}

	@Override
	public void commit(boolean force) {
		try {
			executor.commit(isCommitOrRollbackRequired(force));
			dirty = false;
		} catch (Exception e) {
			logger.error("提交事务异常，Cause: ", e);
			e.printStackTrace();
		} finally {
			ErrorContext.instance().reset();
		}
	}

	@Override
	public Configuration getConfiguration() {
		return this.configuration;
	}

	@Override
	public <T> T getMapper(Class<T> type) throws MapperBindingException {
		return configuration.<T>getMapper(type, this);
	}

	@Override
	public Connection getConnection() {
		Connection conn = null;
		try {
			conn = executor.getTransaction().getConnection();
		} catch (SQLException | ExecutorException e) {
			logger.error("获取jdbc连接异常，Cause: ", e);
			e.printStackTrace();
		}
		return conn;
	}

	public Executor getExecutor() {
		return executor;
	}
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}
	public boolean isAutoCommit() {
		return autoCommit;
	}
	public void setAutoCommit(boolean autoCommit) {
		this.autoCommit = autoCommit;
	}
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public <T> T selectOne(String statement) {
		return this.<T>selectOne(statement, null);
	}

	@Override
	public <T> T selectOne(String statement, Map<Integer,Object> args) {
		List<T> list = this.<T>selectList(statement, args);
		if (list.size() == 1) {
			return list.get(0);
		} else if (list.size() > 1) {
			throw new TooManyResultsException("预期selectOne()返回一个结果(或null), 但是 size: " + list.size());
		} else {
			return null;
		}	
	}

	@Override
	public <E> List<E> selectList(String statement) {
		return this.selectList(statement, null);
	}

	@Override
	public <E> List<E> selectList(String statement, Map<Integer,Object> args) {
		try {
			MappedStatement ms = configuration.getMappedStatement(statement);
			return executor.query(ms, args);
		} catch (Exception e) {
			throw new DichroiteException("查询数据库错误。 Cause: " + e, e);
		} finally {
			ErrorContext.instance().reset();
		}
	}

	@Override
	public int insert(String statement) {
		return insert(statement, null);
	}

	@Override
	public int insert(String statement, Map<Integer,Object> args) {
		return update(statement, args);
	}

	@Override
	public int update(String statement) {
		return update(statement, null);
	}

	@Override
	public int update(String statement, Map<Integer,Object> args) {
		try {
			dirty = true;
			MappedStatement ms = configuration.getMappedStatement(statement);
			return executor.update(ms, args);
		} catch (Exception e) {
			throw new DichroiteException("更新数据库错误.  Cause: " + e, e);
		} finally {
			ErrorContext.instance().reset();
		}
	}

	@Override
	public int delete(String statement) {
		return update(statement, null);
	}

	@Override
	public int delete(String statement, Map<Integer,Object> args) {
		return update(statement, args);
	}
}
