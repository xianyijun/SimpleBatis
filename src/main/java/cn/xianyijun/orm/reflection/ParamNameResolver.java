/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection;

import cn.xianyijun.orm.annotation.Param;
import cn.xianyijun.orm.binding.MapperMethod.ParamMap;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.exception.ReflectionException;
import cn.xianyijun.orm.executor.result.ResultHandler;
import cn.xianyijun.orm.io.Resources;
import cn.xianyijun.orm.session.RowBounds;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The type Param name resolver.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ParamNameResolver {
	private static final String GENERIC_NAME_PREFIX = "param";
	private static final String PARAMETER_CLASS = "java.lang.reflect.Parameter";
	private static Method GET_NAME = null;
	private static Method GET_PARAMS = null;

	static {
		try {
			Class<?> paramClass = Resources.classForName(PARAMETER_CLASS);
			GET_NAME = paramClass.getMethod("getName");
			GET_PARAMS = Method.class.getMethod("getParameters");
		} catch (Exception e) {
		}
	}
	private final SortedMap<Integer, String> names;

	private boolean hasParamAnnotation;

	/**
	 * Instantiates a new Param name resolver.
	 *
	 * @param config the config
	 * @param method the method
	 */
	public ParamNameResolver(StatementHandler.Configuration config, Method method) {
		final Class<?>[] paramTypes = method.getParameterTypes();
		final Annotation[][] paramAnnotations = method.getParameterAnnotations();
		final SortedMap<Integer, String> map = new TreeMap<Integer, String>();
		int paramCount = paramAnnotations.length;
		// get names from @Param annotations
		for (int paramIndex = 0; paramIndex < paramCount; paramIndex++) {
			if (isSpecialParameter(paramTypes[paramIndex])) {
				// skip special parameters
				continue;
			}
			String name = null;
			for (Annotation annotation : paramAnnotations[paramIndex]) {
				if (annotation instanceof Param) {
					hasParamAnnotation = true;
					name = ((Param) annotation).value();
					break;
				}
			}
			if (name == null) {
				if (config.isUseActualParamName()) {
					name = getActualParamName(method, paramIndex);
				}
				if (name == null) {
					// use the parameter index as the name ("0", "1", ...)
					// gcode issue #71
					name = String.valueOf(map.size());
				}
			}
			map.put(paramIndex, name);
		}
		names = Collections.unmodifiableSortedMap(map);
	}

	private static boolean isSpecialParameter(Class<?> clazz) {
		return RowBounds.class.isAssignableFrom(clazz) || ResultHandler.class.isAssignableFrom(clazz);
	}

	/**
	 * Get names string [ ].
	 *
	 * @return the string [ ]
	 */
	public String[] getNames() {
		return names.values().toArray(new String[0]);
	}

	private String getActualParamName(Method method, int paramIndex) {
		if (GET_PARAMS == null) {
			return null;
		}
		try {
			Object[] params = (Object[]) GET_PARAMS.invoke(method);
			return (String) GET_NAME.invoke(params[paramIndex]);
		} catch (Exception e) {
			throw new ReflectionException("Error occurred when invoking Method#getParameters().", e);
		}
	}

	/**
	 * Gets named params.
	 *
	 * @param args the args
	 * @return the named params
	 */
	public Object getNamedParams(Object[] args) {
		final int paramCount = names.size();
		if (args == null || paramCount == 0) {
			return null;
		} else if (!hasParamAnnotation && paramCount == 1) {
			return args[names.firstKey()];
		} else {
			final Map<String, Object> param = new ParamMap<Object>();
			int i = 0;
			for (Map.Entry<Integer, String> entry : names.entrySet()) {
				param.put(entry.getValue(), args[entry.getKey()]);
				// add generic param names (param1, param2, ...)
				final String genericParamName = GENERIC_NAME_PREFIX + String.valueOf(i + 1);
				// ensure not to overwrite parameter named with @Param
				if (!names.containsValue(genericParamName)) {
					param.put(genericParamName, args[entry.getKey()]);
				}
				i++;
			}
			return param;
		}
	}
}
