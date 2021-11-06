package org.zy.dichroite.fluorite.exception;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 泛型擦除异常
 */
public class GenericErasureException extends RuntimeException{
	private static final long serialVersionUID = -7473013178578587884L;
	public GenericErasureException(String msg) {
		super(msg);
	}
	public GenericErasureException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
