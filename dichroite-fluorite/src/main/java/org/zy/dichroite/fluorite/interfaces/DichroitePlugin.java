package org.zy.dichroite.fluorite.interfaces;

/**
 * @DateTime 2021年9月8日;
 * @author zy(azurite-Y);
 * @Description Dichroite 插件根接口
 */
public interface DichroitePlugin {
	
	/**
	 * 具体的插件逻辑
	 * @param t
	 * @return
	 */
	default Object plugin(Object t) {
		return t;
	}
	
	/**
	 * 判断插件是否适配此对象
	 * @param t
	 * @return
	 */
	boolean support(Object obj);
}
