/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.cache;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * The type Cache key.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class CacheKey implements Cloneable, Serializable {
    private static final long serialVersionUID = -5920922646942336374L;
    private static final int DEFAULT_MULTIPLIER = 37;
    private static final int DEFAULT_HASHCODE = 17;

    /**
     * The constant NULL_CACHE_KEY.
     */
    public static final CacheKey NULL_CACHE_KEY = new NullCacheKey();

    private int multiplier;
    private int hashcode;
    private long checksum;
    private int count;
    private transient List<Object> updateList;

    /**
     * Instantiates a new Cache key.
     */
    public CacheKey() {
        this.hashcode = DEFAULT_HASHCODE;
        this.multiplier = DEFAULT_MULTIPLIER;
        this.count = 0;
        this.updateList = new ArrayList<>();
    }

    /**
     * Instantiates a new Cache key.
     *
     * @param objects the objects
     */
    public CacheKey(Object[] objects) {
        this();
        updateAll(objects);
    }

    /**
     * Gets update count.
     *
     * @return the update count
     */
    public int getUpdateCount() {
        return updateList.size();
    }

    /**
     * Update.
     *
     * @param object the object
     */
    public void update(Object object) {
        if (object != null && object.getClass().isArray()) {
            int length = Array.getLength(object);
            for (int i = 0; i < length; i++) {
                Object element = Array.get(object, i);
                doUpdate(element);
            }
        } else {
            doUpdate(object);
        }
    }

    private void doUpdate(Object object) {
        int baseHashCode = object == null ? 1 : object.hashCode();

        count++;
        checksum += baseHashCode;
        baseHashCode *= count;

        hashcode = multiplier * hashcode + baseHashCode;

        updateList.add(object);
    }

    /**
     * Update all.
     *
     * @param objects the objects
     */
    public void updateAll(Object[] objects) {
        for (Object o : objects) {
            update(o);
        }
    }

    /**
     * Equals boolean.
     *
     * @param object the object
     * @return the boolean
     */
    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CacheKey)) {
            return false;
        }

        final CacheKey cacheKey = (CacheKey) object;

        if (hashcode != cacheKey.hashcode) {
            return false;
        }
        if (checksum != cacheKey.checksum) {
            return false;
        }
        if (count != cacheKey.count) {
            return false;
        }

        for (int i = 0; i < updateList.size(); i++) {
            Object thisObject = updateList.get(i);
            Object thatObject = cacheKey.updateList.get(i);
            if (thisObject == null) {
                if (thatObject != null) {
                    return false;
                }
            } else {
                if (!thisObject.equals(thatObject)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Hash code int.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return hashcode;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        StringBuilder returnValue = new StringBuilder().append(hashcode).append(':').append(checksum);
        for (Object object : updateList) {
            returnValue.append(':').append(object);
        }

        return returnValue.toString();
    }

    /**
     * Clone cache key.
     *
     * @return the cache key
     * @throws CloneNotSupportedException the clone not supported exception
     */
    @Override
    public CacheKey clone() throws CloneNotSupportedException {
        CacheKey clonedCacheKey = (CacheKey) super.clone();
        clonedCacheKey.updateList = new ArrayList<>(updateList);
        return clonedCacheKey;
    }
}
