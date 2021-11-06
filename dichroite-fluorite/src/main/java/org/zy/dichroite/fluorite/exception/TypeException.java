package org.zy.dichroite.fluorite.exception;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 类型异常
 */
public class TypeException extends RuntimeException{
	private static final long serialVersionUID = -9164241832214709815L;
	public TypeException(String msg) {
		super(msg);
	}
	public TypeException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
