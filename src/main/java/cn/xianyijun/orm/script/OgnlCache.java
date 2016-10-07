/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.xianyijun.orm.exception.BuilderException;
import ognl.Ognl;
import ognl.OgnlException;

/**
 * The type Ognl cache.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public final class OgnlCache {

    private static final Map<String, Object> expressionCache = new ConcurrentHashMap<>();

    private OgnlCache() {
    }

    /**
     * Gets value.
     *
     * @param expression the expression
     * @param root       the root
     * @return the value
     */
    public static Object getValue(String expression, Object root) {
        try {
            Map context = Ognl.createDefaultContext(root, new OgnlClassResolver());
            return Ognl.getValue(parseExpression(expression), context, root);
        } catch (OgnlException e) {
            throw new BuilderException("Error evaluating expression '" + expression + "'. Cause: " + e, e);
        }
    }

    private static Object parseExpression(String expression) throws OgnlException {
        Object node = expressionCache.get(expression);
        if (node == null) {
            node = Ognl.parseExpression(expression);
            expressionCache.put(expression, node);
        }
        return node;
    }

}
