/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.parse;

/**
 * The type Generic token parser.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class GenericTokenParser {
	private final String openToken;
	private final String closeToken;
	private final TokenHandler handler;

	/**
	 * Instantiates a new Generic token parser.
	 *
	 * @param openToken  the open token
	 * @param closeToken the close token
	 * @param handler    the handler
	 */
	public GenericTokenParser(String openToken, String closeToken, TokenHandler handler) {
		this.openToken = openToken;
		this.closeToken = closeToken;
		this.handler = handler;
	}

	/**
	 * Parse string.
	 *
	 * @param text the text
	 * @return the string
	 */
	public String parse(String text) {
		final StringBuilder builder = new StringBuilder();
		final StringBuilder expression = new StringBuilder();
		if (text != null && text.length() > 0) {
			char[] src = text.toCharArray();
			int offset = 0;
			int start = text.indexOf(openToken, offset);
			while (start > -1) {
				if (start > 0 && src[start - 1] == '\\') {
					builder.append(src, offset, start - offset - 1).append(openToken);
					offset = start + openToken.length();
				} else {
					expression.setLength(0);
					builder.append(src, offset, start - offset);
					offset = start + openToken.length();
					int end = text.indexOf(closeToken, offset);
					while (end > -1) {
						if (end > offset && src[end - 1] == '\\') {
							// this close token is escaped. remove the backslash and continue.
							expression.append(src, offset, end - offset - 1).append(closeToken);
							offset = end + closeToken.length();
							end = text.indexOf(closeToken, offset);
						} else {
							expression.append(src, offset, end - offset);
							offset = end + closeToken.length();
							break;
						}
					}
					if (end == -1) {
						builder.append(src, start, src.length - start);
						offset = src.length;
					} else {
						builder.append(handler.handleToken(expression.toString()));
						offset = end + closeToken.length();
					}
				}
				start = text.indexOf(openToken, offset);
			}
			if (offset < src.length) {
				builder.append(src, offset, src.length - offset);
			}
		}
		return builder.toString();
	}
}
