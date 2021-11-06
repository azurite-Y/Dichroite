package org.zy.dichroite.fluorite.mapping;

import java.util.HashMap;

import org.zy.dichroite.fluorite.exception.BindingException;

/**
 * @DateTime 2021年10月12日;
 * @author zy(azurite-Y);
 * @Description
 */
public class ParamMap extends HashMap<Integer, Object> {
	private static final long serialVersionUID = 4406714588880950888L;

	public ParamMap(Integer count) {
		super(count);
	}
	
	@Override
	public Object get(Object key) {
		if (!super.containsKey(key)) {
			throw new BindingException("未找到参数 '["+ key + "]'. 可获得的参数： " + keySet());
		}
		return super.get(key);
	}
}
