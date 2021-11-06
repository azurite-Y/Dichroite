package org.zy.dichroite.fluorite.exception;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 注解解析异常
 */
public class AnnotationParaseException extends DichroiteException{
	private static final long serialVersionUID = 2485792759327654260L;
	public AnnotationParaseException(String msg) {
		super(msg);
	}
	public AnnotationParaseException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
