package org.zy.dichroite.fluorite.type.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.zy.dichroite.fluorite.exception.ResultMapException;
import org.zy.dichroite.fluorite.exception.TypeException;
import org.zy.dichroite.fluorite.interfaces.TypeHandler;
import org.zy.dichroite.fluorite.type.JdbcType;

/**
 * @DateTime 2021年10月19日;
 * @author zy(azurite-Y);
 * @Description
 */
public abstract class BaseTypeHandler<T> implements TypeHandler<T> {
	@SuppressWarnings("unchecked")
	@Override
	public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
		if (parameter == null) {
			if (jdbcType == null) {
				throw new TypeException("JDBC要求必须为所有可空参数指定JdbcType.");
			}
			try {
				ps.setNull(i, jdbcType.TYPE_CODE);
			} catch (SQLException e) {
				throw new TypeException("错误的设置参数[#" + i + "]，JdbcType[" + jdbcType + " ]为null. 请尝试为这个参数设置一个JdbcType配置属性. "
						+ "Cause: " + e, e);
			}
		} else {
			try {
				setNonNullParameter(ps, i, (T)parameter, jdbcType);
			} catch (Exception e) {
				throw new TypeException("设置非空参数错误[#" + i + "], JdbcType: [" + jdbcType + "]。尝试为该参数设置不同的JdbcType或不同的配置属性. "
						+ "Cause: " + e, e);
			}
		}
	}

	@Override
	public T getResult(ResultSet rs, String columnName) throws SQLException {
		T result;
		try {
			result = getNullableResult(rs, columnName);
		} catch (Exception e) {
			throw new ResultMapException("试图从结果集中获取列'" + columnName + "'时出错.  Cause: " + e, e);
		}
		return rs.wasNull() ? null : result;
	}


	@Override
	public T getResult(ResultSet rs, Integer columnIndex) throws SQLException {
		T result;
		try {
			result = getNullableResult(rs, columnIndex);
		} catch (Exception e) {
			throw new ResultMapException("试图从结果集中获取列#'" + columnIndex + "'时出错.  Cause: " + e, e);
		}
		return rs.wasNull() ? null : result;
	}

	@Override
	public T getResult(CallableStatement cs, Integer columnIndex) throws SQLException {
		T result;
		try {
			result = getNullableResult(cs, columnIndex);
		} catch (Exception e) {
			throw new ResultMapException("试图从结果集中获取列#'" + columnIndex + "'时出错.  Cause: " + e, e);
		}
		return cs.wasNull() ? null : result;
	}

	protected abstract void setNonNullParameter(PreparedStatement ps, int index, T parameter, JdbcType jdbcType) throws SQLException;

	protected abstract T getNullableResult(ResultSet rs, String columnName) throws SQLException;

	protected abstract T getNullableResult(ResultSet rs, int columnIndex) throws SQLException;

	protected abstract T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException;
}
