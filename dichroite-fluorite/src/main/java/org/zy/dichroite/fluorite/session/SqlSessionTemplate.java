package org.zy.dichroite.fluorite.session;

import static java.lang.reflect.Proxy.newProxyInstance;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.zy.dichroite.fluorite.exception.MapperBindingException;
import org.zy.dichroite.fluorite.interfaces.SqlSession;
import org.zy.dichroite.fluorite.interfaces.SqlSessionFactory;
import org.zy.dichroite.fluorite.utils.SqlSessionUtils;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description
 */
public class SqlSessionTemplate implements SqlSession {
	private final SqlSessionFactory sqlSessionFactory;

	private final SqlSession sqlSessionProxy;

	public SqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
		/**
		 * 使用jdk代理进行aop操作
		 */
		this.sqlSessionProxy = (SqlSession) newProxyInstance(SqlSessionFactory.class.getClassLoader(), new Class[] { SqlSession.class }, new SqlSessionInterceptor());
	}

	@Override
	public void close() {
		this.sqlSessionProxy.close();
	}

	@Override
	public void clearCache() {
		this.sqlSessionProxy.clearCache();
	}

	@Override
	public Configuration getConfiguration() {
		return this.sqlSessionFactory.getConfiguration();
	}

	@Override
	public <T> T getMapper(Class<T> type) throws MapperBindingException {
		return getConfiguration().getMapper(type, this);
	}

	@Override
	public Connection getConnection() {
		return this.sqlSessionProxy.getConnection();
	}

	/**
	 * SqlSession拦截器，在调用SqlSession之中的方法时进行拦截，前置处理通过事务环境创建SqlSession后继续执行方法本身，方法执行后继续后置处理
	 * @author Azurite-Y
	 *
	 */
	private class SqlSessionInterceptor implements InvocationHandler {
		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			SqlSession sqlSession = SqlSessionUtils.getSqlSession(SqlSessionTemplate.this.sqlSessionFactory);
			try {
				Object result = method.invoke(sqlSession, args);
				return result;
			} catch (Throwable t) {
				throw t;
			} finally {
				if (sqlSession != null) {
					SqlSessionUtils.closeSqlSession(sqlSession, SqlSessionTemplate.this.sqlSessionFactory);
				}
			}
		}

	}

	@Override
	public void commit() {
		throw new UnsupportedOperationException("此实现不能调用此方法");
	}

	@Override
	public void commit(boolean force) {
		throw new UnsupportedOperationException("此实现不能调用此方法");
	}

	@Override
	public void rollback() {
		throw new UnsupportedOperationException("此实现不能调用此方法");
	}

	@Override
	public void rollback(boolean force) {
		throw new UnsupportedOperationException("此实现不能调用此方法");
	}

	@Override
	public <T> T selectOne(String statement) {
	    return this.sqlSessionProxy.<T> selectOne(statement);
	}

	@Override
	public <T> T selectOne(String statement, Map<Integer,Object> args) {
	    return this.sqlSessionProxy.<T> selectOne(statement, args);
	}

	@Override
	public <E> List<E> selectList(String statement) {
	    return this.sqlSessionProxy.<E> selectList(statement);
	}

	@Override
	public <E> List<E> selectList(String statement, Map<Integer,Object> args) {
	    return this.sqlSessionProxy.<E> selectList(statement, args);
	}

	@Override
	public int insert(String statement) {
	    return this.sqlSessionProxy.insert(statement);
	}

	@Override
	public int insert(String statement, Map<Integer,Object> args) {
	    return this.sqlSessionProxy.insert(statement,args);
	}

	@Override
	public int update(String statement) {
	    return this.sqlSessionProxy.update(statement);
	}

	@Override
	public int update(String statement, Map<Integer,Object> args) {
	    return this.sqlSessionProxy.update(statement, args);
	}

	@Override
	public int delete(String statement) {
	    return this.sqlSessionProxy.delete(statement);
	}

	@Override
	public int delete(String statement, Map<Integer,Object> args) {
	    return this.sqlSessionProxy.delete(statement, args);
	}
}
