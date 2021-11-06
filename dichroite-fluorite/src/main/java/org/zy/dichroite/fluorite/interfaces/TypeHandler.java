package org.zy.dichroite.fluorite.interfaces;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.zy.dichroite.fluorite.type.JdbcType;

/**
 * @DateTime 2021年10月19日;
 * @author zy(azurite-Y);
 * @Description 类型处理器，自定义实现的 TypeHandler可注册到IOC环境中以使程序可获取到，通过 @MappedJdbcTypes 和 @MappedTypes限定其用途
 * @param <T> 处理后的结果值类型
 */
public interface TypeHandler<T> {
	void setParameter(PreparedStatement ps, int i, Object obj, JdbcType jdbcType) throws SQLException;

	/**
	 * 从结果集中获得指定字段名对应的值
	 * @param rs
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	T getResult(ResultSet rs, String columnName) throws SQLException;

	/**
	 * 从结果集中获得指定字段名对应的值
	 * @param rs
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	T getResult(ResultSet rs, Integer columnIndex) throws SQLException;
	
	/**
	 * 
	 * @param cs
	 * @param columnIndex
	 * @return
	 * @throws SQLException
	 */
	T getResult(CallableStatement cs, Integer columnIndex) throws SQLException;
}
