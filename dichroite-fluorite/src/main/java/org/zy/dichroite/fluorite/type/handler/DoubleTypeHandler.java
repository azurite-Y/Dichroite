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
public class DoubleTypeHandler extends BaseTypeHandler<Double> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Double parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setDouble(i, parameter);
	}

	@Override
	public Double getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		return rs.getDouble(columnName);
	}

	@Override
	public Double getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return rs.getDouble(columnIndex);
	}

	@Override
	public Double getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return cs.getDouble(columnIndex);
	}

}
