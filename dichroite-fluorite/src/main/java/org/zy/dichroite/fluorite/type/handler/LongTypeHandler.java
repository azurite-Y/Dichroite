package org.zy.dichroite.fluorite.type.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.zy.dichroite.fluorite.type.JdbcType;

/**
 * @DateTime 2021年10月22日;
 * @author zy(azurite-Y);
 * @Description
 */
public class LongTypeHandler extends BaseTypeHandler<Long> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Long parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setLong(i, parameter);
	}

	@Override
	public Long getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		return rs.getLong(columnName);
	}

	@Override
	public Long getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return rs.getLong(columnIndex);
	}

	@Override
	public Long getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return cs.getLong(columnIndex);
	}
}

