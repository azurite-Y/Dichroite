package org.zy.dichroite.fluorite.session;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

import org.zy.dichroite.fluorite.interfaces.Transaction;
import org.zy.dichroite.fluorite.interfaces.TransactionFactory;

/**
 * @DateTime 2021年9月26日;
 * @author zy(azurite-Y);
 * @Description dichroite托管事务环境工厂
 */
public class ManagedTransactionEnvironmentFactory implements TransactionFactory {

	@Override
	public void setProperties(Properties props) {}

	@Override
	public Transaction newTransaction(Connection conn) {
	    throw new UnsupportedOperationException("托管的外部事务环境需要 'DataSource'");
	}

	@Override
	public Transaction newTransaction(DataSource dataSource, boolean autoCommit) {
	    return new DefaultManagedTransaction(dataSource);
	}
}
