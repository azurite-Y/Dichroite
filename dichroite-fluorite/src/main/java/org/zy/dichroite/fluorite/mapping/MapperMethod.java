package org.zy.dichroite.fluorite.mapping;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.zy.dichroite.fluorite.exception.BindingException;
import org.zy.dichroite.fluorite.interfaces.SqlSession;
import org.zy.dichroite.fluorite.session.Configuration;
import org.zy.fluorite.core.convert.ResolvableType;
import org.zy.fluorite.core.utils.ClassUtils;

/**
 * @DateTime 2021年9月6日;
 * @author zy(azurite-Y);
 * @Description 封装mapper方法基础属性
 */
public class MapperMethod {
	private Class<?> mapperInterface;
	private Method method;
	private Configuration configuration;
	private MappedStatement mappedStatement;
	private ResolvableType returnType;
	private Map<Integer,Object> paramMap;

	public MapperMethod(Class<?> mapperInterface, Method method, Configuration configuration) {
		this.mapperInterface = mapperInterface;
		this.method = method;
		this.configuration = configuration;
		String statementName = ClassUtils.getFullyQualifiedName(method);
		this.mappedStatement = configuration.getMappedStatement(statementName);
		this.returnType = ResolvableType.forClass(method.getReturnType()).getGeneric();
		this.paramMap = new ParamMap(method.getParameterCount());
		this.returnType = ResolvableType.forClass(method.getReturnType());
	}

	/**
	 * 调用自 MapperProxy 的 invoke(...)方法，视情况调用SqlSession实现的对应方法
	 * @param sqlSession
	 * @param args
	 * @return 
	 */
	public Object execute(SqlSession sqlSession, Object[] args) {
		Map<Integer,Object> convertArgs = convertArgsToSqlCommandParam(args);
		SqlCommandType sqlCommandType = mappedStatement.getSqlCommandType();
		String statement = mappedStatement.getId();
		if (sqlCommandType == SqlCommandType.INSERT ) {
			return convertArgs.isEmpty() ? sqlSession.insert(statement): sqlSession.insert(statement, convertArgs);
		} else if (sqlCommandType == SqlCommandType.UPDATE) {
			return convertArgs.isEmpty() ? sqlSession.update(statement): sqlSession.update(statement, convertArgs);
		} else if (sqlCommandType == SqlCommandType.DELETE) {
			return convertArgs.isEmpty() ? sqlSession.delete(statement): sqlSession.delete(statement, convertArgs);
		} else if (sqlCommandType == SqlCommandType.SELECT) {
			Class<?> resolve = this.returnType.resolve();
			if (Collection.class.isAssignableFrom(resolve) || resolve.isArray()) {
				return sqlSession.selectList(statement, convertArgs);
			}
//			else if (Map.class.isAssignableFrom(resolve) && null != method.getAnnotation(MapKey.class)) {
//				return sqlSession.selectOne(statement, convertArgs);
//			} 
			else {
				return sqlSession.selectOne(statement, convertArgs);
			}
		} else {
			throw new BindingException("未知类型的执"
					+ "行方法，by method: " + mappedStatement.getId());
		}
	}

	public Map<Integer,Object> convertArgsToSqlCommandParam(Object[] args) {
		final int paramCount = args.length;
		if (args != null) {
			if (paramCount == 1) {
				paramMap.put(0, args[0]);
			} else {
				for (int i = 0; i < args.length; i++) {
					paramMap.put(i, args[i]);
				}
			}
		}
		return this.paramMap;
	}

	//-------------------------------------------getter、setter-------------------------------------------
	public Class<?> getMapperInterface() {
		return mapperInterface;
	}
	public void setMapperInterface(Class<?> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public Configuration getConfiguration() {
		return configuration;
	}
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	public ResolvableType getReturnType() {
		return returnType;
	}
}
