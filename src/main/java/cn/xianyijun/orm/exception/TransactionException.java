/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Transaction exception.
 *
 * @author xianyijun  对Builder Exception进行封装
 */
public class TransactionException extends PersistenceException {

	private static final long serialVersionUID = -3885164021020443281L;

	/**
	 * Instantiates a new Transaction exception.
	 */
	public TransactionException() {
		super();
	}

	/**
	 * Instantiates a new Transaction exception.
	 *
	 * @param message the message
	 */
	public TransactionException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Transaction exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public TransactionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Transaction exception.
	 *
	 * @param cause the cause
	 */
	public TransactionException(Throwable cause) {
		super(cause);
	}
}
