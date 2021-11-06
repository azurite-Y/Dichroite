package org.zy.dichroite.fluorite.exception;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 结果封装异常
 */
public class ResultMapException extends RuntimeException{
	private static final long serialVersionUID = 5129803055043860891L;
	public ResultMapException(String msg) {
		super(msg);
	}
	public ResultMapException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
