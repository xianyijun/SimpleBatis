/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.script.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.xianyijun.orm.core.SqlSession;
import cn.xianyijun.orm.core.StatementHandler;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.xianyijun.orm.builder.BaseBuilder;
import cn.xianyijun.orm.exception.BuilderException;
import cn.xianyijun.orm.parse.XNode;
import cn.xianyijun.orm.script.defaults.RawSqlSource;

/**
 * The type Xml script builder.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class XMLScriptBuilder extends BaseBuilder {

  private XNode context;
  private boolean isDynamic;
  private Class<?> parameterType;

  /**
   * Instantiates a new Xml script builder.
   *
   * @param configuration the configuration
   * @param context       the context
   */
  public XMLScriptBuilder(StatementHandler.Configuration configuration, XNode context) {
    this(configuration, context, null);
  }

  /**
   * Instantiates a new Xml script builder.
   *
   * @param configuration the configuration
   * @param context       the context
   * @param parameterType the parameter type
   */
  public XMLScriptBuilder(StatementHandler.Configuration configuration, XNode context, Class<?> parameterType) {
    super(configuration);
    this.context = context;
    this.parameterType = parameterType;
  }

  /**
   * Parse script node sql source.
   *
   * @return the sql source
   */
  public SqlSession.SqlSource parseScriptNode() {
    List<SqlNode> contents = parseDynamicTags(context);
    MixedSqlNode rootSqlNode = new MixedSqlNode(contents);
    SqlSession.SqlSource sqlSource = null;
    if (isDynamic) {
      sqlSource = new DynamicSqlSource(configuration, rootSqlNode);
    } else {
      sqlSource = new RawSqlSource(configuration, rootSqlNode, parameterType);
    }
    return sqlSource;
  }

  /**
   * Parse dynamic tags list.
   *
   * @param node the node
   * @return the list
   */
  List<SqlNode> parseDynamicTags(XNode node) {
    List<SqlNode> contents = new ArrayList<SqlNode>();
    NodeList children = node.getNode().getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
      XNode child = node.newXNode(children.item(i));
      if (child.getNode().getNodeType() == Node.CDATA_SECTION_NODE || child.getNode().getNodeType() == Node.TEXT_NODE) {
        String data = child.getStringBody("");
        TextSqlNode textSqlNode = new TextSqlNode(data);
        if (textSqlNode.isDynamic()) {
          contents.add(textSqlNode);
          isDynamic = true;
        } else {
          contents.add(new StaticTextSqlNode(data));
        }
      } else if (child.getNode().getNodeType() == Node.ELEMENT_NODE) { // issue #628
        String nodeName = child.getNode().getNodeName();
        NodeHandler handler = nodeHandlers(nodeName);
        if (handler == null) {
          throw new BuilderException("Unknown element <" + nodeName + "> in SQL statement.");
        }
        handler.handleNode(child, contents);
        isDynamic = true;
      }
    }
    return contents;
  }

  /**
   * Node handlers node handler.
   *
   * @param nodeName the node name
   * @return the node handler
   */
  NodeHandler nodeHandlers(String nodeName) {
    Map<String, NodeHandler> map = new HashMap<String, NodeHandler>();
    map.put("trim", new TrimHandler());
    map.put("where", new WhereHandler());
    map.put("set", new SetHandler());
    map.put("foreach", new ForEachHandler());
    map.put("if", new IfHandler());
    map.put("choose", new ChooseHandler());
    map.put("when", new IfHandler());
    map.put("otherwise", new OtherwiseHandler());
    map.put("bind", new BindHandler());
    return map.get(nodeName);
  }

  private interface NodeHandler {
    /**
     * Handle node.
     *
     * @param nodeToHandle   the node to handle
     * @param targetContents the target contents
     */
    void handleNode(XNode nodeToHandle, List<SqlNode> targetContents);
  }

  private class BindHandler implements NodeHandler {
    /**
     * Instantiates a new Bind handler.
     */
    public BindHandler() {
      // Prevent Synthetic Access
    }

    /**
     * Handle node.
     *
     * @param nodeToHandle   the node to handle
     * @param targetContents the target contents
     */
    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      final String name = nodeToHandle.getStringAttribute("name");
      final String expression = nodeToHandle.getStringAttribute("value");
      final VarDeclSqlNode node = new VarDeclSqlNode(name, expression);
      targetContents.add(node);
    }
  }

  private class TrimHandler implements NodeHandler {
    /**
     * Instantiates a new Trim handler.
     */
    public TrimHandler() {
      // Prevent Synthetic Access
    }

    /**
     * Handle node.
     *
     * @param nodeToHandle   the node to handle
     * @param targetContents the target contents
     */
    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      List<SqlNode> contents = parseDynamicTags(nodeToHandle);
      MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
      String prefix = nodeToHandle.getStringAttribute("prefix");
      String prefixOverrides = nodeToHandle.getStringAttribute("prefixOverrides");
      String suffix = nodeToHandle.getStringAttribute("suffix");
      String suffixOverrides = nodeToHandle.getStringAttribute("suffixOverrides");
      TrimSqlNode trim = new TrimSqlNode(configuration, mixedSqlNode, prefix, prefixOverrides, suffix, suffixOverrides);
      targetContents.add(trim);
    }
  }

  private class WhereHandler implements NodeHandler {
    /**
     * Instantiates a new Where handler.
     */
    public WhereHandler() {
      // Prevent Synthetic Access
    }

    /**
     * Handle node.
     *
     * @param nodeToHandle   the node to handle
     * @param targetContents the target contents
     */
    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      List<SqlNode> contents = parseDynamicTags(nodeToHandle);
      MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
      WhereSqlNode where = new WhereSqlNode(configuration, mixedSqlNode);
      targetContents.add(where);
    }
  }

  private class SetHandler implements NodeHandler {
    /**
     * Instantiates a new Set handler.
     */
    public SetHandler() {
      // Prevent Synthetic Access
    }

    /**
     * Handle node.
     *
     * @param nodeToHandle   the node to handle
     * @param targetContents the target contents
     */
    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      List<SqlNode> contents = parseDynamicTags(nodeToHandle);
      MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
      SetSqlNode set = new SetSqlNode(configuration, mixedSqlNode);
      targetContents.add(set);
    }
  }

  private class ForEachHandler implements NodeHandler {
    /**
     * Instantiates a new For each handler.
     */
    public ForEachHandler() {
      // Prevent Synthetic Access
    }

    /**
     * Handle node.
     *
     * @param nodeToHandle   the node to handle
     * @param targetContents the target contents
     */
    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      List<SqlNode> contents = parseDynamicTags(nodeToHandle);
      MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
      String collection = nodeToHandle.getStringAttribute("collection");
      String item = nodeToHandle.getStringAttribute("item");
      String index = nodeToHandle.getStringAttribute("index");
      String open = nodeToHandle.getStringAttribute("open");
      String close = nodeToHandle.getStringAttribute("close");
      String separator = nodeToHandle.getStringAttribute("separator");
      ForEachSqlNode forEachSqlNode = new ForEachSqlNode(configuration, mixedSqlNode, collection, index, item, open, close, separator);
      targetContents.add(forEachSqlNode);
    }
  }

  private class IfHandler implements NodeHandler {
    /**
     * Instantiates a new If handler.
     */
    public IfHandler() {
      // Prevent Synthetic Access
    }

    /**
     * Handle node.
     *
     * @param nodeToHandle   the node to handle
     * @param targetContents the target contents
     */
    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      List<SqlNode> contents = parseDynamicTags(nodeToHandle);
      MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
      String test = nodeToHandle.getStringAttribute("test");
      IfSqlNode ifSqlNode = new IfSqlNode(mixedSqlNode, test);
      targetContents.add(ifSqlNode);
    }
  }

  private class OtherwiseHandler implements NodeHandler {
    /**
     * Instantiates a new Otherwise handler.
     */
    public OtherwiseHandler() {
      // Prevent Synthetic Access
    }

    /**
     * Handle node.
     *
     * @param nodeToHandle   the node to handle
     * @param targetContents the target contents
     */
    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      List<SqlNode> contents = parseDynamicTags(nodeToHandle);
      MixedSqlNode mixedSqlNode = new MixedSqlNode(contents);
      targetContents.add(mixedSqlNode);
    }
  }

  private class ChooseHandler implements NodeHandler {
    /**
     * Instantiates a new Choose handler.
     */
    public ChooseHandler() {
      // Prevent Synthetic Access
    }

    /**
     * Handle node.
     *
     * @param nodeToHandle   the node to handle
     * @param targetContents the target contents
     */
    @Override
    public void handleNode(XNode nodeToHandle, List<SqlNode> targetContents) {
      List<SqlNode> whenSqlNodes = new ArrayList<SqlNode>();
      List<SqlNode> otherwiseSqlNodes = new ArrayList<SqlNode>();
      handleWhenOtherwiseNodes(nodeToHandle, whenSqlNodes, otherwiseSqlNodes);
      SqlNode defaultSqlNode = getDefaultSqlNode(otherwiseSqlNodes);
      ChooseSqlNode chooseSqlNode = new ChooseSqlNode(whenSqlNodes, defaultSqlNode);
      targetContents.add(chooseSqlNode);
    }

    private void handleWhenOtherwiseNodes(XNode chooseSqlNode, List<SqlNode> ifSqlNodes, List<SqlNode> defaultSqlNodes) {
      List<XNode> children = chooseSqlNode.getChildren();
      for (XNode child : children) {
        String nodeName = child.getNode().getNodeName();
        NodeHandler handler = nodeHandlers(nodeName);
        if (handler instanceof IfHandler) {
          handler.handleNode(child, ifSqlNodes);
        } else if (handler instanceof OtherwiseHandler) {
          handler.handleNode(child, defaultSqlNodes);
        }
      }
    }

    private SqlNode getDefaultSqlNode(List<SqlNode> defaultSqlNodes) {
      SqlNode defaultSqlNode = null;
      if (defaultSqlNodes.size() == 1) {
        defaultSqlNode = defaultSqlNodes.get(0);
      } else if (defaultSqlNodes.size() > 1) {
        throw new BuilderException("Too many default (otherwise) elements in choose statement.");
      }
      return defaultSqlNode;
    }
  }

}