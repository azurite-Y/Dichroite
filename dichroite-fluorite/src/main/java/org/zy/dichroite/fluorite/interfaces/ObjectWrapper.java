package org.zy.dichroite.fluorite.interfaces;

import java.util.List;

import org.zy.dichroite.fluorite.reflection.MapWrapper;

/**
 * @DateTime 2021年10月20日;
 * @author zy(azurite-Y);
 * @Description
 */
public interface ObjectWrapper {
	/**
	 * 获取对应属性值
	 * @param name
	 * @return
	 */
	Object get(String name);

	/**
	 * 设置属性值
	 * @param name
	 * @param value
	 */
	void set(String name, Object value);

	/**
	 * 获取指定名称在对象中对应的属性名
	 * @param name
	 * @param useCamelCaseMapping
	 * @return
	 */
	String findProperty(String name, boolean useCamelCaseMapping);

	String[] getGetterNames();

	String[] getSetterNames();

	Class<?> getSetterType(String name);

	Class<?> getGetterType(String name);

	boolean hasSetter(String name);

	boolean hasGetter(String name);

	boolean isCollection();
	
	/**
	 * 
	 * @param element
	 */
	void add(Object element);

	/**
	 * 
	 * @param <E>
	 * @param element
	 */
	<E> void addAll(List<E> element);
	
	/**
	 * 结果重构，对于返回值对象最后一次操作，可用于组织返回值内部对象的方法
	 * @return 返回实现中持有的处理对象
	 * @see MapWrapper#resultsReconstructed()
	 */
	Object resultsReconstructed();
}
