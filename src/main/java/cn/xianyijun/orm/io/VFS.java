/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class VFS {
    private static final Logger logger = LoggerFactory.getLogger(VFS.class);
    private static VFS instance;
    protected static final Class<?>[] IMPLEMENTATIONS = {DefaultVFS.class};

    protected static final List<Class<? extends VFS>> USER_IMPLEMENTATIONS = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public static VFS getInstance() {
        if (instance != null) {
            return instance;
        }
        List<Class<? extends VFS>> impls = new ArrayList<>();
        impls.addAll(USER_IMPLEMENTATIONS);
        impls.addAll(Arrays.asList((Class<? extends VFS>[]) IMPLEMENTATIONS));

        VFS vfs = null;
        for (int i = 0; vfs == null || !vfs.isValid(); i++) {
            Class<? extends VFS> impl = impls.get(i);
            try {
                vfs = impl.newInstance();
                if (vfs == null || !vfs.isValid()) {
                    logger.debug("the VFS implements :" + impl.getName() + " is not valid");
                }
            } catch (InstantiationException |IllegalAccessException e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
        VFS.instance = vfs;
        return VFS.instance;
    }

    protected static List<URL> getResources(String path) throws IOException {
        return Collections.list(Thread.currentThread().getContextClassLoader().getResources(path));
    }

    public List<String> list(String path) throws IOException {
        List<String> names = new ArrayList<>();
        for (URL url : getResources(path)) {
            names.addAll(list(url, path));
        }
        return names;
    }

    //==================================abstract===========================
    public abstract boolean isValid();

    protected abstract List<String> list(URL url, String forPath) throws IOException;

}
