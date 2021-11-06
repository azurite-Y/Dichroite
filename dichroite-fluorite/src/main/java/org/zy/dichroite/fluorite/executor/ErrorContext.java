package org.zy.dichroite.fluorite.executor;

/**
 * @DateTime 2021年9月26日;
 * @author zy(azurite-Y);
 * @Description
 */
public class ErrorContext {
	private static final ThreadLocal<ErrorContext> LOCAL_ERROR_CONTEXT = new ThreadLocal<ErrorContext>();
	private static final String LINE_SEPARATOR = System.getProperty("line.separator","\n");

	private ErrorContext stored;
	private String resource;
	private String activity;
	private String object;
	private String message;
	private String sql;
	private Throwable cause;

	private ErrorContext() {}

	public static ErrorContext instance() {
		ErrorContext context = LOCAL_ERROR_CONTEXT.get();
		if (context == null) {
			context = new ErrorContext();
		}
		return context;
	}

	public ErrorContext store() {
		stored = this;
		LOCAL_ERROR_CONTEXT.set(new ErrorContext());
		return LOCAL_ERROR_CONTEXT.get();
	}

	public ErrorContext recall() {
		if (stored != null) {
			LOCAL_ERROR_CONTEXT.set(stored);
			stored = null;
		}
		return LOCAL_ERROR_CONTEXT.get();
	}

	public ErrorContext resource(String resource) {
		this.resource = resource;
		return this;
	}

	public ErrorContext activity(String activity) {
		this.activity = activity;
		return this;
	}

	public ErrorContext object(String object) {
		this.object = object;
		return this;
	}

	public ErrorContext message(String message) {
		this.message = message;
		return this;
	}

	public ErrorContext sql(String sql) {
		this.sql = sql;
		return this;
	}

	public ErrorContext cause(Throwable cause) {
		this.cause = cause;
		return this;
	}

	public ErrorContext reset() {
		resource = null;
		activity = null;
		object = null;
		message = null;
		sql = null;
		cause = null;
		LOCAL_ERROR_CONTEXT.remove();
		LOCAL_ERROR_CONTEXT.set(this);
		return this;
	}

	@Override
	public String toString() {
		StringBuilder description = new StringBuilder();

		// message
		if (this.message != null) {
			description.append(LINE_SEPARATOR);
			description.append("### ");
			description.append(this.message);
		}

		// resource
		if (resource != null) {
			description.append(LINE_SEPARATOR);
			description.append("### 错误可能存在于 ");
			description.append(resource);
		}

		// object
		if (object != null) {
			description.append(LINE_SEPARATOR);
			description.append("### 错误可能包括: ");
			description.append(object);
		}

		// activity
		if (activity != null) {
			description.append(LINE_SEPARATOR);
			description.append("### 错误发生在 :");
			description.append(activity);
		}

		// activity
		if (sql != null) {
			description.append(LINE_SEPARATOR);
			description.append("### SQL: ");
			description.append(sql.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').trim());
		}

		// cause
		if (cause != null) {
			description.append(LINE_SEPARATOR);
			description.append("### Cause: ");
			description.append(cause.toString());
		}
		return description.toString();
	}
}
