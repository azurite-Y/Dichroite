package org.zy.dichroite.fluorite.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
/**
 * @DateTime 2021年10月22日;
 * @author zy(azurite-Y);
 * @Description 拟标注与 TypeHandler 实现类上的注解，以解释其应应用那种java类型的处理中
 */
public @interface MappedTypes {
	Class<?>[] value();
}
