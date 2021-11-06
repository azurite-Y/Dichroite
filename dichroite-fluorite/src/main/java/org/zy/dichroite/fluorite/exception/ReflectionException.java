package org.zy.dichroite.fluorite.exception;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 运行中反射异常基类
 */
public class ReflectionException extends DichroiteException {
	private static final long serialVersionUID = 542119311424647079L;
	public ReflectionException(String msg) {
		super(msg);
	}
	public ReflectionException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
