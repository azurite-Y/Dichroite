package org.zy.dichroite.fluorite.type.handler;

import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.zy.dichroite.fluorite.type.JdbcType;

/**
 * @DateTime 2021年10月22日;
 * @author zy(azurite-Y);
 * @Description
 */
public class ClobReaderTypeHandler extends BaseTypeHandler<Reader> {

	/**
	 * 设置 {@link Reader} 参数到 {@link PreparedStatement} 中.
	 * @see PreparedStatement#setClob(int, Reader)
	 */
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, Reader parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setClob(i, parameter);
	}

	/**
	 * 从{@link ResultSet}中获取与指定列名相对应的{@link Reader}。
	 * @see ResultSet#getClob(String)
	 */
	@Override
	public Reader getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		return toReader(rs.getClob(columnName));
	}

	/**
	 * 从{@link ResultSet}中获取对应于指定列索引的{@link Reader}。
	 * @see ResultSet#getClob(int)
	 */
	@Override
	public Reader getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return toReader(rs.getClob(columnIndex));
	}

	/**
	 * 从{@link CallableStatement}中获取对应于指定列索引的{@link Reader}。
	 * @see CallableStatement#getClob(int)
	 */
	@Override
	public Reader getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return toReader(cs.getClob(columnIndex));
	}

	private Reader toReader(Clob clob) throws SQLException {
		if (clob == null) {
			return null;
		} else {
			return clob.getCharacterStream();
		}
	}

}
