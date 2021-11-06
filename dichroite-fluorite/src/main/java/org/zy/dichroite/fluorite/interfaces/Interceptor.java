package org.zy.dichroite.fluorite.interfaces;

import java.util.Properties;

import org.zy.dichroite.fluorite.plugin.Invocation;

/**
 * @DateTime 2021年9月8日;
 * @author zy(azurite-Y);
 * @Description
 */
public interface Interceptor {

	Object intercept(Invocation invocation) throws Throwable;

	Object plugin(Object target);

	void setProperties(Properties properties);

}
