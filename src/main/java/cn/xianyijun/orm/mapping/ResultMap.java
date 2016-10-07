/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.mapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import cn.xianyijun.orm.core.StatementHandler;

/**
 * The type Result map.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ResultMap {
	private String id;
	private Class<?> type;
	private List<ResultMapping> resultMappings;
	private List<ResultMapping> idResultMappings;
	private List<ResultMapping> constructorResultMappings;
	private List<ResultMapping> propertyResultMappings;
	private Set<String> mappedColumns;
	private boolean hasNestedResultMaps;
	private boolean hasNestedQueries;
	private Boolean autoMapping;

	private ResultMap() {
	}

	/**
	 * Has nested result maps boolean.
	 *
	 * @return the boolean
	 */
	public boolean hasNestedResultMaps() {
		return hasNestedResultMaps;
	}

	/**
	 * Has nested queries boolean.
	 *
	 * @return the boolean
	 */
	public boolean hasNestedQueries() {
		return hasNestedQueries;
	}

	/**
	 * The type Builder.
	 *
	 * @author xianyijun xianyijun0@gmail.com
	 */
	public static class Builder {
		private ResultMap resultMap = new ResultMap();

		/**
		 * Instantiates a new Builder.
		 *
		 * @param configuration  the configuration
		 * @param id             the id
		 * @param type           the type
		 * @param resultMappings the result mappings
		 */
		public Builder(StatementHandler.Configuration configuration, String id, Class<?> type, List<ResultMapping> resultMappings) {
			this(configuration, id, type, resultMappings, null);
		}

		/**
		 * Instantiates a new Builder.
		 *
		 * @param configuration  the configuration
		 * @param id             the id
		 * @param type           the type
		 * @param resultMappings the result mappings
		 * @param autoMapping    the auto mapping
		 */
		public Builder(StatementHandler.Configuration configuration, String id, Class<?> type, List<ResultMapping> resultMappings,
					   Boolean autoMapping) {
			resultMap.id = id;
			resultMap.type = type;
			resultMap.resultMappings = resultMappings;
			resultMap.autoMapping = autoMapping;
		}

		/**
		 * Type class.
		 *
		 * @return the class
		 */
		public Class<?> type() {
			return resultMap.type;
		}

		/**
		 * Build result map.
		 *
		 * @return the result map
		 */
		public ResultMap build() {
			if (resultMap.id == null) {
				throw new IllegalArgumentException("ResultMaps must have an id");
			}
			resultMap.mappedColumns = new HashSet<String>();
			resultMap.idResultMappings = new ArrayList<ResultMapping>();
			resultMap.constructorResultMappings = new ArrayList<ResultMapping>();
			resultMap.propertyResultMappings = new ArrayList<ResultMapping>();
			for (ResultMapping resultMapping : resultMap.resultMappings) {
				resultMap.hasNestedQueries = resultMap.hasNestedQueries || resultMapping.getNestedQueryId() != null;
				resultMap.hasNestedResultMaps = resultMap.hasNestedResultMaps
						|| (resultMapping.getNestedResultMapId() != null && resultMapping.getResultSet() == null);
				final String column = resultMapping.getColumn();
				if (column != null) {
					resultMap.mappedColumns.add(column.toUpperCase(Locale.ENGLISH));
				} else if (resultMapping.isCompositeResult()) {
					for (ResultMapping compositeResultMapping : resultMapping.getComposites()) {
						final String compositeColumn = compositeResultMapping.getColumn();
						if (compositeColumn != null) {
							resultMap.mappedColumns.add(compositeColumn.toUpperCase(Locale.ENGLISH));
						}
					}
				}
				if (resultMapping.getFlags().contains(ResultFlag.CONSTRUCTOR)) {
					resultMap.constructorResultMappings.add(resultMapping);
				} else {
					resultMap.propertyResultMappings.add(resultMapping);
				}
				if (resultMapping.getFlags().contains(ResultFlag.ID)) {
					resultMap.idResultMappings.add(resultMapping);
				}
			}
			if (resultMap.idResultMappings.isEmpty()) {
				resultMap.idResultMappings.addAll(resultMap.resultMappings);
			}
			// lock down collections
			resultMap.resultMappings = Collections.unmodifiableList(resultMap.resultMappings);
			resultMap.idResultMappings = Collections.unmodifiableList(resultMap.idResultMappings);
			resultMap.constructorResultMappings = Collections.unmodifiableList(resultMap.constructorResultMappings);
			resultMap.propertyResultMappings = Collections.unmodifiableList(resultMap.propertyResultMappings);
			resultMap.mappedColumns = Collections.unmodifiableSet(resultMap.mappedColumns);
			return resultMap;
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
	 * Gets result mappings.
	 *
	 * @return the result mappings
	 */
	public List<ResultMapping> getResultMappings() {
		return resultMappings;
	}

	/**
	 * Gets constructor result mappings.
	 *
	 * @return the constructor result mappings
	 */
	public List<ResultMapping> getConstructorResultMappings() {
		return constructorResultMappings;
	}

	/**
	 * Gets property result mappings.
	 *
	 * @return the property result mappings
	 */
	public List<ResultMapping> getPropertyResultMappings() {
		return propertyResultMappings;
	}

	/**
	 * Gets id result mappings.
	 *
	 * @return the id result mappings
	 */
	public List<ResultMapping> getIdResultMappings() {
		return idResultMappings;
	}

	/**
	 * Gets mapped columns.
	 *
	 * @return the mapped columns
	 */
	public Set<String> getMappedColumns() {
		return mappedColumns;
	}

	/**
	 * Force nested result maps.
	 */
	public void forceNestedResultMaps() {
		hasNestedResultMaps = true;
	}

	/**
	 * Gets auto mapping.
	 *
	 * @return the auto mapping
	 */
	public Boolean getAutoMapping() {
		return autoMapping;
	}

}
