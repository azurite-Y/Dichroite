package org.zy.dichroite.fluorite.interfaces;

import java.util.List;
import java.util.Properties;

/**
 * @DateTime 2021年10月20日;
 * @author zy(azurite-Y);
 * @Description
 */
public interface ObjectFactory {
	/**
	 * 设置配置属性
	 */
	default void setProperties(Properties properties) {}

	/**
	 * 通过默认构造器创建对象
	 */
	<T> T create(Class<T> type);

	/**
	 * 根据指定类型和构造器参数根据新对象.
	 */
	<T> T create(Class<T> type, List<Object> constructorArgs);

	/**
	 * 判断是否是Collection类型
	 */
	boolean isCollection(Class<?> type);
	
	/**
	 * 判断是否是Map类型
	 */
	boolean isMap(Class<?> type);
}
