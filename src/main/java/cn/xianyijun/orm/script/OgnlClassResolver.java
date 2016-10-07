/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script;

import java.util.HashMap;
import java.util.Map;

import cn.xianyijun.orm.io.Resources;
import ognl.ClassResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Ognl class resolver.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class OgnlClassResolver implements ClassResolver {
    private static final Logger logger = LoggerFactory.getLogger(OgnlClassResolver.class);
    private Map<String, Class<?>> classes = new HashMap<>(101);

    /**
     * Class for name class.
     *
     * @param className the class name
     * @param context   the context
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
    @Override
    public Class classForName(String className, Map context) throws ClassNotFoundException {
        Class<?> result;
        if ((result = classes.get(className)) == null) {
            try {
                result = Resources.classForName(className);
            } catch (ClassNotFoundException e) {

                if (className.indexOf('.') == -1) {
                    result = Resources.classForName("java.lang." + className);
                    classes.put("java.lang." + className, result);
                }
                logger.error(e.getMessage(), e);
            }
            classes.put(className, result);
        }
        return result;
    }

}
