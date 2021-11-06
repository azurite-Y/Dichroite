package org.zy.dichroite.fluorite.interfaces;

import java.sql.SQLException;
import java.util.List;

import org.zy.dichroite.fluorite.executor.resultSet.ResultSetWrapper;
import org.zy.dichroite.fluorite.session.DefaultSqlSession;

/**
 * @DateTime 2021年9月9日;
 * @author zy(azurite-Y);
 * @Description 结果集处理程序,其持有的对象就是返回值对象本身
 */
public interface ResultHandler {
	/**
	 * 处理结果集
	 * @param rsw
	 * @throws SQLException
	 */
	void handleResultSet(ResultSetWrapper rsw) throws SQLException;

	/**
	 * 所有类型的返回值对象都保存于List容器中返回，在顶层方法中安需去值
	 * @return
	 * @see DefaultSqlSession#selectList(String, java.util.Map)
	 * @see DefaultSqlSession#selectOne(String, java.util.Map)
	 */
	List<Object> result();
}
