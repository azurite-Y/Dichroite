package org.zy.dichroite.fluorite.binding;

import java.util.List;
import java.util.Map;

import org.zy.dichroite.fluorite.interfaces.TypeHandler;
import org.zy.dichroite.fluorite.mapping.ParameterMapping;
import org.zy.dichroite.fluorite.session.Configuration;

/**
 * @DateTime 2021年9月9日;
 * @author zy(azurite-Y);
 * @Description
 */
public class BoundSql {
	private String sql;
	/**
	 * 存储参数信息，集合中的顺序为参数的装填顺序
	 */
	private List<ParameterMapping> parameterMappings;
	/**
	 * 存储参数值，集合中的顺序为参数的装填顺序</br>
	 * 可能为空
	 */
	private List<Object> parameterValueMappins;
	/**
	 * 原始的方法参数容器
	 */
	private Map<Integer,Object> sourceArgs;
	/**
	 * 装配参数值所需的 TypeHandler集合，顺序代表使用顺序
	 */
	private List<TypeHandler<?>> typeHandlerList;

	public BoundSql(Configuration configuration, String sql, List<ParameterMapping> parameterMappings, Map<Integer,Object> sourceArgs) {
		this.sql = sql;
		this.parameterMappings = parameterMappings;
		this.sourceArgs = sourceArgs;
	}
	
	//-------------------------------------------getter、setter-------------------------------------------
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public List<ParameterMapping> getParameterMappings() {
		return parameterMappings;
	}
	public void setParameterMappings(List<ParameterMapping> parameterMappings) {
		this.parameterMappings = parameterMappings;
	}
	public List<Object> getParameterValueMappins() {
		return parameterValueMappins;
	}
	public void setParameterValueMappins(List<Object> parameterValueMappins) {
		this.parameterValueMappins = parameterValueMappins;
	}
	public Map<Integer, Object> getSourceArgs() {
		return sourceArgs;
	}
	public void setSourceArgs(Map<Integer, Object> sourceArgs) {
		this.sourceArgs = sourceArgs;
	}
	public List<TypeHandler<?>> getTypeHandlerList() {
		return typeHandlerList;
	}
	public void setTypeHandlerList(List<TypeHandler<?>> typeHandlerList) {
		this.typeHandlerList = typeHandlerList;
	}
}
