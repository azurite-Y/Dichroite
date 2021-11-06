package org.zy.dichroite.autoconfigure;

import javax.sql.DataSource;

import org.zy.dichroite.fluorite.interfaces.SqlSession;
import org.zy.dichroite.fluorite.interfaces.SqlSessionFactory;
import org.zy.dichroite.fluorite.session.ManagedTransactionEnvironmentFactory;
import org.zy.dichroite.fluorite.session.SqlSessionFactoryBean;
import org.zy.dichroite.fluorite.session.SqlSessionTemplate;
import org.zy.fluorite.beans.factory.aware.BeanFactoryAware;
import org.zy.fluorite.beans.factory.interfaces.BeanFactory;
import org.zy.fluorite.context.annotation.conditional.ConditionalOnClass;
import org.zy.fluorite.core.annotation.Bean;
import org.zy.fluorite.core.annotation.Configuration;
import org.zy.fluorite.core.environment.interfaces.ConfigurableEnvironment;
import org.zy.fluorite.core.exception.BeansException;
import org.zy.fluorite.core.interfaces.EnvironmentAware;

/**
 * @author zy(Azurite - Y);
 * @DateTime 2021/9/5;
 * @Description Dichroite的自动配置类,被 {@code @MapperScanner } 导入
 */
@Configuration
@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })
public class DichroiteAutoConfiguration implements EnvironmentAware,BeanFactoryAware {
	private ConfigurableEnvironment environment;
	private BeanFactory beanFactory;
	@Bean
	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		// 在此设置了数据源
		factory.setDataSource(dataSource);
		factory.setTransactionFactory(new ManagedTransactionEnvironmentFactory());
		factory.setBeanFactory(beanFactory);
		factory.setEnvironment(environment);
		return factory.getObject();
	}

	@Bean
	public SqlSession sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		return new SqlSessionTemplate(sqlSessionFactory);
	}

	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		this.environment = environment;		
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
