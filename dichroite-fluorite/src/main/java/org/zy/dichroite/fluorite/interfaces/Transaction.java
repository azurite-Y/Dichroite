package org.zy.dichroite.fluorite.interfaces;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 事务根接口
 */
public interface Transaction {
	Connection getConnection() throws SQLException;

	void commit() throws SQLException;

	void rollback() throws SQLException;

	void close() throws SQLException;

	Integer getTimeout() throws SQLException;
}
