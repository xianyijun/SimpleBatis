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
 * The type Get field invoker.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class GetFieldInvoker implements Invoker {
	private Field field;

	/**
	 * Instantiates a new Get field invoker.
	 *
	 * @param field the field
	 */
	public GetFieldInvoker(Field field) {
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
		return field.get(target);
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
