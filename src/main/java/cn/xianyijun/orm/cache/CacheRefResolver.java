/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.cache;

import cn.xianyijun.orm.builder.MapperBuilderAssistant;

/**
 * The type Cache ref resolver.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class CacheRefResolver {
	private final MapperBuilderAssistant assistant;
	private final String cacheRefNamespace;

	/**
	 * Instantiates a new Cache ref resolver.
	 *
	 * @param assistant         the assistant
	 * @param cacheRefNamespace the cache ref namespace
	 */
	public CacheRefResolver(MapperBuilderAssistant assistant, String cacheRefNamespace) {
		this.assistant = assistant;
		this.cacheRefNamespace = cacheRefNamespace;
	}

	/**
	 * Resolve cache ref cache.
	 *
	 * @return the cache
	 */
	public Cache resolveCacheRef() {
		return assistant.useCacheRef(cacheRefNamespace);
	}
}
