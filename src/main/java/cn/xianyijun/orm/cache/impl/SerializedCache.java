/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.cache.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;

import cn.xianyijun.orm.cache.Cache;
import cn.xianyijun.orm.exception.CacheException;
import cn.xianyijun.orm.io.Resources;

/**
 * The type Serialized cache.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class SerializedCache implements Cache {

	private Cache delegate;

	/**
	 * Instantiates a new Serialized cache.
	 *
	 * @param delegate the delegate
	 */
	public SerializedCache(Cache delegate) {
		this.delegate = delegate;
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
	 * Put object.
	 *
	 * @param key    the key
	 * @param object the object
	 */
	@Override
	public void putObject(Object key, Object object) {
		if (object == null || object instanceof Serializable) {
			delegate.putObject(key, serialize((Serializable) object));
		} else {
			throw new CacheException("SharedCache failed to make a copy of a non-serializable object: " + object);
		}
	}

	/**
	 * Gets object.
	 *
	 * @param key the key
	 * @return the object
	 */
	@Override
	public Object getObject(Object key) {
		Object object = delegate.getObject(key);
		return object == null ? null : deserialize((byte[]) object);
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
	 * Hash code int.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	/**
	 * Equals boolean.
	 *
	 * @param obj the obj
	 * @return the boolean
	 */
	@Override
	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	private byte[] serialize(Serializable value) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(value);
			oos.flush();
			oos.close();
			return bos.toByteArray();
		} catch (Exception e) {
			throw new CacheException("Error serializing object.  Cause: " + e, e);
		}
	}

	private Serializable deserialize(byte[] value) {
		Serializable result;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(value);
			ObjectInputStream ois = new CustomObjectInputStream(bis);
			result = (Serializable) ois.readObject();
			ois.close();
		} catch (Exception e) {
			throw new CacheException("Error deserializing object.  Cause: " + e, e);
		}
		return result;
	}

	/**
	 * The type Custom object input stream.
	 *
	 * @author xianyijun xianyijun0@gmail.com
	 */
	public static class CustomObjectInputStream extends ObjectInputStream {

		/**
		 * Instantiates a new Custom object input stream.
		 *
		 * @param in the in
		 * @throws IOException the io exception
		 */
		public CustomObjectInputStream(InputStream in) throws IOException {
			super(in);
		}

		/**
		 * Resolve class class.
		 *
		 * @param desc the desc
		 * @return the class
		 * @throws IOException            the io exception
		 * @throws ClassNotFoundException the class not found exception
		 */
		@Override
		protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
			return Resources.classForName(desc.getName());
		}

	}

}
