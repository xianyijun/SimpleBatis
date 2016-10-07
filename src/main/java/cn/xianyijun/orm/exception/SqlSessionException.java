/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Sql session exception.
 *
 * @author xianyijun  对Builder Exception进行封装
 */
public class SqlSessionException extends PersistenceException {

	private static final long serialVersionUID = -3885164021020443281L;

	/**
	 * Instantiates a new Sql session exception.
	 */
	public SqlSessionException() {
		super();
	}

	/**
	 * Instantiates a new Sql session exception.
	 *
	 * @param message the message
	 */
	public SqlSessionException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Sql session exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public SqlSessionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Sql session exception.
	 *
	 * @param cause the cause
	 */
	public SqlSessionException(Throwable cause) {
		super(cause);
	}
}
