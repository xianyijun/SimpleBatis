/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Binding exception.
 *
 * @author xianyijun  对Builder Exception进行封装
 */
public class BindingException extends PersistenceException {

	private static final long serialVersionUID = -3885164021020443281L;

	/**
	 * Instantiates a new Binding exception.
	 */
	public BindingException() {
		super();
	}

	/**
	 * Instantiates a new Binding exception.
	 *
	 * @param message the message
	 */
	public BindingException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Binding exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public BindingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Binding exception.
	 *
	 * @param cause the cause
	 */
	public BindingException(Throwable cause) {
		super(cause);
	}
}
