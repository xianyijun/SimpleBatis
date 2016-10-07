/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script;

import java.util.HashMap;
import java.util.Map;

import cn.xianyijun.orm.exception.ScriptingException;

/**
 * The type Language driver registry.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class LanguageDriverRegistry {
    private final Map<Class<?>, LanguageDriver> LANGUAGE_DRIVER_MAP = new HashMap<>();

    private Class<?> defaultDriverClass = null;

    /**
     * Register.
     *
     * @param cls the cls
     */
    public void register(Class<?> cls) {
        if (cls == null) {
            throw new IllegalArgumentException("null is not a valid Language Driver");
        }
        LanguageDriver driver = LANGUAGE_DRIVER_MAP.get(cls);
        if (driver == null) {
            try {
                driver = (LanguageDriver) cls.newInstance();
                LANGUAGE_DRIVER_MAP.put(cls, driver);
            } catch (Exception ex) {
                throw new ScriptingException("Failed to load language driver for " + cls.getName(), ex);
            }
        }
    }

    /**
     * Register.
     *
     * @param instance the instance
     */
    public void register(LanguageDriver instance) {
        if (instance == null) {
            throw new IllegalArgumentException("null is not a valid Language Driver");
        }
        LanguageDriver driver = LANGUAGE_DRIVER_MAP.get(instance.getClass());
        if (driver != null) {
            LANGUAGE_DRIVER_MAP.put(instance.getClass(), driver);
        }
    }

    /**
     * Gets driver.
     *
     * @param cls the cls
     * @return the driver
     */
    public LanguageDriver getDriver(Class<?> cls) {
        return LANGUAGE_DRIVER_MAP.get(cls);
    }

    /**
     * Gets default driver.
     *
     * @return the default driver
     */
    public LanguageDriver getDefaultDriver() {
        return getDriver(getDefaultDriverClass());
    }

    /**
     * Gets default driver class.
     *
     * @return the default driver class
     */
    public Class<?> getDefaultDriverClass() {
        return defaultDriverClass;
    }

    /**
     * Sets default driver class.
     *
     * @param defaultDriverClass the default driver class
     */
    public void setDefaultDriverClass(Class<?> defaultDriverClass) {
        register(defaultDriverClass);
        this.defaultDriverClass = defaultDriverClass;
    }
}
