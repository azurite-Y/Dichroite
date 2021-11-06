package org.zy.dichroite.fluorite.reflection;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ReflectPermission;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.zy.dichroite.fluorite.exception.ReflectionException;
import org.zy.dichroite.fluorite.interfaces.Invoker;
import org.zy.fluorite.core.convert.ResolvableType;


/**
 * @DateTime 2021年10月20日;
 * @author zy(azurite-Y);
 * @Description
 */
public class Reflector {
	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	private Class<?> type;
	private String[] readablePropertyNames = EMPTY_STRING_ARRAY;
	private String[] writeablePropertyNames = EMPTY_STRING_ARRAY;
	private Map<String, Invoker> setMethods = new HashMap<String, Invoker>();
	private Map<String, Invoker> getMethods = new HashMap<String, Invoker>();
	private Map<String, ResolvableType> setTypes = new HashMap<>();
	private Map<String, ResolvableType> getTypes = new HashMap<>();
	private Map<String, PropertyDescriptor> fieldDescriptor = new HashMap<>();
	private Constructor<?> defaultConstructor;
	/**
	 * 忽略大小写
	 */
	private Map<String, String> caseInsensitivePropertyMap = new HashMap<String, String>();

	public Reflector(Class<?> clazz) {
		this.type = clazz;
		addDefaultConstructor(clazz);
		methodClassify(clazz);
		this.readablePropertyNames = getMethods.keySet().toArray(new String[getMethods.keySet().size()]);
		this.writeablePropertyNames = setMethods.keySet().toArray(new String[setMethods.keySet().size()]);
		for (String propName : readablePropertyNames) {
			this.caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
		}
		for (String propName : writeablePropertyNames) {
			this.caseInsensitivePropertyMap.put(propName.toUpperCase(Locale.ENGLISH), propName);
		}
	}

	private void methodClassify(Class<?> clazz) {
		Field[] fields = type.getDeclaredFields();
		for (Field field : fields) {
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
				continue; // 忽略静态和常量属性
			}
				
			String name = field.getName();
			try {
				PropertyDescriptor pd = new PropertyDescriptor(name, type);
				this.fieldDescriptor.put(name, pd);
				this.setMethods.put(name, new MethodInvoker(pd.getWriteMethod()));
				this.getMethods.put(name, new MethodInvoker(pd.getReadMethod()));
				ResolvableType fileType = ResolvableType.forClass(field.getGenericType());
				this.getTypes.put(name, fileType);
				this.setTypes.put(name, fileType);
			} catch (IntrospectionException e) {
				throw new ReflectionException("属性[" + name + "]没有对应的getter、setter方法，无法作为结果集的封装类",e);
			}
		}
	}

	/**
	 * 默认构造器
	 * @param clazz
	 */
	private void addDefaultConstructor(Class<?> clazz) {
		Constructor<?>[] consts = clazz.getDeclaredConstructors();
		for (Constructor<?> constructor : consts) {
			if (constructor.getParameterTypes().length == 0) {
				if (canAccessPrivateMethods()) {
					try {
						constructor.setAccessible(true);
					} catch (Exception e) {}
				}
				if (constructor.isAccessible()) {
					this.defaultConstructor = constructor;
				}
			}
		}
	}

	/**
	 * 权限验证
	 * @return
	 */
	private static boolean canAccessPrivateMethods() {
		try {
			SecurityManager securityManager = System.getSecurityManager();
			if (null != securityManager) {
				securityManager.checkPermission(new ReflectPermission("suppressAccessChecks"));
			}
		} catch (SecurityException e) {
			return false;
		}
		return true;
	}
	
	public Class<?> getType() {
		return type;
	}
	public Constructor<?> getDefaultConstructor() {
		if (defaultConstructor != null) {
			return defaultConstructor;
		} else {
			throw new ReflectionException("在[" + type + "]中未找到默认构造器");
		}
	}
	public boolean hasDefaultConstructor() {
		return defaultConstructor != null;
	}
	public Invoker getSetInvoker(String propertyName) {
		Invoker method = setMethods.get(propertyName);
		if (method == null) {
			throw new ReflectionException("未找到属性['" + propertyName + "']对应的setter方法，by type：'" + type + "'");
		}
		return method;
	}
	public Invoker getGetInvoker(String propertyName) {
		Invoker method = getMethods.get(propertyName);
		if (method == null) {
			throw new ReflectionException("未找到属性[ '" + propertyName + "']对应的getter方法，by type：'" + type + "'");
		}
		return method;
	}
	public Class<?> getSetterType(String propertyName) {
		ResolvableType clazz = setTypes.get(propertyName);
		Class<?> resolved = clazz.resolve();
		if (resolved == null) {
			throw new ReflectionException("未找到属性['" + propertyName + "']对应的setter方法，by type：'" + type + "'");
		}
		return resolved;
	}
	public Class<?> getGetterType(String propertyName) {
		ResolvableType clazz = getTypes.get(propertyName);
		Class<?> resolved = clazz.resolve();
		if (resolved == null) {
			throw new ReflectionException("未找到属性[ '" + propertyName + "']对应的getter方法，by type：'" + type + "'");
		}
		return resolved;
	}
	public String[] getGetablePropertyNames() {
		return readablePropertyNames;
	}
	public String[] getSetablePropertyNames() {
		return writeablePropertyNames;
	}
	public boolean hasSetter(String propertyName) {
		return setMethods.keySet().contains(propertyName);
	}
	public boolean hasGetter(String propertyName) {
		return getMethods.keySet().contains(propertyName);
	}

	public String findPropertyName(String name) {
		return caseInsensitivePropertyMap.get(name.toUpperCase(Locale.ENGLISH));
	}
}
