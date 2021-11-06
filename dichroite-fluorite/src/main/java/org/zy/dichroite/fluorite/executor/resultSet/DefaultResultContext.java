package org.zy.dichroite.fluorite.executor.resultSet;

import org.zy.dichroite.fluorite.interfaces.ResultContext;
import org.zy.dichroite.fluorite.reflection.MetaObject;

/**
 * @DateTime 2021年10月20日;
 * @author zy(azurite-Y);
 * @Description 
 */
public class DefaultResultContext implements ResultContext {
	private int resultCount;
	private boolean stopped;
	private MetaObject metaObject;
	
	public DefaultResultContext() {}

	@Override
	public MetaObject getResultMetaObject() {
		return metaObject;
	}

	@Override
	public int getResultCount() {
		return resultCount;
	}

	@Override
	public boolean isStopped() {
		return stopped;
	}

	public void nextResultMetaObject(MetaObject metaObject) {
		resultCount++;
		this.metaObject = metaObject;
	}

	@Override
	public void stop() {
		this.stopped = true;
	}
}
