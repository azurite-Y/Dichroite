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
	 * 一般来说使用频率高的字段的的梯度应靠前。另外因为拼接动态 SQL 时会自动忽略为默认值的属性。
	 * 所以这就意味着可以在 JavaBean 中定义相同 name 但属性类型的 @Column。然后在使用动态 SQL 构建时选择性的设值。
	 * 例如在进行插入时使用属性 A，在进行批量查询时使用属性 B。
	 * 
	 * @apiNote 建议的编程规约：应用于 insert 语句的属性，为其指定大于 0 的 level。
	 * 而对于其他作为非表映射属性但应用于诸如过滤条件的属性，可设置为默认 level。
	 * 这样可一目了然的得出表结构和区分属性。
	 * @see QueryTemplate
	 */
	int level() default 0;
}
