package org.zy.dichroite.fluorite.annotation;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * @DateTime 2021年9月7日;
 * @author zy(azurite-Y);
 * @Description 配置动态SQL语句之中的where后续语句，与 @MapperMethod注解配合使用
 */
@Documented
@Retention(RUNTIME)
@Target({ElementType.METHOD})
public @interface QueryWhere {
	/**
	 * 使用于where关键字之后，描述查询字段sql的占位符。</br>
	 * 动态where语句拼接的位置索引符,此值必须以‘&’字符为前缀,在其后的字符为位置索引的名称以和其他索引符区分
	 * @return
	 */
	String name() default "&x";
	
	/**
	 * 指定参与动态拼接sql的属性所构成的对象类型
	 * @return
	 */
//	@Deprecated
//	Class<?> parame() default Object.class;
	
	/**
	 * 查询模板，指定在SQL拼接的过程中的逻辑,默认为并行
	 * @see QueryTemplate
	 */
	QueryTemplate mode() default QueryTemplate.parallel;
	
	/**
	 * 默认的查询sql，不包括where关键字。此属性是为 {@code QueryTemplate#serial } 模式的补充配置，在作为入参对象的所有属性都是为空或空集时生效，被追加到查询sql的末端
	 */
	String defaultQuerySql() default "";
}
