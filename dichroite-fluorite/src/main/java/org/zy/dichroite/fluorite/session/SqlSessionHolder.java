package org.zy.dichroite.fluorite.session;

import org.zy.dichroite.fluorite.interfaces.SqlSession;
import org.zy.fluorite.core.utils.Assert;
import org.zy.fluorite.transaction.support.ResourceHolderSupport;

/**
 * @DateTime 2021年9月26日;
 * @author zy(azurite-Y);
 * @Description 用于在TransactionSynchronizationManager中保留当前SqlSession
 */
public final class SqlSessionHolder extends ResourceHolderSupport {
	  private final SqlSession sqlSession;

	public SqlSessionHolder(SqlSession sqlSession) {
		Assert.notNull(sqlSession, "'sqlSession' b不能为null");
		this.sqlSession = sqlSession;
	}

	public SqlSession getSqlSession() {
		return sqlSession;
	}

	@Override
	public String toString() {
		return "SqlSessionHolder [sqlSession=" + sqlSession + "]";
	}
}
