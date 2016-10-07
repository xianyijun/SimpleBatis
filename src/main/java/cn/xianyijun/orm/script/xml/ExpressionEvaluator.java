/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.xianyijun.orm.exception.BuilderException;
import cn.xianyijun.orm.script.OgnlCache;

/**
 * The type Expression evaluator.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ExpressionEvaluator {

	/**
	 * Evaluate boolean boolean.
	 *
	 * @param expression      the expression
	 * @param parameterObject the parameter object
	 * @return the boolean
	 */
	public boolean evaluateBoolean(String expression, Object parameterObject) {
		Object value = OgnlCache.getValue(expression, parameterObject);
		if (value instanceof Boolean) {
			return (Boolean) value;
		}
		if (value instanceof Number) {
			return !new BigDecimal(String.valueOf(value)).equals(BigDecimal.ZERO);
		}
		return value != null;
	}

	/**
	 * Evaluate iterable iterable.
	 *
	 * @param expression      the expression
	 * @param parameterObject the parameter object
	 * @return the iterable
	 */
	public Iterable<?> evaluateIterable(String expression, Object parameterObject) {
		Object value = OgnlCache.getValue(expression, parameterObject);
		if (value == null) {
			throw new BuilderException("The expression '" + expression + "' evaluated to a null value.");
		}
		if (value instanceof Iterable) {
			return (Iterable<?>) value;
		}
		if (value.getClass().isArray()) {
			// the array may be primitive, so Arrays.asList() may throw
			// a ClassCastException (issue 209).  Do the work manually
			// Curse primitives! :) (JGB)
			int size = Array.getLength(value);
			List<Object> answer = new ArrayList<Object>();
			for (int i = 0; i < size; i++) {
				Object o = Array.get(value, i);
				answer.add(o);
			}
			return answer;
		}
		if (value instanceof Map) {
			return ((Map) value).entrySet();
		}
		throw new BuilderException(
				"Error evaluating expression '" + expression + "'.  Return value (" + value + ") was not iterable.");
	}

}
