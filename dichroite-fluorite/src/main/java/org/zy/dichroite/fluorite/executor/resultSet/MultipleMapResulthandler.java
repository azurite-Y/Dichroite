package org.zy.dichroite.fluorite.executor.resultSet;

import java.sql.SQLException;
import java.util.List;

import org.zy.dichroite.fluorite.interfaces.ObjectFactory;
import org.zy.dichroite.fluorite.interfaces.ObjectWrapper;
import org.zy.dichroite.fluorite.interfaces.ReflectorFactory;
import org.zy.dichroite.fluorite.interfaces.ResultContext;
import org.zy.dichroite.fluorite.mapping.MappedStatement;
import org.zy.dichroite.fluorite.mapping.ResultMap;
import org.zy.dichroite.fluorite.reflection.MetaObject;
import org.zy.dichroite.fluorite.session.Configuration;
import org.zy.fluorite.core.utils.Assert;

/**
 * @DateTime 2021年10月27日;
 * @author zy(azurite-Y);
 * @Description {@link ObjectWrapper}
 */
public class MultipleMapResulthandler extends AbstractResultHandler {
	
	public MultipleMapResulthandler(MappedStatement mappedStatement, Configuration configuration,
			ObjectFactory objectFactory, ReflectorFactory reflectorFactory, ResultMap resultMap) {
		super(mappedStatement, configuration, objectFactory, reflectorFactory, resultMap);
	}

	private MetaObject resultMetaObject;
	

	@Override
	protected void preReserve(ResultSetWrapper rsw) throws SQLException {
		// 创建返回值对象
		Object resultObject = createResultObject(rsw, resultObjectType);
		// 获得返回值对象对于的ObjectWrapper实现
		this.resultMetaObject = configuration.newMetaObject(resultMap, resultObject);
	}
	
	@Override
	protected void storeObject(ResultContext resultContext) {
		MetaObject metaObject = resultContext.getResultMetaObject();
		String keyName = resultMap.getMapKey().value();
		Assert.hasText(keyName,"@MapKey value()返回值不能为空串");
		this.resultMetaObject.setValue(metaObject.getValue(keyName).toString(), metaObject.resultsReconstructed());
		
	}
	
	@Override
	public List<Object> result() {
		list.add(resultMetaObject.resultsReconstructed());
		return list;
	}
}
