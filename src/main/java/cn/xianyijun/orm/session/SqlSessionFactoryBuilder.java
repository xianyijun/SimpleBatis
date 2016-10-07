/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.session;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import cn.xianyijun.orm.core.StatementHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.xianyijun.orm.builder.xml.XMLConfigBuilder;
import cn.xianyijun.orm.exception.BuilderException;
import cn.xianyijun.orm.session.impl.DefaultSqlSessionFactory;

/**
 * The type Sql session factory builder.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class SqlSessionFactoryBuilder {
    private static final Logger logger = LoggerFactory.getLogger(SqlSessionFactoryBuilder.class);

    /**
     * Build sql session factory.
     *
     * @param reader the reader
     * @return the sql session factory
     */
    public SqlSessionFactory build(Reader reader) {
        return build(reader, null, null);
    }

    /**
     * Build sql session factory.
     *
     * @param reader      the reader
     * @param environment the environment
     * @return the sql session factory
     */
    public SqlSessionFactory build(Reader reader, String environment) {
        return build(reader, environment, null);
    }

    /**
     * Build sql session factory.
     *
     * @param reader     the reader
     * @param properties the properties
     * @return the sql session factory
     */
    public SqlSessionFactory build(Reader reader, Properties properties) {
        return build(reader, null, properties);
    }

    private SqlSessionFactory build(Reader reader, String environment, Properties properties) {
        try {
            XMLConfigBuilder builder = new XMLConfigBuilder(reader, environment, properties);
            return build(builder.parse());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BuilderException(" can not build the SqlSession");
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Build sql session factory.
     *
     * @param inputStream the input stream
     * @param environment the environment
     * @param properties  the properties
     * @return the sql session factory
     */
    public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
        try {
            XMLConfigBuilder parser = new XMLConfigBuilder(inputStream, environment, properties);
            return build(parser.parse());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new BuilderException(" can not build the SqlSession");
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Build sql session factory.
     *
     * @param config the config
     * @return the sql session factory
     */
    public SqlSessionFactory build(StatementHandler.Configuration config) {
        return new DefaultSqlSessionFactory(config);
    }
}
