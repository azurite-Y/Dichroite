package org.zy.dichroite.fluorite.executor.cache;

import java.util.HashMap;
import java.util.Map;

import org.zy.dichroite.fluorite.binding.BoundSql;
import org.zy.dichroite.fluorite.interfaces.Cache;
import org.zy.dichroite.fluorite.interfaces.DataCacheManager;
import org.zy.dichroite.fluorite.mapping.MappedStatement;

/**
 * @DateTime 2021年10月25日;
 * @author zy(azurite-Y);
 * @Description
 */
public class DefaultDataCacheManager implements DataCacheManager {
	/**
	 * 
	 */
	protected final Map<CacheKey, Cache> cacheTemporary = new HashMap<>();
	/**
	 * 
	 */
	protected final Map<String, Cache> caches = new HashMap<>();

	@Override
	public void rollback(CacheKey key) {
		this.clear(key);
	}

	@Override
	public void commit(CacheKey key) {
	}

	@Override
	public Object getObject(CacheKey key) {
		return null;
	}

	@Override
	public void putObject(CacheKey key, Object value) {
		Cache cache = new ObjectDataCache();
		cacheTemporary.put(key, cache);
	}

	@Override
	public CacheKey createCacheKey(MappedStatement ms, Map<Integer, Object> args, BoundSql boundSql) {
		CacheKey cacheKey = new CacheKey(ms.getId(), args);
		ms.getStatementLogger().info("==> CacheKey: " + cacheKey);
		return cacheKey;
	}

	@Override
	public boolean isCached(MappedStatement ms, CacheKey key) {
		return this.caches.containsKey(key);
	}

	@Override
	public void clear(CacheKey cacheKey) {
		this.cacheTemporary.remove(cacheKey);
	}

	@Override
	public int getSize() {
		return caches.size();
	}

	@Override
	public void storeObject(CacheKey key, Object object) {
		// TODO 自动生成的方法存根
		
	}

}
