package org.zy.dichroite.fluorite.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @DateTime 2021年9月30日;
 * @author zy(azurite-Y);
 * @Description 参数pojo映射注解，提供此注解关联入参
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Column {
	/**
	 * SQL中使用的数据库字段名，未指定时使用属性名来作为数据库中的字段名
	 * @return
	 */
	String name() default "";
	
	/**
	 * 属性梯度，升序排列.在启用 {@code QueryTemplate#serial }模式时使用。</br>
	 * 一般来说使用频率高的字段的的梯度应靠前，可按照使用频率
	 * @see QueryTemplate
	 */
	int level() default 0;
}
