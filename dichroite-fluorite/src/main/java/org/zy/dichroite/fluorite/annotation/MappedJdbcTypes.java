package org.zy.dichroite.fluorite.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.zy.dichroite.fluorite.type.JdbcType;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
/**
 * @DateTime 2021年10月22日;
 * @author zy(azurite-Y);
 * @Description 拟标注与 TypeHandler 实现类上的注解，以为其解释应应用那种jdbc类型的处理中
 */
public @interface MappedJdbcTypes {
	  JdbcType[] value();
	  boolean includeNullJdbcType() default false;
}
