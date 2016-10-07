/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

import cn.xianyijun.orm.script.OgnlCache;

/**
 * The type Var decl sql node.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class VarDeclSqlNode implements SqlNode {

	private final String name;
	private final String expression;

	/**
	 * Instantiates a new Var decl sql node.
	 *
	 * @param var the var
	 * @param exp the exp
	 */
	public VarDeclSqlNode(String var, String exp) {
		name = var;
		expression = exp;
	}

	/**
	 * Apply boolean.
	 *
	 * @param context the context
	 * @return the boolean
	 */
	@Override
	public boolean apply(DynamicContext context) {
		final Object value = OgnlCache.getValue(expression, context.getBindings());
		context.bind(name, value);
		return true;
	}

}
