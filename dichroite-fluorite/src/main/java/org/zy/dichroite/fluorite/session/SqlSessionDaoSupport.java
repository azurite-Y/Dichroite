package org.zy.dichroite.fluorite.session;

import org.zy.dichroite.fluorite.interfaces.SqlSession;
import org.zy.fluorite.core.utils.Assert;
import org.zy.fluorite.transaction.dao.DaoSupport;

/**
 * @DateTime 2021年9月6日 下午2:50:49;
 * @author zy(azurite-Y);
 * @Description
 */
public class SqlSessionDaoSupport extends DaoSupport {
	/**
	 * 通过PropertyValues特性注入此属性
	 */
	private SqlSession sqlSession;

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	@Override
	protected void checkDaoConfig() {
		Assert.notNull(this.sqlSession, "sqlSession 不能为空");
	}

	public SqlSession getSqlSession() {
		return this.sqlSession;
	}
}
