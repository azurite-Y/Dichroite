package org.zy.dichroite.fluorite.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import org.zy.dichroite.fluorite.interfaces.Invoker;

/**
 * @DateTime 2021年10月20日;
 * @author zy(azurite-Y);
 * @Description
 */
public class GetFieldInvoker implements Invoker {
	private Field field;

	public GetFieldInvoker(Field field) {
		this.field = field;
	}

	@Override
	public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
		return field.get(target);
	}

	@Override
	public Class<?> getType() {
		return field.getType();
	}
}
