package org.zy.dichroite.fluorite.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @DateTime 2021年10月8日;
 * @author zy(azurite-Y);
 * @Description 标注此属性则代表标注类需要被解析
 */
@Documented
@Retention(RUNTIME)
@Target(ElementType.TYPE)
public @interface BeanMapping {
	 /**
     * 实体对应的表名
     */
    String value() default "";
}
