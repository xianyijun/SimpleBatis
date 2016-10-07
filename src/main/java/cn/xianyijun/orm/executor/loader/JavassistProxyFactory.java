/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.loader;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.exception.ExecutorException;
import cn.xianyijun.orm.io.Resources;
import cn.xianyijun.orm.reflection.ObjectFactory;
import cn.xianyijun.orm.reflection.property.PropertyCopier;
import cn.xianyijun.orm.reflection.property.PropertyNamer;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;

/**
 * The type Javassist proxy factory.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class JavassistProxyFactory implements cn.xianyijun.orm.executor.loader.ProxyFactory {

    private static final String FINALIZE_METHOD = "finalize";
    private static final String WRITE_REPLACE_METHOD = "writeReplace";

    /**
     * Instantiates a new Javassist proxy factory.
     */
    public JavassistProxyFactory() {
        try {
            Resources.classForName("javassist.util.proxy.ProxyFactory");
        } catch (Throwable e) {
            throw new IllegalStateException(
                    "Cannot enable lazy loading because Javassist is not available. Add Javassist to your classpath.",
                    e);
        }
    }

    /**
     * Create proxy object.
     *
     * @param target              the target
     * @param lazyLoader          the lazy loader
     * @param configuration       the configuration
     * @param objectFactory       the object factory
     * @param constructorArgTypes the constructor arg types
     * @param constructorArgs     the constructor args
     * @return the object
     */
    @Override
    public Object createProxy(Object target, ResultLoaderMap lazyLoader, StatementHandler.Configuration configuration,
                              ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        return EnhancedResultObjectProxyImpl.createProxy(target, lazyLoader, configuration, objectFactory,
                constructorArgTypes, constructorArgs);
    }

    /**
     * Create deserialization proxy object.
     *
     * @param target              the target
     * @param unloadedProperties  the unloaded properties
     * @param objectFactory       the object factory
     * @param constructorArgTypes the constructor arg types
     * @param constructorArgs     the constructor args
     * @return the object
     */
    public Object createDeserializationProxy(Object target, Map<String, ResultLoaderMap.LoadPair> unloadedProperties,
                                             ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
        return EnhancedDeserializationProxyImpl.createProxy(target, unloadedProperties, objectFactory,
                constructorArgTypes, constructorArgs);
    }

    /**
     * Sets properties.
     *
     * @param properties the properties
     */
    @Override
    public void setProperties(Properties properties) {
        // Not Implemented
    }

    /**
     * Crate proxy object.
     *
     * @param type                the type
     * @param callback            the callback
     * @param constructorArgTypes the constructor arg types
     * @param constructorArgs     the constructor args
     * @return the object
     */
    static Object crateProxy(Class<?> type, MethodHandler callback, List<Class<?>> constructorArgTypes,
                             List<Object> constructorArgs) {

        ProxyFactory enhancer = new ProxyFactory();
        enhancer.setSuperclass(type);

        try {
            type.getDeclaredMethod(WRITE_REPLACE_METHOD);
        } catch (NoSuchMethodException e) {
            enhancer.setInterfaces(new Class[]{WriteReplaceInterface.class});
        } catch (SecurityException e) {
            // nothing to do here
        }

        Object enhanced;
        Class<?>[] typesArray = constructorArgTypes.toArray(new Class[constructorArgTypes.size()]);
        Object[] valuesArray = constructorArgs.toArray(new Object[constructorArgs.size()]);
        try {
            enhanced = enhancer.create(typesArray, valuesArray);
        } catch (Exception e) {
            throw new ExecutorException("Error creating lazy proxy.  Cause: " + e, e);
        }
        ((Proxy) enhanced).setHandler(callback);
        return enhanced;
    }

    private static class EnhancedResultObjectProxyImpl implements MethodHandler {

        private final Class<?> type;
        private final ResultLoaderMap lazyLoader;
        private final boolean aggressive;
        private final Set<String> lazyLoadTriggerMethods;
        private final ObjectFactory objectFactory;
        private final List<Class<?>> constructorArgTypes;
        private final List<Object> constructorArgs;

        private EnhancedResultObjectProxyImpl(Class<?> type, ResultLoaderMap lazyLoader, StatementHandler.Configuration configuration,
                                              ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
            this.type = type;
            this.lazyLoader = lazyLoader;
            this.aggressive = configuration.isAggressiveLazyLoading();
            this.lazyLoadTriggerMethods = configuration.getLazyLoadTriggerMethods();
            this.objectFactory = objectFactory;
            this.constructorArgTypes = constructorArgTypes;
            this.constructorArgs = constructorArgs;
        }

        /**
         * Create proxy object.
         *
         * @param target              the target
         * @param lazyLoader          the lazy loader
         * @param configuration       the configuration
         * @param objectFactory       the object factory
         * @param constructorArgTypes the constructor arg types
         * @param constructorArgs     the constructor args
         * @return the object
         */
        public static Object createProxy(Object target, ResultLoaderMap lazyLoader, StatementHandler.Configuration configuration,
                                         ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
            final Class<?> type = target.getClass();
            EnhancedResultObjectProxyImpl callback = new EnhancedResultObjectProxyImpl(type, lazyLoader, configuration,
                    objectFactory, constructorArgTypes, constructorArgs);
            Object enhanced = crateProxy(type, callback, constructorArgTypes, constructorArgs);
            PropertyCopier.copyBeanProperties(type, target, enhanced);
            return enhanced;
        }

        /**
         * Invoke object.
         *
         * @param enhanced    the enhanced
         * @param method      the method
         * @param methodProxy the method proxy
         * @param args        the args
         * @return the object
         * @throws Throwable the throwable
         */
        @Override
        public Object invoke(Object enhanced, Method method, Method methodProxy, Object[] args) throws Throwable {
            final String methodName = method.getName();
            try {
                synchronized (lazyLoader) {
                    if (WRITE_REPLACE_METHOD.equals(methodName)) {
                        Object original;
                        if (constructorArgTypes.isEmpty()) {
                            original = objectFactory.create(type);
                        } else {
                            original = objectFactory.create(type, constructorArgTypes, constructorArgs);
                        }
                        PropertyCopier.copyBeanProperties(type, enhanced, original);
                        if (lazyLoader.size() > 0) {
                            return new JavassistSerialStateHolder(original, lazyLoader.getProperties(), objectFactory,
                                    constructorArgTypes, constructorArgs);
                        } else {
                            return original;
                        }
                    } else {
                        if (lazyLoader.size() > 0 && !FINALIZE_METHOD.equals(methodName)) {
                            if (aggressive || lazyLoadTriggerMethods.contains(methodName)) {
                                lazyLoader.loadAll();
                            } else if (PropertyNamer.isProperty(methodName)) {
                                final String property = PropertyNamer.methodToProperty(methodName);
                                if (lazyLoader.hasLoader(property)) {
                                    lazyLoader.load(property);
                                }
                            }
                        }
                    }
                }
                return methodProxy.invoke(enhanced, args);
            } catch (Throwable t) {
                throw new ExecutorException(t);
            }
        }
    }

    private static class EnhancedDeserializationProxyImpl extends AbstractEnhancedDeserializationProxy
            implements MethodHandler {

        private EnhancedDeserializationProxyImpl(Class<?> type,
                                                 Map<String, ResultLoaderMap.LoadPair> unloadedProperties, ObjectFactory objectFactory,
                                                 List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
            super(type, unloadedProperties, objectFactory, constructorArgTypes, constructorArgs);
        }

        /**
         * Create proxy object.
         *
         * @param target              the target
         * @param unloadedProperties  the unloaded properties
         * @param objectFactory       the object factory
         * @param constructorArgTypes the constructor arg types
         * @param constructorArgs     the constructor args
         * @return the object
         */
        public static Object createProxy(Object target, Map<String, ResultLoaderMap.LoadPair> unloadedProperties,
                                         ObjectFactory objectFactory, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
            final Class<?> type = target.getClass();
            EnhancedDeserializationProxyImpl callback = new EnhancedDeserializationProxyImpl(type, unloadedProperties,
                    objectFactory, constructorArgTypes, constructorArgs);
            Object enhanced = crateProxy(type, callback, constructorArgTypes, constructorArgs);
            PropertyCopier.copyBeanProperties(type, target, enhanced);
            return enhanced;
        }

        /**
         * Invoke object.
         *
         * @param enhanced    the enhanced
         * @param method      the method
         * @param methodProxy the method proxy
         * @param args        the args
         * @return the object
         * @throws Throwable the throwable
         */
        @Override
        public Object invoke(Object enhanced, Method method, Method methodProxy, Object[] args) throws Throwable {
            final Object o = super.invoke(enhanced, method, args);
            return (o instanceof AbstractSerialStateHolder) ? o : methodProxy.invoke(o, args);
        }

        /**
         * New serial state holder abstract serial state holder.
         *
         * @param userBean            the user bean
         * @param unloadedProperties  the unloaded properties
         * @param objectFactory       the object factory
         * @param constructorArgTypes the constructor arg types
         * @param constructorArgs     the constructor args
         * @return the abstract serial state holder
         */
        @Override
        protected AbstractSerialStateHolder newSerialStateHolder(Object userBean,
                                                                 Map<String, ResultLoaderMap.LoadPair> unloadedProperties, ObjectFactory objectFactory,
                                                                 List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
            return new JavassistSerialStateHolder(userBean, unloadedProperties, objectFactory, constructorArgTypes,
                    constructorArgs);
        }
    }
}
