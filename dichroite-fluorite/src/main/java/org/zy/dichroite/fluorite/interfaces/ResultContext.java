package org.zy.dichroite.fluorite.interfaces;

import org.zy.dichroite.fluorite.reflection.MetaObject;

/**
 * @DateTime 2021年9月9日;
 * @author zy(azurite-Y);
 * @Description 单行结果对象封装
 */
public interface ResultContext {
	MetaObject getResultMetaObject();

	int getResultCount();

	boolean isStopped();

	void stop();

	/**
	 * 下一个结果
	 * @param metaObject
	 */
	void nextResultMetaObject(MetaObject metaObject);
}
