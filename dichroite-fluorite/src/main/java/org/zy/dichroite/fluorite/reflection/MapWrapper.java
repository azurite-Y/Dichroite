package org.zy.dichroite.fluorite.reflection;

import java.util.List;
import java.util.Map;

import org.zy.dichroite.fluorite.interfaces.ObjectWrapper;

/**
 * @DateTime 2021年10月20日;
 * @author zy(azurite-Y);
 * @Description
 */
public class MapWrapper implements ObjectWrapper {
	private Map<String, Object> map;

	/**
	 * 构造 {@code ObjectWrapper }实现类对象
	 * @param metaObject
	 * @param map - 返回值对象
	 */
	public MapWrapper(MetaObject metaObject, Map<String, Object> map) {
		this.map = map;
	}

	@Override
	public Object get(String name) {
		return this.map.get(name);
	}

	@Override
	public void set(String name, Object value) {
		this.map.put(name, value);
	}

	@Override
	public String findProperty(String name, boolean useCamelCaseMapping) {
		return name;
	}

	@Override
	public String[] getGetterNames() {
		return map.keySet().toArray(new String[map.keySet().size()]);
	}

	@Override
	public String[] getSetterNames() {
		return map.keySet().toArray(new String[map.keySet().size()]);
	}

	@Override
	public Class<?> getSetterType(String name) {
		if (map.get(name) != null) {
			return map.get(name).getClass();
		}
		return Object.class;
	}

	@Override
	public Class<?> getGetterType(String name) {
		if (map.get(name) != null) {
			return map.get(name).getClass();
		} else {
			return Object.class;
		}
	}

	@Override
	public boolean hasSetter(String name) {
		return true;
	}

	@Override
	public boolean hasGetter(String name) {
		return map.containsKey(name);
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
		return this.map;
	}
}
