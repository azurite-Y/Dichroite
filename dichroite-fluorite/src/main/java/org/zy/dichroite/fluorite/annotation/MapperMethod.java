package org.zy.dichroite.fluorite.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.zy.dichroite.fluorite.mapping.SqlCommandType;

/**
 * @DateTime 2021年9月7日;
 * @author zy(azurite-Y);
 * @Description mapper方法执行SQL根注解
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface MapperMethod {
	/**
	 * 配置执行的SQL，使用前缀为‘&’的位置索引符标记其他 @insertValue 、 @queryWhere 、 @UpdateSet注解属性使用的位置
	 * @return
	 */
	String value();
	
	/**
	 * 标注SQL语句的执行类型,有效的执行类型：INSERT、 UPDATE、 DELETE、 SELECT。在使用动态SQL语句注解时必须指定此属性
	 * @return
	 */
	SqlCommandType sqlCommandType() default SqlCommandType.SELECT;
}
