/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * The type Default vfs.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class DefaultVFS extends VFS {
    private static final Logger logger = LoggerFactory.getLogger(DefaultVFS.class);
    private static final byte[] JAR_MAGIC = new byte[]{'P', 'K', 3, 4};

    /**
     * Is valid boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * List list.
     *
     * @param url  the url
     * @param path the path
     * @return the list
     * @throws IOException the io exception
     */
    @Override
    protected List<String> list(URL url, String path) throws IOException {
        InputStream is = null;
        try {
            List<String> resources = new ArrayList<>();
            URL jarUrl = getJARForResource(url);
            if (jarUrl != null) {
                is = jarUrl.openStream();
                resources = listResources(new JarInputStream(is), path);
            } else {
                List<String> children = new ArrayList<>();
                try {
                    if (isJAR(url)) {
                        is = url.openStream();
                        JarInputStream jarInput = new JarInputStream(is);
                        for (JarEntry entry; (entry = jarInput.getNextJarEntry()) != null; ) {
                            children.add(entry.getName());
                        }
                        jarInput.close();
                    } else {
                        is = url.openStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                        List<String> lines = new ArrayList<>();
                        for (String line; (line = reader.readLine()) != null; ) {
                            lines.add(line);
                            if (getResources(path + "/" + line).isEmpty()) {
                                lines.clear();
                                break;
                            }
                        }

                        if (!lines.isEmpty()) {
                            children.addAll(lines);
                        }
                    }
                } catch (FileNotFoundException e) {
                    if ("file".equals(url.getProtocol())) {
                        File file = new File(url.getFile());
                        if (file.isDirectory()) {
                            children = Arrays.asList(file.list());
                        }
                    } else {
                        throw e;
                    }
                }

                String prefix = url.toExternalForm();
                if (!prefix.endsWith("/")) {
                    prefix = prefix + "/";
                }

                for (String child : children) {
                    String resourcePath = path + "/" + child;
                    resources.add(resourcePath);
                    URL childUrl = new URL(prefix + child);
                    resources.addAll(list(childUrl, resourcePath));
                }
            }

            return resources;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private URL getJARForResource(URL url) {
        logger.debug(" the jar Url :" + url.toString());
        try {
            url = new URL(url.getFile());
        } catch (MalformedURLException e) {
            logger.error(e.getMessage(), e);
        }
        StringBuilder sb = new StringBuilder(url.toExternalForm());
        int index = sb.lastIndexOf(".jar");
        if (index > 0) {
            sb.setLength(index + 4);
        } else {
            return null;
        }
        try {
            URL testUrl = new URL(sb.toString());
            if (isJAR(testUrl)) {
                return testUrl;
            }
            sb.replace(0, sb.length(), testUrl.getFile());
            File file = new File(sb.toString());
            if (!file.exists()) {
                file = new File(URLEncoder.encode(sb.toString(), "UTF-8"));
            }
            if (file.exists()) {
                testUrl = file.toURI().toURL();
                if (isJAR(testUrl)) {
                    return testUrl;
                }
            }
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Is jar boolean.
     *
     * @param url the url
     * @return the boolean
     */
    protected boolean isJAR(URL url) {
        return isJAR(url, new byte[JAR_MAGIC.length]);
    }

    private boolean isJAR(URL url, byte[] buffer) {
        InputStream is = null;
        try {
            is = url.openStream();
            is.read(buffer, 0, JAR_MAGIC.length);
            if (Arrays.equals(buffer, JAR_MAGIC)) {
                return true;
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return false;
    }

    protected List<String> listResources(JarInputStream jar, String path) throws IOException {
        String targetPath = path.startsWith("/") ? path : "/" + path;
        targetPath = targetPath.endsWith("/") ? targetPath : targetPath + "/";
        List<String> resources = new ArrayList<>();
        for (JarEntry entry; (entry = jar.getNextJarEntry()) != null; ) {
            if (!entry.isDirectory()) {
                String className = entry.getName().startsWith("/") ? entry.getName() : "/" + entry.getName();

                if (className.startsWith(targetPath)) {
                    resources.add(className.substring(1));
                }
            }
        }
        return resources;
    }

}
