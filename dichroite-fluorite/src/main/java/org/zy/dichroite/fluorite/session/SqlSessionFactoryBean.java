package org.zy.dichroite.fluorite.session;

import java.io.IOException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zy.dichroite.fluorite.interfaces.SqlSessionFactory;
import org.zy.dichroite.fluorite.interfaces.TransactionFactory;
import org.zy.dichroite.fluorite.type.TypeHandlerRegistry;
import org.zy.fluorite.beans.factory.aware.BeanFactoryAware;
import org.zy.fluorite.beans.factory.interfaces.BeanFactory;
import org.zy.fluorite.beans.factory.interfaces.ListableBeanFactory;
import org.zy.fluorite.core.environment.interfaces.ConfigurableEnvironment;
import org.zy.fluorite.core.environment.interfaces.PropertyResolver;
import org.zy.fluorite.core.exception.BeansException;
import org.zy.fluorite.core.interfaces.EnvironmentAware;
import org.zy.fluorite.core.interfaces.instantiation.FactoryBean;
import org.zy.fluorite.core.utils.Assert;
import org.zy.fluorite.core.utils.DebugUtils;
import org.zy.fluorite.transaction.dataSource.DataSourceUtils;

/**
 * @author zy(Azurite - Y);
 * @DateTime 2021/9/5;
 * @Description 创建SqlSessionFactory的FactoryBean实现
 */
public class SqlSessionFactoryBean implements FactoryBean<SqlSessionFactory>,EnvironmentAware,BeanFactoryAware{
	private final Logger logger = LoggerFactory.getLogger(SqlSessionFactoryBean.class);

	private DataSource dataSource;

	/**
	 * 配置属性元
	 */
	private PropertyResolver propertyResolver;

	private ListableBeanFactory listableBeanFactory;

	private TransactionFactory transactionFactory;

	private SqlSessionFactory sqlSessionFactory;

	@Override
	public SqlSessionFactory getObject() throws Exception {
		if (this.sqlSessionFactory == null) {
			Assert.notNull(dataSource, "属性'dataSource'不能为空");
			this.sqlSessionFactory = buildSqlSessionFactory();
		}
		return this.sqlSessionFactory;
	}

	@Override
	public Class<?> getObjectType() {
		return this.sqlSessionFactory == null ? SqlSessionFactory.class : this.sqlSessionFactory.getClass();
	}

	/**
	 * 初始化逻辑方法
	 * @return
	 * @throws IOException
	 */
	protected SqlSessionFactory buildSqlSessionFactory() throws IOException {
		String databaseId = DataSourceUtils.getdatabaseIdForDataSource(dataSource);
		DebugUtils.logFromTransaction(logger, "生效的'databaseId'：" + databaseId);
		Configuration config = new Configuration(databaseId, transactionFactory, dataSource);
		config.setPropertyResolver(propertyResolver);

		TypeHandlerRegistry typeHandlerRegistry = new TypeHandlerRegistry();
		
		// 从IOC环境中获得自定义的TypeHandler实现
//		String[] beanNamesForType = listableBeanFactory.getBeanNamesForType(TypeHandler.class);
//		for (String beanName : beanNamesForType) {
//			TypeHandler<?> typehandler = (TypeHandler<?>) listableBeanFactory.getBean;
//			if (null != typehandler) {
//				typeHandlerRegistry.register(typehandler);
//			}
//		}

		config.setTypeHandlerRegistry(typeHandlerRegistry);
		// 创建应用
		return new DefaultSqlSessionFactory(config);
	}


	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public TransactionFactory getTransactionFactory() {
		return transactionFactory;
	}

	public void setTransactionFactory(TransactionFactory transactionFactory) {
		this.transactionFactory = transactionFactory;
	}

	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		this.propertyResolver = environment;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		ListableBeanFactory listableBeanFactory = (ListableBeanFactory) beanFactory;
		this.listableBeanFactory = listableBeanFactory;
	}

	public ListableBeanFactory getListableBeanFactory() {
		return listableBeanFactory;
	}
}
