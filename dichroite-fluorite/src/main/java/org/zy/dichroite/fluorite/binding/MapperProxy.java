package org.zy.dichroite.fluorite.binding;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

import org.zy.dichroite.fluorite.interfaces.SqlSession;
import org.zy.dichroite.fluorite.mapping.MapperMethod;


/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 代理Mapper接口的JDK代理类
 */
@SuppressWarnings("serial")
public class MapperProxy<T> implements InvocationHandler, Serializable {
	private final SqlSession sqlSession;
	private final Class<T> mapperInterface;
	private final Map<Method, MapperMethod> methodCache;

	/**
	 * 创建自  {@link MapperRegistry} 的 getMapper(Class<T>, SqlSession) 方法
	 * @param sqlSession
	 * @param mapperInterface
	 * @param methodCache
	 */
	public MapperProxy(SqlSession sqlSession, Class<T> mapperInterface, Map<Method, MapperMethod> methodCache) {
		this.sqlSession = sqlSession;
		this.mapperInterface = mapperInterface;
		this.methodCache = methodCache;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		if (Object.class.equals(method.getDeclaringClass())) { // 如果调用的是Object类的方法则直接执行并返回结果，若不是则进入代理逻辑处理
			return method.invoke(this, args);
		}
		// 缓存MapperMethod对象
		final MapperMethod mapperMethod = cachedMapperMethod(method);
		return mapperMethod.execute(sqlSession, args);
	}

	private MapperMethod cachedMapperMethod(Method method) {
		MapperMethod mapperMethod = methodCache.get(method);
		if (mapperMethod == null) {
			mapperMethod = new MapperMethod(mapperInterface, method, sqlSession.getConfiguration());
			methodCache.put(method, mapperMethod);
		}
		return mapperMethod;
	}
}
