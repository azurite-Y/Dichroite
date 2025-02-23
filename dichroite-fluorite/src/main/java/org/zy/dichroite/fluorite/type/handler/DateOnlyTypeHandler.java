package org.zy.dichroite.fluorite.type.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.zy.dichroite.fluorite.type.JdbcType;

/**
 * @DateTime 2021年10月22日;
 * @author zy(azurite-Y);
 * @Description
 */
public class DateOnlyTypeHandler extends BaseTypeHandler<Date> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setDate(i, new java.sql.Date((parameter.getTime())));
	}

	@Override
	public Date getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		java.sql.Date sqlDate = rs.getDate(columnName);
		if (sqlDate != null) {
			return new java.util.Date(sqlDate.getTime());
		}
		return null;
	}

	@Override
	public Date getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		java.sql.Date sqlDate = rs.getDate(columnIndex);
		if (sqlDate != null) {
			return new java.util.Date(sqlDate.getTime());
		}
		return null;
	}

	@Override
	public Date getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		java.sql.Date sqlDate = cs.getDate(columnIndex);
		if (sqlDate != null) {
			return new java.util.Date(sqlDate.getTime());
		}
		return null;
	}

}
