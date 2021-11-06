package org.zy.dichroite.fluorite.session;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zy.dichroite.fluorite.executor.ErrorContext;
import org.zy.dichroite.fluorite.interfaces.Executor;
import org.zy.dichroite.fluorite.interfaces.SqlSession;
import org.zy.dichroite.fluorite.interfaces.SqlSessionFactory;
import org.zy.dichroite.fluorite.interfaces.Transaction;
import org.zy.dichroite.fluorite.interfaces.TransactionFactory;

/**
 * @DateTime 2021年9月8日;
 * @author zy(azurite-Y);
 * @Description
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {
	private final Logger logger = LoggerFactory.getLogger(DefaultSqlSession.class);

	private final Configuration configuration;

	public DefaultSqlSessionFactory(Configuration configuration) {
		this.configuration = configuration;
	}

	@Override
	public SqlSession openSession() {
		return openSessionFromDataSource(configuration.isAutoCommit());
	}

	/**
	 * 根据DataSoure创建SqlSession
	 * @param autoCommit - 是否自动提交
	 * @return
	 */
	private SqlSession openSessionFromDataSource(boolean autoCommit) {
		Transaction tx = null;
//		asd
		try {
			TransactionFactory transactionFactory = configuration.getTransactionFactory();
			Transaction transaction = transactionFactory.newTransaction(configuration.getDataSource(), autoCommit);
			// 通过指定的执行器类型创建对应的执行器
			Executor executor = configuration.newExecutor(transaction);
			return new DefaultSqlSession(configuration, executor, autoCommit);
		} catch (Exception e) {
			closeTransaction(tx);
			logger.error("开启session错误.  Cause: " + e, e);
			throw e;
		} finally {
			ErrorContext.instance().reset();
		}
	}

	@Override
	public SqlSession openSession(Connection connection) {
		return this.openSessionFromDataSource(false);
	}

	@Override
	public Configuration getConfiguration() {
		return this.configuration;
	}

	private void closeTransaction(Transaction tx) {
		if (tx != null) {
			try {
				tx.close();
			} catch (SQLException ignore) {}
		}
	}

}
