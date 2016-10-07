/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Builder exception.
 *
 * @author xianyijun  对Builder Exception进行封装
 */
public class BuilderException extends PersistenceException {

	private static final long serialVersionUID = -3885164021020443281L;

	/**
	 * Instantiates a new Builder exception.
	 */
	public BuilderException() {
		super();
	}

	/**
	 * Instantiates a new Builder exception.
	 *
	 * @param message the message
	 */
	public BuilderException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Builder exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public BuilderException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Builder exception.
	 *
	 * @param cause the cause
	 */
	public BuilderException(Throwable cause) {
		super(cause);
	}
}
