/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor;

import java.lang.reflect.Array;
import java.util.List;

import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.exception.ExecutorException;
import cn.xianyijun.orm.reflection.MetaObject;
import cn.xianyijun.orm.reflection.ObjectFactory;

/**
 * The type Result extractor.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ResultExtractor {
	private final StatementHandler.Configuration configuration;
	private final ObjectFactory objectFactory;

	/**
	 * Instantiates a new Result extractor.
	 *
	 * @param configuration the configuration
	 * @param objectFactory the object factory
	 */
	public ResultExtractor(StatementHandler.Configuration configuration, ObjectFactory objectFactory) {
		this.configuration = configuration;
		this.objectFactory = objectFactory;
	}

	/**
	 * Extract object from list object.
	 *
	 * @param list       the list
	 * @param targetType the target type
	 * @return the object
	 */
	public Object extractObjectFromList(List<Object> list, Class<?> targetType) {
		Object value = null;
		if (targetType != null && targetType.isAssignableFrom(list.getClass())) {
			value = list;
		} else if (targetType != null && objectFactory.isCollection(targetType)) {
			value = objectFactory.create(targetType);
			MetaObject metaObject = configuration.newMetaObject(value);
			metaObject.addAll(list);
		} else if (targetType != null && targetType.isArray()) {
			Class<?> arrayComponentType = targetType.getComponentType();
			Object array = Array.newInstance(arrayComponentType, list.size());
			if (arrayComponentType.isPrimitive()) {
				for (int i = 0; i < list.size(); i++) {
					Array.set(array, i, list.get(i));
				}
				value = array;
			} else {
				value = list.toArray((Object[]) array);
			}
		} else {
			if (list != null && list.size() > 1) {
				throw new ExecutorException(
						"Statement returned more than one row, where no more than one was expected.");
			} else if (list != null && list.size() == 1) {
				value = list.get(0);
			}
		}
		return value;
	}
}
