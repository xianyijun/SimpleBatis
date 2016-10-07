/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

import java.util.Map;

import cn.xianyijun.orm.builder.SqlSourceBuilder;
import cn.xianyijun.orm.core.SqlSession;
import cn.xianyijun.orm.core.StatementHandler;

/**
 * The type Dynamic sql source.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class DynamicSqlSource implements SqlSession.SqlSource {

	private StatementHandler.Configuration configuration;
	private SqlNode rootSqlNode;

	/**
	 * Instantiates a new Dynamic sql source.
	 *
	 * @param configuration the configuration
	 * @param rootSqlNode   the root sql node
	 */
	public DynamicSqlSource(StatementHandler.Configuration configuration, SqlNode rootSqlNode) {
		this.configuration = configuration;
		this.rootSqlNode = rootSqlNode;
	}

	/**
	 * Gets bound sql.
	 *
	 * @param parameterObject the parameter object
	 * @return the bound sql
	 */
	@Override
	public StatementHandler.BoundSql getBoundSql(Object parameterObject) {
		DynamicContext context = new DynamicContext(configuration, parameterObject);
		rootSqlNode.apply(context);
		SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
		Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
		SqlSession.SqlSource sqlSource = sqlSourceParser.parse(context.getSql(), parameterType, context.getBindings());
		StatementHandler.BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
		for (Map.Entry<String, Object> entry : context.getBindings().entrySet()) {
			boundSql.setAdditionalParameter(entry.getKey(), entry.getValue());
		}
		return boundSql;
	}

}