/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Executor exception.
 *
 * @author xianyijun  对Builder Exception进行封装
 */
public class ExecutorException extends PersistenceException {

	private static final long serialVersionUID = -3885164021020443281L;

	/**
	 * Instantiates a new Executor exception.
	 */
	public ExecutorException() {
		super();
	}

	/**
	 * Instantiates a new Executor exception.
	 *
	 * @param message the message
	 */
	public ExecutorException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Executor exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public ExecutorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Executor exception.
	 *
	 * @param cause the cause
	 */
	public ExecutorException(Throwable cause) {
		super(cause);
	}
}
