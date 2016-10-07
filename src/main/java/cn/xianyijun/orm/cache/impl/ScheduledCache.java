/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.cache.impl;

import java.util.concurrent.locks.ReadWriteLock;

import cn.xianyijun.orm.cache.Cache;

/**
 * The type Scheduled cache.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ScheduledCache implements Cache {

    private Cache delegate;
    /**
     * The Clear interval.
     */
    protected long clearInterval;
    /**
     * The Last clear.
     */
    protected long lastClear;

    /**
     * Instantiates a new Scheduled cache.
     *
     * @param delegate the delegate
     */
    public ScheduledCache(Cache delegate) {
        this.delegate = delegate;
        this.clearInterval = 36000000;//60 * 60 *1000
        this.lastClear = System.currentTimeMillis();
    }

    /**
     * Sets clear interval.
     *
     * @param clearInterval the clear interval
     */
    public void setClearInterval(long clearInterval) {
        this.clearInterval = clearInterval;
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
        clearWhenStale();
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
        clearWhenStale();
        delegate.putObject(key, object);
    }

    /**
     * Gets object.
     *
     * @param key the key
     * @return the object
     */
    @Override
    public Object getObject(Object key) {
        return clearWhenStale() ? null : delegate.getObject(key);
    }

    /**
     * Remove object object.
     *
     * @param key the key
     * @return the object
     */
    @Override
    public Object removeObject(Object key) {
        clearWhenStale();
        return delegate.removeObject(key);
    }

    /**
     * Clear.
     */
    @Override
    public void clear() {
        lastClear = System.currentTimeMillis();
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

    private boolean clearWhenStale() {
        if (System.currentTimeMillis() - lastClear > clearInterval) {
            clear();
            return true;
        }
        return false;
    }

}
