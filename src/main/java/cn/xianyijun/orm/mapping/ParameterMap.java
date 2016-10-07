/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.mapping;

import java.util.Collections;
import java.util.List;

/**
 * The type Parameter map.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ParameterMap {
	private String id;
	private Class<?> type;
	private List<ParameterMapping> parameterMappings;

	private ParameterMap() {
	}

	/**
	 * The type Builder.
	 *
	 * @author xianyijun xianyijun0@gmail.com
	 */
	public static class Builder {
		private ParameterMap parameterMap = new ParameterMap();

		/**
		 * Instantiates a new Builder.
		 *
		 * @param id                the id
		 * @param type              the type
		 * @param parameterMappings the parameter mappings
		 */
		public Builder(String id, Class<?> type,
				List<ParameterMapping> parameterMappings) {
			parameterMap.id = id;
			parameterMap.type = type;
			parameterMap.parameterMappings = parameterMappings;
		}

		/**
		 * Type class.
		 *
		 * @return the class
		 */
		public Class<?> type() {
			return parameterMap.type;
		}

		/**
		 * Build parameter map.
		 *
		 * @return the parameter map
		 */
		public ParameterMap build() {
			parameterMap.parameterMappings = Collections.unmodifiableList(parameterMap.parameterMappings);
			return parameterMap;
		}
	}

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets type.
	 *
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * Gets parameter mappings.
	 *
	 * @return the parameter mappings
	 */
	public List<ParameterMapping> getParameterMappings() {
		return parameterMappings;
	}

}
