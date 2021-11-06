package org.zy.dichroite.fluorite.session;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zy.dichroite.fluorite.interfaces.Transaction;
import org.zy.fluorite.core.utils.Assert;
import org.zy.fluorite.core.utils.DebugUtils;
import org.zy.fluorite.transaction.dataSource.ConnectionHolder;
import org.zy.fluorite.transaction.dataSource.DataSourceUtils;
import org.zy.fluorite.transaction.support.TransactionSynchronizationManager;

/**
 * @DateTime 2021年9月26日;
 * @author zy(azurite-Y);
 * @Description 默认的托管事务对象。
 */
public class DefaultManagedTransaction implements Transaction{
	private static final Logger logger = LoggerFactory.getLogger(DefaultManagedTransaction.class);

	private final DataSource dataSource;

	private Connection connection;

	/**
	 * 事务连接判别的标识</br>
	 * 原则上提交、回滚、获取jdbc连接的操作不会和事务环境冲突，即同一个连接在事务环境中做过以上动作之后在本事务对象中就不会被重复执行对应额操作。
	 */
	private boolean isConnectionTransactional;

	private boolean autoCommit;


	public DefaultManagedTransaction(DataSource dataSource) {
		Assert.notNull(dataSource,"属性 'dataSource' 不能为null");
		this.dataSource = dataSource;
	}

	@Override
	public Connection getConnection() throws SQLException {
		if (this.connection == null) {
			openConnection();
		}
		return this.connection;
	}

	private void openConnection() throws SQLException {
		this.connection = DataSourceUtils.getConnection(this.dataSource);
		this.autoCommit = this.connection.getAutoCommit();
		this.isConnectionTransactional = DataSourceUtils.isConnectionTransactional(this.connection, this.dataSource);

		DebugUtils.logFromTransaction(logger, "JDBC Connection [" + this.connection + "]" 
				+(this.isConnectionTransactional ? "已" : "未 ") + "注册到事务环境");
	}

	@Override
	public void commit() throws SQLException {
		if (this.connection != null && !this.isConnectionTransactional && !this.autoCommit) {
			DebugUtils.logFromTransaction(logger, "JDBC Connection [" + this.connection + "] 事务提交");
			this.connection.commit();
		}
	}

	@Override
	public void rollback() throws SQLException {
		if (this.connection != null && !this.isConnectionTransactional && !this.autoCommit) {
			DebugUtils.logFromTransaction(logger, "回滚 JDBC Connection [" + this.connection + "] ");
			this.connection.rollback();
		}
	}

	@Override
	public void close() throws SQLException {
		DataSourceUtils.releaseConnection(this.connection, this.dataSource);

	}

	@Override
	public Integer getTimeout() throws SQLException {
		ConnectionHolder holder = (ConnectionHolder) TransactionSynchronizationManager.getResource(dataSource);
		if (holder != null && holder.hasTimeout()) {
			return holder.getTimeToLiveInSeconds();
		} 
		return 0;
	}
}
