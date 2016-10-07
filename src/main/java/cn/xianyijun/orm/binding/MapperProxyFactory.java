/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.binding;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.xianyijun.orm.core.SqlSession;

/**
 * The type Mapper proxy factory.
 *
 * @param <T> the type parameter
 * @author xianyijun xianyijun0@gmail.com
 */
public class MapperProxyFactory<T> {
    private final Class<T> mapperInterface;
    private final Map<Method, MapperMethod> methodCache = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Mapper proxy factory.
     *
     * @param mapperInterface the mapper interface
     */
    public MapperProxyFactory(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    /**
     * Gets mapper interface.
     *
     * @return the mapper interface
     */
    public Class<T> getMapperInterface() {
        return mapperInterface;
    }

    /**
     * Gets method cache.
     *
     * @return the method cache
     */
    public Map<Method, MapperMethod> getMethodCache() {
        return methodCache;
    }

    /**
     * New instance t.
     *
     * @param mapperProxy the mapper proxy
     * @return the t
     */
    @SuppressWarnings("unchecked")
    protected T newInstance(MapperProxy<T> mapperProxy) {
        return (T) Proxy.newProxyInstance(mapperInterface.getClassLoader(), new Class[]{mapperInterface},
                mapperProxy);
    }

    /**
     * New instance t.
     *
     * @param sqlSession the sql session
     * @return the t
     */
    public T newInstance(SqlSession sqlSession) {
        final MapperProxy<T> mapperProxy = new MapperProxy<>(sqlSession, mapperInterface, methodCache);
        return newInstance(mapperProxy);
    }
}
