package org.zy.dichroite.fluorite.interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.zy.dichroite.fluorite.binding.BoundSql;
import org.zy.dichroite.fluorite.exception.ExecutorException;
import org.zy.dichroite.fluorite.mapping.MappedStatement;

/**
 * @DateTime 2021年9月8日;
 * @author zy(azurite-Y);
 * @Description
 */
public interface Executor {
	ResultHandler NO_RESULT_HANDLER = null;

	int update(MappedStatement ms, Map<Integer,Object> args) throws SQLException;

	<E> List<E> query(MappedStatement ms, Map<Integer,Object> args) throws SQLException;

	<E> List<E> query(MappedStatement ms, Map<Integer,Object> args, BoundSql boundSql) throws SQLException;

	/**
	 * 执行器是否已关闭
	 * @return
	 */
	boolean isClosed();

	Transaction getTransaction() throws ExecutorException;

	void close(boolean forceRollback);

	void commit(boolean required) throws SQLException;

	void rollback(boolean required) throws SQLException;
}
