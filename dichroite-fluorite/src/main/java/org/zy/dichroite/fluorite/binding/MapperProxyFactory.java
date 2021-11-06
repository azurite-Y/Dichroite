package org.zy.dichroite.fluorite.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.zy.dichroite.fluorite.interfaces.SqlSession;
import org.zy.dichroite.fluorite.mapping.MapperMethod;


/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 通过指定的mapper代理类代理Mapper接口的JDK代理类
 */
public class MapperProxyFactory<T> {
	private final Class<T> mapperInterface;
	private final Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<Method, MapperMethod>();

	public MapperProxyFactory(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	public Class<T> getMapperInterface() {
		return mapperInterface;
	}

	public Map<Method, MapperMethod> getMethodCache() {
		return methodCache;
	}

	@SuppressWarnings("unchecked")
	protected T newInstance(MapperProxy<T> mapperProxy) {
		/**
		 * 将指定的类动态生成为代理类
		 * newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler hadler)
		 * loader：定义代理类的类加载器
		 * interfaces：代理类要实现的接口列表
		 * hadler：将方法调用分派到的调用处理程序
		 */
		return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[] { mapperInterface }, mapperProxy);
	}

	/**
	 * 调用自MapperFactoryBean的getObject方法
	 * @param sqlSession
	 * @return
	 */
	public T newInstance(SqlSession sqlSession) {
		final MapperProxy<T> mapperProxy = new MapperProxy<T>(sqlSession, mapperInterface, methodCache);
		return newInstance(mapperProxy);
	}
}
