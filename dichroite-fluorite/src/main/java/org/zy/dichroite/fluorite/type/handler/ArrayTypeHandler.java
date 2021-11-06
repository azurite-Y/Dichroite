package org.zy.dichroite.fluorite.type.handler;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.zy.dichroite.fluorite.type.JdbcType;

/**
 * @DateTime 2021年10月22日;
 * @author zy(azurite-Y);
 * @Description 此类暂且弃用，jdbc中的Array类型的运用暂代考证研究
 */
@Deprecated
public class ArrayTypeHandler extends BaseTypeHandler<Object> {
	public ArrayTypeHandler() {
		super();
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
		ps.setArray(i, (Array) parameter);
	}

	@Override
	public Object getNullableResult(ResultSet rs, String columnName) throws SQLException {
		Array array = rs.getArray(columnName);
		return array == null ? null : array.getArray();
	}

	@Override
	public Object getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		Array array = rs.getArray(columnIndex);
		return array == null ? null : array.getArray();
	}

	@Override
	public Object getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		Array array = cs.getArray(columnIndex);
		return array == null ? null : array.getArray();
	}
}
