/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * The type Resources.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class Resources {
    private static Charset charset;
    private static ClassLoaderWrapper classLoaderWrapper = new ClassLoaderWrapper();

    private Resources() {

    }

    /**
     * Gets resource as reader.
     *
     * @param resource the resource
     * @return the resource as reader
     * @throws IOException the io exception
     */
    public static Reader getResourceAsReader(String resource) throws IOException {
        Reader reader;
        if (charset == null) {
            reader = new InputStreamReader(getResourceAsStream(resource));
        } else {
            reader = new InputStreamReader(getResourceAsStream(resource), charset);
        }
        return reader;
    }

    /**
     * Gets resource as stream.
     *
     * @param resource the resource
     * @return the resource as stream
     * @throws IOException the io exception
     */
    public static InputStream getResourceAsStream(String resource) throws IOException {
        return getResourceAsStream(null, resource);
    }

    /**
     * Gets resource as stream.
     *
     * @param loader   the loader
     * @param resource the resource
     * @return the resource as stream
     * @throws IOException the io exception
     */
    public static InputStream getResourceAsStream(ClassLoader loader, String resource) throws IOException {
        InputStream in = classLoaderWrapper.getResourceAsStream(resource, loader);
        if (in == null) {
            throw new IOException("Could not find resource " + resource);
        }
        return in;
    }

    /**
     * Gets resource as properties.
     *
     * @param resource the resource
     * @return the resource as properties
     * @throws IOException the io exception
     */
    public static Properties getResourceAsProperties(String resource) throws IOException {
        Properties props = new Properties();
        InputStream in = getResourceAsStream(resource);
        props.load(in);
        in.close();
        return props;
    }

    /**
     * Gets url as properties.
     *
     * @param urlString the url string
     * @return the url as properties
     * @throws IOException the io exception
     */
    public static Properties getUrlAsProperties(String urlString) throws IOException {
        Properties props = new Properties();
        InputStream in = getUrlAsStream(urlString);
        props.load(in);
        in.close();
        return props;
    }

    /**
     * Gets url as stream.
     *
     * @param urlString the url string
     * @return the url as stream
     * @throws IOException the io exception
     */
    public static InputStream getUrlAsStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        return conn.getInputStream();
    }

    /**
     * Class for name class.
     *
     * @param className the class name
     * @return the class
     * @throws ClassNotFoundException the class not found exception
     */
    public static Class<?> classForName(String className) throws ClassNotFoundException {
        return classLoaderWrapper.classForName(className);
    }
}
