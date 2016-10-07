/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.cursor;

import java.io.Closeable;

/**
 * The interface Cursor.
 *
 * @param <T> the type parameter
 * @author xianyijun xianyijun0@gmail.com
 */
public interface Cursor<T> extends Closeable, Iterable<T> {

	/**
	 * Is open boolean.
	 *
	 * @return the boolean
	 */
	boolean isOpen();

	/**
	 * Is consumed boolean.
	 *
	 * @return the boolean
	 */
	boolean isConsumed();

	/**
	 * Gets current index.
	 *
	 * @return the current index
	 */
	int getCurrentIndex();
}
