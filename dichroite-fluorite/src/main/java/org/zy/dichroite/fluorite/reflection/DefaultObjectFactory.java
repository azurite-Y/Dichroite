package org.zy.dichroite.fluorite.reflection;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.zy.dichroite.fluorite.interfaces.ObjectFactory;
import org.zy.fluorite.core.utils.ReflectionUtils;

/**
 * @DateTime 2021年10月20日;
 * @author zy(azurite-Y);
 * @Description
 */
public class DefaultObjectFactory implements ObjectFactory, Serializable{
	private static final long serialVersionUID = -9214431731758932736L;

	@Override
	  public <T> T create(Class<T> type) {
	    return create(type, null);
	  }

	@Override
	@SuppressWarnings("unchecked")
	  public <T> T create(Class<T> type, List<Object> constructorArgs) {
	    Class<T> classToCreate = (Class<T>) resolveInterface(type);
	    Object[] args = constructorArgs == null ? null: constructorArgs.toArray();
	    return ReflectionUtils.instantiateClass(classToCreate, args);
	  }

	  protected Class<?> resolveInterface(Class<?> type) {
	    Class<?> classToCreate;
	    if (type == List.class || type == Collection.class || type == Iterable.class) {
	      classToCreate = ArrayList.class;
	    } else if (type == Map.class) {
	      classToCreate = HashMap.class;
	    } else if (type == SortedSet.class) {
	      classToCreate = TreeSet.class;
	    } else if (type == Set.class) {
	      classToCreate = HashSet.class;
	    } else {
	      classToCreate = type;
	    }
	    return classToCreate;
	  }

	  @Override
	  public boolean isCollection(Class<?> type) {
	    return Collection.class.isAssignableFrom(type);
	  }
	  
	  @Override
	public boolean isMap(Class<?> type) {
		return Map.class.isAssignableFrom(type);
	}
}
