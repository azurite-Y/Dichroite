package org.zy.dichroite.fluorite.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zy.dichroite.fluorite.exception.TransientDataAccessResourceException;
import org.zy.dichroite.fluorite.interfaces.SqlSession;
import org.zy.dichroite.fluorite.interfaces.SqlSessionFactory;
import org.zy.dichroite.fluorite.interfaces.TransactionFactory;
import org.zy.dichroite.fluorite.session.Configuration;
import org.zy.dichroite.fluorite.session.SqlSessionHolder;
import org.zy.fluorite.core.utils.Assert;
import org.zy.fluorite.core.utils.DebugUtils;
import org.zy.fluorite.transaction.interfaces.TransactionSynchronization;
import org.zy.fluorite.transaction.support.TransactionSynchronizationManager;

/**
 * @author zy(Azurite - Y);
 * @DateTime 2021/9/5;
 * @Description
 */
public final class SqlSessionUtils {
	private static final Logger logger = LoggerFactory.getLogger(SqlSessionUtils.class);

	/**
	 * 尝试从事务同步管理器通过SqlSessionFactory获得对应的SqlSession，若没有则重新创建一个并注册到事务环境中
	 * @param sessionFactory
	 * @return
	 * @throws TransientDataAccessResourceException 
	 */
	public static SqlSession getSqlSession(SqlSessionFactory sessionFactory) throws TransientDataAccessResourceException {
		Assert.notNull(sessionFactory, "指定的‘SqlSessionFactory’不能为null");

		// 尝试从当前线程中获得与sessionFactory 绑定的SqlSessionHolder对象
		SqlSessionHolder holder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sessionFactory);
		SqlSession session = sessionHolder(holder);
		if (session != null) {
			return session;
		}
		DebugUtils.logFromTransaction(logger, "创建一个新的SqlSession");
		session = sessionFactory.openSession();
		registerSessionHolder(sessionFactory, session);
		return session;
	}

	private static void registerSessionHolder(SqlSessionFactory sessionFactory, SqlSession session) throws TransientDataAccessResourceException {
		SqlSessionHolder holder;
		if (TransactionSynchronizationManager.isSynchronizationActive()) {
			Configuration configuration = sessionFactory.getConfiguration();

			if (configuration.getTransactionFactory() instanceof TransactionFactory) {
				DebugUtils.logFromTransaction(logger, "注册事务同步对象  SqlSession:[" + session + "]");

				holder = new SqlSessionHolder(session);
				TransactionSynchronizationManager.bindResource(sessionFactory, holder);
				TransactionSynchronizationManager.registerSynchronization(new SqlSessionSynchronization(holder, sessionFactory));
				holder.setSynchronizedWithTransaction(true);
				holder.requested();
			} else {
				throw new TransientDataAccessResourceException("SqlSessionFactory必须使用已注册到事务环境的数据源，by 当前数据源:" + configuration.getDataSource());
			}
		} else {
			DebugUtils.logFromTransaction(logger, "SqlSession[" + session + "]未注册同步，因为同步未处于活动状态");
		}
	}

	private static SqlSession sessionHolder(SqlSessionHolder holder) {
		SqlSession session = null;
		if (holder != null && holder.isSynchronizedWithTransaction()) {
			holder.requested();

			DebugUtils.logFromTransaction(logger, "已从当前事务中获取SqlSession [" + holder.getSqlSession() + "]");
			session = holder.getSqlSession();
		}
		return session;
	}

	/**
	 * 后置方法关闭SqlSession的回调
	 * @param sqlSessionFactory
	 * @return
	 */
	public static void closeSqlSession(SqlSession sqlSession, SqlSessionFactory sqlSessionFactory) {
		Assert.notNull(sqlSession, "参数'sqlSession'不能为null");
		Assert.notNull(sqlSessionFactory, "参数'sqlSessionFactory'不能为null");

		SqlSessionHolder holder = (SqlSessionHolder) TransactionSynchronizationManager.getResource(sqlSessionFactory);
		if ((holder != null) && (holder.getSqlSession() == sqlSession)) {
			DebugUtils.logFromTransaction(logger, "重置事务性的 SqlSession [" + sqlSession + "]");
			holder.released();
		} else {
			DebugUtils.logFromTransaction(logger, "关闭非事务性的 SqlSession [" + sqlSession + "]");
			sqlSession.close();
		}
	}

	/**
	 * dichroite 的事务同步对象，适配于事务环境中各生命周期的调用
	 * @author Azurite-Y
	 *
	 */
	private static final class SqlSessionSynchronization implements TransactionSynchronization {
		private final SqlSessionHolder holder;

		private final SqlSessionFactory sessionFactory;

		private boolean holderActive = true;

		public SqlSessionSynchronization(SqlSessionHolder holder, SqlSessionFactory sessionFactory) {
			Assert.notNull(holder, "参数 'holder' 不能为空");
			Assert.notNull(sessionFactory, "参数 'sessionFactory' 不能为null");

			this.holder = holder;
			this.sessionFactory = sessionFactory;
		}

		@Override
		public void suspend() {
			if (this.holderActive) {
				DebugUtils.logFromTransaction(logger, "事务同步挂起SqlSession [" + this.holder.getSqlSession() + "]");
				TransactionSynchronizationManager.unbindResource(this.sessionFactory);
			}
		}

		@Override
		public void resume() {
			if (this.holderActive) {
				DebugUtils.logFromTransaction(logger, "事务同步恢复SqlSession [" + this.holder.getSqlSession() + "]");
				TransactionSynchronizationManager.bindResource(this.sessionFactory, this.holder);
			}
		}

		@Override
		public void beforeCommit(boolean readOnly) {
			if (TransactionSynchronizationManager.isActualTransactionActive()) {
				DebugUtils.logFromTransaction(logger, "事务同步提交SqlSession [" + this.holder.getSqlSession() + "]");
				this.holder.getSqlSession().commit();
			}
		}

		@Override
		public void beforeCompletion() {
			if (!this.holder.isOpen()) {
				DebugUtils.logFromTransaction(logger, "解绑注册的事务同步对象 SqlSession [" + this.holder.getSqlSession() + "]");
				TransactionSynchronizationManager.unbindResource(sessionFactory);

				this.holderActive = false;

				DebugUtils.logFromTransaction(logger, "关闭的注册事务同步对象 SqlSession [" + this.holder.getSqlSession() + "]");
				this.holder.getSqlSession().close();
			}
		}

		@Override
		public void afterCompletion(int status) {
			if (this.holderActive) {
				DebugUtils.logFromTransaction(logger, "解绑注册的事务同步对象 SqlSession [" + this.holder.getSqlSession() + "]");
				TransactionSynchronizationManager.unbindResourceIfPossible(sessionFactory);

				this.holderActive = false;

				DebugUtils.logFromTransaction(logger, "关闭的注册事务同步对象 SqlSession [" + this.holder.getSqlSession() + "]");
				this.holder.getSqlSession().close();
			}
			this.holder.reset();
		}
	}

}
