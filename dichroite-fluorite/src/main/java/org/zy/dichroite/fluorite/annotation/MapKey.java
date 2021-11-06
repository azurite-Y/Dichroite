package org.zy.dichroite.fluorite.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @DateTime 2021年10月27日;
 * @author zy(azurite-Y);
 * @Description 查询到多条数据，使用@MapKey指定结果集中哪些个属性作为Map的key，而单行结果集作为其value值.</br>
 * ps: 为了应对反射后泛型被擦除的问题，所以在属性注入是会依据泛型进行类型校验和转换（如果有必要的话）.</br>
 * 若指定的类型转换器或现有的类型转换器不满足类型转换的要求，那么key的泛型会被永久擦除（），在此视之为一种检查或编程式异常，进而抛出‘GenericErasureException’异常，建议指定的key为常见的数据类型或提供特定的类型转换器
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MapKey {
	String value();
}
