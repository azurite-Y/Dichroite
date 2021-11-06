package org.zy.dichroite.fluorite.exception;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 绑定异常
 */
public class BindingException extends RuntimeException{
	private static final long serialVersionUID = -7473013178578587884L;
	public BindingException(String msg) {
		super(msg);
	}
	public BindingException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
