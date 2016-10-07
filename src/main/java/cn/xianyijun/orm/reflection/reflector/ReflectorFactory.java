/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection.reflector;

/**
 * The interface Reflector factory.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface ReflectorFactory {
	/**
	 * Is class cache enabled boolean.
	 *
	 * @return the boolean
	 */
	boolean isClassCacheEnabled();

	/**
	 * Sets class cache enabled.
	 *
	 * @param classCacheEnabled the class cache enabled
	 */
	void setClassCacheEnabled(boolean classCacheEnabled);

	/**
	 * Find for class reflector.
	 *
	 * @param type the type
	 * @return the reflector
	 */
	Reflector findForClass(Class<?> type);
}
