/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.cache;

import cn.xianyijun.orm.exception.CacheException;

/**
 * The type Null cache key.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public final class NullCacheKey extends CacheKey {

    private static final long serialVersionUID = 3704229911977019465L;

    /**
     * Instantiates a new Null cache key.
     */
    public NullCacheKey() {
        super();
    }

    /**
     * Update.
     *
     * @param object the object
     */
    @Override
    public void update(Object object) {
        throw new CacheException("Not allowed to update a NullCacheKey instance.");
    }

    /**
     * Update all.
     *
     * @param objects the objects
     */
    @Override
    public void updateAll(Object[] objects) {
        throw new CacheException("Not allowed to update a NullCacheKey instance.");
    }
}