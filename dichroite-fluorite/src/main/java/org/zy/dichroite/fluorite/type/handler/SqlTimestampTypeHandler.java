package org.zy.dichroite.fluorite.type.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.zy.dichroite.fluorite.type.JdbcType;

/**
 * @DateTime 2021年10月22日;
 * @author zy(azurite-Y);
 * @Description
 */
public class SqlTimestampTypeHandler extends BaseTypeHandler<Timestamp> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Timestamp parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setTimestamp(i, parameter);
	}

	@Override
	public Timestamp getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		return rs.getTimestamp(columnName);
	}

	@Override
	public Timestamp getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return rs.getTimestamp(columnIndex);
	}

	@Override
	public Timestamp getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return cs.getTimestamp(columnIndex);
	}
}
