/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.binding;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.executor.result.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.xianyijun.orm.cursor.Cursor;
import cn.xianyijun.orm.exception.BindingException;
import cn.xianyijun.orm.core.MappedStatement;
import cn.xianyijun.orm.mapping.SqlCommandType;
import cn.xianyijun.orm.reflection.MetaObject;
import cn.xianyijun.orm.reflection.ParamNameResolver;
import cn.xianyijun.orm.reflection.TypeParameterResolver;
import cn.xianyijun.orm.session.RowBounds;
import cn.xianyijun.orm.core.SqlSession;

/**
 * The type Mapper method.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class MapperMethod {
    private final SqlCommand command;
    private final MethodSignature method;

    /**
     * Instantiates a new Mapper method.
     *
     * @param mapperInterface the mapper interface
     * @param method          the method
     * @param config          the config
     */
    public MapperMethod(Class<?> mapperInterface, Method method, StatementHandler.Configuration config) {
        this.command = new SqlCommand(config, mapperInterface, method);
        this.method = new MethodSignature(config, mapperInterface, method);
    }

    /**
     * Execute object.
     *
     * @param sqlSession the sql session
     * @param args       the args
     * @return the object
     */
    public Object execute(SqlSession sqlSession, Object[] args) {
        Object result = null;
        switch (command.getType()) {
            case UNKNOWN:
                break;
            case INSERT:
                result = doInsert(sqlSession, args);
                break;
            case UPDATE:
                result = doUpdate(sqlSession, args);
                break;
            case DELETE:
                result = doDelete(sqlSession, args);
                break;
            case SELECT:
                result = doSelect(sqlSession, args);
                break;
            default:
                throw new BindingException("Unknown execution method for: " + command.getName());
        }
        if (result == null && method.getReturnType().isPrimitive() && !method.returnsVoid()) {
            throw new BindingException("Mapper method '" + command.getName()
                    + " attempted to return null from a method with a primitive return type (" + method.getReturnType()
                    + ").");
        }
        return result;
    }

    private Object doSelect(SqlSession sqlSession, Object[] args) {
        if (method.returnsVoid() && method.hasResultHandler()) {
            executeWithResultHandler(sqlSession, args);
            return null;
        } else if (method.returnsMany()) {
            return executeForMany(sqlSession, args);
        } else if (method.returnsCursor()) {
            return executeForCursor(sqlSession, args);
        } else {
            Object param = method.convertArgsToSqlCommandParam(args);
            return sqlSession.selectOne(command.getName(), param);
        }
    }

    private Object doDelete(SqlSession sqlSession, Object[] args) {
        Object param = method.convertArgsToSqlCommandParam(args);
        return rowCountResult(sqlSession.delete(command.getName(), param));
    }

    private Object doUpdate(SqlSession sqlSession, Object[] args) {
        Object param = method.convertArgsToSqlCommandParam(args);
        return rowCountResult(sqlSession.update(command.getName(), param));
    }

    private Object doInsert(SqlSession sqlSession, Object[] args) {
        Object param = method.convertArgsToSqlCommandParam(args);
        return rowCountResult(sqlSession.insert(command.getName(), param));
    }

    private Object rowCountResult(int rowCount) {
        final Object result;
        if (method.returnsVoid()) {
            result = null;
        } else if (Integer.class.equals(method.getReturnType()) || Integer.TYPE.equals(method.getReturnType())) {
            result = rowCount;
        } else if (Long.class.equals(method.getReturnType()) || Long.TYPE.equals(method.getReturnType())) {
            result = (long) rowCount;
        } else if (Boolean.class.equals(method.getReturnType()) || Boolean.TYPE.equals(method.getReturnType())) {
            result = rowCount > 0;
        } else {
            throw new BindingException("Mapper method '" + command.getName() + "' has an unsupported return type: "
                    + method.getReturnType());
        }
        return result;
    }

    private void executeWithResultHandler(SqlSession sqlSession, Object[] args) {
        MappedStatement ms = sqlSession.getConfiguration().getMappedStatement(command.getName());
        if (void.class.equals(ms.getResultMaps().get(0).getType())) {
            throw new BindingException(
                    "method " + command.getName() + " needs either a @ResultMap annotation, a @ResultType annotation,"
                            + " or a resultType attribute in XML so a ResultHandler can be used as a parameter.");
        }
        Object param = method.convertArgsToSqlCommandParam(args);
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            sqlSession.select(command.getName(), param, rowBounds, method.extractResultHandler(args));
        } else {
            sqlSession.select(command.getName(), param, method.extractResultHandler(args));
        }
    }

    private <E> Object executeForMany(SqlSession sqlSession, Object[] args) {
        List<E> result;
        Object param = method.convertArgsToSqlCommandParam(args);
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            result = sqlSession.<E>selectList(command.getName(), param, rowBounds);
        } else {
            result = sqlSession.<E>selectList(command.getName(), param);
        }
        if (!method.getReturnType().isAssignableFrom(result.getClass())) {
            if (method.getReturnType().isArray()) {
                return convertToArray(result);
            } else {
                return convertToDeclaredCollection(sqlSession.getConfiguration(), result);
            }
        }
        return result;
    }

    private <E> Object convertToDeclaredCollection(StatementHandler.Configuration config, List<E> list) {
        Object collection = config.getObjectFactory().create(method.getReturnType());
        MetaObject metaObject = config.newMetaObject(collection);
        metaObject.addAll(list);
        return collection;
    }

    @SuppressWarnings("unchecked")
    private <E> E[] convertToArray(List<E> list) {
        E[] array = (E[]) Array.newInstance(method.getReturnType().getComponentType(), list.size());
        array = list.toArray(array);
        return array;
    }

    private <T> Cursor<T> executeForCursor(SqlSession sqlSession, Object[] args) {
        Cursor<T> result;
        Object param = method.convertArgsToSqlCommandParam(args);
        if (method.hasRowBounds()) {
            RowBounds rowBounds = method.extractRowBounds(args);
            result = sqlSession.<T>selectCursor(command.getName(), param, rowBounds);
        } else {
            result = sqlSession.<T>selectCursor(command.getName(), param);
        }
        return result;
    }

    //=======================================SqlCommand====================================

    /**
     * The type Sql command.
     *
     * @author xianyijun xianyijun0@gmail.com
     */
    public static class SqlCommand {
        private static final Logger logger = LoggerFactory.getLogger(SqlCommand.class);
        private final String name;
        private final SqlCommandType type;

        /**
         * Instantiates a new Sql command.
         *
         * @param configuration   the configuration
         * @param mapperInterface the mapper interface
         * @param method          the method
         */
        public SqlCommand(StatementHandler.Configuration configuration, Class<?> mapperInterface, Method method) {
            String statementName = mapperInterface.getName() + "." + method.getName();
            logger.debug(" the method name : " + statementName);
            MappedStatement ms = null;
            if (configuration.hasStatement(statementName)) {
                ms = configuration.getMappedStatement(statementName);
            } else if (!mapperInterface.equals(method.getDeclaringClass())) {
                String parentStatementName = method.getDeclaringClass().getName() + "." + method.getName();
                if (configuration.hasStatement(parentStatementName)) {
                    ms = configuration.getMappedStatement(parentStatementName);
                }
            }
            if (ms == null) {
                throw new BindingException("Invalid bound statement (not found): " + statementName);
            } else {
                name = ms.getId();
                type = ms.getSqlCommandType();
                if (type == SqlCommandType.UNKNOWN) {
                    throw new BindingException("Unknown execution method for: " + name);
                }
            }
        }

        /**
         * Gets name.
         *
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * Gets type.
         *
         * @return the type
         */
        public SqlCommandType getType() {
            return type;
        }
    }

    /**
     * The type Method signature.
     *
     * @author xianyijun xianyijun0@gmail.com
     */
    public static class MethodSignature {

        private final boolean returnsMany;
        private final boolean returnsVoid;
        private final boolean returnsCursor;
        private final Class<?> returnType;
        private final Integer resultHandlerIndex;
        private final Integer rowBoundsIndex;
        private final ParamNameResolver paramNameResolver;

        /**
         * Instantiates a new Method signature.
         *
         * @param configuration   the configuration
         * @param mapperInterface the mapper interface
         * @param method          the method
         */
        public MethodSignature(StatementHandler.Configuration configuration, Class<?> mapperInterface, Method method) {
            Type resolvedReturnType = TypeParameterResolver.resolveReturnType(method, mapperInterface);
            if (resolvedReturnType instanceof Class<?>) {
                this.returnType = (Class<?>) resolvedReturnType;
            } else if (resolvedReturnType instanceof ParameterizedType) {
                this.returnType = (Class<?>) ((ParameterizedType) resolvedReturnType).getRawType();
            } else {
                this.returnType = method.getReturnType();
            }
            this.returnsVoid = void.class.equals(this.returnType);
            this.returnsMany = configuration.getObjectFactory().isCollection(this.returnType)
                    || this.returnType.isArray();
            this.returnsCursor = Cursor.class.equals(this.returnType);
            this.rowBoundsIndex = getUniqueParamIndex(method, RowBounds.class);
            this.resultHandlerIndex = getUniqueParamIndex(method, ResultHandler.class);
            this.paramNameResolver = new ParamNameResolver(configuration, method);
        }

        private Integer getUniqueParamIndex(Method method, Class<?> paramType) {
            Integer index = null;
            final Class<?>[] argTypes = method.getParameterTypes();
            for (int i = 0; i < argTypes.length; i++) {
                if (paramType.isAssignableFrom(argTypes[i])) {
                    if (index == null) {
                        index = i;
                    } else {
                        throw new BindingException(method.getName() + " cannot have multiple "
                                + paramType.getSimpleName() + " parameters");
                    }
                }
            }
            return index;
        }

        /**
         * Gets return type.
         *
         * @return the return type
         */
        public Class<?> getReturnType() {
            return returnType;
        }

        /**
         * Returns many boolean.
         *
         * @return the boolean
         */
        public boolean returnsMany() {
            return returnsMany;
        }

        /**
         * Returns void boolean.
         *
         * @return the boolean
         */
        public boolean returnsVoid() {
            return returnsVoid;
        }

        /**
         * Returns cursor boolean.
         *
         * @return the boolean
         */
        public boolean returnsCursor() {
            return returnsCursor;
        }

        /**
         * Convert args to sql command param object.
         *
         * @param args the args
         * @return the object
         */
        public Object convertArgsToSqlCommandParam(Object[] args) {
            return paramNameResolver.getNamedParams(args);
        }

        /**
         * Has row bounds boolean.
         *
         * @return the boolean
         */
        public boolean hasRowBounds() {
            return rowBoundsIndex != null;
        }

        /**
         * Extract row bounds row bounds.
         *
         * @param args the args
         * @return the row bounds
         */
        public RowBounds extractRowBounds(Object[] args) {
            return hasRowBounds() ? (RowBounds) args[rowBoundsIndex] : null;
        }

        /**
         * Has result handler boolean.
         *
         * @return the boolean
         */
        public boolean hasResultHandler() {
            return resultHandlerIndex != null;
        }


        /**
         * Extract result handler result handler.
         *
         * @param args the args
         * @return the result handler
         */
        public ResultHandler extractResultHandler(Object[] args) {
            return hasResultHandler() ? (ResultHandler) args[resultHandlerIndex] : null;
        }
    }

    /**
     * The type Param map.
     *
     * @param <V> the type parameter
     * @author xianyijun xianyijun0@gmail.com
     */
    public static class ParamMap<V> extends HashMap<String, V> {

        private static final long serialVersionUID = -5096483338549395560L;

        /**
         * Get v.
         *
         * @param key the key
         * @return the v
         */
        @Override
        public V get(Object key) {
            if (!super.containsKey(key)) {
                throw new BindingException("Parameter '" + key + "' not found. Available parameters are " + keySet());
            }
            return super.get(key);
        }

    }

}
