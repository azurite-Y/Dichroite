package org.zy.dichroite.fluorite.exception;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description Mapper绑定异常基类
 */
public class TransientDataAccessResourceException extends Exception {
	private static final long serialVersionUID = 8524036748674061059L;
	public TransientDataAccessResourceException(String msg) {
		super(msg);
	}
	public TransientDataAccessResourceException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
