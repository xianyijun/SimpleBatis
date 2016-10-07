/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Type exception.
 *
 * @author xianyijun  对Builder Exception进行封装
 */
public class TypeException extends PersistenceException {

	private static final long serialVersionUID = -3885164021020443281L;

	/**
	 * Instantiates a new Type exception.
	 */
	public TypeException() {
		super();
	}

	/**
	 * Instantiates a new Type exception.
	 *
	 * @param message the message
	 */
	public TypeException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Type exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public TypeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Type exception.
	 *
	 * @param cause the cause
	 */
	public TypeException(Throwable cause) {
		super(cause);
	}
}
