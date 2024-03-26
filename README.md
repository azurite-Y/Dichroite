# Dichroite

## 一、介绍

一个基础的数据访问层实现，依托于Fluorite自动配置所运行而与Fluorite协同使用，内部原理参考Mybatis，使用方法与Mybatis大体相同，但仅支持注解式。

## 二、使用

在根启动类或其他可被扫描类中使用`@MapperScnner`指定Mapper接口包路径
```java
@RunnerAs(debug = false, debugFormAop = false, debugFromTransaction = false)
@EnableTransactionManagement
@MapperScanner("com.zy.mapper")
public class App {
	public static void main(String[] args) {
		ConfigurableApplicationContext run = FluoriteApplication.run(App.class, args);
	}
}
```
---
## 三、测试项目地址

框架测试项目地址：https://gitee.com/azurite_y/dichroite-test <br/>

## 四、原理概述

### 1、启动与初始化

1. 框架由标注于启动类上的`@MapperScanner`导入 Mapper 接口扫描器和自动配置类启动。
2. 在未开启事务环境时在非事务环境下运行，在开启事务后则在事务环境下运行，依据事务环境中注册的相关事务对象来保证程序的同步性。简而言之就是事务由底层数据库支持，而框架只负责在 SQL 执行周期中对事务进行操作，如提交回滚。

### 2、SQL 构建

1. 静态 SQL

   1. 支持参数捕获：`#{propretyName}`使用于单个对象属性参数、`#{parameterIndex}`使用于一或多个基本数据类型参数、单个 List 集合参数或数组参数、`#{mapKey}`使用于单个 Map 类型参数。

   2. 对于一个方法参数中有基本数据类型又有数组、集合类型之中任意一个或多个参数的情况建议封装为类，通过`#{propretyName}`引用，即若集合或数组类型作为多个入参当中的一员时，无法解析其中的数据，只能将这个集合或数组封装对象注入 SQL。

   3. Method 传入的参数对象其原始属性名在编译时会被 JVM 舍弃，需在解析方法时保存其有序的属性集合。所以对于可将参数值直接注入 SQL 的参数使用`#{argsIndex}`捕获，argsIndex 自 0 开始。

2. 动态 SQL

> - 入参限制：有且只有一个参数且此参数封装了所需可能应用于 SQL 中的属性。
>
> - 本质：为 MyBatis 动态 SQL 的另一种实现方式，只是使用 ORM 的思想封装了构建 SQL 的操作。

   1. 参数封装对象必须标注`@BeanMapping`注解，而其属性可通过`@Column`注解描述其对应的表字段名和属性优先级。在进行 SQL 构建时会中排除空值或空集的属性，而其他的属性则为可应用于构建 SQL 的有效属性。

   2. 解析动态 update 语句时，根据`@Column`中的 level 值来确定**非默认属性值**之中谁作为查询字段。梯度按升序排列，相对靠前的属性为查询字段，相对靠后的属性的为被更新字段。但查询字段只限定为一个字段，其余字段则视为需要更新的字段。

   3. `@QueryWhere`注解的 mode 属性可控制查询语句的拼接逻辑。

```xml
<!-- 串行等同于 MyBatis 的 -->
<where>
    <choose>
        <when></when>
        <otherwise></otherwise>
    </choose>
</where>
    
<!-- 并行等同于 MyBatis 的 -->
<where>
  <if></if>
  <if></if>
</where>
```


> 只有方法标注有动态 SQL 注解时才会启用动态 SQL 式的入参解析和 SQL 构建，动态SQL 注解有：@QueryWhere、@InsertValue、@UpdateSet。

### 3、返回值封装

针对反射后泛型擦除的原因导致 Mapper 方法定义的返回值泛型失效，所以在返回值对象创建时严格按照返回值泛型构造返回值对象。使用对应的 TypeHandler 实现设值。支持的对象类型如下表所示。

1. JavaBean：
封装单行数据，属性名为字段名，可通过@Column进行字段名映射。属性值为字段值。若结果集中未找到与字段名匹配的属性名则此属性为默认值。

2. List<T>：
封装多行数据，泛型参数可为 JavaBean、Map对象类型

3. Map<String,T>：
封装单行数据，key 为字段名，value 为字段值。

4. Map<String, Map<String,?>>、Map<String, JavaBean>：
与 Map<String,T> 不同，可封装多行数据，使用`@MapKey`指定结果集中哪些个属性作为 Map 的 key，而单行结果集作为其 value 值。

4、待定实现

1. 命名空间缓存
2. 插件功能