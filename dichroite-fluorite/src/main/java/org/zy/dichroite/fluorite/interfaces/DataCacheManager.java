package org.zy.dichroite.fluorite.interfaces;

import java.util.Map;

import org.zy.dichroite.fluorite.binding.BoundSql;
import org.zy.dichroite.fluorite.executor.cache.CacheKey;
import org.zy.dichroite.fluorite.mapping.MappedStatement;

/**
 * @DateTime 2021年9月27日;
 * @author zy(azurite-Y);
 * @Description 数据缓存管理器基础接口 </br>
 * 缓存的范围说明：缓存创建后在执行下一次的增删改操作时被删除，没有时效要求。</br>
 * 通过命名空间来划分一级缓存，在同一命名空间下多次响应相同请求可重用缓存。
 */
public interface DataCacheManager {
	/**
	 * 
	 */
	void rollback(CacheKey key);

	/**
	 * 
	 */
	void commit(CacheKey key);
	
	/**
	 * 
	 * @param key
	 * @param cache
	 * @return
	 */
	Object getObject(CacheKey key);
	
	/**
	 * 缓存指定值
	 */
	void storeObject(CacheKey key, Object object);
	
	/**
	 * 
	 * @param cache
	 * @param key
	 * @param value
	 */
	void putObject(CacheKey key, Object value);
	
	/**
	 * 根据调用mapper方法的信息创建缓存键
	 * @param ms
	 * @param args
	 * @param boundSql
	 * @return
	 */
	CacheKey createCacheKey(MappedStatement ms, Map<Integer,Object> args, BoundSql boundSql);

	/**
	 * 判断是否已缓存，对于不同的执行器拥有不同的语义
	 * @param ms
	 * @param key
	 * @return
	 */
	boolean isCached(MappedStatement ms, CacheKey key);

	/**
	 * 
	 * @param cacheKey
	 * @param cache
	 */
	void clear(CacheKey cacheKey);
	
	/**
	 * @return 缓存中存储的元素数量(而不是容量).
	 */
	int getSize();
}
