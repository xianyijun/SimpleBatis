/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import cn.xianyijun.orm.exception.ReflectionException;

/**
 * The type Default object factory.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class DefaultObjectFactory implements ObjectFactory {

	/**
	 * Create t.
	 *
	 * @param <T>  the type parameter
	 * @param type the type
	 * @return the t
	 */
	@Override
	public <T> T create(Class<T> type) {
		return create(type, null, null);
	}

	/**
	 * Create t.
	 *
	 * @param <T>                 the type parameter
	 * @param type                the type
	 * @param constructorArgTypes the constructor arg types
	 * @param constructorArgs     the constructor args
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
		Class<?> classToCreate = resolveInterface(type);
		return (T) instantiateClass(classToCreate, constructorArgTypes, constructorArgs);
	}

	/**
	 * Sets properties.
	 *
	 * @param properties the properties
	 */
	@Override
	public void setProperties(Properties properties) {
	}

	/**
	 * Instantiate class t.
	 *
	 * @param <T>                 the type parameter
	 * @param type                the type
	 * @param constructorArgTypes the constructor arg types
	 * @param constructorArgs     the constructor args
	 * @return the t
	 */
	<T> T instantiateClass(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
		try {
			Constructor<T> constructor;
			if (constructorArgTypes == null || constructorArgs == null) {
				constructor = type.getDeclaredConstructor();
				if (!constructor.isAccessible()) {
					constructor.setAccessible(true);
				}
				return constructor.newInstance();
			}
			constructor = type
					.getDeclaredConstructor(constructorArgTypes.toArray(new Class[constructorArgTypes.size()]));
			if (!constructor.isAccessible()) {
				constructor.setAccessible(true);
			}
			return constructor.newInstance(constructorArgs.toArray(new Object[constructorArgs.size()]));
		} catch (Exception e) {
			StringBuilder argTypes = new StringBuilder();
			if (constructorArgTypes != null && !constructorArgTypes.isEmpty()) {
				for (Class<?> argType : constructorArgTypes) {
					argTypes.append(argType.getSimpleName());
					argTypes.append(",");
				}
				argTypes.deleteCharAt(argTypes.length() - 1); // remove trailing ,
			}
			StringBuilder argValues = new StringBuilder();
			if (constructorArgs != null && !constructorArgs.isEmpty()) {
				for (Object argValue : constructorArgs) {
					argValues.append(String.valueOf(argValue));
					argValues.append(",");
				}
				argValues.deleteCharAt(argValues.length() - 1); // remove trailing ,
			}
			throw new ReflectionException("Error instantiating " + type + " with invalid types (" + argTypes
					+ ") or values (" + argValues + "). Cause: " + e, e);
		}
	}

	/**
	 * Resolve interface class.
	 *
	 * @param type the type
	 * @return the class
	 */
	protected Class<?> resolveInterface(Class<?> type) {
		Class<?> classToCreate;
		if (type == List.class || type == Collection.class || type == Iterable.class) {
			classToCreate = ArrayList.class;
		} else if (type == Map.class) {
			classToCreate = HashMap.class;
		} else if (type == SortedSet.class) { // issue #510 Collections Support
			classToCreate = TreeSet.class;
		} else if (type == Set.class) {
			classToCreate = HashSet.class;
		} else {
			classToCreate = type;
		}
		return classToCreate;
	}

	/**
	 * Is collection boolean.
	 *
	 * @param <T>  the type parameter
	 * @param type the type
	 * @return the boolean
	 */
	@Override
	public <T> boolean isCollection(Class<T> type) {
		return Collection.class.isAssignableFrom(type);
	}

}
