/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.loader;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.xianyijun.orm.exception.ExecutorException;
import cn.xianyijun.orm.reflection.ObjectFactory;
import cn.xianyijun.orm.reflection.property.PropertyCopier;
import cn.xianyijun.orm.reflection.property.PropertyNamer;

/**
 * The type Abstract enhanced deserialization proxy.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public abstract class AbstractEnhancedDeserializationProxy {

	/**
	 * The constant FINALIZE_METHOD.
	 */
	protected static final String FINALIZE_METHOD = "finalize";
	/**
	 * The constant WRITE_REPLACE_METHOD.
	 */
	protected static final String WRITE_REPLACE_METHOD = "writeReplace";
	private Class<?> type;
	private Map<String, ResultLoaderMap.LoadPair> unloadedProperties;
	private ObjectFactory objectFactory;
	private List<Class<?>> constructorArgTypes;
	private List<Object> constructorArgs;
	private final Object reloadingPropertyLock;
	private boolean reloadingProperty;

	/**
	 * Instantiates a new Abstract enhanced deserialization proxy.
	 *
	 * @param type                the type
	 * @param unloadedProperties  the unloaded properties
	 * @param objectFactory       the object factory
	 * @param constructorArgTypes the constructor arg types
	 * @param constructorArgs     the constructor args
	 */
	protected AbstractEnhancedDeserializationProxy(Class<?> type,
			Map<String, ResultLoaderMap.LoadPair> unloadedProperties, ObjectFactory objectFactory,
			List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
		this.type = type;
		this.unloadedProperties = unloadedProperties;
		this.objectFactory = objectFactory;
		this.constructorArgTypes = constructorArgTypes;
		this.constructorArgs = constructorArgs;
		this.reloadingPropertyLock = new Object();
		this.reloadingProperty = false;
	}

	/**
	 * Invoke object.
	 *
	 * @param enhanced the enhanced
	 * @param method   the method
	 * @param args     the args
	 * @return the object
	 * @throws Throwable the throwable
	 */
	public final Object invoke(Object enhanced, Method method, Object[] args) throws Throwable {
		final String methodName = method.getName();
		try {
			if (WRITE_REPLACE_METHOD.equals(methodName)) {
				final Object original;
				if (constructorArgTypes.isEmpty()) {
					original = objectFactory.create(type);
				} else {
					original = objectFactory.create(type, constructorArgTypes, constructorArgs);
				}

				PropertyCopier.copyBeanProperties(type, enhanced, original);
				return this.newSerialStateHolder(original, unloadedProperties, objectFactory, constructorArgTypes,
						constructorArgs);
			} else {
				synchronized (this.reloadingPropertyLock) {
					if (!FINALIZE_METHOD.equals(methodName) && PropertyNamer.isProperty(methodName)
							&& !reloadingProperty) {
						final String property = PropertyNamer.methodToProperty(methodName);
						final String propertyKey = property.toUpperCase(Locale.ENGLISH);
						if (unloadedProperties.containsKey(propertyKey)) {
							final ResultLoaderMap.LoadPair loadPair = unloadedProperties.remove(propertyKey);
							if (loadPair != null) {
								try {
									reloadingProperty = true;
									loadPair.load(enhanced);
								} finally {
									reloadingProperty = false;
								}
							} else {
								throw new ExecutorException(
										"An attempt has been made to read a not loaded lazy property '" + property
												+ "' of a disconnected object");
							}
						}
					}

					return enhanced;
				}
			}
		} catch (Throwable t) {
			throw new ExecutorException(t);
		}
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
	protected abstract AbstractSerialStateHolder newSerialStateHolder(Object userBean,
			Map<String, ResultLoaderMap.LoadPair> unloadedProperties, ObjectFactory objectFactory,
			List<Class<?>> constructorArgTypes, List<Object> constructorArgs);

}
