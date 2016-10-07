/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script;

import cn.xianyijun.orm.core.SqlSession;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.executor.parameter.ParameterHandler;
import cn.xianyijun.orm.core.MappedStatement;
import cn.xianyijun.orm.parse.XNode;

/**
 * The interface Language driver.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public interface LanguageDriver {
	/**
	 * Create parameter handler parameter handler.
	 *
	 * @param mappedStatement the mapped statement
	 * @param parameterObject the parameter object
	 * @param boundSql        the bound sql
	 * @return the parameter handler
	 */
	ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, StatementHandler.BoundSql boundSql);

	/**
	 * Create sql source sql source.
	 *
	 * @param configuration the configuration
	 * @param script        the script
	 * @param parameterType the parameter type
	 * @return the sql source
	 */
	SqlSession.SqlSource createSqlSource(StatementHandler.Configuration configuration, XNode script, Class<?> parameterType);

	/**
	 * Create sql source sql source.
	 *
	 * @param configuration the configuration
	 * @param script        the script
	 * @param parameterType the parameter type
	 * @return the sql source
	 */
	SqlSession.SqlSource createSqlSource(StatementHandler.Configuration configuration, String script, Class<?> parameterType);

}
