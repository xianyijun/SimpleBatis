/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection.property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * The type Property copier.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public final class PropertyCopier {
    private static final Logger logger = LoggerFactory.getLogger(PropertyCopier.class);

    private PropertyCopier() {
    }

    /**
     * Copy bean properties.
     *
     * @param type            the type
     * @param sourceBean      the source bean
     * @param destinationBean the destination bean
     */
    public static void copyBeanProperties(Class<?> type, Object sourceBean, Object destinationBean) {
        Class<?> parent = type;
        while (parent != null) {
            final Field[] fields = parent.getDeclaredFields();
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    field.set(destinationBean, field.get(sourceBean));
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
            parent = parent.getSuperclass();
        }
    }

}