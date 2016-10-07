/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.cache;

import java.util.concurrent.locks.ReadWriteLock;

/**
 * The interface Cache.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface Cache {
	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	String getId();

	/**
	 * Put object.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	void putObject(Object key, Object value);

	/**
	 * Gets object.
	 *
	 * @param key the key
	 * @return the object
	 */
	Object getObject(Object key);

	/**
	 * Remove object object.
	 *
	 * @param key the key
	 * @return the object
	 */
	Object removeObject(Object key);

	/**
	 * Clear.
	 */
	void clear();

	/**
	 * Gets size.
	 *
	 * @return the size
	 */
	int getSize();

	/**
	 * Gets read write lock.
	 *
	 * @return the read write lock
	 */
	ReadWriteLock getReadWriteLock();

}
