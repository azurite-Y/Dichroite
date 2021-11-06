package org.zy.dichroite.fluorite.interfaces;

import java.util.Map;

import org.zy.dichroite.fluorite.binding.BoundSql;

/**
 * @DateTime 2021年9月27日;
 * @author zy(azurite-Y);
 * @Description 
 */
public interface SqlSource {
	/**
	 * 提供入参对象获得执行SQL所需的各项参数并封装为BoundSql。</br>
	 * <p>
	 * 基本逻辑：</br>
	 * 1.创建 ParameterMapping</br>
	 * 2.生成可用的SQL字符串
	 * @param parameterObject
	 * @return
	 */
	BoundSql getBoundSql(Map<Integer,Object> args);
}
