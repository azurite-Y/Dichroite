package org.zy.dichroite.fluorite.type.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Date;

import org.zy.dichroite.fluorite.type.JdbcType;

/**
 * @DateTime 2021年10月22日;
 * @author zy(azurite-Y);
 * @Description
 */
public class TimeOnlyTypeHandler extends BaseTypeHandler<Date> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setTime(i, new Time(parameter.getTime()));
	}

	@Override
	public Date getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		java.sql.Time sqlTime = rs.getTime(columnName);
		if (sqlTime != null) {
			return new Date(sqlTime.getTime());
		}
		return null;
	}

	@Override
	public Date getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		java.sql.Time sqlTime = rs.getTime(columnIndex);
		if (sqlTime != null) {
			return new Date(sqlTime.getTime());
		}
		return null;
	}

	@Override
	public Date getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		java.sql.Time sqlTime = cs.getTime(columnIndex);
		if (sqlTime != null) {
			return new Date(sqlTime.getTime());
		}
		return null;
	}
}
