/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

import java.util.HashMap;
import java.util.Map;

import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.reflection.MetaObject;
import ognl.OgnlContext;
import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

/**
 * The type Dynamic context.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class DynamicContext {

	/**
	 * The constant PARAMETER_OBJECT_KEY.
	 */
	public static final String PARAMETER_OBJECT_KEY = "_parameter";
	/**
	 * The constant DATABASE_ID_KEY.
	 */
	public static final String DATABASE_ID_KEY = "_databaseId";

	static {
		OgnlRuntime.setPropertyAccessor(ContextMap.class, new ContextAccessor());
	}

	private final ContextMap bindings;
	private final StringBuilder sqlBuilder = new StringBuilder();
	private int uniqueNumber = 0;

	/**
	 * Instantiates a new Dynamic context.
	 *
	 * @param configuration   the configuration
	 * @param parameterObject the parameter object
	 */
	public DynamicContext(StatementHandler.Configuration configuration, Object parameterObject) {
		if (parameterObject != null && !(parameterObject instanceof Map)) {
			MetaObject metaObject = configuration.newMetaObject(parameterObject);
			bindings = new ContextMap(metaObject);
		} else {
			bindings = new ContextMap(null);
		}
		bindings.put(PARAMETER_OBJECT_KEY, parameterObject);
		bindings.put(DATABASE_ID_KEY, configuration.getDatabaseId());
	}

	/**
	 * Gets bindings.
	 *
	 * @return the bindings
	 */
	public Map<String, Object> getBindings() {
		return bindings;
	}

	/**
	 * Bind.
	 *
	 * @param name  the name
	 * @param value the value
	 */
	public void bind(String name, Object value) {
		bindings.put(name, value);
	}

	/**
	 * Append sql.
	 *
	 * @param sql the sql
	 */
	public void appendSql(String sql) {
		sqlBuilder.append(sql);
		sqlBuilder.append(" ");
	}

	/**
	 * Gets sql.
	 *
	 * @return the sql
	 */
	public String getSql() {
		return sqlBuilder.toString().trim();
	}

	/**
	 * Gets unique number.
	 *
	 * @return the unique number
	 */
	public int getUniqueNumber() {
		return uniqueNumber++;
	}

	/**
	 * The type Context map.
	 *
	 * @author xianyijun xianyijun0@gmail.com
	 */
	static class ContextMap extends HashMap<String, Object> {
		private static final long serialVersionUID = 2977601501966151582L;

		private MetaObject parameterMetaObject;

		/**
		 * Instantiates a new Context map.
		 *
		 * @param parameterMetaObject the parameter meta object
		 */
		public ContextMap(MetaObject parameterMetaObject) {
			this.parameterMetaObject = parameterMetaObject;
		}

		/**
		 * Get object.
		 *
		 * @param key the key
		 * @return the object
		 */
		@Override
		public Object get(Object key) {
			String strKey = (String) key;
			if (super.containsKey(strKey)) {
				return super.get(strKey);
			}

			if (parameterMetaObject != null) {
				// issue #61 do not modify the context when reading
				return parameterMetaObject.getValue(strKey);
			}

			return null;
		}
	}

	/**
	 * The type Context accessor.
	 *
	 * @author xianyijun xianyijun0@gmail.com
	 */
	static class ContextAccessor implements PropertyAccessor {

		/**
		 * Gets property.
		 *
		 * @param context the context
		 * @param target  the target
		 * @param name    the name
		 * @return the property
		 * @throws OgnlException the ognl exception
		 */
		@Override
		public Object getProperty(Map context, Object target, Object name) throws OgnlException {
			Map map = (Map) target;

			Object result = map.get(name);
			if (map.containsKey(name) || result != null) {
				return result;
			}

			Object parameterObject = map.get(PARAMETER_OBJECT_KEY);
			if (parameterObject instanceof Map) {
				return ((Map) parameterObject).get(name);
			}

			return null;
		}

		/**
		 * Sets property.
		 *
		 * @param context the context
		 * @param target  the target
		 * @param name    the name
		 * @param value   the value
		 * @throws OgnlException the ognl exception
		 */
		@Override
		public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
			Map<Object, Object> map = (Map<Object, Object>) target;
			map.put(name, value);
		}

		/**
		 * Gets source accessor.
		 *
		 * @param arg0 the arg 0
		 * @param arg1 the arg 1
		 * @param arg2 the arg 2
		 * @return the source accessor
		 */
		@Override
		public String getSourceAccessor(OgnlContext arg0, Object arg1, Object arg2) {
			return null;
		}

		/**
		 * Gets source setter.
		 *
		 * @param arg0 the arg 0
		 * @param arg1 the arg 1
		 * @param arg2 the arg 2
		 * @return the source setter
		 */
		@Override
		public String getSourceSetter(OgnlContext arg0, Object arg1, Object arg2) {
			return null;
		}
	}
}