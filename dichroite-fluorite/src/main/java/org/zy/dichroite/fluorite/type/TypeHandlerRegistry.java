package org.zy.dichroite.fluorite.type;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.zy.dichroite.fluorite.annotation.MappedJdbcTypes;
import org.zy.dichroite.fluorite.annotation.MappedTypes;
import org.zy.dichroite.fluorite.interfaces.TypeHandler;
import org.zy.dichroite.fluorite.type.handler.BigDecimalTypeHandler;
import org.zy.dichroite.fluorite.type.handler.BigIntegerTypeHandler;
import org.zy.dichroite.fluorite.type.handler.BlobByteObjectArrayTypeHandler;
import org.zy.dichroite.fluorite.type.handler.BlobInputStreamTypeHandler;
import org.zy.dichroite.fluorite.type.handler.BlobTypeHandler;
import org.zy.dichroite.fluorite.type.handler.BooleanTypeHandler;
import org.zy.dichroite.fluorite.type.handler.ByteArrayTypeHandler;
import org.zy.dichroite.fluorite.type.handler.ByteObjectArrayTypeHandler;
import org.zy.dichroite.fluorite.type.handler.ByteTypeHandler;
import org.zy.dichroite.fluorite.type.handler.ClobReaderTypeHandler;
import org.zy.dichroite.fluorite.type.handler.ClobTypeHandler;
import org.zy.dichroite.fluorite.type.handler.DateOnlyTypeHandler;
import org.zy.dichroite.fluorite.type.handler.DateTypeHandler;
import org.zy.dichroite.fluorite.type.handler.DoubleTypeHandler;
import org.zy.dichroite.fluorite.type.handler.EnumTypeHandler;
import org.zy.dichroite.fluorite.type.handler.FloatTypeHandler;
import org.zy.dichroite.fluorite.type.handler.IntegerTypeHandler;
import org.zy.dichroite.fluorite.type.handler.LongTypeHandler;
import org.zy.dichroite.fluorite.type.handler.NClobTypeHandler;
import org.zy.dichroite.fluorite.type.handler.NStringTypeHandler;
import org.zy.dichroite.fluorite.type.handler.ObjectTypeHandler;
import org.zy.dichroite.fluorite.type.handler.ShortTypeHandler;
import org.zy.dichroite.fluorite.type.handler.SqlDateTypeHandler;
import org.zy.dichroite.fluorite.type.handler.SqlTimeTypeHandler;
import org.zy.dichroite.fluorite.type.handler.SqlTimestampTypeHandler;
import org.zy.dichroite.fluorite.type.handler.StringTypeHandler;
import org.zy.dichroite.fluorite.type.handler.TimeOnlyTypeHandler;

/**
 * @DateTime 2021年10月19日;
 * @author zy(azurite-Y);
 * @Description
 */
public final class TypeHandlerRegistry {
	/**
	 * TYPE_HANDLER_MAP value值的额外索引
	 */
	private final Map<JdbcType, TypeHandler<?>> JDBC_TYPE_HANDLER_MAP = new HashMap<>();
	private final Map<Type, Map<JdbcType, TypeHandler<?>>> TYPE_HANDLER_MAP = new HashMap<Type, Map<JdbcType, TypeHandler<?>>>();
	private final TypeHandler<Object> UNKNOWN_TYPE_HANDLER = new ObjectTypeHandler();
	private final Map<Class<?>, TypeHandler<?>> ALL_TYPE_HANDLERS_MAP = new HashMap<Class<?>, TypeHandler<?>>();

	public TypeHandlerRegistry() {
		register(Boolean.class, new BooleanTypeHandler());
		register(boolean.class, new BooleanTypeHandler());
		register(JdbcType.BOOLEAN, new BooleanTypeHandler());
		register(JdbcType.BIT, new BooleanTypeHandler());

		register(Byte.class, new ByteTypeHandler());
		register(byte.class, new ByteTypeHandler());
		register(JdbcType.TINYINT, new ByteTypeHandler());

		register(Short.class, new ShortTypeHandler());
		register(short.class, new ShortTypeHandler());
		register(JdbcType.SMALLINT, new ShortTypeHandler());

		register(Integer.class, new IntegerTypeHandler());
		register(int.class, new IntegerTypeHandler());
		register(JdbcType.INTEGER, new IntegerTypeHandler());

		register(Long.class, new LongTypeHandler());
		register(long.class, new LongTypeHandler());

		register(Float.class, new FloatTypeHandler());
		register(float.class, new FloatTypeHandler());
		register(JdbcType.FLOAT, new FloatTypeHandler());

		register(Double.class, new DoubleTypeHandler());
		register(double.class, new DoubleTypeHandler());
		register(JdbcType.DOUBLE, new DoubleTypeHandler());

		register(Reader.class, new ClobReaderTypeHandler());
		register(String.class, new StringTypeHandler());
		register(String.class, JdbcType.CHAR, new StringTypeHandler());
		register(String.class, JdbcType.CLOB, new ClobTypeHandler());
		register(String.class, JdbcType.VARCHAR, new StringTypeHandler());
		register(String.class, JdbcType.LONGVARCHAR, new ClobTypeHandler());
		register(String.class, JdbcType.NVARCHAR, new NStringTypeHandler());
		register(String.class, JdbcType.NCHAR, new NStringTypeHandler());
		register(String.class, JdbcType.NCLOB, new NClobTypeHandler());
		register(JdbcType.CHAR, new StringTypeHandler());
		register(JdbcType.VARCHAR, new StringTypeHandler());
		register(JdbcType.CLOB, new ClobTypeHandler());
		register(JdbcType.LONGVARCHAR, new ClobTypeHandler());
		register(JdbcType.NVARCHAR, new NStringTypeHandler());
		register(JdbcType.NCHAR, new NStringTypeHandler());
		register(JdbcType.NCLOB, new NClobTypeHandler());

		//	    register(Object.class, JdbcType.ARRAY, new ArrayTypeHandler());
		//	    register(JdbcType.ARRAY, new ArrayTypeHandler());

		register(BigInteger.class, new BigIntegerTypeHandler());
		register(JdbcType.BIGINT, new LongTypeHandler());

		register(BigDecimal.class, new BigDecimalTypeHandler());
		register(JdbcType.REAL, new BigDecimalTypeHandler());
		register(JdbcType.DECIMAL, new BigDecimalTypeHandler());
		register(JdbcType.NUMERIC, new BigDecimalTypeHandler());

		register(InputStream.class, new BlobInputStreamTypeHandler());
		register(Byte[].class, new ByteObjectArrayTypeHandler());
		register(Byte[].class, JdbcType.BLOB, new BlobByteObjectArrayTypeHandler());
		register(Byte[].class, JdbcType.LONGVARBINARY, new BlobByteObjectArrayTypeHandler());
		register(byte[].class, new ByteArrayTypeHandler());
		register(byte[].class, JdbcType.BLOB, new BlobTypeHandler());
		register(byte[].class, JdbcType.LONGVARBINARY, new BlobTypeHandler());
		register(JdbcType.LONGVARBINARY, new BlobTypeHandler());
		register(JdbcType.BLOB, new BlobTypeHandler());

		register(Object.class, UNKNOWN_TYPE_HANDLER);
		register(Object.class, JdbcType.OTHER, UNKNOWN_TYPE_HANDLER);
		register(JdbcType.OTHER, UNKNOWN_TYPE_HANDLER);

		register(Date.class, new DateTypeHandler());
		register(Date.class, JdbcType.DATE, new DateOnlyTypeHandler());
		register(Date.class, JdbcType.TIME, new TimeOnlyTypeHandler());
		register(JdbcType.TIMESTAMP, new DateTypeHandler());
		register(JdbcType.DATE, new DateOnlyTypeHandler());
		register(JdbcType.TIME, new TimeOnlyTypeHandler());

		register(java.sql.Date.class, new SqlDateTypeHandler());
		register(java.sql.Time.class, new SqlTimeTypeHandler());
		register(java.sql.Timestamp.class, new SqlTimestampTypeHandler());
	}

	public <T> void register(Class<T> javaType, TypeHandler<? extends T> typeHandler) {
		register((Type) javaType, typeHandler);
	}

	/**
	 * 覆盖注册 {@code TypeHandler }实现，通过解析可能标注其上的 {@code MappedTypes }和{@code MappedJdbcTypes }注解判定其用途
	 * @param <T>
	 * @param typeHandler
	 */
	public <T> void register(TypeHandler<T> typeHandler) {
		boolean mappedTypeFound = false;
		MappedTypes mappedTypes = typeHandler.getClass().getAnnotation(MappedTypes.class);
		if (mappedTypes != null) {
			for (Class<?> handledType : mappedTypes.value()) {
				register(handledType, typeHandler);
				mappedTypeFound = true;
			}
		}
		if (!mappedTypeFound) {
			register((Class<T>) null, typeHandler);
		}
	}

	/**
	 * 注册指定java类型的类型转换器
	 * @param <T>
	 * @param javaType
	 * @param typeHandler
	 */
	private <T> void register(Type javaType, TypeHandler<? extends T> typeHandler) {
		MappedJdbcTypes mappedJdbcTypes = typeHandler.getClass().getAnnotation(MappedJdbcTypes.class);
		if (mappedJdbcTypes != null) {
			for (JdbcType handledJdbcType : mappedJdbcTypes.value()) {
				register(javaType, handledJdbcType, typeHandler);
			}
			if (mappedJdbcTypes.includeNullJdbcType()) {
				register(javaType, null, typeHandler);
			}
		} else {
			register(javaType, null, typeHandler);
		}
	}

	/**
	 * 注册 TypeHandler，并关联其处理的java类型和jdbc类型
	 * @param javaType
	 * @param jdbcType
	 * @param handler
	 */
	private void register(Type javaType, JdbcType jdbcType, TypeHandler<?> handler) {
		if (javaType != null) {
			Map<JdbcType, TypeHandler<?>> map = TYPE_HANDLER_MAP.get(javaType);
			if (map == null) {
				JDBC_TYPE_HANDLER_MAP.put(jdbcType, handler);
				map = new HashMap<JdbcType, TypeHandler<?>>();
				TYPE_HANDLER_MAP.put(javaType, map);
			}
			map.put(jdbcType, handler);
		}
		ALL_TYPE_HANDLERS_MAP.put(handler.getClass(), handler);
	}

	public TypeHandler<Object> getUnknownTypeHandler() {
		return UNKNOWN_TYPE_HANDLER;
	}

	/**
	 * 注册指定jdbc类型的类型转换器
	 * @param jdbcType
	 * @param handler
	 */
	public void register(JdbcType jdbcType, TypeHandler<?> handler) {
		JDBC_TYPE_HANDLER_MAP.put(jdbcType, handler);
	}

	/**
	 * 判断 指定的java类型和对应的jdbc类型是否有对应的 {@code TypeHandler } 实现
	 * @param javaType
	 * @param jdbcType
	 * @return
	 */
	public boolean hasTypeHandler(Class<?> javaType, JdbcType jdbcType) {
		return javaType != null && getTypeHandler((Type) javaType, jdbcType) != null;
	}

	/**
	 * 通过java类型获得对应的 {@code TypeHandler }实现
	 * @param <T>
	 * @param javaType
	 * @return
	 */
	public <T> TypeHandler<T> getTypeHandler(Type javaType) {
		return this.getTypeHandler(javaType, null);
	}

	/**
	 * 通过java类型和对应的jdbc类型获得 {@code TypeHandler }实现
	 * @param <T>
	 * @param javaType java类型
	 * @param jdbcType jdbc类型
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> TypeHandler<T> getTypeHandler(Type javaType, JdbcType jdbcType) {
		Map<JdbcType, TypeHandler<?>> jdbcHandlerMap = TYPE_HANDLER_MAP.get(javaType);
		TypeHandler<?> handler = null;
		if (jdbcHandlerMap != null) {
			handler = jdbcHandlerMap.get(jdbcType);
			if (handler == null) {
				// 若 jdbcType 无对应类型处理器则尝试获取基础 TypeHandler
				handler = jdbcHandlerMap.get(null);
			}
		}
		if (handler == null && javaType != null && javaType instanceof Class && Enum.class.isAssignableFrom((Class<?>) javaType)) {
			handler = new EnumTypeHandler((Class<?>) javaType);
			// 缓存EnumTypeHandler
			register(javaType, jdbcType, handler);
		}
		return (TypeHandler<T>) handler;
	}
}
