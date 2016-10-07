/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Scripting exception.
 *
 * @author xianyijun  对Builder Exception进行封装
 */
public class ScriptingException extends PersistenceException {

	private static final long serialVersionUID = -3885164021020443281L;

	/**
	 * Instantiates a new Scripting exception.
	 */
	public ScriptingException() {
		super();
	}

	/**
	 * Instantiates a new Scripting exception.
	 *
	 * @param message the message
	 */
	public ScriptingException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new Scripting exception.
	 *
	 * @param message the message
	 * @param cause   the cause
	 */
	public ScriptingException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Instantiates a new Scripting exception.
	 *
	 * @param cause the cause
	 */
	public ScriptingException(Throwable cause) {
		super(cause);
	}
}
