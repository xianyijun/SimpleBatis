/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import cn.xianyijun.orm.reflection.property.PropertyTokenizer;
import cn.xianyijun.orm.reflection.reflector.ReflectorFactory;
import cn.xianyijun.orm.reflection.wrapper.BeanWrapper;
import cn.xianyijun.orm.reflection.wrapper.CollectionWrapper;
import cn.xianyijun.orm.reflection.wrapper.MapWrapper;
import cn.xianyijun.orm.reflection.wrapper.ObjectWrapper;
import cn.xianyijun.orm.reflection.wrapper.ObjectWrapperFactory;

/**
 * The type Meta object.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class MetaObject {

	private Object originalObject;
	private ObjectWrapper objectWrapper;
	private ObjectFactory objectFactory;
	private ObjectWrapperFactory objectWrapperFactory;
	private ReflectorFactory reflectorFactory;

	private MetaObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory,
			ReflectorFactory reflectorFactory) {
		this.originalObject = object;
		this.objectFactory = objectFactory;
		this.objectWrapperFactory = objectWrapperFactory;
		this.reflectorFactory = reflectorFactory;

		if (object instanceof ObjectWrapper) {
			this.objectWrapper = (ObjectWrapper) object;
		} else if (objectWrapperFactory.hasWrapperFor(object)) {
			this.objectWrapper = objectWrapperFactory.getWrapperFor(this, object);
		} else if (object instanceof Map) {
			this.objectWrapper = new MapWrapper(this, (Map) object);
		} else if (object instanceof Collection) {
			this.objectWrapper = new CollectionWrapper(this, (Collection) object);
		} else {
			this.objectWrapper = new BeanWrapper(this, object);
		}
	}

	/**
	 * For object meta object.
	 *
	 * @param object               the object
	 * @param objectFactory        the object factory
	 * @param objectWrapperFactory the object wrapper factory
	 * @param reflectorFactory     the reflector factory
	 * @return the meta object
	 */
	public static MetaObject forObject(Object object, ObjectFactory objectFactory,
			ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
		if (object == null) {
			return SystemMetaObject.NULL_META_OBJECT;
		} else {
			return new MetaObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
		}
	}

	/**
	 * Gets object factory.
	 *
	 * @return the object factory
	 */
	public ObjectFactory getObjectFactory() {
		return objectFactory;
	}

	/**
	 * Gets object wrapper factory.
	 *
	 * @return the object wrapper factory
	 */
	public ObjectWrapperFactory getObjectWrapperFactory() {
		return objectWrapperFactory;
	}

	/**
	 * Gets reflector factory.
	 *
	 * @return the reflector factory
	 */
	public ReflectorFactory getReflectorFactory() {
		return reflectorFactory;
	}

	/**
	 * Gets original object.
	 *
	 * @return the original object
	 */
	public Object getOriginalObject() {
		return originalObject;
	}

	/**
	 * Find property string.
	 *
	 * @param propName            the prop name
	 * @param useCamelCaseMapping the use camel case mapping
	 * @return the string
	 */
	public String findProperty(String propName, boolean useCamelCaseMapping) {
		return objectWrapper.findProperty(propName, useCamelCaseMapping);
	}

	/**
	 * Get getter names string [ ].
	 *
	 * @return the string [ ]
	 */
	public String[] getGetterNames() {
		return objectWrapper.getGetterNames();
	}

	/**
	 * Get setter names string [ ].
	 *
	 * @return the string [ ]
	 */
	public String[] getSetterNames() {
		return objectWrapper.getSetterNames();
	}

	/**
	 * Gets setter type.
	 *
	 * @param name the name
	 * @return the setter type
	 */
	public Class<?> getSetterType(String name) {
		return objectWrapper.getSetterType(name);
	}

	/**
	 * Gets getter type.
	 *
	 * @param name the name
	 * @return the getter type
	 */
	public Class<?> getGetterType(String name) {
		return objectWrapper.getGetterType(name);
	}

	/**
	 * Has setter boolean.
	 *
	 * @param name the name
	 * @return the boolean
	 */
	public boolean hasSetter(String name) {
		return objectWrapper.hasSetter(name);
	}

	/**
	 * Has getter boolean.
	 *
	 * @param name the name
	 * @return the boolean
	 */
	public boolean hasGetter(String name) {
		return objectWrapper.hasGetter(name);
	}

	/**
	 * Gets value.
	 *
	 * @param name the name
	 * @return the value
	 */
	public Object getValue(String name) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
			if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
				return null;
			} else {
				return metaValue.getValue(prop.getChildren());
			}
		} else {
			return objectWrapper.get(prop);
		}
	}

	/**
	 * Sets value.
	 *
	 * @param name  the name
	 * @param value the value
	 */
	public void setValue(String name, Object value) {
		PropertyTokenizer prop = new PropertyTokenizer(name);
		if (prop.hasNext()) {
			MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
			if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
				if (value == null && prop.getChildren() != null) {
					// don't instantiate child path if value is null
					return;
				} else {
					metaValue = objectWrapper.instantiatePropertyValue(name, prop, objectFactory);
				}
			}
			metaValue.setValue(prop.getChildren(), value);
		} else {
			objectWrapper.set(prop, value);
		}
	}

	/**
	 * Meta object for property meta object.
	 *
	 * @param name the name
	 * @return the meta object
	 */
	public MetaObject metaObjectForProperty(String name) {
		Object value = getValue(name);
		return MetaObject.forObject(value, objectFactory, objectWrapperFactory, reflectorFactory);
	}

	/**
	 * Gets object wrapper.
	 *
	 * @return the object wrapper
	 */
	public ObjectWrapper getObjectWrapper() {
		return objectWrapper;
	}

	/**
	 * Is collection boolean.
	 *
	 * @return the boolean
	 */
	public boolean isCollection() {
		return objectWrapper.isCollection();
	}

	/**
	 * Add.
	 *
	 * @param element the element
	 */
	public void add(Object element) {
		objectWrapper.add(element);
	}

	/**
	 * Add all.
	 *
	 * @param <E>  the type parameter
	 * @param list the list
	 */
	public <E> void addAll(List<E> list) {
		objectWrapper.addAll(list);
	}

}
