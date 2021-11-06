package org.zy.dichroite.fluorite.exception;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 执行器异常基类
 */
public class ExecutorException extends DichroiteException{
	private static final long serialVersionUID = -4642915789128695062L;
	public ExecutorException(String msg) {
		super(msg);
	}
	public ExecutorException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
