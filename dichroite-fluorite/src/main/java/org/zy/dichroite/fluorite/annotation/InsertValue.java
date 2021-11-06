package org.zy.dichroite.fluorite.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * @DateTime 2021年9月7日;
 * @author zy(azurite-Y);
 * @Description 配置动态insert语句之中的插入字段集和属性集的注解，与 @MapperMethod注解配合使用
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
public @interface InsertValue {
	/**
	 * 使用于insert into关键字之后，描述插入字段sql的占位符。</br>
	 * 动态insert语句拼接的位置索引符,此值必须以‘&’字符为前缀,在其后的字符为位置索引的名称以和其他索引符区分
	 * @return
	 */
	String name() default "&x";
	
	/**
	 * 使用于value关键字之后，描述插入字段值sql的占位符。</br>
	 * 动态insert语句拼接的位置索引符,此值必须以‘&’字符为前缀,在其后的字符为位置索引的名称以和其他索引符区分
	 * @return
	 */
	String value() default "&y";
	
	/**
	 * 指定参与动态拼接sql的属性所构成的对象类型
	 * @return
	 */
//	@Deprecated
//	Class<?> parame() default Object.class;
}
