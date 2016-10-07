/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Too many results exception.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class TooManyResultsException extends PersistenceException {

  private static final long serialVersionUID = 8935197089745865786L;

  /**
   * Instantiates a new Too many results exception.
   */
  public TooManyResultsException() {
    super();
  }

  /**
   * Instantiates a new Too many results exception.
   *
   * @param message the message
   */
  public TooManyResultsException(String message) {
    super(message);
  }

  /**
   * Instantiates a new Too many results exception.
   *
   * @param message the message
   * @param cause   the cause
   */
  public TooManyResultsException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new Too many results exception.
   *
   * @param cause the cause
   */
  public TooManyResultsException(Throwable cause) {
		super(cause);
	}
}
