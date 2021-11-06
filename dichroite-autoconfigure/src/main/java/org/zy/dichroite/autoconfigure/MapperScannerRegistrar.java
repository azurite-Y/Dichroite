package org.zy.dichroite.autoconfigure;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zy.dichroite.autoconfigure.annotation.MapperScanner;
import org.zy.dichroite.fluorite.binding.MapperFactoryBean;
import org.zy.fluorite.beans.beanDefinittion.RootBeanDefinition;
import org.zy.fluorite.beans.factory.interfaces.BeanDefinitionRegistry;
import org.zy.fluorite.beans.factory.interfaces.BeanNameGenerator;
import org.zy.fluorite.beans.support.AnnotationMetadataHolder;
import org.zy.fluorite.context.annotation.interfaces.ImportBeanDefinitionRegistrar;
import org.zy.fluorite.core.environment.interfaces.Environment;
import org.zy.fluorite.core.interfaces.AnnotationMetadata;
import org.zy.fluorite.core.interfaces.Resource;
import org.zy.fluorite.core.utils.FileSearch;
import org.zy.fluorite.core.utils.ReflectionUtils;

/**
 * @author zy(Azurite - Y);
 * @Date 2021/9/5;
 * @Description ,被 {@code @MapperScanner } 导入
 */
public class MapperScannerRegistrar implements ImportBeanDefinitionRegistrar {
	public final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry,BeanNameGenerator importBeanNameGenerator) {
		MapperScanner annotationForClass = metadata.getAnnotationForClass(MapperScanner.class);
		String[] value = annotationForClass.value();

		Set<String> candidate = new LinkedHashSet<>();
		// 解析mapper接口包路径
		if (value.length == 1) {
			candidate.add(value[0]);
		} else {
			FileSearch.sortPackagePath(Arrays.asList(value), candidate);
		}

		List<Resource> componentResources = null;
		for (String packagePath : candidate) {
			componentResources = FileSearch.searchToFile(packagePath,"class");
		}

		for (Resource resource : componentResources) {
			Class<?> searchClz = ReflectionUtils.forName(resource.getResourceName());
			AnnotationMetadataHolder metadataHolder = new AnnotationMetadataHolder(searchClz);

			RootBeanDefinition rootBeanDefinition = new RootBeanDefinition(searchClz);
			rootBeanDefinition.setResource(resource);
			rootBeanDefinition.setAnnotationMetadata(metadataHolder);

			// 解析注解，生成beanName
			String beanName = importBeanNameGenerator.generateBeanName(rootBeanDefinition, registry);
			rootBeanDefinition.setBeanName(beanName);

			processBeanDefinitions(rootBeanDefinition);
			
			// 注册到bean工厂
			registry.registerBeanDefinition(beanName, rootBeanDefinition);
		}
	}

	/**
	 * BeanDefinition 的额外处理
	 * @param definition
	 */
	private void processBeanDefinitions(RootBeanDefinition definition) {
		// 设置MapperFactoryBean需要创建的对象
		definition.getConstructorArgumentValues().addIndexedArgumentValue(0, definition.getBeanClass());
		definition.getPropertyValues()
			// 设置MapperFactoryBean其他属性
			.add("annotationMetadata", definition.getAnnotationMetadata())
			.add("sqlSession", null);
		definition.setBeanClass(MapperFactoryBean.class);
	}

	@Override
	public void invokeAwareMethods(Environment environment, BeanDefinitionRegistry beanDefinitionRegistry) {

	}
}
