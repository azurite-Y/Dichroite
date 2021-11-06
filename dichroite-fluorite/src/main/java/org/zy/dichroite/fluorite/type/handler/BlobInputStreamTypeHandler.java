package org.zy.dichroite.fluorite.type.handler;

import java.io.InputStream;
import java.sql.Blob;
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
public class BlobInputStreamTypeHandler extends BaseTypeHandler<InputStream> {

	/**
	 * 将{@link InputStream}设置为{@link PreparedStatement}。
	 * @see PreparedStatement#setBlob(int, InputStream)
	 */
	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, InputStream parameter, JdbcType jdbcType)
			throws SQLException {
		ps.setBlob(i, parameter);
	}

	/**
	 * 从{@link ResultSet}中获取与指定列名相对应的{@link InputStream}。
	 * @see ResultSet#getBlob(String)
	 */
	@Override
	public InputStream getNullableResult(ResultSet rs, String columnName)
			throws SQLException {
		return toInputStream(rs.getBlob(columnName));
	}

	/**
	 * 从{@link ResultSet}中获取对应于指定列索引的{@link InputStream}。
	 * @see ResultSet#getBlob(int)
	 */
	@Override
	public InputStream getNullableResult(ResultSet rs, int columnIndex)
			throws SQLException {
		return toInputStream(rs.getBlob(columnIndex));
	}

	/**
	 * 从{@link CallableStatement}中获取对应于指定列索引的{@link InputStream}。
	 * @see CallableStatement#getBlob(int)
	 */
	@Override
	public InputStream getNullableResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return toInputStream(cs.getBlob(columnIndex));
	}

	private InputStream toInputStream(Blob blob) throws SQLException {
		if (blob == null) {
			return null;
		} else {
			return blob.getBinaryStream();
		}
	}

}
