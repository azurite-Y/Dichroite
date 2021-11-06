package org.zy.dichroite.fluorite.binding;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zy.dichroite.fluorite.executor.ErrorContext;
import org.zy.dichroite.fluorite.session.Configuration;
import org.zy.dichroite.fluorite.session.SqlSessionDaoSupport;
import org.zy.fluorite.core.interfaces.AnnotationMetadata;
import org.zy.fluorite.core.interfaces.instantiation.FactoryBean;
import org.zy.fluorite.core.utils.Assert;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description Mapper接口创建Bean
 */
public class MapperFactoryBean <T> extends SqlSessionDaoSupport implements FactoryBean<T> {
	public final Logger logger = LoggerFactory.getLogger(getClass());
	
	/** 通过PropertyValues注入 */
	private Class<T> mapperInterface;
	
	/** 通过PropertyValues注入 */
	private AnnotationMetadata annotationMetadata;

	public MapperFactoryBean(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	@Override
	protected void checkDaoConfig() {
		super.checkDaoConfig();
		Assert.notNull(this.mapperInterface, "属性 'mapperInterface'是必须的");
		Configuration configuration = super.getSqlSession().getConfiguration();

		if (!configuration.hasMapper(this.mapperInterface)) {
			try {
				// 将mapper接口注册到 MapperRegistry.knownMappers 容器中，key为mapper接口的Class对象
				configuration.addMapper(this.mapperInterface,this.annotationMetadata);
			} catch (Exception e) {
				logger.error("添加mapper到配置中出错，by: '" + this.mapperInterface + "'.", e);
				throw new IllegalArgumentException(e);
			} finally {
				ErrorContext.instance().reset();
			}
		}
	}

	@Override
	public T getObject() throws Exception {
		return super.getSqlSession().getMapper(mapperInterface);
	}

	@Override
	public Class<?> getObjectType() {
		return this.mapperInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public Class<T> getMapperInterface() {
		return mapperInterface;
	}
	public void setMapperInterface(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}
	public AnnotationMetadata getAnnotationMetadata() {
		return annotationMetadata;
	}
	public void setAnnotationMetadata(AnnotationMetadata annotationMetadata) {
		this.annotationMetadata = annotationMetadata;
	}
	
	public static void main(String[] args) throws IntrospectionException {
		PropertyDescriptor descriptor = new PropertyDescriptor("mapperInterface", MapperFactoryBean.class);
		Method readMethod = descriptor.getReadMethod();
		System.out.println(readMethod.getName());
	}
}
