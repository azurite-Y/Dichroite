package org.zy.dichroite.fluorite.executor.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.zy.dichroite.fluorite.binding.BoundSql;
import org.zy.dichroite.fluorite.interfaces.ParameterHandler;
import org.zy.dichroite.fluorite.interfaces.TypeHandler;
import org.zy.dichroite.fluorite.mapping.MappedStatement;
import org.zy.dichroite.fluorite.session.Configuration;

/**
 * @DateTime 2021年9月9日;
 * @author zy(azurite-Y);
 * @Description 默认参数处理器，根据参数名调用对应的getter方法获得参数值
 */
public class DefaultParameterHandler implements ParameterHandler {
	private final MappedStatement mappedStatement;
	private final Map<Integer,Object> args;
	private final BoundSql boundSql;
	private final Configuration configuration;

	public DefaultParameterHandler(MappedStatement mappedStatement, Map<Integer,Object> args, BoundSql boundSql) {
		this.mappedStatement = mappedStatement;
		this.configuration = mappedStatement.getConfiguration();
		this.args = args;
		this.boundSql = boundSql;
	}

	@Override
	public Map<Integer, Object> getParameterObject() {
		return this.args;
	}

	@Override
	public void setParameters(PreparedStatement ps) {
		// 按类型调用不同的set方法
		Logger logger = mappedStatement.getStatementLogger();
		List<Object> parameterValueMappins = boundSql.getParameterValueMappins();
		List<TypeHandler<?>> typeHandlerList = boundSql.getTypeHandlerList();
		logger.info("==> Parameters\t: " + parameterValueMappins);
		for (int i = 0; i < parameterValueMappins.size(); i++) {
			Object obj = parameterValueMappins.get(i);
			try {
				TypeHandler<?> typeHandler = typeHandlerList.get(i);
				if (null != typeHandler) {
					typeHandler.setParameter(ps, i + 1, obj, null);
				} else {
					ps.setObject(i + 1, obj);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	public MappedStatement getMappedStatement() {
		return mappedStatement;
	}
	public BoundSql getBoundSql() {
		return boundSql;
	}
	public Configuration getConfiguration() {
		return configuration;
	}
}
