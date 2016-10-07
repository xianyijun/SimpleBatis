/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.cache;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.xianyijun.orm.cache.impl.BlockingCache;
import cn.xianyijun.orm.cache.impl.LruCache;
import cn.xianyijun.orm.cache.impl.PerpetualCache;
import cn.xianyijun.orm.cache.impl.ScheduledCache;
import cn.xianyijun.orm.cache.impl.SerializedCache;
import cn.xianyijun.orm.exception.CacheException;
import cn.xianyijun.orm.reflection.MetaObject;
import cn.xianyijun.orm.reflection.SystemMetaObject;

/**
 * The type Cache builder.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class CacheBuilder {
    private String id;
    private Class<? extends Cache> implementation;
    private List<Class<? extends Cache>> decorators;
    private Integer size;
    private Long clearInterval;
    private boolean readWrite;
    private Properties properties;
    private boolean blocking;

    /**
     * Instantiates a new Cache builder.
     *
     * @param id the id
     */
    public CacheBuilder(String id) {
        this.id = id;
        this.decorators = new ArrayList<>();
    }

    /**
     * Implementation cache builder.
     *
     * @param implementation the implementation
     * @return the cache builder
     */
    public CacheBuilder implementation(Class<? extends Cache> implementation) {
        this.implementation = implementation;
        return this;
    }

    /**
     * Add decorator cache builder.
     *
     * @param decorator the decorator
     * @return the cache builder
     */
    public CacheBuilder addDecorator(Class<? extends Cache> decorator) {
        if (decorator != null) {
            this.decorators.add(decorator);
        }
        return this;
    }

    /**
     * Size cache builder.
     *
     * @param size the size
     * @return the cache builder
     */
    public CacheBuilder size(Integer size) {
        this.size = size;
        return this;
    }

    /**
     * Clear interval cache builder.
     *
     * @param clearInterval the clear interval
     * @return the cache builder
     */
    public CacheBuilder clearInterval(Long clearInterval) {
        this.clearInterval = clearInterval;
        return this;
    }

    /**
     * Read write cache builder.
     *
     * @param readWrite the read write
     * @return the cache builder
     */
    public CacheBuilder readWrite(boolean readWrite) {
        this.readWrite = readWrite;
        return this;
    }

    /**
     * Blocking cache builder.
     *
     * @param blocking the blocking
     * @return the cache builder
     */
    public CacheBuilder blocking(boolean blocking) {
        this.blocking = blocking;
        return this;
    }

    /**
     * Properties cache builder.
     *
     * @param properties the properties
     * @return the cache builder
     */
    public CacheBuilder properties(Properties properties) {
        this.properties = properties;
        return this;
    }

    /**
     * Build cache.
     *
     * @return the cache
     */
    public Cache build() {
        setDefaultImplementations();
        Cache cache = newBaseCacheInstance(implementation, id);
        setCacheProperties(cache);
        if (PerpetualCache.class.equals(cache.getClass())) {
            for (Class<? extends Cache> decorator : decorators) {
                cache = newCacheDecoratorInstance(decorator, cache);
                setCacheProperties(cache);
            }
            cache = setStandardDecorators(cache);
        }
        return cache;
    }

    private void setDefaultImplementations() {
        if (implementation == null) {
            implementation = PerpetualCache.class;
            if (decorators.isEmpty()) {
                decorators.add(LruCache.class);
            }
        }
    }

    private Cache setStandardDecorators(Cache cache) {
        try {
            MetaObject metaCache = SystemMetaObject.forObject(cache);
            if (size != null && metaCache.hasSetter("size")) {
                metaCache.setValue("size", size);
            }
            if (clearInterval != null) {
                cache = new ScheduledCache(cache);
                ((ScheduledCache) cache).setClearInterval(clearInterval);
            }
            if (readWrite) {
                cache = new SerializedCache(cache);
            }
            if (blocking) {
                cache = new BlockingCache(cache);
            }
            return cache;
        } catch (Exception e) {
            throw new CacheException("Error building standard cache decorators.  Cause: " + e, e);
        }
    }

    private void setCacheProperties(Cache cache) {
        if (properties != null) {
            MetaObject metaCache = SystemMetaObject.forObject(cache);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                String name = (String) entry.getKey();
                String value = (String) entry.getValue();
                if (metaCache.hasSetter(name)) {
                    Class<?> type = metaCache.getSetterType(name);
                    if (String.class == type) {
                        metaCache.setValue(name, value);
                    } else if (int.class == type || Integer.class == type) {
                        metaCache.setValue(name, Integer.valueOf(value));
                    } else if (long.class == type || Long.class == type) {
                        metaCache.setValue(name, Long.valueOf(value));
                    } else if (short.class == type || Short.class == type) {
                        metaCache.setValue(name, Short.valueOf(value));
                    } else if (byte.class == type || Byte.class == type) {
                        metaCache.setValue(name, Byte.valueOf(value));
                    } else if (float.class == type || Float.class == type) {
                        metaCache.setValue(name, Float.valueOf(value));
                    } else if (boolean.class == type || Boolean.class == type) {
                        metaCache.setValue(name, Boolean.valueOf(value));
                    } else if (double.class == type || Double.class == type) {
                        metaCache.setValue(name, Double.valueOf(value));
                    } else {
                        throw new CacheException("Unsupported property type for cache: '" + name + "' of type " + type);
                    }
                }
            }
        }
    }

    private Cache newBaseCacheInstance(Class<? extends Cache> cacheClass, String id) {
        Constructor<? extends Cache> cacheConstructor = getBaseCacheConstructor(cacheClass);
        try {
            return cacheConstructor.newInstance(id);
        } catch (Exception e) {
            throw new CacheException("Could not instantiate cache implementation (" + cacheClass + "). Cause: " + e, e);
        }
    }

    private Constructor<? extends Cache> getBaseCacheConstructor(Class<? extends Cache> cacheClass) {
        try {
            return cacheClass.getConstructor(String.class);
        } catch (Exception e) {
            throw new CacheException("Invalid base cache implementation (" + cacheClass + ").  "
                    + "Base cache implementations must have a constructor that takes a String id as a parameter.  Cause: "
                    + e, e);
        }
    }

    private Cache newCacheDecoratorInstance(Class<? extends Cache> cacheClass, Cache base) {
        Constructor<? extends Cache> cacheConstructor = getCacheDecoratorConstructor(cacheClass);
        try {
            return cacheConstructor.newInstance(base);
        } catch (Exception e) {
            throw new CacheException("Could not instantiate cache decorator (" + cacheClass + "). Cause: " + e, e);
        }
    }

    private Constructor<? extends Cache> getCacheDecoratorConstructor(Class<? extends Cache> cacheClass) {
        try {
            return cacheClass.getConstructor(Cache.class);
        } catch (Exception e) {
            throw new CacheException("Invalid cache decorator (" + cacheClass + ").  "
                    + "Cache decorators must have a constructor that takes a Cache instance as a parameter.  Cause: "
                    + e, e);
        }
    }
}
