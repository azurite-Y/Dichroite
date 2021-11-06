package org.zy.dichroite.fluorite.interfaces;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @DateTime 2021年9月8日;
 * @author zy(azurite-Y);
 * @Description 结果集处理器接口
 */
public interface ResultSetHandler {
	/**
	 * 结果集处理程序，通过表名和pojo映射关联属性值
	 * @param stmt
	 * @return
	 * @throws SQLException
	 */
	<E> List<E> handleResultSets(Statement stmt) throws SQLException;

	void handleOutputParameters(CallableStatement cs) throws SQLException;
}
