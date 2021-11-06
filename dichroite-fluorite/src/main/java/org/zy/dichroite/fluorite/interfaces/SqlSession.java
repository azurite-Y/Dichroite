package org.zy.dichroite.fluorite.interfaces;

import java.io.Closeable;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.zy.dichroite.fluorite.exception.MapperBindingException;
import org.zy.dichroite.fluorite.session.Configuration;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 定义session下执行的sql方法 【statement参数为 每一个mapper方法的全限定签名】
 */
public interface SqlSession extends Closeable{
	void close();

	void clearCache();

	Configuration getConfiguration();

	<T> T getMapper(Class<T> type) throws MapperBindingException;

	Connection getConnection();

	void commit();

	void commit(boolean force);

	void rollback();

	void rollback(boolean force);

	<T> T selectOne(String statement);
	<T> T selectOne(String statement, Map<Integer,Object> args);
	<E> List<E> selectList(String statement);
	<E> List<E> selectList(String statement, Map<Integer,Object> args);
	int insert(String statement);
	int insert(String statement, Map<Integer,Object> args);
	int update(String statement);
	int update(String statement, Map<Integer,Object> args);
	int delete(String statement);
	int delete(String statement, Map<Integer,Object> args);
}
