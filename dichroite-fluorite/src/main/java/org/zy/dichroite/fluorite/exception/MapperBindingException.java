package org.zy.dichroite.fluorite.exception;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description Mapper绑定异常基类
 */
public class MapperBindingException extends Exception {
	private static final long serialVersionUID = -9146120306882464234L;
	public MapperBindingException(String msg) {
		super(msg);
	}
	public MapperBindingException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
