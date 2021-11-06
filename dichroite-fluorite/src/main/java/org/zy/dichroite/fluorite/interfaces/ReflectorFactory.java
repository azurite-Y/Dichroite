package org.zy.dichroite.fluorite.interfaces;

import org.zy.dichroite.fluorite.reflection.Reflector;

/**
 * @DateTime 2021年10月20日;
 * @author zy(azurite-Y);
 * @Description
 */
public interface ReflectorFactory {
	boolean isClassCacheEnabled();

	void setClassCacheEnabled(boolean classCacheEnabled);

	Reflector findForClass(Class<?> type);
}
