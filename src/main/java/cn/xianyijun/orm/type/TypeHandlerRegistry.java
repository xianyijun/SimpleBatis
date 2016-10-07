/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.type;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.xianyijun.orm.core.ResultSetHandler;
import cn.xianyijun.orm.exception.TypeException;
import cn.xianyijun.orm.io.Resources;
import cn.xianyijun.orm.util.ResolverUtil;

/**
 * The type Type handler registry.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public final class TypeHandlerRegistry {

    private final Map<JdbcType, ResultSetHandler.TypeHandler<?>> JDBC_TYPE_HANDLER_MAP = new EnumMap<>(
            JdbcType.class);
    private final Map<Type, Map<JdbcType, ResultSetHandler.TypeHandler<?>>> TYPE_HANDLER_MAP = new HashMap<>();
    private final ResultSetHandler.TypeHandler<Object> UNKNOWN_TYPE_HANDLER = new UnknownTypeHandler(this);
    private final Map<Class<?>, ResultSetHandler.TypeHandler<?>> ALL_TYPE_HANDLERS_MAP = new HashMap<>();

    /**
     * Instantiates a new Type handler registry.
     */
    public TypeHandlerRegistry() {
        register(Boolean.class, new BooleanTypeHandler());
        register(boolean.class, new BooleanTypeHandler());
        register(JdbcType.BOOLEAN, new BooleanTypeHandler());
        register(JdbcType.BIT, new BooleanTypeHandler());

        register(Integer.class, new IntegerTypeHandler());
        register(int.class, new IntegerTypeHandler());
        register(JdbcType.INTEGER, new IntegerTypeHandler());

        register(Long.class, new LongTypeHandler());
        register(long.class, new LongTypeHandler());

        register(String.class, new StringTypeHandler());
        register(String.class, JdbcType.CHAR, new StringTypeHandler());
        register(String.class, JdbcType.VARCHAR, new StringTypeHandler());
        register(JdbcType.CHAR, new StringTypeHandler());
        register(JdbcType.VARCHAR, new StringTypeHandler());

        register(Object.class, JdbcType.ARRAY, new ArrayTypeHandler());
        register(JdbcType.ARRAY, new ArrayTypeHandler());

        register(JdbcType.BIGINT, new LongTypeHandler());

        register(Object.class, UNKNOWN_TYPE_HANDLER);
        register(Object.class, JdbcType.OTHER, UNKNOWN_TYPE_HANDLER);
        register(JdbcType.OTHER, UNKNOWN_TYPE_HANDLER);

        register(Character.class, new CharacterTypeHandler());
        register(char.class, new CharacterTypeHandler());
    }

    /**
     * Has type handler boolean.
     *
     * @param javaType the java type
     * @return the boolean
     */
    public boolean hasTypeHandler(Class<?> javaType) {
        return hasTypeHandler(javaType, null);
    }

    /**
     * Has type handler boolean.
     *
     * @param javaTypeReference the java type reference
     * @return the boolean
     */
    public boolean hasTypeHandler(TypeReference<?> javaTypeReference) {
        return hasTypeHandler(javaTypeReference, null);
    }

    /**
     * Has type handler boolean.
     *
     * @param javaType the java type
     * @param jdbcType the jdbc type
     * @return the boolean
     */
    public boolean hasTypeHandler(Class<?> javaType, JdbcType jdbcType) {
        return javaType != null && getTypeHandler((Type) javaType, jdbcType) != null;
    }

    /**
     * Has type handler boolean.
     *
     * @param javaTypeReference the java type reference
     * @param jdbcType          the jdbc type
     * @return the boolean
     */
    public boolean hasTypeHandler(TypeReference<?> javaTypeReference, JdbcType jdbcType) {
        return javaTypeReference != null && getTypeHandler(javaTypeReference, jdbcType) != null;
    }

    /**
     * Gets mapping type handler.
     *
     * @param handlerType the handler type
     * @return the mapping type handler
     */
    public ResultSetHandler.TypeHandler getMappingTypeHandler(Class<? extends ResultSetHandler.TypeHandler<?>> handlerType) {
        return ALL_TYPE_HANDLERS_MAP.get(handlerType);
    }

    /**
     * Gets type handler.
     *
     * @param <T>  the type parameter
     * @param type the type
     * @return the type handler
     */
    public <T> ResultSetHandler.TypeHandler<T> getTypeHandler(Class<T> type) {
        return getTypeHandler((Type) type, null);
    }

    /**
     * Gets type handler.
     *
     * @param <T>               the type parameter
     * @param javaTypeReference the java type reference
     * @return the type handler
     */
    public <T> ResultSetHandler.TypeHandler<T> getTypeHandler(TypeReference<T> javaTypeReference) {
        return getTypeHandler(javaTypeReference, null);
    }

    /**
     * Gets type handler.
     *
     * @param jdbcType the jdbc type
     * @return the type handler
     */
    public ResultSetHandler.TypeHandler getTypeHandler(JdbcType jdbcType) {
        return JDBC_TYPE_HANDLER_MAP.get(jdbcType);
    }

    /**
     * Gets type handler.
     *
     * @param <T>      the type parameter
     * @param type     the type
     * @param jdbcType the jdbc type
     * @return the type handler
     */
    public <T> ResultSetHandler.TypeHandler<T> getTypeHandler(Class<T> type, JdbcType jdbcType) {
        return getTypeHandler((Type) type, jdbcType);
    }

    /**
     * Gets type handler.
     *
     * @param <T>               the type parameter
     * @param javaTypeReference the java type reference
     * @param jdbcType          the jdbc type
     * @return the type handler
     */
    public <T> ResultSetHandler.TypeHandler<T> getTypeHandler(TypeReference<T> javaTypeReference, JdbcType jdbcType) {
        return getTypeHandler(javaTypeReference.getRawType(), jdbcType);
    }

    @SuppressWarnings("unchecked")
    private <T> ResultSetHandler.TypeHandler<T> getTypeHandler(Type type, JdbcType jdbcType) {
        Map<JdbcType, ResultSetHandler.TypeHandler<?>> jdbcHandlerMap = TYPE_HANDLER_MAP.get(type);
        ResultSetHandler.TypeHandler<?> handler = null;
        if (jdbcHandlerMap != null) {
            handler = jdbcHandlerMap.get(jdbcType);
            if (handler == null) {
                handler = jdbcHandlerMap.get(null);
            }
            if (handler == null) {
                handler = pickSoleHandler(jdbcHandlerMap);
            }
        }
        return (ResultSetHandler.TypeHandler<T>) handler;
    }

    private ResultSetHandler.TypeHandler<?> pickSoleHandler(Map<JdbcType, ResultSetHandler.TypeHandler<?>> jdbcHandlerMap) {
        ResultSetHandler.TypeHandler<?> soleHandler = null;
        for (ResultSetHandler.TypeHandler<?> handler : jdbcHandlerMap.values()) {
            if (soleHandler == null) {
                soleHandler = handler;
            } else if (!handler.getClass().equals(soleHandler.getClass())) {
                // More than one type handlers registered.
                return null;
            }
        }
        return soleHandler;
    }

    /**
     * Gets unknown type handler.
     *
     * @return the unknown type handler
     */
    public ResultSetHandler.TypeHandler<Object> getUnknownTypeHandler() {
        return UNKNOWN_TYPE_HANDLER;
    }

    /**
     * Register.
     *
     * @param jdbcType the jdbc type
     * @param handler  the handler
     */
    public void register(JdbcType jdbcType, ResultSetHandler.TypeHandler<?> handler) {
        JDBC_TYPE_HANDLER_MAP.put(jdbcType, handler);
    }

    //
    // REGISTER INSTANCE
    //

    // Only handler

    /**
     * Register.
     *
     * @param <T>         the type parameter
     * @param typeHandler the type handler
     */
    @SuppressWarnings("unchecked")
    public <T> void register(ResultSetHandler.TypeHandler<T> typeHandler) {
        boolean mappedTypeFound = false;
        MappedTypes mappedTypes = typeHandler.getClass().getAnnotation(MappedTypes.class);
        if (mappedTypes != null) {
            for (Class<?> handledType : mappedTypes.value()) {
                register(handledType, typeHandler);
                mappedTypeFound = true;
            }
        }
        // @since 3.1.0 - try to auto-discover the mapped type
        if (!mappedTypeFound && typeHandler instanceof TypeReference) {
            try {
                TypeReference<T> typeReference = (TypeReference<T>) typeHandler;
                register(typeReference.getRawType(), typeHandler);
                mappedTypeFound = true;
            } catch (Exception e) {
                //
            }
        }
        if (!mappedTypeFound) {
            register((Class<T>) null, typeHandler);
        }
    }

    // java type + handler

    /**
     * Register.
     *
     * @param <T>         the type parameter
     * @param javaType    the java type
     * @param typeHandler the type handler
     */
    public <T> void register(Class<T> javaType, ResultSetHandler.TypeHandler<? extends T> typeHandler) {
        register((Type) javaType, typeHandler);
    }

    private <T> void register(Type javaType, ResultSetHandler.TypeHandler<? extends T> typeHandler) {
        MappedJdbcTypes mappedJdbcTypes = typeHandler.getClass().getAnnotation(MappedJdbcTypes.class);
        if (mappedJdbcTypes != null) {
            for (JdbcType handledJdbcType : mappedJdbcTypes.value()) {
                register(javaType, handledJdbcType, typeHandler);
            }
            if (mappedJdbcTypes.includeNullJdbcType()) {
                register(javaType, null, typeHandler);
            }
        } else {
            register(javaType, null, typeHandler);
        }
    }

    /**
     * Register.
     *
     * @param <T>               the type parameter
     * @param javaTypeReference the java type reference
     * @param handler           the handler
     */
    public <T> void register(TypeReference<T> javaTypeReference, ResultSetHandler.TypeHandler<? extends T> handler) {
        register(javaTypeReference.getRawType(), handler);
    }

    // java type + jdbc type + handler

    /**
     * Register.
     *
     * @param <T>      the type parameter
     * @param type     the type
     * @param jdbcType the jdbc type
     * @param handler  the handler
     */
    public <T> void register(Class<T> type, JdbcType jdbcType, ResultSetHandler.TypeHandler<? extends T> handler) {
        register((Type) type, jdbcType, handler);
    }

    private void register(Type javaType, JdbcType jdbcType, ResultSetHandler.TypeHandler<?> handler) {
        if (javaType != null) {
            Map<JdbcType, ResultSetHandler.TypeHandler<?>> map = TYPE_HANDLER_MAP.get(javaType);
            if (map == null) {
                map = new HashMap<>();
                TYPE_HANDLER_MAP.put(javaType, map);
            }
            map.put(jdbcType, handler);
        }
        ALL_TYPE_HANDLERS_MAP.put(handler.getClass(), handler);
    }

    //
    // REGISTER CLASS
    //

    // Only handler type

    /**
     * Register.
     *
     * @param typeHandlerClass the type handler class
     */
    public void register(Class<?> typeHandlerClass) {
        boolean mappedTypeFound = false;
        MappedTypes mappedTypes = typeHandlerClass.getAnnotation(MappedTypes.class);
        if (mappedTypes != null) {
            for (Class<?> javaTypeClass : mappedTypes.value()) {
                register(javaTypeClass, typeHandlerClass);
                mappedTypeFound = true;
            }
        }
        if (!mappedTypeFound) {
            register(getInstance(null, typeHandlerClass));
        }
    }

    // java type + handler type

    /**
     * Register.
     *
     * @param javaTypeClassName    the java type class name
     * @param typeHandlerClassName the type handler class name
     * @throws ClassNotFoundException the class not found exception
     */
    public void register(String javaTypeClassName, String typeHandlerClassName) throws ClassNotFoundException {
        register(Resources.classForName(javaTypeClassName), Resources.classForName(typeHandlerClassName));
    }

    /**
     * Register.
     *
     * @param javaTypeClass    the java type class
     * @param typeHandlerClass the type handler class
     */
    public void register(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
        register(javaTypeClass, getInstance(javaTypeClass, typeHandlerClass));
    }

    // java type + jdbc type + handler type

    /**
     * Register.
     *
     * @param javaTypeClass    the java type class
     * @param jdbcType         the jdbc type
     * @param typeHandlerClass the type handler class
     */
    public void register(Class<?> javaTypeClass, JdbcType jdbcType, Class<?> typeHandlerClass) {
        register(javaTypeClass, jdbcType, getInstance(javaTypeClass, typeHandlerClass));
    }

    // Construct a handler (used also from Builders)

    /**
     * Gets instance.
     *
     * @param <T>              the type parameter
     * @param javaTypeClass    the java type class
     * @param typeHandlerClass the type handler class
     * @return the instance
     */
    @SuppressWarnings("unchecked")
    public <T> ResultSetHandler.TypeHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
        if (javaTypeClass != null) {
            try {
                Constructor<?> c = typeHandlerClass.getConstructor(Class.class);
                return (ResultSetHandler.TypeHandler<T>) c.newInstance(javaTypeClass);
            } catch (NoSuchMethodException ignored) {
                // ignored
            } catch (Exception e) {
                throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
            }
        }
        try {
            Constructor<?> c = typeHandlerClass.getConstructor();
            return (ResultSetHandler.TypeHandler<T>) c.newInstance();
        } catch (Exception e) {
            throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, e);
        }
    }

    // scan

    /**
     * Register.
     *
     * @param packageName the package name
     */
    public void register(String packageName) {
        ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<>();
        resolverUtil.find(new ResolverUtil.IsA(ResultSetHandler.TypeHandler.class), packageName);
        Set<Class<? extends Class<?>>> handlerSet = resolverUtil.getClasses();
        for (Class<?> type : handlerSet) {
            if (!type.isAnonymousClass() && !type.isInterface() && !Modifier.isAbstract(type.getModifiers())) {
                register(type);
            }
        }
    }

    // get information

    /**
     * Gets type handlers.
     *
     * @return the type handlers
     * @since 3.2.2
     */
    public Collection<ResultSetHandler.TypeHandler> getTypeHandlers() {
        return Collections.unmodifiableCollection(ALL_TYPE_HANDLERS_MAP.values());
    }

}
