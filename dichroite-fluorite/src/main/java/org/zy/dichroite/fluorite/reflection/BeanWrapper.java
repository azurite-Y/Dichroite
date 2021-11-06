package org.zy.dichroite.fluorite.reflection;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.zy.dichroite.fluorite.exception.ReflectionException;
import org.zy.dichroite.fluorite.interfaces.Invoker;
import org.zy.dichroite.fluorite.interfaces.ObjectWrapper;
import org.zy.fluorite.core.utils.StringUtils;

/**
 * @DateTime 2021年10月20日;
 * @author zy(azurite-Y);
 * @Description
 */
public class BeanWrapper implements ObjectWrapper {
	protected static final Object[] NO_ARGUMENTS = new Object[0];
	private Object object;
	private Reflector reflector;

	public BeanWrapper(Reflector reflector, Object object) {
		this.object = object;
		this.reflector = reflector;
	}
	public BeanWrapper(MetaObject metaObject, Object object) {
		this.object = object;
		this.reflector = metaObject.getReflectorFactory().findForClass(object.getClass());
	}

	@Override
	public Object get(String name) {
		Object obj = null;
		try {
			Invoker getInvoker = reflector.getGetInvoker(name);
			obj = getInvoker.invoke(object, NO_ARGUMENTS);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public void set(String name, Object value) {
		Invoker setInvoker = reflector.getSetInvoker(name);
		Object[] args = {value};
		try {
			setInvoker.invoke(object, args);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException("入参类型[" + value.getClass() + "]与setter方法入参类型[" + reflector.getSetterType(name) + "]不符，",e);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String findProperty(String name, boolean useCamelCaseMapping) {
		if (useCamelCaseMapping) { // "tom_and_jack" ==> "tomAndJack"
			String[] split = name.split("_");
			StringBuilder builder = new StringBuilder();
			builder.append(split[0]);
			for (int i = 1; i < split.length; i++) {
				String string = split[i];
				builder.append(StringUtils.capitalize(string));
			}
			name = builder.toString();
		}
		return name;
	}

	@Override
	public String[] getGetterNames() {
		return reflector.getGetablePropertyNames();
	}

	@Override
	public String[] getSetterNames() {
		return reflector.getSetablePropertyNames();
	}

	@Override
	public Class<?> getSetterType(String name) {
		return reflector.getSetterType(name);
	}

	@Override
	public Class<?> getGetterType(String name) {
		return reflector.getGetterType(name);
	}

	@Override
	public boolean hasSetter(String name) {
		return reflector.hasSetter(name);
	}

	@Override
	public boolean hasGetter(String name) {
		return reflector.hasGetter(name);
	}

	@Override
	public boolean isCollection() {
		return false;
	}

	@Override
	public void add(Object element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public <E> void addAll(List<E> element) {
		throw new UnsupportedOperationException();
	}
	@Override
	public Object resultsReconstructed() {
		return object;
	}
}
