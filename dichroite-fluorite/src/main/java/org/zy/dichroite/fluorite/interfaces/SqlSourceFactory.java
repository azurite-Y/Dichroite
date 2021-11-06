package org.zy.dichroite.fluorite.interfaces;

import java.util.Map;

/**
 * @DateTime 2021年9月30日;
 * @author zy(azurite-Y);
 * @Description SqlSource构建工厂抽象接口
 */
public interface SqlSourceFactory {
	/**
	 * 
	 * @param originalSql
	 * @param parameterType
	 * @param additionalParameters
	 * @return
	 */
	SqlSource parse(String originalSql, Class<?> parameterType, Map<String, Object> additionalParameters);
}
