/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.executor.keygen;

import java.sql.Statement;
import java.util.List;

import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.exception.ExecutorException;
import cn.xianyijun.orm.core.Executor;
import cn.xianyijun.orm.core.MappedStatement;
import cn.xianyijun.orm.reflection.MetaObject;
import cn.xianyijun.orm.session.RowBounds;

/**
 * The type Select key generator.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class SelectKeyGenerator implements KeyGenerator {

	/**
	 * The constant SELECT_KEY_SUFFIX.
	 */
	public static final String SELECT_KEY_SUFFIX = "!selectKey";
	private boolean executeBefore;
	private MappedStatement keyStatement;

	/**
	 * Instantiates a new Select key generator.
	 *
	 * @param keyStatement  the key statement
	 * @param executeBefore the execute before
	 */
	public SelectKeyGenerator(MappedStatement keyStatement, boolean executeBefore) {
		this.executeBefore = executeBefore;
		this.keyStatement = keyStatement;
	}

	/**
	 * Process before.
	 *
	 * @param executor  the executor
	 * @param ms        the ms
	 * @param stmt      the stmt
	 * @param parameter the parameter
	 */
	@Override
	public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
		if (executeBefore) {
			processGeneratedKeys(executor, ms, parameter);
		}
	}

	/**
	 * Process after.
	 *
	 * @param executor  the executor
	 * @param ms        the ms
	 * @param stmt      the stmt
	 * @param parameter the parameter
	 */
	@Override
	public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
		if (!executeBefore) {
			processGeneratedKeys(executor, ms, parameter);
		}
	}

	private void processGeneratedKeys(Executor executor, MappedStatement ms, Object parameter) {
		try {
			if (parameter != null && keyStatement != null && keyStatement.getKeyProperties() != null) {
				String[] keyProperties = keyStatement.getKeyProperties();
				final StatementHandler.Configuration configuration = ms.getConfiguration();
				final MetaObject metaParam = configuration.newMetaObject(parameter);
				if (keyProperties != null) {
					Executor keyExecutor = configuration.newExecutor(executor.getTransaction());
					List<Object> values = keyExecutor.query(keyStatement, parameter, RowBounds.DEFAULT,
							Executor.NO_RESULT_HANDLER);
					if (values.size() == 0) {
						throw new ExecutorException("SelectKey returned no data.");
					} else if (values.size() > 1) {
						throw new ExecutorException("SelectKey returned more than one value.");
					} else {
						MetaObject metaResult = configuration.newMetaObject(values.get(0));
						if (keyProperties.length == 1) {
							if (metaResult.hasGetter(keyProperties[0])) {
								setValue(metaParam, keyProperties[0], metaResult.getValue(keyProperties[0]));
							} else {
								setValue(metaParam, keyProperties[0], values.get(0));
							}
						} else {
							handleMultipleProperties(keyProperties, metaParam, metaResult);
						}
					}
				}
			}
		} catch (ExecutorException e) {
			throw e;
		} catch (Exception e) {
			throw new ExecutorException("Error selecting key or setting result to parameter object. Cause: " + e, e);
		}
	}

	private void handleMultipleProperties(String[] keyProperties, MetaObject metaParam, MetaObject metaResult) {
		String[] keyColumns = keyStatement.getKeyColumns();

		if (keyColumns == null || keyColumns.length == 0) {
			// no key columns specified, just use the property names
			for (String keyProperty : keyProperties) {
				setValue(metaParam, keyProperty, metaResult.getValue(keyProperty));
			}
		} else {
			if (keyColumns.length != keyProperties.length) {
				throw new ExecutorException(
						"If SelectKey has key columns, the number must match the number of key properties.");
			}
			for (int i = 0; i < keyProperties.length; i++) {
				setValue(metaParam, keyProperties[i], metaResult.getValue(keyColumns[i]));
			}
		}
	}

	private void setValue(MetaObject metaParam, String property, Object value) {
		if (metaParam.hasSetter(property)) {
			metaParam.setValue(property, value);
		} else {
			throw new ExecutorException("No setter found for the keyProperty '" + property + "' in "
					+ metaParam.getOriginalObject().getClass().getName() + ".");
		}
	}
}
