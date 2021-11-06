package org.zy.dichroite.fluorite.executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zy.dichroite.fluorite.binding.BoundSql;
import org.zy.dichroite.fluorite.exception.ExecutorException;
import org.zy.dichroite.fluorite.executor.cache.CacheKey;
import org.zy.dichroite.fluorite.interfaces.Executor;
import org.zy.dichroite.fluorite.interfaces.Transaction;
import org.zy.dichroite.fluorite.mapping.MappedStatement;
import org.zy.dichroite.fluorite.session.Configuration;
import org.zy.fluorite.core.utils.DebugUtils;

/**
 * @DateTime 2021年9月8日;
 * @author zy(azurite-Y);
 * @Description 执行器基类
 */
public abstract class BaseExecutor  implements Executor {
	protected static final Logger logger = LoggerFactory.getLogger(BaseExecutor.class);

	protected Transaction transaction;

	protected Configuration configuration;

	private boolean closed;

	protected int queryStack;

	public BaseExecutor(Transaction transaction, Configuration configuration) {
		this.transaction = transaction;
		this.configuration = configuration;
	}

	@Override
	public Transaction getTransaction() throws ExecutorException {
		if (closed) {
			throw new ExecutorException("执行器已关闭.");
		}
		return transaction;
	}

	@Override
	public void close(boolean forceRollback) {
		try {
			try {
				rollback(forceRollback);
			} finally {
				if (transaction != null) {
					transaction.close();
				}
			}
		} catch (SQLException e) {
			logger.warn("在关闭事务时触发了意料之外的异常。 Cause: " + e);
		} finally {
			transaction = null;
			closed = true;
		}
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void commit(boolean required) throws SQLException {
		if (closed) {
			throw new ExecutorException("无法提交，事务已关闭");
		}
		if (required) {
			transaction.commit();
		}		
	}

	@Override
	public void rollback(boolean required) throws SQLException {
		if (!closed) {
			try {
			} finally {
				if (required) {
					transaction.rollback();
				}
			}
		}		
	}

	protected Connection getConnection(Logger statementLogger) throws SQLException {
		Connection connection = transaction.getConnection();
		DebugUtils.log(statementLogger, "从事务环境中获得jdbc连接，by Connection：" + connection);
		return connection;
	}

	@Override
	public int update(MappedStatement ms, Map<Integer,Object> args) throws SQLException {
		ErrorContext.instance().resource(ms.getId()).activity("执行update语句").object(ms.getId());
		if (closed) {
			throw new ExecutorException("执行器已关闭");
		}
		return doUpdate(ms, args);
	}

	@Override
	public <E> List<E> query(MappedStatement ms, Map<Integer,Object> args) throws SQLException {
		BoundSql boundSql = ms.getBoundSql(args);
		return query(ms, args, boundSql);
	}

	public <E> List<E> query(MappedStatement ms, Map<Integer,Object> args, BoundSql boundSql) throws SQLException {
		ErrorContext.instance().resource(ms.getId()).activity("执行查询语句").object(ms.getId());
		if (closed) {
			throw new ExecutorException("执行器已关闭.");
		}
		List<E> list;
		try {
			queryStack++;
			// 查询一级缓存
//			list = resultHandler == null ? (List<E>) transactionalCacheManager.getObject(cacheKey) : null;
//			if (list != null) {
//				handleLocallyCachedOutputParameters(ms, cacheKey, args, boundSql);
//			} else {
				list = queryFromDatabase(ms, args, boundSql);
//			}
		} finally {
			queryStack--;
		}
		return list;
	}

	/**
	 * 处理从本地缓存中获得的结果集
	 * @param ms
	 * @param cacheKey
	 * @param parameter
	 * @param boundSql
	 */
	protected void handleLocallyCachedOutputParameters(MappedStatement ms, CacheKey cacheKey, Map<Integer,Object> args,
			BoundSql boundSql) {
		
	}

	/**
	 * 
	 * @param ms
	 * @param parameter
	 * @param resultHandler
	 * @param boundSql
	 * @return
	 * @throws SQLException 
	 */
	private <E> List<E> queryFromDatabase(MappedStatement ms, Map<Integer,Object> args,	BoundSql boundSql) throws SQLException {
		List<E> list;
//	    localCache.putObject(key, EXECUTION_PLACEHOLDER);
	    try {
	      list = doQuery(ms, args, boundSql);
	    } finally {
//	      localCache.removeObject(key);
	    }
//	    localCache.putObject(key, list);
//	    if (ms.getStatementType() == StatementType.CALLABLE) {
//	      localOutputParameterCache.putObject(key, parameter);
//	    }
	    return list;
	}

	protected abstract int doUpdate(MappedStatement ms, Map<Integer,Object> args) throws SQLException;

	protected abstract <E> List<E> doQuery(MappedStatement ms, Map<Integer,Object> args, BoundSql boundSql) throws SQLException;
}
