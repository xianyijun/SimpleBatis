/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection.invoker;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * The type Set field invoker.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class SetFieldInvoker implements Invoker {
	private Field field;

	/**
	 * Instantiates a new Set field invoker.
	 *
	 * @param field the field
	 */
	public SetFieldInvoker(Field field) {
		this.field = field;
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
		field.set(target, args[0]);
		return null;
	}

	/**
	 * Gets type.
	 *
	 * @return the type
	 */
	@Override
	public Class<?> getType() {
		return field.getType();
	}
}
