/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.binding;

import cn.xianyijun.orm.exception.BindingException;
import cn.xianyijun.orm.core.SqlSession;
import cn.xianyijun.orm.util.ResolverUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The type Mapper registry.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class MapperRegistry {
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<>();

    /**
     * Add mapper.
     *
     * @param <T>  the type parameter
     * @param type the type
     */
    public <T> void addMapper(Class<T> type) {
        if (type.isInterface()) {
            if (hasMapper(type)) {
                throw new BindingException("Type " + type + " is already known to the MapperRegistry.");
            }
            boolean loadCompleted = false;
            try {
                knownMappers.put(type, new MapperProxyFactory<T>(type));
                loadCompleted = true;
            } finally {
                if (!loadCompleted) {
                    knownMappers.remove(type);
                }
            }
        }
    }

    /**
     * Has mapper boolean.
     *
     * @param <T>  the type parameter
     * @param type the type
     * @return the boolean
     */
    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    /**
     * Add mappers.
     *
     * @param packageName the package name
     * @param superType   the super type
     */
    public void addMappers(String packageName, Class<?> superType) {
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
        resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
        Set<Class<? extends Class<?>>> mapperSet = resolverUtil.getClasses();
        mapperSet.forEach(this::addMapper);
    }

    /**
     * Add mappers.
     *
     * @param packageName the package name
     */
    public void addMappers(String packageName) {
        addMappers(packageName, Object.class);
    }

    /**
     * Gets mapper.
     *
     * @param <T>        the type parameter
     * @param type       the type
     * @param sqlSession the sql session
     * @return the mapper
     */
    public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
        final MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(type);
        if (mapperProxyFactory == null) {
            throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
        }
        try {
            return mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new BindingException("Error getting mapper instance. Cause: " + e, e);
        }
    }

}
