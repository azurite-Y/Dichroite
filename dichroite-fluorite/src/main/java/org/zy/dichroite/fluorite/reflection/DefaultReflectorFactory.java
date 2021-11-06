package org.zy.dichroite.fluorite.reflection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.zy.dichroite.fluorite.interfaces.ReflectorFactory;

/**
 * @DateTime 2021年10月20日;
 * @author zy(azurite-Y);
 * @Description
 */
public class DefaultReflectorFactory implements ReflectorFactory {
	private boolean classCacheEnabled = true;
	private final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<Class<?>, Reflector>();

	public DefaultReflectorFactory() {
	}

	@Override
	public boolean isClassCacheEnabled() {
		return classCacheEnabled;
	}

	@Override
	public void setClassCacheEnabled(boolean classCacheEnabled) {
		this.classCacheEnabled = classCacheEnabled;
	}

	@Override
	public Reflector findForClass(Class<?> type) {
		if (classCacheEnabled) {
			Reflector cached = reflectorMap.get(type);
			if (cached == null) {
				cached = new Reflector(type);
				reflectorMap.put(type, cached);
			}
			return cached;
		} else {
			return new Reflector(type);
		}
	}
}
