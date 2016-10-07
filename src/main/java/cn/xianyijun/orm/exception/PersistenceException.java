/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Persistence exception.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class PersistenceException extends SimpleBatisException {
	private static final long serialVersionUID = -7340124891682008795L;

	/**
	 * Instantiates a new Persistence exception.
	 */
	public PersistenceException() {
		super();
	}

	/**
	 * Instantiates a new Persistence exception.
	 *
	 * @param message the message
	 */
	public PersistenceException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Persistence exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public PersistenceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Persistence exception.
	 *
	 * @param cause the cause
	 */
	public PersistenceException(Throwable cause) {
		super(cause);
	}
}
