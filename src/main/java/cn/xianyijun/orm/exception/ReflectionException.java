/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Reflection exception.
 *
 * @author xianyijun  对Builder Exception进行封装
 */
public class ReflectionException extends PersistenceException {

	private static final long serialVersionUID = -3885164021020443281L;

	/**
	 * Instantiates a new Reflection exception.
	 */
	public ReflectionException() {
		super();
	}

	/**
	 * Instantiates a new Reflection exception.
	 *
	 * @param message the message
	 */
	public ReflectionException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Reflection exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public ReflectionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Reflection exception.
	 *
	 * @param cause the cause
	 */
	public ReflectionException(Throwable cause) {
		super(cause);
	}
}
