package org.zy.dichroite.fluorite.mapping;

/**
 * @DateTime 2021年9月27日;
 * @author zy(azurite-Y);
 * @Description
 */
public class ResultMapping {
	/**
	 * 属性名
	 */
	private String property;
	/**
	 * 属性值
	 */
	private Object value;
	/**
	 * 参数或属性类型
	 */
	private Class<?> javaType = Object.class;
	/**
	 * 字段类型
	 */
	private Integer columnType;
	/**
	 * 字段类型名称，全大写
	 */
	private String columnTypeName;
	/**
	 * 字段名
	 */
	private String columnLabel;
	
	/**
	 * 创建初始的 ResultMapping,若 {@code columnLabel } 值为null则代表数据库字段名和属性名相同
	 * @param property
	 * @param javaType
	 * @param columnLabel
	 */
	public ResultMapping(String property, Class<?> javaType, String columnLabel) {
		super();
		this.property = property;
		this.javaType = javaType;
		this.columnLabel = columnLabel;
	}
	
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Class<?> getJavaType() {
		return javaType;
	}
	public void setJavaType(Class<?> javaType) {
		this.javaType = javaType;
	}
	public Integer getColumnType() {
		return columnType;
	}
	public void setColumnType(Integer columnType) {
		this.columnType = columnType;
	}
	public String getColumnTypeName() {
		return columnTypeName;
	}
	public void setColumnTypeName(String columnTypeName) {
		this.columnTypeName = columnTypeName;
	}
	public String getColumnLabel() {
		return columnLabel;
	}
	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}

	@Override
	public String toString() {
		return "ResultMapping [property=" + property + ", value=" + value + ", javaType=" + javaType + ", columnType="
				+ columnType + ", columnTypeName=" + columnTypeName + ", columnLabel=" + columnLabel + "]";
	}
}
