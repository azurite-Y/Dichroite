package org.zy.dichroite.fluorite.interfaces;

import java.lang.reflect.InvocationTargetException;

/**
 * @DateTime 2021年10月20日;
 * @author zy(azurite-Y);
 * @Description
 */
public interface Invoker {
	Object invoke(Object target, Object[] args) throws IllegalArgumentException,IllegalAccessException, InvocationTargetException;

	Class<?> getType();
}
