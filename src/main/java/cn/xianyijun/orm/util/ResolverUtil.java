/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.xianyijun.orm.io.VFS;

/**
 * The type Resolver util.
 *
 * @param <T> the type parameter
 * @author xianyijun xianyijun0@gmail.com
 */
public class ResolverUtil<T> {
    private static final Logger logger = LoggerFactory.getLogger(ResolverUtil.class);

    private Set<Class<? extends T>> matches = new HashSet<>();

    private ClassLoader classloader;

    /**
     * The interface Test.
     *
     * @author xianyijun xianyijun0@gmail.com
     */
    @FunctionalInterface
    public static interface Test {
        /**
         * Matches boolean.
         *
         * @param type the type
         * @return the boolean
         */
        boolean matches(Class<?> type);
    }

    /**
     * The type Is a.
     *
     * @author xianyijun xianyijun0@gmail.com
     */
    public static class IsA implements Test {
        private Class<?> parent;

        /**
         * Instantiates a new Is a.
         *
         * @param parentType the parent type
         */
        public IsA(Class<?> parentType) {
            this.parent = parentType;
        }

        /**
         * Matches boolean.
         *
         * @param type the type
         * @return the boolean
         */
        @Override
        public boolean matches(Class<?> type) {
            return type != null && parent.isAssignableFrom(type);
        }

        /**
         * To string string.
         *
         * @return the string
         */
        @Override
        public String toString() {
            return "is assignable to " + parent.getSimpleName();
        }
    }

    /**
     * Find resolver util.
     *
     * @param test        the test
     * @param packageName the package name
     * @return the resolver util
     */
    public ResolverUtil<T> find(Test test, String packageName) {
        String path = getPackagePath(packageName);

        try {
            List<String> children = VFS.getInstance().list(path);
            children.forEach(child -> {
                if (child.endsWith(".class")) {
                    addIfMatching(test, child);
                }
            });
        } catch (IOException ioe) {
            logger.error("Could not read package: " + packageName, ioe);
        }

        return this;
    }

    /**
     * Gets package path.
     *
     * @param packageName the package name
     * @return the package path
     */
    protected String getPackagePath(String packageName) {
        return packageName == null ? null : packageName.replace('.', '/');
    }

    /**
     * Add if matching.
     *
     * @param test the test
     * @param fqn  the fqn
     */
    @SuppressWarnings("unchecked")
    protected void addIfMatching(Test test, String fqn) {
        try {
            String externalName = fqn.substring(0, fqn.indexOf('.')).replace('/', '.');
            ClassLoader loader = getClassLoader();
            Class<?> type = loader.loadClass(externalName);
            if (test.matches(type)) {
                matches.add((Class<T>) type);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * Gets class loader.
     *
     * @return the class loader
     */
    public ClassLoader getClassLoader() {
        return classloader == null ? Thread.currentThread().getContextClassLoader() : classloader;
    }

    /**
     * Gets classes.
     *
     * @return the classes
     */
    public Set<Class<? extends T>> getClasses() {
        return matches;
    }
}
