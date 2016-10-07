/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.parse;

import java.util.Properties;

/**
 * The type Property parser.
 *
 * @author xianyijun 　XNode节点解析工具类
 */
public class PropertyParser {
	private PropertyParser() {
	}

	/**
	 * Parse string.
	 *
	 * @param string    the string
	 * @param variables the variables
	 * @return the string
	 */
	public static String parse(String string, Properties variables) {
		VariableTokenHandler handler = new VariableTokenHandler(variables);
		GenericTokenParser parser = new GenericTokenParser("${", "}", handler);
		return parser.parse(string);
	}

	private static class VariableTokenHandler implements TokenHandler {
		private Properties variables;

		/**
		 * Instantiates a new Variable token handler.
		 *
		 * @param variables the variables
		 */
		public VariableTokenHandler(Properties variables) {
			this.variables = variables;
		}

		/**
		 * Handle token string.
		 *
		 * @param content the content
		 * @return the string
		 */
		@Override
		public String handleToken(String content) {
			if (variables != null && variables.containsKey(content)) {
				return variables.getProperty(content);
			}
			return "${" + content + "}";
		}
	}
}
