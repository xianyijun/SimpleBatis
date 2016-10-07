/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.defaults;

import cn.xianyijun.orm.core.SqlSession;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.exception.BuilderException;
import cn.xianyijun.orm.parse.XNode;
import cn.xianyijun.orm.script.xml.XMLLanguageDriver;

/**
 * The type Raw language driver.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class RawLanguageDriver extends XMLLanguageDriver {
    /**
     * Create sql source sql source.
     *
     * @param configuration the configuration
     * @param script        the script
     * @param parameterType the parameter type
     * @return the sql source
     */
    @Override
    public SqlSession.SqlSource createSqlSource(StatementHandler.Configuration configuration, XNode script, Class<?> parameterType) {
        SqlSession.SqlSource source = super.createSqlSource(configuration, script, parameterType);
        checkIsNotDynamic(source);
        return source;
    }

    /**
     * Create sql source sql source.
     *
     * @param configuration the configuration
     * @param script        the script
     * @param parameterType the parameter type
     * @return the sql source
     */
    @Override
    public SqlSession.SqlSource createSqlSource(StatementHandler.Configuration configuration, String script, Class<?> parameterType) {
        SqlSession.SqlSource source = super.createSqlSource(configuration, script, parameterType);
        checkIsNotDynamic(source);
        return source;
    }

    private void checkIsNotDynamic(SqlSession.SqlSource source) {
        if (!RawSqlSource.class.equals(source.getClass()))
            throw new BuilderException("Dynamic content is not allowed when using RAW language");
    }
}
