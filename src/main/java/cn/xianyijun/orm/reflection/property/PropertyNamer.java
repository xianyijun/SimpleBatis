/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection.property;

import java.util.Locale;

import cn.xianyijun.orm.exception.ReflectionException;

/**
 * The type Property namer.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public final class PropertyNamer {

	private PropertyNamer() {
	}

	/**
	 * Method to property string.
	 *
	 * @param name the name
	 * @return the string
	 */
	public static String methodToProperty(String name) {
		if (name.startsWith("is")) {
			name = name.substring(2);
		} else if (name.startsWith("get") || name.startsWith("set")) {
			name = name.substring(3);
		} else {
			throw new ReflectionException(
					"Error parsing property name '" + name + "'.  Didn't start with 'is', 'get' or 'set'.");
		}

		if (name.length() == 1 || (name.length() > 1 && !Character.isUpperCase(name.charAt(1)))) {
			name = name.substring(0, 1).toLowerCase(Locale.ENGLISH) + name.substring(1);
		}

		return name;
	}

	/**
	 * Is property boolean.
	 *
	 * @param name the name
	 * @return the boolean
	 */
	public static boolean isProperty(String name) {
		return name.startsWith("get") || name.startsWith("set") || name.startsWith("is");
	}

	/**
	 * Is getter boolean.
	 *
	 * @param name the name
	 * @return the boolean
	 */
	public static boolean isGetter(String name) {
		return name.startsWith("get") || name.startsWith("is");
	}

	/**
	 * Is setter boolean.
	 *
	 * @param name the name
	 * @return the boolean
	 */
	public static boolean isSetter(String name) {
		return name.startsWith("set");
	}

}
