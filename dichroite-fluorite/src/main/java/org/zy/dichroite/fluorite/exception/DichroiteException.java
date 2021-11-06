package org.zy.dichroite.fluorite.exception;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description Dichroite 运行时异常基类
 */
public class DichroiteException extends RuntimeException{
	private static final long serialVersionUID = 920839876410693481L;
	public DichroiteException(String msg) {
		super(msg);
	}
	public DichroiteException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
