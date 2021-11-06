package org.zy.dichroite.fluorite.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @DateTime 2021年9月8日;
 * @author zy(azurite-Y);
 * @Description
 */
public class Invocation {
	private Object target;
	private Method method;
	private Object[] args;

	public Invocation(Object target, Method method, Object[] args) {
		this.target = target;
		this.method = method;
		this.args = args;
	}

	public Object getTarget() {
		return target;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArgs() {
		return args;
	}

	public Object proceed() throws InvocationTargetException, IllegalAccessException {
		return method.invoke(target, args);
	}
}
