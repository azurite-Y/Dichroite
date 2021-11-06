package org.zy.dichroite.fluorite.interfaces;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * @DateTime 2021年9月28日;
 * @author zy(azurite-Y);
 * @Description 缓存数据持有类抽象接口
 */
public interface Cache {
	String getId();

	void setObject(Object key, Object value);

	Object getObject(Object key);

	Object removeObject(Object key);

	/**
	 * 清空缓存
	 */  
	void clear();

	ReadWriteLock getReadWriteLock();
}
