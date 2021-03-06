/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Cache exception.
 *
 * @author xianyijun  对Builder Exception进行封装
 */
public class CacheException extends PersistenceException {

	private static final long serialVersionUID = -3885164021020443281L;

	/**
	 * Instantiates a new Cache exception.
	 */
	public CacheException() {
		super();
	}

	/**
	 * Instantiates a new Cache exception.
	 *
	 * @param message the message
	 */
	public CacheException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Cache exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public CacheException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Cache exception.
	 *
	 * @param cause the cause
	 */
	public CacheException(Throwable cause) {
		super(cause);
	}
}
