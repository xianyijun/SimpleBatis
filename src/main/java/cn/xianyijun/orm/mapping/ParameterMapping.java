/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.mapping;

import cn.xianyijun.orm.core.ResultSetHandler;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.type.JdbcType;
import cn.xianyijun.orm.type.TypeHandlerRegistry;

/**
 * The type Parameter mapping.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ParameterMapping {
	private StatementHandler.Configuration configuration;

	private String property;
	private ParameterMode mode;
	private Integer numericScale;
	private String resultMapId;
	private String jdbcTypeName;
	private String expression;
	private ResultSetHandler.TypeHandler<?> typeHandler;
	private Class<?> javaType = Object.class;
	private JdbcType jdbcType;

	private ParameterMapping() {
	}

	/**
	 * The type Builder.
	 *
	 * @author xianyijun xianyijun0@gmail.com
	 */
	public static class Builder {
		private ParameterMapping parameterMapping = new ParameterMapping();

		/**
		 * Instantiates a new Builder.
		 *
		 * @param configuration the configuration
		 * @param property      the property
		 * @param typeHandler   the type handler
		 */
		public Builder(StatementHandler.Configuration configuration, String property, ResultSetHandler.TypeHandler<?> typeHandler) {
			parameterMapping.configuration = configuration;
			parameterMapping.property = property;
			parameterMapping.typeHandler = typeHandler;
			parameterMapping.mode = ParameterMode.IN;
		}

		/**
		 * Instantiates a new Builder.
		 *
		 * @param configuration the configuration
		 * @param property      the property
		 * @param javaType      the java type
		 */
		public Builder(StatementHandler.Configuration configuration, String property, Class<?> javaType) {
			parameterMapping.configuration = configuration;
			parameterMapping.property = property;
			parameterMapping.javaType = javaType;
			parameterMapping.mode = ParameterMode.IN;
		}

		/**
		 * Mode builder.
		 *
		 * @param mode the mode
		 * @return the builder
		 */
		public Builder mode(ParameterMode mode) {
			parameterMapping.mode = mode;
			return this;
		}

		/**
		 * Numeric scale builder.
		 *
		 * @param numericScale the numeric scale
		 * @return the builder
		 */
		public Builder numericScale(Integer numericScale) {
			parameterMapping.numericScale = numericScale;
			return this;
		}

		/**
		 * Result map id builder.
		 *
		 * @param resultMapId the result map id
		 * @return the builder
		 */
		public Builder resultMapId(String resultMapId) {
			parameterMapping.resultMapId = resultMapId;
			return this;
		}

		/**
		 * Jdbc type builder.
		 *
		 * @param jdbcType the jdbc type
		 * @return the builder
		 */
		public Builder jdbcType(JdbcType jdbcType) {
			parameterMapping.jdbcType = jdbcType;
			return this;
		}

		/**
		 * Java type builder.
		 *
		 * @param javaType the java type
		 * @return the builder
		 */
		public Builder javaType(Class<?> javaType) {
			parameterMapping.javaType = javaType;
			return this;
		}

		/**
		 * Jdbc type name builder.
		 *
		 * @param jdbcTypeName the jdbc type name
		 * @return the builder
		 */
		public Builder jdbcTypeName(String jdbcTypeName) {
			parameterMapping.jdbcTypeName = jdbcTypeName;
			return this;
		}

		/**
		 * Expression builder.
		 *
		 * @param expression the expression
		 * @return the builder
		 */
		public Builder expression(String expression) {
			parameterMapping.expression = expression;
			return this;
		}

		/**
		 * Type handler builder.
		 *
		 * @param typeHandler the type handler
		 * @return the builder
		 */
		public Builder typeHandler(ResultSetHandler.TypeHandler<?> typeHandler) {
			parameterMapping.typeHandler = typeHandler;
			return this;
		}

		/**
		 * Build parameter mapping.
		 *
		 * @return the parameter mapping
		 */
		public ParameterMapping build() {
			resolveTypeHandler();
			return parameterMapping;
		}

		private void resolveTypeHandler() {
			if (parameterMapping.typeHandler == null && parameterMapping.javaType != null) {
				StatementHandler.Configuration configuration = parameterMapping.configuration;
				TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
				parameterMapping.typeHandler = typeHandlerRegistry.getTypeHandler(parameterMapping.javaType);
			}
		}

	}

	/**
	 * Gets property.
	 *
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Gets mode.
	 *
	 * @return the mode
	 */
	public ParameterMode getMode() {
		return mode;
	}

	/**
	 * Gets numeric scale.
	 *
	 * @return the numeric scale
	 */
	public Integer getNumericScale() {
		return numericScale;
	}

	/**
	 * Gets result map id.
	 *
	 * @return the result map id
	 */
	public String getResultMapId() {
		return resultMapId;
	}

	/**
	 * Gets expression.
	 *
	 * @return the expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * Gets java type.
	 *
	 * @return the java type
	 */
	public Class<?> getJavaType() {
		return javaType;
	}

	/**
	 * Gets type handler.
	 *
	 * @return the type handler
	 */
	public ResultSetHandler.TypeHandler<?> getTypeHandler() {
		return typeHandler;
	}

	/**
	 * Gets jdbc type.
	 *
	 * @return the jdbc type
	 */
	public JdbcType getJdbcType() {
		return jdbcType;
	}

	/**
	 * To string string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("ParameterMapping{");
		//sb.append("configuration=").append(configuration); // configuration doesn't have a useful .toString()
		sb.append("property='").append(property).append('\'');
		sb.append(", mode=").append(mode);
		sb.append(", numericScale=").append(numericScale);
		//sb.append(", typeHandler=").append(typeHandler); // typeHandler also doesn't have a useful .toString()
		sb.append(", resultMapId='").append(resultMapId).append('\'');
		sb.append(", jdbcTypeName='").append(jdbcTypeName).append('\'');
		sb.append(", expression='").append(expression).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
