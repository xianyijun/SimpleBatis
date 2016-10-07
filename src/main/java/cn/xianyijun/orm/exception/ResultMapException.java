/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Result map exception.
 *
 * @author xianyijun  对Builder Exception进行封装
 */
public class ResultMapException extends PersistenceException {

	private static final long serialVersionUID = -3885164021020443281L;

	/**
	 * Instantiates a new Result map exception.
	 */
	public ResultMapException() {
		super();
	}

	/**
	 * Instantiates a new Result map exception.
	 *
	 * @param message the message
	 */
	public ResultMapException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Result map exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public ResultMapException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Result map exception.
	 *
	 * @param cause the cause
	 */
	public ResultMapException(Throwable cause) {
		super(cause);
	}
}
