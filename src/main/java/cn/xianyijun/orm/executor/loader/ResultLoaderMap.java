/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.loader;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.cursor.Cursor;
import cn.xianyijun.orm.exception.ExecutorException;
import cn.xianyijun.orm.executor.BaseExecutor;
import cn.xianyijun.orm.executor.result.ResultHandler;
import cn.xianyijun.orm.core.MappedStatement;
import cn.xianyijun.orm.reflection.MetaObject;
import cn.xianyijun.orm.session.RowBounds;

/**
 * The type Result loader map.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ResultLoaderMap {

	private final Map<String, LoadPair> loaderMap = new HashMap<String, LoadPair>();

	/**
	 * Add loader.
	 *
	 * @param property         the property
	 * @param metaResultObject the meta result object
	 * @param resultLoader     the result loader
	 */
	public void addLoader(String property, MetaObject metaResultObject, ResultLoader resultLoader) {
		String upperFirst = getUppercaseFirstProperty(property);
		if (!upperFirst.equalsIgnoreCase(property) && loaderMap.containsKey(upperFirst)) {
			throw new ExecutorException("Nested lazy loaded result property '" + property + "' for query id '"
					+ resultLoader.mappedStatement.getId()
					+ " already exists in the result map. The leftmost property of all lazy loaded properties must be unique within a result map.");
		}
		loaderMap.put(upperFirst, new LoadPair(property, metaResultObject, resultLoader));
	}

	/**
	 * Gets properties.
	 *
	 * @return the properties
	 */
	public final Map<String, LoadPair> getProperties() {
		return new HashMap<String, LoadPair>(this.loaderMap);
	}

	/**
	 * Gets property names.
	 *
	 * @return the property names
	 */
	public Set<String> getPropertyNames() {
		return loaderMap.keySet();
	}

	/**
	 * Size int.
	 *
	 * @return the int
	 */
	public int size() {
		return loaderMap.size();
	}

	/**
	 * Has loader boolean.
	 *
	 * @param property the property
	 * @return the boolean
	 */
	public boolean hasLoader(String property) {
		return loaderMap.containsKey(property.toUpperCase(Locale.ENGLISH));
	}

	/**
	 * Load boolean.
	 *
	 * @param property the property
	 * @return the boolean
	 * @throws SQLException the sql exception
	 */
	public boolean load(String property) throws SQLException {
		LoadPair pair = loaderMap.remove(property.toUpperCase(Locale.ENGLISH));
		if (pair != null) {
			pair.load();
			return true;
		}
		return false;
	}

	/**
	 * Load all.
	 *
	 * @throws SQLException the sql exception
	 */
	public void loadAll() throws SQLException {
		final Set<String> methodNameSet = loaderMap.keySet();
		String[] methodNames = methodNameSet.toArray(new String[methodNameSet.size()]);
		for (String methodName : methodNames) {
			load(methodName);
		}
	}

	private static String getUppercaseFirstProperty(String property) {
		String[] parts = property.split("\\.");
		return parts[0].toUpperCase(Locale.ENGLISH);
	}

	/**
	 * Property which was not loaded yet.
	 *
	 * @author xianyijun xianyijun0@gmail.com
	 */
	public static class LoadPair implements Serializable {

		private static final long serialVersionUID = 20130412;
		/**
		 * Name of factory method which returns database connection.
		 */
		private static final String FACTORY_METHOD = "getConfiguration";
		/**
		 * Object to check whether we went through serialization..
		 */
		private final transient Object serializationCheck = new Object();
		private transient MetaObject metaResultObject;
		private transient ResultLoader resultLoader;
		private Class<?> configurationFactory;
		private String property;
		private String mappedStatement;
		private Serializable mappedParameter;

		private LoadPair(final String property, MetaObject metaResultObject, ResultLoader resultLoader) {
			this.property = property;
			this.metaResultObject = metaResultObject;
			this.resultLoader = resultLoader;

			if (metaResultObject != null && metaResultObject.getOriginalObject() instanceof Serializable) {
				final Object mappedStatementParameter = resultLoader.parameterObject;

				if (mappedStatementParameter instanceof Serializable) {
					this.mappedStatement = resultLoader.mappedStatement.getId();
					this.mappedParameter = (Serializable) mappedStatementParameter;

					this.configurationFactory = resultLoader.configuration.getConfigurationFactory();
				}
			}
		}

		/**
		 * Load.
		 *
		 * @throws SQLException the sql exception
		 */
		public void load() throws SQLException {
			if (this.metaResultObject == null) {
				throw new IllegalArgumentException("metaResultObject is null");
			}
			if (this.resultLoader == null) {
				throw new IllegalArgumentException("resultLoader is null");
			}

			this.load(null);
		}

		/**
		 * Load.
		 *
		 * @param userObject the user object
		 * @throws SQLException the sql exception
		 */
		public void load(final Object userObject) throws SQLException {
			if (this.metaResultObject == null || this.resultLoader == null) {
				if (this.mappedParameter == null) {
					throw new ExecutorException("Property [" + this.property + "] cannot be loaded because "
							+ "required parameter of mapped statement [" + this.mappedStatement
							+ "] is not serializable.");
				}

				final StatementHandler.Configuration config = this.getConfiguration();
				final MappedStatement ms = config.getMappedStatement(this.mappedStatement);
				if (ms == null) {
					throw new ExecutorException("Cannot lazy load property [" + this.property
							+ "] of deserialized object [" + userObject.getClass()
							+ "] because configuration does not contain statement [" + this.mappedStatement + "]");
				}

				this.metaResultObject = config.newMetaObject(userObject);
				this.resultLoader = new ResultLoader(config, new ClosedExecutor(), ms, this.mappedParameter,
						metaResultObject.getSetterType(this.property), null, null);
			}

			if (this.serializationCheck == null) {
				final ResultLoader old = this.resultLoader;
				this.resultLoader = new ResultLoader(old.configuration, new ClosedExecutor(), old.mappedStatement,
						old.parameterObject, old.targetType, old.cacheKey, old.boundSql);
			}

			this.metaResultObject.setValue(property, this.resultLoader.loadResult());
		}

		private StatementHandler.Configuration getConfiguration() {
			if (this.configurationFactory == null) {
				throw new ExecutorException("Cannot get Configuration as configuration factory was not set.");
			}

			Object configurationObject = null;
			try {
				final Method factoryMethod = this.configurationFactory.getDeclaredMethod(FACTORY_METHOD);
				if (!Modifier.isStatic(factoryMethod.getModifiers())) {
					throw new ExecutorException("Cannot get Configuration as factory method ["
							+ this.configurationFactory + "]#[" + FACTORY_METHOD + "] is not static.");
				}

				if (!factoryMethod.isAccessible()) {
					configurationObject = AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
						@Override
						public Object run() throws Exception {
							try {
								factoryMethod.setAccessible(true);
								return factoryMethod.invoke(null);
							} finally {
								factoryMethod.setAccessible(false);
							}
						}
					});
				} else {
					configurationObject = factoryMethod.invoke(null);
				}
			} catch (final NoSuchMethodException ex) {
				throw new ExecutorException("Cannot get Configuration as factory class [" + this.configurationFactory
						+ "] is missing factory method of name [" + FACTORY_METHOD + "].", ex);
			} catch (final PrivilegedActionException ex) {
				throw new ExecutorException("Cannot get Configuration as factory method [" + this.configurationFactory
						+ "]#[" + FACTORY_METHOD + "] threw an exception.", ex.getCause());
			} catch (final Exception ex) {
				throw new ExecutorException("Cannot get Configuration as factory method [" + this.configurationFactory
						+ "]#[" + FACTORY_METHOD + "] threw an exception.", ex);
			}

			if (!(configurationObject instanceof StatementHandler.Configuration)) {
				throw new ExecutorException("Cannot get Configuration as factory method [" + this.configurationFactory
						+ "]#[" + FACTORY_METHOD + "] didn't return [" + StatementHandler.Configuration.class + "] but ["
						+ (configurationObject == null ? "null" : configurationObject.getClass()) + "].");
			}

			return StatementHandler.Configuration.class.cast(configurationObject);
		}
	}

	private static final class ClosedExecutor extends BaseExecutor {

		/**
		 * Instantiates a new Closed executor.
		 */
		public ClosedExecutor() {
			super(null, null);
		}

		/**
		 * Is closed boolean.
		 *
		 * @return the boolean
		 */
		@Override
		public boolean isClosed() {
			return true;
		}

		/**
		 * Do update int.
		 *
		 * @param ms        the ms
		 * @param parameter the parameter
		 * @return the int
		 * @throws SQLException the sql exception
		 */
		@Override
		protected int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
			throw new UnsupportedOperationException("Not supported.");
		}

		/**
		 * Do query list.
		 *
		 * @param <E>           the type parameter
		 * @param ms            the ms
		 * @param parameter     the parameter
		 * @param rowBounds     the row bounds
		 * @param resultHandler the result handler
		 * @param boundSql      the bound sql
		 * @return the list
		 * @throws SQLException the sql exception
		 */
		@Override
		protected <E> List<E> doQuery(MappedStatement ms, Object parameter, RowBounds rowBounds,
									  ResultHandler<?> resultHandler, StatementHandler.BoundSql boundSql) throws SQLException {
			throw new UnsupportedOperationException("Not supported.");
		}

		/**
		 * Do query cursor cursor.
		 *
		 * @param <E>       the type parameter
		 * @param ms        the ms
		 * @param parameter the parameter
		 * @param rowBounds the row bounds
		 * @param boundSql  the bound sql
		 * @return the cursor
		 * @throws SQLException the sql exception
		 */
		@Override
		protected <E> Cursor<E> doQueryCursor(MappedStatement ms, Object parameter, RowBounds rowBounds,
				StatementHandler.BoundSql boundSql) throws SQLException {
			throw new UnsupportedOperationException("Not supported.");
		}
	}
}
