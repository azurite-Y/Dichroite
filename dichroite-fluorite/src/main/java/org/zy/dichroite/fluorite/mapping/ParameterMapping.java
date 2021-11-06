package org.zy.dichroite.fluorite.mapping;

import org.zy.dichroite.fluorite.annotation.QueryTemplate;

/**
 * @DateTime 2021年9月9日;
 * @author zy(azurite-Y);
 * @Description 描述参数对象的基本属性
 */
public class ParameterMapping {
	/**
	 * 参数或属性名
	 */
	private String property;
	/**
	 * 参数或属性类型
	 */
	private Class<?> javaType = Object.class;
	/** 
	 * 参数出入参标识 
	 */
	private ParameterMode mode;
	/**
	 * 映射的数据库表字段名
	 */
	private String columnName;
	/**
	 * 属性梯度，升序排列.在启用 {@code QueryTemplate#serial }模式时使用
	 * @see QueryTemplate
	 */
	private Integer level = 0;

	/**
	 * 创建入参的ParameterMapping对象
	 * @param property
	 * @param javaType
	 * @param expression
	 */
	public ParameterMapping(String property, Class<?> javaType, String columnName) {
		this(property, javaType, ParameterMode.IN, columnName);
	}

	/**
	 * 创建指定参数类型的ParameterMapping对象
	 * @param property
	 * @param javaType
	 * @param mode
	 * @param expression
	 */
	public ParameterMapping(String property, Class<?> javaType, ParameterMode mode, String columnName) {
		this.property = property;
		this.javaType = javaType;
		this.mode = mode;
		this.columnName = columnName;
	}

	//-------------------------------------------getter、setter-------------------------------------------
	public String getProperty() {
		return property;
	}
	public void setProperty(String property) {
		this.property = property;
	}
	public Class<?> getJavaType() {
		return javaType;
	}
	public void setJavaType(Class<?> javaType) {
		this.javaType = javaType;
	}
	public ParameterMode getMode() {
		return mode;
	}
	public void setMode(ParameterMode mode) {
		this.mode = mode;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "ParameterMapping [property=" + property + ", javaType=" + javaType + ", mode=" + mode + ", columnName="
				+ columnName + ", level=" + level + "]";
	}
}
