package org.zy.dichroite.fluorite.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @DateTime 2021年9月28日;
 * @author zy(azurite-Y);
 * @Description 标注于Mapper接口类头，为整个mapper接口中的方法指定缓存空间，标注此注解且激活才会启用一级缓存
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface Mapper {
	/**
	 * 命名空间缓存名，若未指定则没人使用Mapper接口的首字母小写类名作为命名空间缓存名
	 * @return
	 */
	String cacheNameSpace() default "";
	
	/**
	 * 是否使用命名空间缓存，只有为true是才根据 {@code Mapper#cacheNamespace() } 创建命名空间缓存
	 * @return
	 */
	boolean nameSpaceActive() default true;
}
