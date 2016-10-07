/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

import java.util.regex.Pattern;

import cn.xianyijun.orm.exception.ScriptingException;
import cn.xianyijun.orm.parse.GenericTokenParser;
import cn.xianyijun.orm.parse.TokenHandler;
import cn.xianyijun.orm.script.OgnlCache;

/**
 * The type Text sql node.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class TextSqlNode implements SqlNode {
	private String text;
	private Pattern injectionFilter;

	/**
	 * Instantiates a new Text sql node.
	 *
	 * @param text the text
	 */
	public TextSqlNode(String text) {
		this(text, null);
	}

	/**
	 * Instantiates a new Text sql node.
	 *
	 * @param text            the text
	 * @param injectionFilter the injection filter
	 */
	public TextSqlNode(String text, Pattern injectionFilter) {
		this.text = text;
		this.injectionFilter = injectionFilter;
	}

	/**
	 * Is dynamic boolean.
	 *
	 * @return the boolean
	 */
	public boolean isDynamic() {
		DynamicCheckerTokenParser checker = new DynamicCheckerTokenParser();
		GenericTokenParser parser = createParser(checker);
		parser.parse(text);
		return checker.isDynamic();
	}

	/**
	 * Apply boolean.
	 *
	 * @param context the context
	 * @return the boolean
	 */
	@Override
	public boolean apply(DynamicContext context) {
		GenericTokenParser parser = createParser(new BindingTokenParser(context, injectionFilter));
		context.appendSql(parser.parse(text));
		return true;
	}

	private GenericTokenParser createParser(TokenHandler handler) {
		return new GenericTokenParser("${", "}", handler);
	}

	private static class BindingTokenParser implements TokenHandler {

		private DynamicContext context;
		private Pattern injectionFilter;

		/**
		 * Instantiates a new Binding token parser.
		 *
		 * @param context         the context
		 * @param injectionFilter the injection filter
		 */
		public BindingTokenParser(DynamicContext context, Pattern injectionFilter) {
			this.context = context;
			this.injectionFilter = injectionFilter;
		}

		/**
		 * Handle token string.
		 *
		 * @param content the content
		 * @return the string
		 */
		@Override
		public String handleToken(String content) {
			Object parameter = context.getBindings().get("_parameter");
			if (parameter == null) {
				context.getBindings().put("value", null);
			}
			Object value = OgnlCache.getValue(content, context.getBindings());
			String srtValue = (value == null ? "" : String.valueOf(value)); // issue #274 return "" instead of "null"
			checkInjection(srtValue);
			return srtValue;
		}

		private void checkInjection(String value) {
			if (injectionFilter != null && !injectionFilter.matcher(value).matches()) {
				throw new ScriptingException("Invalid input. Please conform to regex" + injectionFilter.pattern());
			}
		}
	}

	private static class DynamicCheckerTokenParser implements TokenHandler {

		private boolean isDynamic;

		/**
		 * Instantiates a new Dynamic checker token parser.
		 */
		public DynamicCheckerTokenParser() {
			// Prevent Synthetic Access
		}

		/**
		 * Is dynamic boolean.
		 *
		 * @return the boolean
		 */
		public boolean isDynamic() {
			return isDynamic;
		}

		/**
		 * Handle token string.
		 *
		 * @param content the content
		 * @return the string
		 */
		@Override
		public String handleToken(String content) {
			this.isDynamic = true;
			return null;
		}
	}

}