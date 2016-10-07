/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Incomplete element exception.
 *
 * @author xianyijun  对Builder Exception进行封装
 */
public class IncompleteElementException extends PersistenceException {

	private static final long serialVersionUID = -3885164021020443281L;

	/**
	 * Instantiates a new Incomplete element exception.
	 */
	public IncompleteElementException() {
		super();
	}

	/**
	 * Instantiates a new Incomplete element exception.
	 *
	 * @param message the message
	 */
	public IncompleteElementException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Incomplete element exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public IncompleteElementException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Incomplete element exception.
	 *
	 * @param cause the cause
	 */
	public IncompleteElementException(Throwable cause) {
		super(cause);
	}
}
