package org.zy.dichroite.fluorite.reflection;

import java.util.Collection;
import java.util.List;

import org.zy.dichroite.fluorite.interfaces.ObjectWrapper;

/**
 * @DateTime 2021年10月20日;
 * @author zy(azurite-Y);
 * @Description
 */
public class CollectionWrapper implements ObjectWrapper {
	private Collection<Object> object;

	public CollectionWrapper(MetaObject metaObject, Collection<Object> object) {
		this.object = object;
	}

	@Override
	public Object get(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void set(String name, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String findProperty(String name, boolean useCamelCaseMapping) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getGetterNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String[] getSetterNames() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> getSetterType(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Class<?> getGetterType(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasSetter(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasGetter(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isCollection() {
		return true;
	}

	@Override
	public void add(Object element) {
		object.add(element);
	}

	@Override
	public <E> void addAll(List<E> element) {
		object.addAll(element);
	}

	@Override
	public Object resultsReconstructed() {
		return this.object;
	}
}
