package org.zy.dichroite.fluorite.exception;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 大于一的结果集触发的异常
 */
public class TooManyResultsException extends DichroiteException{
	private static final long serialVersionUID = 6500471942855338560L;
	public TooManyResultsException(String msg) {
		super(msg);
	}
	public TooManyResultsException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
