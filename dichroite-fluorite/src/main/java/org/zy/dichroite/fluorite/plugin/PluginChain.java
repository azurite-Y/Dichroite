package org.zy.dichroite.fluorite.plugin;

import java.util.ArrayList;
import java.util.List;

import org.zy.dichroite.fluorite.interfaces.DichroitePlugin;

/**
 * @DateTime 2021年9月8日;
 * @author zy(azurite-Y);
 * @Description 插件调用执行链
 */
public class PluginChain {
	private final List<DichroitePlugin> plugins = new ArrayList<>();

	public Object pluginAll(Object target) {
		for (DichroitePlugin plugin : plugins) {
//			if (plugin.support(obj))
//			target = interceptor.plugin(target);
		}
		return target;
	}
}
