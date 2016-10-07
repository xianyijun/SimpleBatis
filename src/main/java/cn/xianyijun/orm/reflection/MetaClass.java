/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection;

import cn.xianyijun.orm.reflection.invoker.GetFieldInvoker;
import cn.xianyijun.orm.reflection.invoker.Invoker;
import cn.xianyijun.orm.reflection.invoker.MethodInvoker;
import cn.xianyijun.orm.reflection.property.PropertyTokenizer;
import cn.xianyijun.orm.reflection.reflector.Reflector;
import cn.xianyijun.orm.reflection.reflector.ReflectorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * The type Meta class.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class MetaClass {
	private static final Logger logger = LoggerFactory.getLogger(MetaClass.class);
	private ReflectorFactory reflectorFactory;
	private Reflector reflector;

	private MetaClass(Class<?> type, ReflectorFactory reflectorFactory) {
		this.reflectorFactory = reflectorFactory;
		this.reflector = reflectorFactory.findForClass(type);
	}

	/**
	 * For class meta class.
	 *
	 * @param type             the type
	 * @param reflectorFactory the reflector factory
	 * @return the meta class
	 */
	public static MetaClass forClass(Class<?> type, ReflectorFactory reflectorFactory) {
		return new MetaClass(type, reflectorFactory);
	}

	/**
	 * Meta class for property meta class.
	 *
	 * @param name the name
	 * @return the meta class
	 */
	public MetaClass metaClassForProperty(String name) {
		Class<?> propType = reflector.getGetterType(name);
		return MetaClass.forClass(propType, reflectorFactory);
	}

	/**
	 * Find property string.
	 *
	 * @param name the name
	 * @return the string
	 */
	public String findProperty(String name) {
		StringBuilder prop = buildProperty(name, new StringBuilder());
		return prop.length() > 0 ? prop.toString() : null;
	}

	/**
	 * Find property string.
	 *
	 * @param name                the name
	 * @param useCamelCaseMapping the use camel case mapping
	 * @return the string
	 */
	public String findProperty(String name, boolean useCamelCaseMapping) {
		if (useCamelCaseMapping) {
			name = name.replace("_", "");
		}
		return findProperty(name);
	}

	/**
	 * Get getter names string [ ].
	 *
	 * @return the string [ ]
	 */
	public String[] getGetterNames() {
		return reflector.getGetablePropertyNames();
	}

	/**
	 * Get setter names string [ ].
	 *
	 * @return the string [ ]
	 */
	public String[] getSetterNames() {
		return reflector.getSetablePropertyNames();
	}

	/**
	 * Gets setter type.
	 *
	 * @param name the name
	 * @return the setter type
	 */
	public Class<?> getSetterType(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaClass metaProp = metaClassForProperty(prop.getName());
			return metaProp.getSetterType(prop.getChildren());
		} else {
			return reflector.getSetterType(prop.getName());
		}
	}

	/**
	 * Gets getter type.
	 *
	 * @param name the name
	 * @return the getter type
	 */
	public Class<?> getGetterType(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaClass metaProp = metaClassForProperty(prop);
			return metaProp.getGetterType(prop.getChildren());
		}
		return getGetterType(prop);
	}

	private MetaClass metaClassForProperty(PropertyTokenizer prop) {
		Class<?> propType = getGetterType(prop);
		return MetaClass.forClass(propType, reflectorFactory);
	}

	private Class<?> getGetterType(PropertyTokenizer prop) {
		Class<?> type = reflector.getGetterType(prop.getName());
		if (prop.getIndex() != null && Collection.class.isAssignableFrom(type)) {
			Type returnType = getGenericGetterType(prop.getName());
			if (returnType instanceof ParameterizedType) {
				Type[] actualTypeArguments = ((ParameterizedType) returnType).getActualTypeArguments();
				if (actualTypeArguments != null && actualTypeArguments.length == 1) {
					returnType = actualTypeArguments[0];
					if (returnType instanceof Class) {
						type = (Class<?>) returnType;
					} else if (returnType instanceof ParameterizedType) {
						type = (Class<?>) ((ParameterizedType) returnType).getRawType();
					}
				}
			}
		}
		return type;
	}

	private Type getGenericGetterType(String propertyName) {
		try {
			Invoker invoker = reflector.getGetInvoker(propertyName);
			if (invoker instanceof MethodInvoker) {
				Field _method = MethodInvoker.class.getDeclaredField("method");
				_method.setAccessible(true);
				Method method = (Method) _method.get(invoker);
				return TypeParameterResolver.resolveReturnType(method, reflector.getType());
			} else if (invoker instanceof GetFieldInvoker) {
				Field _field = GetFieldInvoker.class.getDeclaredField("field");
				_field.setAccessible(true);
				Field field = (Field) _field.get(invoker);
				return TypeParameterResolver.resolveFieldType(field, reflector.getType());
			}
		} catch (NoSuchFieldException e) {
		} catch (IllegalAccessException e) {
		}
		return null;
	}

	/**
	 * Has setter boolean.
	 *
	 * @param name the name
	 * @return the boolean
	 */
	public boolean hasSetter(String name) {
		logger.debug(" the mataClass name : " + name);
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			if (reflector.hasSetter(prop.getName())) {
				MetaClass metaProp = metaClassForProperty(prop.getName());
				return metaProp.hasSetter(prop.getChildren());
			} else {
				return false;
			}
		} else {
			return reflector.hasSetter(prop.getName());
		}
	}

	/**
	 * Has getter boolean.
	 *
	 * @param name the name
	 * @return the boolean
	 */
	public boolean hasGetter(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			if (reflector.hasGetter(prop.getName())) {
				MetaClass metaProp = metaClassForProperty(prop);
				return metaProp.hasGetter(prop.getChildren());
			} else {
				return false;
			}
		} else {
			return reflector.hasGetter(prop.getName());
		}
	}

	/**
	 * Gets get invoker.
	 *
	 * @param name the name
	 * @return the get invoker
	 */
	public Invoker getGetInvoker(String name) {
		return reflector.getGetInvoker(name);
	}

	/**
	 * Gets set invoker.
	 *
	 * @param name the name
	 * @return the set invoker
	 */
	public Invoker getSetInvoker(String name) {
		return reflector.getSetInvoker(name);
	}

	private StringBuilder buildProperty(String name, StringBuilder builder) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			String propertyName = reflector.findPropertyName(prop.getName());
			if (propertyName != null) {
				builder.append(propertyName);
				builder.append(".");
				MetaClass metaProp = metaClassForProperty(propertyName);
				metaProp.buildProperty(prop.getChildren(), builder);
			}
		} else {
			String propertyName = reflector.findPropertyName(name);
			if (propertyName != null) {
				builder.append(propertyName);
			}
		}
		return builder;
	}

	/**
	 * Has default constructor boolean.
	 *
	 * @return the boolean
	 */
	public boolean hasDefaultConstructor() {
		return reflector.hasDefaultConstructor();
	}
}
