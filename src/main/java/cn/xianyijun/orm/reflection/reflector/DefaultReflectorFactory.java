/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection.reflector;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Default reflector factory.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class DefaultReflectorFactory implements ReflectorFactory {
	private static Logger logger = LoggerFactory.getLogger(DefaultReflectorFactory.class);
	private boolean classCacheEnabled = true;
	private final ConcurrentMap<Class<?>, Reflector> reflectorMap = new ConcurrentHashMap<Class<?>, Reflector>();

	/**
	 * Instantiates a new Default reflector factory.
	 */
	public DefaultReflectorFactory() {
	}

	/**
	 * Is class cache enabled boolean.
	 *
	 * @return the boolean
	 */
	@Override
	public boolean isClassCacheEnabled() {
		return classCacheEnabled;
	}

	/**
	 * Sets class cache enabled.
	 *
	 * @param classCacheEnabled the class cache enabled
	 */
	@Override
	public void setClassCacheEnabled(boolean classCacheEnabled) {
		this.classCacheEnabled = classCacheEnabled;
	}

	/**
	 * Find for class reflector.
	 *
	 * @param type the type
	 * @return the reflector
	 */
	@Override
	public Reflector findForClass(Class<?> type) {
		logger.debug(" the reflector class type :" + type.toString());
		if (classCacheEnabled) {
			Reflector cached = reflectorMap.get(type);
			if (cached == null) {
				cached = new Reflector(type);
				reflectorMap.put(type, cached);
			}
			return cached;
		} else {
			return new Reflector(type);
		}
	}

}
