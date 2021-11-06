package org.zy.dichroite.fluorite.reflection;

import java.util.Collection;
import java.util.Map;

import org.zy.dichroite.fluorite.interfaces.ObjectFactory;
import org.zy.dichroite.fluorite.interfaces.ObjectWrapper;
import org.zy.dichroite.fluorite.interfaces.ReflectorFactory;
import org.zy.dichroite.fluorite.mapping.ResultMap;

/**
 * @DateTime 2021年10月20日;
 * @author zy(azurite-Y);
 * @Description
 */
public class MetaObject {
	private Object originalObject;
	private ObjectWrapper objectWrapper;
	private ObjectFactory objectFactory;
	private ReflectorFactory reflectorFactory;
	private ResultMap resultMap;

	@SuppressWarnings("unchecked")
	private MetaObject(ResultMap resultMap, Object object, ObjectFactory objectFactory, ReflectorFactory reflectorFactory) {
		this.resultMap = resultMap;
		this.originalObject = object;
		this.objectFactory = objectFactory;
		this.reflectorFactory = reflectorFactory;

		if (object instanceof ObjectWrapper) {
			this.objectWrapper = (ObjectWrapper) object;
		} else if (object instanceof Map) {
			this.objectWrapper = new MapWrapper(this, (Map<String, Object>) object);
		} else if (object instanceof Collection) {
			this.objectWrapper = new CollectionWrapper(this, (Collection<Object>) object);
		} else {
			this.objectWrapper = new BeanWrapper(this, object);
		}
	}

	public static MetaObject forObject(ResultMap resultMap, Object resultObject, ObjectFactory objectFactory, ReflectorFactory reflectorFactory) {
		return new MetaObject(resultMap, resultObject, objectFactory, reflectorFactory);
	}

	public ResultMap getResultMap() {
		return resultMap;
	}
	public void setResultMap(ResultMap resultMap) {
		this.resultMap = resultMap;
	}
	public Object getOriginalObject() {
		return originalObject;
	}
	public ObjectWrapper getObjectWrapper() {
		return objectWrapper;
	}
	public ObjectFactory getObjectFactory() {
		return objectFactory;
	}
	public ReflectorFactory getReflectorFactory() {
		return reflectorFactory;
	}

	public boolean hasSetter(String name) {
		return objectWrapper.hasSetter(name);
	}
	public boolean hasGetter(String name) {
		return objectWrapper.hasGetter(name);
	}
	public String findProperty(String propName, boolean useCamelCaseMapping) {
		return objectWrapper.findProperty(propName, useCamelCaseMapping);
	}
	public String[] getGetterNames() {
		return objectWrapper.getGetterNames();
	}
	public String[] getSetterNames() {
		return objectWrapper.getSetterNames();
	}
	public Class<?> getSetterType(String name) {
		return objectWrapper.getSetterType(name);
	}
	public Class<?> getGetterType(String name) {
		return objectWrapper.getGetterType(name);
	}

	public void setValue(String property, Object value) {
		objectWrapper.set(property, value);		
	}
	public Object getValue(String name) {
		return objectWrapper.get(name);
	}
	/**
	 * 结果重构，对于返回值对象进行最后一次操作，可用于组织返回值内部对象的方法
	 * @return 返回实现中持有的处理对象
	 * @see MapWrapper#resultsReconstructed()
	 */
	public Object resultsReconstructed() {
		return objectWrapper.resultsReconstructed();
	}
}
