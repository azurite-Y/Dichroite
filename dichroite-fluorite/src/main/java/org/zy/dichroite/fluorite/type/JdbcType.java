package org.zy.dichroite.fluorite.type;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @DateTime 2021年10月19日;
 * @author zy(azurite-Y);
 * @Description
 */
public enum JdbcType {
	//	  ARRAY(Types.ARRAY),
	BIT(Types.BIT),
	TINYINT(Types.TINYINT),
	SMALLINT(Types.SMALLINT),
	INTEGER(Types.INTEGER),
	BIGINT(Types.BIGINT),
	FLOAT(Types.FLOAT),
	REAL(Types.REAL),
	DOUBLE(Types.DOUBLE),
	NUMERIC(Types.NUMERIC),
	DECIMAL(Types.DECIMAL),
	CHAR(Types.CHAR),
	VARCHAR(Types.VARCHAR),
	LONGVARCHAR(Types.LONGVARCHAR),
	DATE(Types.DATE),
	TIME(Types.TIME),
	TIMESTAMP(Types.TIMESTAMP),
	BINARY(Types.BINARY),
	VARBINARY(Types.VARBINARY),
	LONGVARBINARY(Types.LONGVARBINARY),
	NULL(Types.NULL),
	OTHER(Types.OTHER),
	BLOB(Types.BLOB),
	CLOB(Types.CLOB),
	BOOLEAN(Types.BOOLEAN),
	CURSOR(-10), // Oracle
	NVARCHAR(Types.NVARCHAR), // JDK6
	NCHAR(Types.NCHAR), // JDK6
	NCLOB(Types.NCLOB), // JDK6
	STRUCT(Types.STRUCT),
	JAVA_OBJECT(Types.JAVA_OBJECT),
	ROWID(Types.ROWID), // Oracle
	LONGNVARCHAR(Types.LONGNVARCHAR);

	public final int TYPE_CODE;
	private static Map<Integer,JdbcType> codeLookup = new HashMap<Integer,JdbcType>();

	static {
		for (JdbcType type : JdbcType.values()) {
			codeLookup.put(type.TYPE_CODE, type);
		}
	}

	JdbcType(int code) {
		this.TYPE_CODE = code;
	}

	public static JdbcType forCode(int code)  {
		return codeLookup.get(code);
	}
}
