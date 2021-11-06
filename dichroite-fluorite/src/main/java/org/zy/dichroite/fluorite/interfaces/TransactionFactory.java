package org.zy.dichroite.fluorite.interfaces;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description dichroited的事务工厂
 */
public interface TransactionFactory {

	void setProperties(Properties props);

	Transaction newTransaction(Connection conn);

	Transaction newTransaction(DataSource dataSource, boolean autoCommit);
}
