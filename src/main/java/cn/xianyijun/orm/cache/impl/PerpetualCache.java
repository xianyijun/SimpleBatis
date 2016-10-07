/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.cache.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;

import cn.xianyijun.orm.cache.Cache;
import cn.xianyijun.orm.exception.CacheException;

/**
 * The type Perpetual cache.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class PerpetualCache implements Cache {

	private String id;

	private Map<Object, Object> cache = new HashMap<>();

	/**
	 * Instantiates a new Perpetual cache.
	 *
	 * @param id the id
	 */
	public PerpetualCache(String id) {
		this.id = id;
	}

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	@Override
	public String getId() {
		return id;
	}

	/**
	 * Gets size.
	 *
	 * @return the size
	 */
	@Override
	public int getSize() {
		return cache.size();
	}

	/**
	 * Put object.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	@Override
	public void putObject(Object key, Object value) {
		cache.put(key, value);
	}

	/**
	 * Gets object.
	 *
	 * @param key the key
	 * @return the object
	 */
	@Override
	public Object getObject(Object key) {
		return cache.get(key);
	}

	/**
	 * Remove object object.
	 *
	 * @param key the key
	 * @return the object
	 */
	@Override
	public Object removeObject(Object key) {
		return cache.remove(key);
	}

	/**
	 * Clear.
	 */
	@Override
	public void clear() {
		cache.clear();
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

	/**
	 * Equals boolean.
	 *
	 * @param o the o
	 * @return the boolean
	 */
	@Override
	public boolean equals(Object o) {
		if (getId() == null) {
			throw new CacheException("Cache instances require an ID.");
		}
		if (this == o) {
			return true;
		}
		if (!(o instanceof Cache)) {
			return false;
		}

		Cache otherCache = (Cache) o;
		return getId().equals(otherCache.getId());
	}

	/**
	 * Hash code int.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		if (getId() == null) {
			throw new CacheException("Cache instances require an ID.");
		}
		return getId().hashCode();
	}

}
