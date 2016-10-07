/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * The type Class loader wrapper.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ClassLoaderWrapper {
    private static final Logger logger = LoggerFactory.getLogger(ClassLoaderWrapper.class);
    /**
     * The Default class loader.
     */
    ClassLoader defaultClassLoader;
    /**
     * The System class loader.
     */
    ClassLoader systemClassLoader;

    /**
     * Instantiates a new Class loader wrapper.
     */
    public ClassLoaderWrapper() {
        try {
            systemClassLoader = ClassLoader.getSystemClassLoader();
        } catch (SecurityException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Gets resource as stream.
     *
     * @param resource the resource
     * @return the resource as stream
     */
    public InputStream getResourceAsStream(String resource) {
        return getResourceAsStream(resource, getClassLoaders(null));
    }

    /**
     * Gets resource as stream.
     *
     * @param resource the resource
     * @param loader   the loader
     * @return the resource as stream
     */
    public InputStream getResourceAsStream(String resource, ClassLoader loader) {
        return getResourceAsStream(resource, getClassLoaders(loader));
    }

    private InputStream getResourceAsStream(String resource, ClassLoader[] classLoaders) {
        for (ClassLoader classLoader : classLoaders) {
            if (classLoader != null) {
                InputStream returnValue = classLoader.getResourceAsStream(resource);
                if (returnValue == null) {
                    returnValue = classLoader.getResourceAsStream("/" + resource);
                }
                if (returnValue != null) {
                    return returnValue;
                }
            }
        }
        return null;
    }

    private ClassLoader[] getClassLoaders(ClassLoader loader) {
        return new ClassLoader[]{loader, defaultClassLoader, Thread.currentThread().getContextClassLoader(),
                getClass().getClassLoader(), systemClassLoader};
    }

    /**
     * Class for name class.
     *
     * @param name the name
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
    public Class<?> classForName(String name) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(null));
    }

    /**
     * Class for name class.
     *
     * @param name        the name
     * @param classLoader the class loader
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
    public Class<?> classForName(String name, ClassLoader classLoader) throws ClassNotFoundException {
        return classForName(name, getClassLoaders(classLoader));
    }

    /**
     * Class for name class.
     *
     * @param name        the name
     * @param classLoader the class loader
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
    Class<?> classForName(String name, ClassLoader[] classLoader) throws ClassNotFoundException {
        for (ClassLoader cl : classLoader) {
            if (cl != null) {
                try {
                    Class<?> c = Class.forName(name, true, cl);
                    if (c != null) {
                        return c;
                    }
                } catch (ClassNotFoundException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        throw new ClassNotFoundException("Cannot find class: " + name);
    }
}
