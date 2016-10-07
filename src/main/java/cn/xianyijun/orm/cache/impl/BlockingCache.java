/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.cache.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;

import cn.xianyijun.orm.cache.Cache;
import cn.xianyijun.orm.exception.CacheException;

/**
 * The type Blocking cache.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class BlockingCache implements Cache {

    private long timeout;
    private final Cache delegate;
    private final ConcurrentHashMap<Object, ReentrantLock> locks;

    /**
     * Instantiates a new Blocking cache.
     *
     * @param delegate the delegate
     */
    public BlockingCache(Cache delegate) {
        this.delegate = delegate;
        this.locks = new ConcurrentHashMap<>();
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
     * @param key   the key
     * @param value the value
     */
    @Override
    public void putObject(Object key, Object value) {
        try {
            delegate.putObject(key, value);
        } finally {
            releaseLock(key);
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
        acquireLock(key);
        Object value = delegate.getObject(key);
        if (value != null) {
            releaseLock(key);
        }
        return value;
    }

    /**
     * Remove object object.
     *
     * @param key the key
     * @return the object
     */
    @Override
    public Object removeObject(Object key) {
        releaseLock(key);
        return null;
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

    private ReentrantLock getLockForKey(Object key) {
        ReentrantLock lock = new ReentrantLock();
        ReentrantLock previous = locks.putIfAbsent(key, lock);
        return previous == null ? lock : previous;
    }

    private void acquireLock(Object key) {
        Lock lock = getLockForKey(key);
        if (timeout > 0) {
            try {
                boolean acquired = lock.tryLock(timeout, TimeUnit.MILLISECONDS);
                if (!acquired) {
                    throw new CacheException("Couldn't get a lock in " + timeout + " for the key " + key + " at the cache " + delegate.getId());
                }
            } catch (InterruptedException e) {
                throw new CacheException("Got interrupted while trying to acquire lock for key " + key, e);
            }
        } else {
            lock.lock();
        }
    }

    private void releaseLock(Object key) {
        ReentrantLock lock = locks.get(key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    /**
     * Gets timeout.
     *
     * @return the timeout
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Sets timeout.
     *
     * @param timeout the timeout
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}