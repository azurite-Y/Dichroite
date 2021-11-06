package org.zy.dichroite.fluorite.executor;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.zy.dichroite.fluorite.binding.BoundSql;
import org.zy.dichroite.fluorite.executor.cache.CacheKey;
import org.zy.dichroite.fluorite.executor.cache.DefaultDataCacheManager;
import org.zy.dichroite.fluorite.interfaces.Cache;
import org.zy.dichroite.fluorite.interfaces.DataCacheManager;
import org.zy.dichroite.fluorite.interfaces.Executor;
import org.zy.dichroite.fluorite.interfaces.Transaction;
import org.zy.dichroite.fluorite.mapping.MappedStatement;

/**
 * @DateTime 2021年9月8日;
 * @author zy(azurite-Y);
 * @Description 缓存执行器
 */
public class CachingExecutor implements Executor {
	/** 数据缓存管理器 */
	private DataCacheManager dataCacheManager = new DefaultDataCacheManager();

	/**	可能被代理的Executor */
	private Executor wrapper;

	private CacheKey cacheKey;

	public CachingExecutor(Executor executor) {
		this.wrapper = executor;
	}

	@Override
	public int update(MappedStatement ms, Map<Integer,Object> args) throws SQLException {
		flushCacheIfRequired(ms);
		return wrapper.update(ms, args);
	}

	@Override
	public <E> List<E> query(MappedStatement ms, Map<Integer,Object> args) throws SQLException {
		BoundSql boundSql = ms.getBoundSql(args);
//		this.cacheKey = dataCacheManager.createCacheKey(ms, args, boundSql);
		return query(ms, args, boundSql);
	}

	@Override
	public <E> List<E> query(MappedStatement ms, Map<Integer,Object> args, BoundSql boundSql) throws SQLException {
	    return wrapper.<E>query(ms, args, boundSql);
	}

	@Override
	public Transaction getTransaction() {
		return wrapper.getTransaction();
	}

	@Override
	public void close(boolean forceRollback) {
		wrapper.close(forceRollback);
	}

	@Override
	public boolean isClosed() {
		return wrapper.isClosed();
	}

	@Override
	public void commit(boolean required) throws SQLException {
		wrapper.commit(required);
	}

	@Override
	public void rollback(boolean required) throws SQLException {
		try {
			wrapper.rollback(required);
		} finally {
			if (required) {
				dataCacheManager.rollback(this.cacheKey);
			}
		}
	}
	
	/**
	 * 按需清空一级缓存
	 * @param ms
	 */
	private void flushCacheIfRequired(MappedStatement ms) {
		Cache cache = ms.getCache();
		if (cache != null && ms.isFlushCacheRequired()) {      
			dataCacheManager.clear(this.cacheKey);
		}
	}
}
