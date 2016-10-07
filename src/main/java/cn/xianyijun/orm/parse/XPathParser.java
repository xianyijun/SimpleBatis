/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.parse;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import cn.xianyijun.orm.exception.BuilderException;

/**
 * The type X path parser.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class XPathParser {
	private static final Logger logger = LoggerFactory.getLogger(XPathParser.class);
	private Document document;
	private boolean validation;
	private EntityResolver entityResolver;
	private Properties variables;
	private XPath xpath;

	/**
	 * Instantiates a new X path parser.
	 *
	 * @param reader         the reader
	 * @param validation     the validation
	 * @param variables      the variables
	 * @param entityResolver the entity resolver
	 */
	public XPathParser(Reader reader, boolean validation, Properties variables,
			XMLMapperEntityResolver entityResolver) {
		commonConstructor(validation, variables, entityResolver);
		this.document = createDocument(new InputSource(reader));
	}

	/**
	 * Instantiates a new X path parser.
	 *
	 * @param inputStream    the input stream
	 * @param validation     the validation
	 * @param variables      the variables
	 * @param entityResolver the entity resolver
	 */
	public XPathParser(InputStream inputStream, boolean validation, Properties variables,
			EntityResolver entityResolver) {
		commonConstructor(validation, variables, entityResolver);
		this.document = createDocument(new InputSource(inputStream));
	}

	/**
	 * Instantiates a new X path parser.
	 *
	 * @param document       the document
	 * @param validation     the validation
	 * @param variables      the variables
	 * @param entityResolver the entity resolver
	 */
	public XPathParser(Document document, boolean validation, Properties variables, EntityResolver entityResolver) {
		commonConstructor(validation, variables, entityResolver);
		this.document = document;
	}

	/**
	 * Instantiates a new X path parser.
	 *
	 * @param xml            the xml
	 * @param validation     the validation
	 * @param variables      the variables
	 * @param entityResolver the entity resolver
	 */
	public XPathParser(String xml, boolean validation, Properties variables, EntityResolver entityResolver) {
		commonConstructor(validation, variables, entityResolver);
		this.document = createDocument(new InputSource(new StringReader(xml)));
	}

	/**
	* @Title: createDocument
	* @Description: 根据读取InputSource创建Document
	* @param     参数
	* @return Document    返回类型
	* @throws
	*/
	private Document createDocument(InputSource inputSource) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(validation);

			factory.setNamespaceAware(false);
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(false);
			factory.setCoalescing(false);
			factory.setExpandEntityReferences(true);

			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setEntityResolver(entityResolver);
			builder.setErrorHandler(new ErrorHandler() {
				@Override
				public void error(SAXParseException exception) throws SAXException {
					throw exception;
				}

				@Override
				public void fatalError(SAXParseException exception) throws SAXException {
					throw exception;
				}

				@Override
				public void warning(SAXParseException exception) throws SAXException {
				}
			});
			return builder.parse(inputSource);
		} catch (Exception e) {
			throw new BuilderException("Error creating document instance.  Cause: " + e, e);
		}
	}

	/**
	* @Title: commonConstructor
	* @Description: 初始化通用属性
	* @param     参数
	* @return void    返回类型
	* @throws
	*/
	private void commonConstructor(boolean validation, Properties variables, EntityResolver entityResolver) {
		this.validation = validation;
		this.entityResolver = entityResolver;
		this.variables = variables;
		XPathFactory factory = XPathFactory.newInstance();
		this.xpath = factory.newXPath();
	}

	/**
	 * Eval string string.
	 *
	 * @param expression the expression
	 * @return the string
	 */
//============================================evalNode======================================
	public String evalString(String expression) {
		return evalString(document, expression);
	}

	/**
	 * Eval string string.
	 *
	 * @param root       the root
	 * @param expression the expression
	 * @return the string
	 */
	public String evalString(Object root, String expression) {
		String result = (String) evaluate(expression, root, XPathConstants.STRING);
		result = PropertyParser.parse(result, variables);
		return result;
	}

	/**
	 * Eval boolean boolean.
	 *
	 * @param expression the expression
	 * @return the boolean
	 */
	public Boolean evalBoolean(String expression) {
		return evalBoolean(document, expression);
	}

	/**
	 * Eval boolean boolean.
	 *
	 * @param root       the root
	 * @param expression the expression
	 * @return the boolean
	 */
	public Boolean evalBoolean(Object root, String expression) {
		return (Boolean) evaluate(expression, root, XPathConstants.BOOLEAN);
	}

	/**
	 * Eval short short.
	 *
	 * @param expression the expression
	 * @return the short
	 */
	public Short evalShort(String expression) {
		return evalShort(document, expression);
	}

	/**
	 * Eval short short.
	 *
	 * @param root       the root
	 * @param expression the expression
	 * @return the short
	 */
	public Short evalShort(Object root, String expression) {
		return Short.valueOf(evalString(root, expression));
	}

	/**
	 * Eval integer integer.
	 *
	 * @param expression the expression
	 * @return the integer
	 */
	public Integer evalInteger(String expression) {
		return evalInteger(document, expression);
	}

	/**
	 * Eval integer integer.
	 *
	 * @param root       the root
	 * @param expression the expression
	 * @return the integer
	 */
	public Integer evalInteger(Object root, String expression) {
		return Integer.valueOf(evalString(root, expression));
	}

	/**
	 * Eval long long.
	 *
	 * @param expression the expression
	 * @return the long
	 */
	public Long evalLong(String expression) {
		return evalLong(document, expression);
	}

	/**
	 * Eval long long.
	 *
	 * @param root       the root
	 * @param expression the expression
	 * @return the long
	 */
	public Long evalLong(Object root, String expression) {
		return Long.valueOf(evalString(root, expression));
	}

	/**
	 * Eval float float.
	 *
	 * @param expression the expression
	 * @return the float
	 */
	public Float evalFloat(String expression) {
		return evalFloat(document, expression);
	}

	/**
	 * Eval float float.
	 *
	 * @param root       the root
	 * @param expression the expression
	 * @return the float
	 */
	public Float evalFloat(Object root, String expression) {
		return Float.valueOf(evalString(root, expression));
	}

	/**
	 * Eval double double.
	 *
	 * @param expression the expression
	 * @return the double
	 */
	public Double evalDouble(String expression) {
		return evalDouble(document, expression);
	}

	/**
	 * Eval double double.
	 *
	 * @param root       the root
	 * @param expression the expression
	 * @return the double
	 */
	public Double evalDouble(Object root, String expression) {
		return (Double) evaluate(expression, root, XPathConstants.NUMBER);
	}

	/**
	 * Eval nodes list.
	 *
	 * @param expression the expression
	 * @return the list
	 */
	public List<XNode> evalNodes(String expression) {
		return evalNodes(document, expression);
	}

	/**
	 * Eval nodes list.
	 *
	 * @param root       the root
	 * @param expression the expression
	 * @return the list
	 */
	public List<XNode> evalNodes(Object root, String expression) {
		List<XNode> xnodes = new ArrayList<XNode>();
		NodeList nodes = (NodeList) evaluate(expression, root, XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			xnodes.add(new XNode(this, nodes.item(i), variables));
		}
		return xnodes;
	}

	/**
	 * Eval node x node.
	 *
	 * @param expression the expression
	 * @return the x node
	 */
	public XNode evalNode(String expression) {
		return evalNode(document, expression);
	}

	/**
	 * Eval node x node.
	 *
	 * @param root       the root
	 * @param expression the expression
	 * @return the x node
	 */
	public XNode evalNode(Object root, String expression) {
		Node node = (Node) evaluate(expression, root, XPathConstants.NODE);
		if (node == null) {
			return null;
		}
		return new XNode(this, node, variables);
	}

	//========================evaluate================================================
	private Object evaluate(String expression, Object root, QName returnType) {
		try {
			return xpath.evaluate(expression, root, returnType);
		} catch (Exception e) {
			throw new BuilderException("Error evaluating XPath.  Cause: " + e, e);
		}
	}

	/**
	 * Sets variables.
	 *
	 * @param variables the variables
	 */
//========================getter/setter===========================================
	public void setVariables(Properties variables) {
		this.variables = variables;
	}

	/**
	 * Gets variables.
	 *
	 * @return the variables
	 */
	public Properties getVariables() {
		return variables;
	}

}
