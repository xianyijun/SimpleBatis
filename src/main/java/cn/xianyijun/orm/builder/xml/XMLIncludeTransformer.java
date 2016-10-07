/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.builder.xml;

import java.util.Properties;

import cn.xianyijun.orm.core.StatementHandler;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.xianyijun.orm.builder.MapperBuilderAssistant;
import cn.xianyijun.orm.exception.BuilderException;
import cn.xianyijun.orm.exception.IncompleteElementException;
import cn.xianyijun.orm.parse.PropertyParser;
import cn.xianyijun.orm.parse.XNode;

/**
 * The type Xml include transformer.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class XMLIncludeTransformer {
	private final StatementHandler.Configuration configuration;
	private final MapperBuilderAssistant builderAssistant;

	/**
	 * Instantiates a new Xml include transformer.
	 *
	 * @param configuration    the configuration
	 * @param builderAssistant the builder assistant
	 */
	public XMLIncludeTransformer(StatementHandler.Configuration configuration, MapperBuilderAssistant builderAssistant) {
		this.configuration = configuration;
		this.builderAssistant = builderAssistant;


	}


	/**
	 * Apply includes.
	 *
	 * @param source the source
	 */
	public void applyIncludes(Node source) {
		Properties variablesContext = new Properties();
		Properties configurationVariables = configuration.getVariables();
		if (configurationVariables != null) {
			variablesContext.putAll(configurationVariables);
		}
		applyIncludes(source, variablesContext);
	}

	private void applyIncludes(Node source, final Properties variablesContext) {
		if (source.getNodeName().equals("include")) {
			Properties fullContext;

			String refId = getStringAttribute(source, "refid");
			refId = PropertyParser.parse(refId, variablesContext);
			Node toInclude = findSqlFragment(refId);
			Properties newVariablesContext = getVariablesContext(source, variablesContext);
			if (!newVariablesContext.isEmpty()) {
				fullContext = new Properties();
				fullContext.putAll(variablesContext);
				fullContext.putAll(newVariablesContext);
			} else {
				fullContext = variablesContext;
			}
			applyIncludes(toInclude, fullContext);
			if (toInclude.getOwnerDocument() != source.getOwnerDocument()) {
				toInclude = source.getOwnerDocument().importNode(toInclude, true);
			}
			source.getParentNode().replaceChild(toInclude, source);
			while (toInclude.hasChildNodes()) {
				toInclude.getParentNode().insertBefore(toInclude.getFirstChild(), toInclude);
			}
			toInclude.getParentNode().removeChild(toInclude);
		} else if (source.getNodeType() == Node.ELEMENT_NODE) {
			NodeList children = source.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				applyIncludes(children.item(i), variablesContext);
			}
		} else if (source.getNodeType() == Node.ATTRIBUTE_NODE && !variablesContext.isEmpty()) {
			source.setNodeValue(PropertyParser.parse(source.getNodeValue(), variablesContext));
		} else if (source.getNodeType() == Node.TEXT_NODE && !variablesContext.isEmpty()) {
			source.setNodeValue(PropertyParser.parse(source.getNodeValue(), variablesContext));
		}
	}

	private Node findSqlFragment(String refId) {
		refId = builderAssistant.applyCurrentNamespace(refId, true);
		try {
			XNode nodeToInclude = configuration.getSqlFragments().get(refId);
			return nodeToInclude.getNode().cloneNode(true);
		} catch (IllegalArgumentException e) {
			throw new IncompleteElementException("Could not find SQL statement to include with refId '" + refId + "'",
					e);
		}
	}

	private String getStringAttribute(Node node, String name) {
		return node.getAttributes().getNamedItem(name).getNodeValue();
	}

	/**
	 * Read placholders and their values from include node definition. 
	 * @param node Include node instance
	 * @param inheritedVariablesContext Current context used for replace variables in new variables values
	 * @return variables context from include instance (no inherited values)
	 */
	private Properties getVariablesContext(Node node, Properties inheritedVariablesContext) {
		Properties variablesContext = new Properties();
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node n = children.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				String name = getStringAttribute(n, "name");
				String value = getStringAttribute(n, "value");
				value = PropertyParser.parse(value, inheritedVariablesContext);
				Object originalValue = variablesContext.put(name, value);
				if (originalValue != null) {
					throw new BuilderException("Variable " + name + " defined twice in the same include definition");
				}
			}
		}
		return variablesContext;
	}
}
