/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.type;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cn.xianyijun.orm.exception.TypeException;

/**
 * The type Type reference.
 *
 * @param <T> the type parameter
 * @author xianyijun xianyijun0@gmail.com
 */
public abstract class TypeReference<T> {

    private final Type rawType;

    /**
     * Instantiates a new Type reference.
     */
    protected TypeReference() {
        rawType = getSuperclassTypeParameter(getClass());
    }

    /**
     * Gets superclass type parameter.
     *
     * @param clazz the clazz
     * @return the superclass type parameter
     */
    Type getSuperclassTypeParameter(Class<?> clazz) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof Class) {
            // try to climb up the hierarchy until meet something useful
            if (TypeReference.class != genericSuperclass) {
                return getSuperclassTypeParameter(clazz.getSuperclass());
            }

            throw new TypeException("'" + getClass() + "' extends TypeReference but misses the type parameter. "
                    + "Remove the extension or add a type parameter to it.");
        }

        Type type = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }

        return type;
    }

    /**
     * Gets raw type.
     *
     * @return the raw type
     */
    public final Type getRawType() {
        return rawType;
    }

    /**
     * To string string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        return rawType.toString();
    }

}
