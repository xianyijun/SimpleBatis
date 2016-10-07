/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection;

import java.util.List;
import java.util.Properties;

/**
 * The interface Object factory.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface ObjectFactory {
	/**
	 * Sets properties.
	 *
	 * @param properties the properties
	 */
	void setProperties(Properties properties);

	/**
	 * Create t.
	 *
	 * @param <T>  the type parameter
	 * @param type the type
	 * @return the t
	 */
	<T> T create(Class<T> type);

	/**
	 * Create t.
	 *
	 * @param <T>                 the type parameter
	 * @param type                the type
	 * @param constructorArgTypes the constructor arg types
	 * @param constructorArgs     the constructor args
	 * @return the t
	 */
	<T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs);

	/**
	 * Is collection boolean.
	 *
	 * @param <T>  the type parameter
	 * @param type the type
	 * @return the boolean
	 */
	<T> boolean isCollection(Class<T> type);

}
