package org.zy.dichroite.fluorite.interfaces;

/**
 * @DateTime 2021年9月8日;
 * @author zy(azurite-Y);
 * @Description 适用于Executor类的插件接口
 * @param <T> 应用此插件的对象类型
 */
public interface ExecutorPlugin extends DichroitePlugin {
	
	@Override
	default boolean support(Object obj) {
		return Executor.class.isAssignableFrom(obj.getClass());
	}
}
