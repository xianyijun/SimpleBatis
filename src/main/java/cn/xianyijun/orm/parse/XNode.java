/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * The type X node.
 *
 * @author xianyijun          对Node的xml封装
 */
public class XNode {
    private Node node;
    private String name;
    private String body;
    private Properties attributes;
    private Properties variables;
    private XPathParser xpathParser;

    /**
     * Instantiates a new X node.
     *
     * @param xpathParser the xpath parser
     * @param node        the node
     * @param variables   the variables
     */
    public XNode(XPathParser xpathParser, Node node, Properties variables) {
        this.xpathParser = xpathParser;
        this.node = node;
        this.name = node.getNodeName();
        this.variables = variables;
        this.attributes = parseAttributes(node);
        this.body = parseBody(node);
    }

    /**
     * New x node x node.
     *
     * @param node the node
     * @return the x node
     */
    public XNode newXNode(Node node) {
        return new XNode(xpathParser, node, variables);
    }

    private Properties parseAttributes(Node n) {
        Properties attributes = new Properties();
        NamedNodeMap attributeNodes = n.getAttributes();
        if (attributeNodes != null) {
            for (int i = 0; i < attributeNodes.getLength(); i++) {
                Node attribute = attributeNodes.item(i);
                String value = PropertyParser.parse(attribute.getNodeValue(), variables);
                attributes.put(attribute.getNodeName(), value);
            }
        }
        return attributes;
    }

    private String parseBody(Node node) {
        String data = getBodyData(node);
        if (data == null) {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                data = getBodyData(child);
                if (data != null) {
                    break;
                }
            }
        }
        return data;
    }

    private String getBodyData(Node child) {
        if (child.getNodeType() == Node.CDATA_SECTION_NODE || child.getNodeType() == Node.TEXT_NODE) {
            String data = ((CharacterData) child).getData();
            data = PropertyParser.parse(data, variables);
            return data;
        }
        return null;
    }

    /**
     * Eval string string.
     *
     * @param expression the expression
     * @return the string
     */
    public String evalString(String expression) {
        return xpathParser.evalString(node, expression);
    }

    /**
     * Eval boolean boolean.
     *
     * @param expression the expression
     * @return the boolean
     */
    public Boolean evalBoolean(String expression) {
        return xpathParser.evalBoolean(node, expression);
    }

    /**
     * Eval double double.
     *
     * @param expression the expression
     * @return the double
     */
    public Double evalDouble(String expression) {
        return xpathParser.evalDouble(node, expression);
    }

    /**
     * Eval nodes list.
     *
     * @param expression the expression
     * @return the list
     */
    public List<XNode> evalNodes(String expression) {
        return xpathParser.evalNodes(node, expression);
    }

    /**
     * Eval node x node.
     *
     * @param expression the expression
     * @return the x node
     */
    public XNode evalNode(String expression) {
        return xpathParser.evalNode(node, expression);
    }

    /**
     * Gets node.
     *
     * @return the node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets string body.
     *
     * @return the string body
     */
    public String getStringBody() {
        return getStringBody(null);
    }

    /**
     * Gets string body.
     *
     * @param def the def
     * @return the string body
     */
    public String getStringBody(String def) {
        if (body == null) {
            return def;
        } else {
            return body;
        }
    }

    /**
     * Gets children as properties.
     *
     * @return the children as properties
     */
    public Properties getChildrenAsProperties() {
        Properties properties = new Properties();
        for (XNode child : getChildren()) {
            String name = child.getStringAttribute("name");
            String value = child.getStringAttribute("value");
            if (name != null && value != null) {
                properties.put(name, value);
            }
        }
        return properties;
    }

    /**
     * Gets children.
     *
     * @return the children
     */
    public List<XNode> getChildren() {
        List<XNode> children = new ArrayList<>();
        NodeList nodeList = node.getChildNodes();
        if (nodeList != null) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    children.add(new XNode(xpathParser, node, variables));
                }
            }
        }
        return children;
    }

    /**
     * Gets string attribute.
     *
     * @param name the name
     * @return the string attribute
     */
    public String getStringAttribute(String name) {
        return getStringAttribute(name, null);
    }

    /**
     * Gets string attribute.
     *
     * @param name the name
     * @param def  the def
     * @return the string attribute
     */
    public String getStringAttribute(String name, String def) {
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return value;
        }
    }

    /**
     * Gets boolean body.
     *
     * @return the boolean body
     */
    public Boolean getBooleanBody() {
        return getBooleanBody(null);
    }

    /**
     * Gets boolean body.
     *
     * @param def the def
     * @return the boolean body
     */
    public Boolean getBooleanBody(Boolean def) {
        if (body == null) {
            return def;
        } else {
            return Boolean.valueOf(body);
        }
    }

    /**
     * Gets int body.
     *
     * @return the int body
     */
    public Integer getIntBody() {
        return getIntBody(null);
    }

    /**
     * Gets int body.
     *
     * @param def the def
     * @return the int body
     */
    public Integer getIntBody(Integer def) {
        if (body == null) {
            return def;
        } else {
            return Integer.parseInt(body);
        }
    }

    /**
     * Gets long body.
     *
     * @return the long body
     */
    public Long getLongBody() {
        return getLongBody(null);
    }

    /**
     * Gets long body.
     *
     * @param def the def
     * @return the long body
     */
    public Long getLongBody(Long def) {
        if (body == null) {
            return def;
        } else {
            return Long.parseLong(body);
        }
    }

    /**
     * Gets double body.
     *
     * @return the double body
     */
    public Double getDoubleBody() {
        return getDoubleBody(null);
    }

    /**
     * Gets double body.
     *
     * @param def the def
     * @return the double body
     */
    public Double getDoubleBody(Double def) {
        if (body == null) {
            return def;
        } else {
            return Double.parseDouble(body);
        }
    }

    /**
     * Gets float body.
     *
     * @return the float body
     */
    public Float getFloatBody() {
        return getFloatBody(null);
    }

    /**
     * Gets float body.
     *
     * @param def the def
     * @return the float body
     */
    public Float getFloatBody(Float def) {
        if (body == null) {
            return def;
        } else {
            return Float.parseFloat(body);
        }
    }

    /**
     * Gets enum attribute.
     *
     * @param <T>      the type parameter
     * @param enumType the enum type
     * @param name     the name
     * @return the enum attribute
     */
    public <T extends Enum<T>> T getEnumAttribute(Class<T> enumType, String name) {
        return getEnumAttribute(enumType, name, null);
    }

    /**
     * Gets enum attribute.
     *
     * @param <T>      the type parameter
     * @param enumType the enum type
     * @param name     the name
     * @param def      the def
     * @return the enum attribute
     */
    public <T extends Enum<T>> T getEnumAttribute(Class<T> enumType, String name, T def) {
        String value = getStringAttribute(name);
        if (value == null) {
            return def;
        } else {
            return Enum.valueOf(enumType, value);
        }
    }

    /**
     * Gets boolean attribute.
     *
     * @param name the name
     * @return the boolean attribute
     */
    public Boolean getBooleanAttribute(String name) {
        return getBooleanAttribute(name, null);
    }

    /**
     * Gets boolean attribute.
     *
     * @param name the name
     * @param def  the def
     * @return the boolean attribute
     */
    public Boolean getBooleanAttribute(String name, Boolean def) {
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Boolean.valueOf(value);
        }
    }

    /**
     * Gets int attribute.
     *
     * @param name the name
     * @return the int attribute
     */
    public Integer getIntAttribute(String name) {
        return getIntAttribute(name, null);
    }

    /**
     * Gets int attribute.
     *
     * @param name the name
     * @param def  the def
     * @return the int attribute
     */
    public Integer getIntAttribute(String name, Integer def) {
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Integer.parseInt(value);
        }
    }

    /**
     * Gets long attribute.
     *
     * @param name the name
     * @return the long attribute
     */
    public Long getLongAttribute(String name) {
        return getLongAttribute(name, null);
    }

    /**
     * Gets long attribute.
     *
     * @param name the name
     * @param def  the def
     * @return the long attribute
     */
    public Long getLongAttribute(String name, Long def) {
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Long.parseLong(value);
        }
    }

    /**
     * Gets double attribute.
     *
     * @param name the name
     * @return the double attribute
     */
    public Double getDoubleAttribute(String name) {
        return getDoubleAttribute(name, null);
    }

    /**
     * Gets double attribute.
     *
     * @param name the name
     * @param def  the def
     * @return the double attribute
     */
    public Double getDoubleAttribute(String name, Double def) {
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Double.parseDouble(value);
        }
    }

    /**
     * Gets float attribute.
     *
     * @param name the name
     * @return the float attribute
     */
    public Float getFloatAttribute(String name) {
        return getFloatAttribute(name, null);
    }

    /**
     * Gets float attribute.
     *
     * @param name the name
     * @param def  the def
     * @return the float attribute
     */
    public Float getFloatAttribute(String name, Float def) {
        String value = attributes.getProperty(name);
        if (value == null) {
            return def;
        } else {
            return Float.parseFloat(value);
        }
    }

    /**
     * Gets parent.
     *
     * @return the parent
     */
    public XNode getParent() {
        Node parent = node.getParentNode();
        if (parent == null || !(parent instanceof Element)) {
            return null;
        } else {
            return new XNode(xpathParser, parent, variables);
        }
    }

    /**
     * Gets value based identifier.
     *
     * @return the value based identifier
     */
    public String getValueBasedIdentifier() {
        StringBuilder builder = new StringBuilder();
        XNode current = this;
        while (current != null) {
            if (current != this) {
                builder.insert(0, "_");
            }
            String value = current.getStringAttribute("id",
                    current.getStringAttribute("value", current.getStringAttribute("property", null)));
            if (value != null) {
                value = value.replace('.', '_');
                builder.insert(0, "]");
                builder.insert(0, value);
                builder.insert(0, "[");
            }
            builder.insert(0, current.getName());
            current = current.getParent();
        }
        return builder.toString();
    }
}
