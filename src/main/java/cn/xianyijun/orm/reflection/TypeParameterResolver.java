/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection;

import java.lang.reflect.*;
import java.util.Arrays;

/**
 * The type Type parameter resolver.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class TypeParameterResolver {

    /**
     * Resolve field type type.
     *
     * @param field   the field
     * @param srcType the src type
     * @return the type
     */
    public static Type resolveFieldType(Field field, Type srcType) {
        Type fieldType = field.getGenericType();
        Class<?> declaringClass = field.getDeclaringClass();
        return resolveType(fieldType, srcType, declaringClass);
    }

    /**
     * Resolve return type type.
     *
     * @param method  the method
     * @param srcType the src type
     * @return the type
     */
    public static Type resolveReturnType(Method method, Type srcType) {
        Type returnType = method.getGenericReturnType();
        Class<?> declaringClass = method.getDeclaringClass();
        return resolveType(returnType, srcType, declaringClass);
    }

    /**
     * Resolve param types type [ ].
     *
     * @param method  the method
     * @param srcType the src type
     * @return the type [ ]
     */
    public static Type[] resolveParamTypes(Method method, Type srcType) {
        Type[] paramTypes = method.getGenericParameterTypes();
        Class<?> declaringClass = method.getDeclaringClass();
        Type[] result = new Type[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            result[i] = resolveType(paramTypes[i], srcType, declaringClass);
        }
        return result;
    }

    private static Type resolveType(Type type, Type srcType, Class<?> declaringClass) {
        if (type instanceof TypeVariable) {
            return resolveTypeVar((TypeVariable<?>) type, srcType, declaringClass);
        } else if (type instanceof ParameterizedType) {
            return resolveParameterizeType((ParameterizedType) type, srcType, declaringClass);
        } else if (type instanceof GenericArrayType) {
            return resolveGenericArrayType((GenericArrayType) type, srcType, declaringClass);
        } else {
            return type;
        }
    }

    private static Type resolveGenericArrayType(GenericArrayType genericArrayType, Type srcType,
                                                Class<?> declaringClass) {
        Type componentType = genericArrayType.getGenericComponentType();
        Type resolvedComponentType = null;
        if (componentType instanceof TypeVariable) {
            resolvedComponentType = resolveTypeVar((TypeVariable<?>) componentType, srcType, declaringClass);
        } else if (componentType instanceof GenericArrayType) {
            resolvedComponentType = resolveGenericArrayType((GenericArrayType) componentType, srcType, declaringClass);
        } else if (componentType instanceof ParameterizedType) {
            resolvedComponentType = resolveParameterizeType((ParameterizedType) componentType, srcType,
                    declaringClass);
        }
        if (resolvedComponentType instanceof Class) {
            return Array.newInstance((Class<?>) resolvedComponentType, 0).getClass();
        } else {
            return new GenericArrayTypeImpl(resolvedComponentType);
        }
    }

    private static ParameterizedType resolveParameterizeType(ParameterizedType parameterizedType, Type srcType,
                                                             Class<?> declaringClass) {
        Class<?> rawType = (Class<?>) parameterizedType.getRawType();
        Type[] typeArgs = parameterizedType.getActualTypeArguments();
        Type[] args = resolveWildcardTypeBounds(typeArgs, srcType, declaringClass);
        return new ParameterizedTypeImpl(rawType, null, args);
    }

    private static Type resolveWildcardType(WildcardType wildcardType, Type srcType, Class<?> declaringClass) {
        Type[] lowerBounds = resolveWildcardTypeBounds(wildcardType.getLowerBounds(), srcType, declaringClass);
        Type[] upperBounds = resolveWildcardTypeBounds(wildcardType.getUpperBounds(), srcType, declaringClass);
        return new WildcardTypeImpl(lowerBounds, upperBounds);
    }

    private static Type[] resolveWildcardTypeBounds(Type[] bounds, Type srcType, Class<?> declaringClass) {
        Type[] result = new Type[bounds.length];
        for (int i = 0; i < bounds.length; i++) {
            if (bounds[i] instanceof TypeVariable) {
                result[i] = resolveTypeVar((TypeVariable<?>) bounds[i], srcType, declaringClass);
            } else if (bounds[i] instanceof ParameterizedType) {
                result[i] = resolveParameterizeType((ParameterizedType) bounds[i], srcType, declaringClass);
            } else if (bounds[i] instanceof WildcardType) {
                result[i] = resolveWildcardType((WildcardType) bounds[i], srcType, declaringClass);
            } else {
                result[i] = bounds[i];
            }
        }
        return result;
    }

    private static Type resolveTypeVar(TypeVariable<?> typeVar, Type srcType, Class<?> declaringClass) {
        Type result;
        Class<?> clazz;
        if (srcType instanceof Class) {
            clazz = (Class<?>) srcType;
        } else if (srcType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) srcType;
            clazz = (Class<?>) parameterizedType.getRawType();
        } else {
            throw new IllegalArgumentException(
                    "The 2nd arg must be Class or ParameterizeType, but was: " + srcType.getClass());
        }

        if (clazz == declaringClass) {
            return Object.class;
        }

        Type superclass = clazz.getGenericSuperclass();
        result = scanSuperTypes(typeVar, srcType, declaringClass, clazz, superclass);
        if (result != null) {
            return result;
        }

        Type[] superInterfaces = clazz.getGenericInterfaces();
        for (Type superInterface : superInterfaces) {
            result = scanSuperTypes(typeVar, srcType, declaringClass, clazz, superInterface);
            if (result != null) {
                return result;
            }
        }
        return Object.class;
    }

    private static Type scanSuperTypes(TypeVariable<?> typeVar, Type srcType, Class<?> declaringClass, Class<?> clazz,
                                       Type superclass) {
        Type result = null;
        if (superclass instanceof ParameterizedType) {
            ParameterizedType parentAsType = (ParameterizedType) superclass;
            Class<?> parentAsClass = (Class<?>) parentAsType.getRawType();
            if (declaringClass == parentAsClass) {
                Type[] typeArgs = parentAsType.getActualTypeArguments();
                TypeVariable<?>[] declaredTypeVars = declaringClass.getTypeParameters();
                for (int i = 0; i < declaredTypeVars.length; i++) {
                    if (declaredTypeVars[i] == typeVar) {
                        if (typeArgs[i] instanceof TypeVariable) {
                            TypeVariable<?>[] typeParams = clazz.getTypeParameters();
                            for (int j = 0; j < typeParams.length; j++) {
                                if (typeParams[j] == typeArgs[i]) {
                                    if (srcType instanceof ParameterizedType) {
                                        result = ((ParameterizedType) srcType).getActualTypeArguments()[j];
                                    }
                                    break;
                                }
                            }
                        } else {
                            result = typeArgs[i];
                        }
                    }
                }
            } else if (declaringClass.isAssignableFrom(parentAsClass)) {
                result = resolveTypeVar(typeVar, parentAsType, declaringClass);
            }
        } else if (superclass instanceof Class) {
            if (declaringClass.isAssignableFrom((Class<?>) superclass)) {
                result = resolveTypeVar(typeVar, superclass, declaringClass);
            }
        }
        return result;
    }

    private TypeParameterResolver() {
        super();
    }

    /**
     * The type Parameterized type.
     *
     * @author xianyijun xianyijun0@gmail.com
     */
    static class ParameterizedTypeImpl implements ParameterizedType {
        private Class<?> rawType;

        private Type ownerType;

        private Type[] actualTypeArguments;

        /**
         * Instantiates a new Parameterized type.
         *
         * @param rawType             the raw type
         * @param ownerType           the owner type
         * @param actualTypeArguments the actual type arguments
         */
        public ParameterizedTypeImpl(Class<?> rawType, Type ownerType, Type[] actualTypeArguments) {
            super();
            this.rawType = rawType;
            this.ownerType = ownerType;
            this.actualTypeArguments = actualTypeArguments;
        }

        /**
         * Get actual type arguments type [ ].
         *
         * @return the type [ ]
         */
        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments;
        }

        /**
         * Gets owner type.
         *
         * @return the owner type
         */
        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        /**
         * Gets raw type.
         *
         * @return the raw type
         */
        @Override
        public Type getRawType() {
            return rawType;
        }

        /**
         * To string string.
         *
         * @return the string
         */
        @Override
        public String toString() {
            return "ParameterizedTypeImpl [rawType=" + rawType + ", ownerType=" + ownerType + ", actualTypeArguments="
                    + Arrays.toString(actualTypeArguments) + "]";
        }
    }

    /**
     * The type Wildcard type.
     *
     * @author xianyijun xianyijun0@gmail.com
     */
    static class WildcardTypeImpl implements WildcardType {
        private Type[] lowerBounds;

        private Type[] upperBounds;

        private WildcardTypeImpl(Type[] lowerBounds, Type[] upperBounds) {
            super();
            this.lowerBounds = lowerBounds;
            this.upperBounds = upperBounds;
        }

        /**
         * Get lower bounds type [ ].
         *
         * @return the type [ ]
         */
        @Override
        public Type[] getLowerBounds() {
            return lowerBounds;
        }

        /**
         * Get upper bounds type [ ].
         *
         * @return the type [ ]
         */
        @Override
        public Type[] getUpperBounds() {
            return upperBounds;
        }
    }

    /**
     * The type Generic array type.
     *
     * @author xianyijun xianyijun0@gmail.com
     */
    static class GenericArrayTypeImpl implements GenericArrayType {
        private Type genericComponentType;

        private GenericArrayTypeImpl(Type genericComponentType) {
            super();
            this.genericComponentType = genericComponentType;
        }

        /**
         * Gets generic component type.
         *
         * @return the generic component type
         */
        @Override
        public Type getGenericComponentType() {
            return genericComponentType;
        }
    }
}
