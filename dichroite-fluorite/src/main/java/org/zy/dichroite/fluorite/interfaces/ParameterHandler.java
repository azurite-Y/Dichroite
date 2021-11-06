package org.zy.dichroite.fluorite.interfaces;

import java.sql.PreparedStatement;
import java.util.Map;

/**
 * @DateTime 2021年9月8日;
 * @author zy(azurite-Y);
 * @Description 擦书处理器接口
 */
public interface ParameterHandler {
	/**
	 * 获得参数对象
	 * @return
	 */
	Map<Integer,Object> getParameterObject();
	
	/**
	 * 参数处理方法。从boundSql的ParameterMapping获得入参参数，后根据入参类型调用PreparedStatement之中对应的set方法设置sql参数
	 * @param statement
	 */
	void setParameters(PreparedStatement ps);
}
