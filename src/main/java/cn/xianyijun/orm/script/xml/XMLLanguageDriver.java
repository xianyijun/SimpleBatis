/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

import cn.xianyijun.orm.core.SqlSession;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.executor.parameter.ParameterHandler;
import cn.xianyijun.orm.core.MappedStatement;
import cn.xianyijun.orm.parse.PropertyParser;
import cn.xianyijun.orm.parse.XMLMapperEntityResolver;
import cn.xianyijun.orm.parse.XNode;
import cn.xianyijun.orm.parse.XPathParser;
import cn.xianyijun.orm.script.LanguageDriver;
import cn.xianyijun.orm.executor.parameter.DefaultParameterHandler;
import cn.xianyijun.orm.script.defaults.RawSqlSource;

/**
 * The type Xml language driver.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class XMLLanguageDriver implements LanguageDriver {

	/**
	 * Create parameter handler parameter handler.
	 *
	 * @param mappedStatement the mapped statement
	 * @param parameterObject the parameter object
	 * @param boundSql        the bound sql
	 * @return the parameter handler
	 */
	@Override
	  public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, StatementHandler.BoundSql boundSql) {
	    return new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
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
	  public SqlSession.SqlSource createSqlSource(StatementHandler.Configuration configuration, XNode script, Class<?> parameterType) {
	    XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
	    return builder.parseScriptNode();
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
	    if (script.startsWith("<script>")) {
	      XPathParser parser = new XPathParser(script, false, configuration.getVariables(), new XMLMapperEntityResolver());
	      return createSqlSource(configuration, parser.evalNode("/script"), parameterType);
	    } else {
	      script = PropertyParser.parse(script, configuration.getVariables());
	      TextSqlNode textSqlNode = new TextSqlNode(script);
	      if (textSqlNode.isDynamic()) {
	        return new DynamicSqlSource(configuration, textSqlNode);
	      } else {
	        return new RawSqlSource(configuration, script, parameterType);
	      }
	    }
	  }
}
