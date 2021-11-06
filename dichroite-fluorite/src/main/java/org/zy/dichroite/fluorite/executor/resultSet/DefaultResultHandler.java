package org.zy.dichroite.fluorite.executor.resultSet;

import java.util.ArrayList;
import java.util.List;

import org.zy.dichroite.fluorite.interfaces.ObjectFactory;
import org.zy.dichroite.fluorite.interfaces.ReflectorFactory;
import org.zy.dichroite.fluorite.interfaces.ResultContext;
import org.zy.dichroite.fluorite.mapping.MappedStatement;
import org.zy.dichroite.fluorite.mapping.ResultMap;
import org.zy.dichroite.fluorite.session.Configuration;

/**
 * @DateTime 2021年10月20日;
 * @author zy(azurite-Y);
 * @Description
 */
public class DefaultResultHandler extends AbstractResultHandler {
	private final List<Object> list ;
	
	public DefaultResultHandler(MappedStatement mappedStatement, Configuration configuration,
			ObjectFactory objectFactory, ReflectorFactory reflectorFactory, ResultMap resultMap) {
		super(mappedStatement, configuration, objectFactory, reflectorFactory, resultMap);
		list = new ArrayList<>();
	}

	@Override
	public List<Object> result() {
		return list;
	}

	@Override
	protected void storeObject(ResultContext resultContext) {
		list.add(resultContext.getResultMetaObject().resultsReconstructed());		
	}

}
