/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.cache.impl;

import cn.xianyijun.orm.cache.Cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * The type Lru cache.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class LruCache implements Cache {

	private final Cache delegate;
	private Map<Object, Object> keyMap;
	private Object eldestKey;

	/**
	 * Instantiates a new Lru cache.
	 *
	 * @param delegate the delegate
	 */
	public LruCache(Cache delegate) {
		this.delegate = delegate;
		setSize(1024);
	}

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	@Override
	public String getId() {
		return delegate.getId();
	}

	/**
	 * Gets size.
	 *
	 * @return the size
	 */
	@Override
	public int getSize() {
		return delegate.getSize();
	}

	/**
	 * Sets size.
	 *
	 * @param size the size
	 */
	public void setSize(final int size) {
		keyMap = new LinkedHashMap<Object, Object>(size, .75F, true) {
			private static final long serialVersionUID = 4267176411845948333L;

			@Override
			protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
				boolean tooBig = size() > size;
				if (tooBig) {
					eldestKey = eldest.getKey();
				}
				return tooBig;
			}
		};
	}

	/**
	 * Put object.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	@Override
	public void putObject(Object key, Object value) {
		delegate.putObject(key, value);
		cycleKeyList(key);
	}

	/**
	 * Gets object.
	 *
	 * @param key the key
	 * @return the object
	 */
	@Override
	public Object getObject(Object key) {
		keyMap.get(key);
		return delegate.getObject(key);
	}

	/**
	 * Remove object object.
	 *
	 * @param key the key
	 * @return the object
	 */
	@Override
	public Object removeObject(Object key) {
		return delegate.removeObject(key);
	}

	/**
	 * Clear.
	 */
	@Override
	public void clear() {
		delegate.clear();
		keyMap.clear();
	}

	/**
	 * Gets read write lock.
	 *
	 * @return the read write lock
	 */
	@Override
	public ReadWriteLock getReadWriteLock() {
		return null;
	}

	private void cycleKeyList(Object key) {
		keyMap.put(key, key);
		if (eldestKey != null) {
			delegate.removeObject(eldestKey);
			eldestKey = null;
		}
	}

}
