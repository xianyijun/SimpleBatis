/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Data source exception.
 *
 * @author xianyijun  对Builder Exception进行封装
 */
public class DataSourceException extends PersistenceException {

	private static final long serialVersionUID = -3885164021020443281L;

	/**
	 * Instantiates a new Data source exception.
	 */
	public DataSourceException() {
		super();
	}

	/**
	 * Instantiates a new Data source exception.
	 *
	 * @param message the message
	 */
	public DataSourceException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Data source exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public DataSourceException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Data source exception.
	 *
	 * @param cause the cause
	 */
	public DataSourceException(Throwable cause) {
		super(cause);
	}
}
