package org.zy.dichroite.autoconfigure.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.zy.dichroite.autoconfigure.DichroiteAutoConfiguration;
import org.zy.dichroite.autoconfigure.MapperScannerRegistrar;
import org.zy.fluorite.core.annotation.Import;

/**
 * @DateTime 2021年9月6日 下午3:07:00;
 * @author zy(azurite-Y);
 * @Description Mapper接口扫描注解
 */
@Documented
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
@Import({DichroiteAutoConfiguration.class,MapperScannerRegistrar.class})
public @interface MapperScanner {
	/**
	 * Mapper接口包路径
	 * @return
	 */
	String[] value() default {};
}
