package org.zy.dichroite.fluorite.type.handler;

import java.math.BigDecimal;
import java.math.BigInteger;
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
public class BigIntegerTypeHandler extends BaseTypeHandler<BigInteger> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, BigInteger parameter, JdbcType jdbcType) throws SQLException {
		ps.setBigDecimal(i, new BigDecimal(parameter));
	}

	@Override
	public BigInteger getNullableResult(ResultSet rs, String columnName) throws SQLException {
		BigDecimal bigDecimal = rs.getBigDecimal(columnName);
		return bigDecimal == null ? null : bigDecimal.toBigInteger();
	}

	@Override
	public BigInteger getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		BigDecimal bigDecimal = rs.getBigDecimal(columnIndex);
		return bigDecimal == null ? null : bigDecimal.toBigInteger();
	}

	@Override
	public BigInteger getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		BigDecimal bigDecimal = cs.getBigDecimal(columnIndex);
		return bigDecimal == null ? null : bigDecimal.toBigInteger();
	}
}
