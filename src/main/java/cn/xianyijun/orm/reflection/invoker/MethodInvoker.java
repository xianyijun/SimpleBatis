/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The type Method invoker.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class MethodInvoker implements Invoker {

	private Class<?> type;
	private Method method;

	/**
	 * Instantiates a new Method invoker.
	 *
	 * @param method the method
	 */
	public MethodInvoker(Method method) {
		this.method = method;

		if (method.getParameterTypes().length == 1) {
			type = method.getParameterTypes()[0];
		} else {
			type = method.getReturnType();
		}
	}

	/**
	 * Invoke object.
	 *
	 * @param target the target
	 * @param args   the args
	 * @return the object
	 * @throws IllegalAccessException    the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	@Override
	public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
		return method.invoke(target, args);
	}

	/**
	 * Gets type.
	 *
	 * @return the type
	 */
	@Override
	public Class<?> getType() {
		return type;
	}
}
