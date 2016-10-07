/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

/**
 * The type Simple batis exception.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class SimpleBatisException extends RuntimeException {
    private static final long serialVersionUID = 7247194781497673563L;

    /**
     * Instantiates a new Simple batis exception.
     */
    public SimpleBatisException() {
        super();
    }

    /**
     * Instantiates a new Simple batis exception.
     *
     * @param message the message
     */
    public SimpleBatisException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Simple batis exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public SimpleBatisException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Simple batis exception.
     *
     * @param cause the cause
     */
    public SimpleBatisException(Throwable cause) {
        super(cause);
    }
}
