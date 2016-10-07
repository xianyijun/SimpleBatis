/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection.invoker;

import java.lang.reflect.InvocationTargetException;

/**
 * The interface Invoker.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface Invoker {
	/**
	 * Invoke object.
	 *
	 * @param target the target
	 * @param args   the args
	 * @return the object
	 * @throws IllegalAccessException    the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException;

	/**
	 * Gets type.
	 *
	 * @return the type
	 */
	Class<?> getType();
}
