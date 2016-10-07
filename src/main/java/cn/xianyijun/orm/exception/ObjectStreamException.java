/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.exception;

import java.io.IOException;

/**
 * The type Object stream exception.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public abstract class ObjectStreamException extends IOException {

    private static final long serialVersionUID = 7260898174833392607L;


    /**
     * Instantiates a new Object stream exception.
     *
     * @param classname the classname
     */
    protected ObjectStreamException(String classname) {
        super(classname);
    }

    /**
     * Instantiates a new Object stream exception.
     */
    protected ObjectStreamException() {
        super();
    }
}