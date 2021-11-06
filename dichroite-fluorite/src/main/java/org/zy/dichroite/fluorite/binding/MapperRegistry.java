package org.zy.dichroite.fluorite.binding;

import java.util.HashMap;
import java.util.Map;

import org.zy.dichroite.fluorite.exception.MapperBindingException;
import org.zy.dichroite.fluorite.interfaces.SqlSession;
import org.zy.dichroite.fluorite.session.Configuration;
import org.zy.dichroite.fluorite.session.MapperAnnotationParser;
import org.zy.fluorite.core.interfaces.AnnotationMetadata;


/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 收集创建的MapperProxyFactory类
 */
public class MapperRegistry {
	private final Configuration config;
	private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<Class<?>, MapperProxyFactory<?>>();

	public MapperRegistry(Configuration config) {
		this.config = config;
	}

	@SuppressWarnings("unchecked")
	public <T> T getMapper(Class<T> type, SqlSession sqlSession) throws MapperBindingException {
		final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
		if (mapperProxyFactory == null) {
			throw new MapperBindingException("已存在类型，by： " + type + ".");
		}
		try {
			// 创建MapperProxy实例
			return mapperProxyFactory.newInstance(sqlSession);
		} catch (Exception e) {
			throw new MapperBindingException("获得mapper实例出错。 Cause: " + e, e);
		}
	}

	/**
	 * 判断此mapper接口是否已注册到mapperRegistry的容器中
	 * @param <T>
	 * @param clz
	 * @return
	 */
	public <T> boolean hasMapper(Class<T> clz) {
		return knownMappers.containsKey(clz);
	}

	public <T> void addMapper(Class<T> mapperInterface,AnnotationMetadata annotationMetadata) throws MapperBindingException {
		if (mapperInterface.isInterface()) {
			if (hasMapper(mapperInterface)) {
				throw new MapperBindingException("已存在类型，by： " + mapperInterface + ".");
			}
			boolean loadCompleted = false;
			try {
				knownMappers.put(mapperInterface, new MapperProxyFactory<T>(mapperInterface));
				MapperAnnotationParser parser = new MapperAnnotationParser(config, annotationMetadata, mapperInterface);
				parser.parse();
				loadCompleted = true;
			} finally {
				if (!loadCompleted) { // 在解析注解过程中可能的异常
					knownMappers.remove(mapperInterface);
				}
			}
		}
	}

}
