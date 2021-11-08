Dichroite
#### 介绍
一个基础的数据访问层实现，依托于Fluorite自动配置所运行而与Fluorite协同使用，内部原理参考Mybatis，使用方法与Mybatis大体相同，但仅支持注解式。

#### 使用
在根启动类或其他可被扫描类中使用@MapperScnner指定Mapper接口包路径
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
框架测试项目地址：https://gitee.com/azurite_y/DichroiteTest <br/>
如果您有什么建议或发现的BUG，随时欢迎您的来信。联系方式：15969413461@163.com
