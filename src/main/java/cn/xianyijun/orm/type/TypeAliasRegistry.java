/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.type;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import cn.xianyijun.orm.exception.TypeException;
import cn.xianyijun.orm.io.Resources;
import cn.xianyijun.orm.util.ResolverUtil;

/**
 * The type Type alias registry.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class TypeAliasRegistry {

	private final Map<String, Class<?>> TYPE_ALIASES = new HashMap<String, Class<?>>();

	/**
	 * Instantiates a new Type alias registry.
	 */
	public TypeAliasRegistry() {
		registerAlias("string", String.class);

		registerAlias("byte", Byte.class);
		registerAlias("long", Long.class);
		registerAlias("short", Short.class);
		registerAlias("int", Integer.class);
		registerAlias("integer", Integer.class);
		registerAlias("double", Double.class);
		registerAlias("float", Float.class);
		registerAlias("boolean", Boolean.class);

		registerAlias("byte[]", Byte[].class);
		registerAlias("long[]", Long[].class);
		registerAlias("short[]", Short[].class);
		registerAlias("int[]", Integer[].class);
		registerAlias("integer[]", Integer[].class);
		registerAlias("double[]", Double[].class);
		registerAlias("float[]", Float[].class);
		registerAlias("boolean[]", Boolean[].class);

		registerAlias("ResultSet", ResultSet.class);
	}

	/**
	 * Resolve alias class.
	 *
	 * @param <T>    the type parameter
	 * @param string the string
	 * @return the class
	 */
	@SuppressWarnings("unchecked")
	public <T> Class<T> resolveAlias(String string) {
		try {
			if (string == null) {
				return null;
			}
			String key = string.toLowerCase(Locale.ENGLISH);
			Class<T> value;
			if (TYPE_ALIASES.containsKey(key)) {
				value = (Class<T>) TYPE_ALIASES.get(key);
			} else {
				value = (Class<T>) Resources.classForName(string);
			}
			return value;
		} catch (ClassNotFoundException e) {
			throw new TypeException("Could not resolve type alias '" + string + "'.  Cause: " + e, e);
		}
	}

	/**
	 * Register aliases.
	 *
	 * @param packageName the package name
	 */
	public void registerAliases(String packageName) {
		registerAliases(packageName, Object.class);
	}

	/**
	 * Register aliases.
	 *
	 * @param packageName the package name
	 * @param superType   the super type
	 */
	public void registerAliases(String packageName, Class<?> superType) {
		ResolverUtil<Class<?>> resolverUtil = new ResolverUtil<Class<?>>();
		resolverUtil.find(new ResolverUtil.IsA(superType), packageName);
		Set<Class<? extends Class<?>>> typeSet = resolverUtil.getClasses();
		for (Class<?> type : typeSet) {
			if (!type.isAnonymousClass() && !type.isInterface() && !type.isMemberClass()) {
				registerAlias(type);
			}
		}
	}

	/**
	 * Register alias.
	 *
	 * @param type the type
	 */
	public void registerAlias(Class<?> type) {
		String alias = type.getSimpleName();
		registerAlias(alias, type);
	}

	/**
	 * Register alias.
	 *
	 * @param alias the alias
	 * @param value the value
	 */
	public void registerAlias(String alias, Class<?> value) {
		if (alias == null) {
			throw new TypeException("The parameter alias cannot be null");
		}
		String key = alias.toLowerCase(Locale.ENGLISH);
		if (TYPE_ALIASES.containsKey(key) && TYPE_ALIASES.get(key) != null && !TYPE_ALIASES.get(key).equals(value)) {
			throw new TypeException("The alias '" + alias + "' is already mapped to the value '"
					+ TYPE_ALIASES.get(key).getName() + "'.");
		}
		TYPE_ALIASES.put(key, value);
	}

	/**
	 * Register alias.
	 *
	 * @param alias the alias
	 * @param value the value
	 */
	public void registerAlias(String alias, String value) {
		try {
			registerAlias(alias, Resources.classForName(value));
		} catch (ClassNotFoundException e) {
			throw new TypeException("Error registering type alias " + alias + " for " + value + ". Cause: " + e, e);
		}
	}

	/**
	 * Gets type aliases.
	 *
	 * @return the type aliases
	 */
	public Map<String, Class<?>> getTypeAliases() {
		return Collections.unmodifiableMap(TYPE_ALIASES);
	}

}
