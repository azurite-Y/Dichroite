package org.zy.dichroite.fluorite.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * @DateTime 2021年9月7日;
 * @author zy(azurite-Y);
 * @Description 配置动态update语句之中的set后续语句，与 @MapperMethod 注解配合使用。</br>
 * 在解析过程中，根据@Column中的level值来确定不为空的属性集中谁作为查询字段，梯度按升序排列，相对靠前的属性为查询字段，
 * 相对靠后的属性则为被更新字段。但查询字段只限定为一个字段，而其余他字段则为视为需要更新的字段
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
public @interface UpdateSet {
	/**
	 * 使用于set关键字之后，描述更新字段sql的占位符。</br>
	 * 动态update语句拼接的位置索引符,此值必须以‘&’字符为前缀,在其后的字符为位置索引的名称以和其他索引符区分。
	 * @return
	 */
	String name() default "&x";
	
	/**
	 * 指定参与动态拼接sql的属性所构成的对象类型
	 * @return
	 */
//	@Deprecated
//	Class<?> parame() default Object.class;
}
