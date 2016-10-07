/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.builder.xml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import cn.xianyijun.orm.builder.BaseBuilder;
import cn.xianyijun.orm.builder.MapperBuilderAssistant;
import cn.xianyijun.orm.builder.ResultMapResolver;
import cn.xianyijun.orm.cache.Cache;
import cn.xianyijun.orm.cache.CacheRefResolver;
import cn.xianyijun.orm.cache.impl.LruCache;
import cn.xianyijun.orm.cache.impl.PerpetualCache;
import cn.xianyijun.orm.core.ResultSetHandler;
import cn.xianyijun.orm.core.StatementHandler;
import cn.xianyijun.orm.exception.BuilderException;
import cn.xianyijun.orm.exception.IncompleteElementException;
import cn.xianyijun.orm.mapping.ParameterMapping;
import cn.xianyijun.orm.mapping.ResultFlag;
import cn.xianyijun.orm.mapping.ResultMap;
import cn.xianyijun.orm.mapping.ResultMapping;
import cn.xianyijun.orm.parse.XMLMapperEntityResolver;
import cn.xianyijun.orm.parse.XNode;
import cn.xianyijun.orm.parse.XPathParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type Xml mapper builder.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class XMLMapperBuilder extends BaseBuilder {
    private static final Logger logger = LoggerFactory.getLogger(XMLMapperBuilder.class);
    private XPathParser parser;
    private MapperBuilderAssistant builderAssistant;
    private Map<String, XNode> sqlFragments;
    private String resource;

    /**
     * Instantiates a new Xml mapper builder.
     *
     * @param inputStream   the input stream
     * @param configuration the configuration
     * @param resource      the resource
     * @param sqlFragments  the sql fragments
     * @param namespace     the namespace
     */
    public XMLMapperBuilder(InputStream inputStream, StatementHandler.Configuration configuration, String resource,
                            Map<String, XNode> sqlFragments, String namespace) {
        this(inputStream, configuration, resource, sqlFragments);
        this.builderAssistant.setCurrentNamespace(namespace);
    }

    /**
     * Instantiates a new Xml mapper builder.
     *
     * @param inputStream   the input stream
     * @param configuration the configuration
     * @param resource      the resource
     * @param sqlFragments  the sql fragments
     */
    public XMLMapperBuilder(InputStream inputStream, StatementHandler.Configuration configuration, String resource,
                            Map<String, XNode> sqlFragments) {
        this(new XPathParser(inputStream, true, configuration.getVariables(), new XMLMapperEntityResolver()),
                configuration, resource, sqlFragments);
    }

    private XMLMapperBuilder(XPathParser parser, StatementHandler.Configuration configuration, String resource,
                             Map<String, XNode> sqlFragments) {
        super(configuration);
        this.builderAssistant = new MapperBuilderAssistant(configuration, resource);
        this.parser = parser;
        this.sqlFragments = sqlFragments;
        this.resource = resource;
    }

    /**
     * Parse.
     */
    public void parse() {
        if (!configuration.isResourceLoaded(resource)) {
            configurationElement(parser.evalNode("/mapper"));
            configuration.addLoadedResource(resource);
            bindMapperForNamespace();
        }

        parsePendingResultMaps();
        parsePendingCacheRefs();
        parsePendingStatements();
    }

    /**
     * Gets sql fragment.
     *
     * @param refId the ref id
     * @return the sql fragment
     */
    public XNode getSqlFragment(String refId) {
        return sqlFragments.get(refId);
    }

    private void configurationElement(XNode context) {
        try {
            String namespace = context.getStringAttribute("namespace");
            if (namespace == null || namespace.isEmpty()) {
                throw new BuilderException("Mapper's namespace cannot be empty");
            }
            builderAssistant.setCurrentNamespace(namespace);
            cacheRefElement(context.evalNode("cache-ref"));
            cacheElement(context.evalNode("cache"));
            parameterMapElement(context.evalNodes("/mapper/parameterMap"));
            resultMapElements(context.evalNodes("/mapper/resultMap"));
            sqlElement(context.evalNodes("/mapper/sql"));
            buildStatementFromContext(context.evalNodes("select|insert|update|delete"));
        } catch (Exception e) {
            throw new BuilderException("Error parsing Mapper XML. Cause: " + e, e);
        }
    }

    private void buildStatementFromContext(List<XNode> list) {
        if (configuration.getDatabaseId() != null) {
            buildStatementFromContext(list, configuration.getDatabaseId());
        }
        buildStatementFromContext(list, null);
    }

    private void buildStatementFromContext(List<XNode> list, String requiredDatabaseId) {
        for (XNode context : list) {
            final XMLStatementBuilder statementParser = new XMLStatementBuilder(configuration, builderAssistant,
                    context, requiredDatabaseId);
            try {
                statementParser.parseStatementNode();
            } catch (IncompleteElementException e) {
                configuration.addIncompleteStatement(statementParser);
            }
        }
    }

    private void parsePendingResultMaps() {
        Collection<ResultMapResolver> incompleteResultMaps = configuration.getIncompleteResultMaps();
        synchronized (incompleteResultMaps) {
            Iterator<ResultMapResolver> iter = incompleteResultMaps.iterator();
            while (iter.hasNext()) {
                try {
                    iter.next().resolve();
                    iter.remove();
                } catch (IncompleteElementException e) {
                    // ResultMap is still missing a resource...
                }
            }
//            incompleteResultMaps.forEach(ResultMapResolver::resolve);
        }
    }

    private void parsePendingCacheRefs() {
        Collection<CacheRefResolver> incompleteCacheRefs = configuration.getIncompleteCacheRefs();
        synchronized (incompleteCacheRefs) {
            Iterator<CacheRefResolver> iter = incompleteCacheRefs.iterator();
            while (iter.hasNext()) {
                try {
                    iter.next().resolveCacheRef();
                    iter.remove();
                } catch (IncompleteElementException e) {
                    // Cache ref is still missing a resource...
                }
            }
        }
    }

    private void parsePendingStatements() {
        Collection<XMLStatementBuilder> incompleteStatements = configuration.getIncompleteStatements();
        synchronized (incompleteStatements) {
            Iterator<XMLStatementBuilder> iter = incompleteStatements.iterator();
            while (iter.hasNext()) {
                try {
                    iter.next().parseStatementNode();
                    iter.remove();
                } catch (IncompleteElementException e) {
                }
            }
        }
    }

    private void cacheRefElement(XNode context) {
        if (context != null) {
            configuration.addCacheRef(builderAssistant.getCurrentNamespace(), context.getStringAttribute("namespace"));
            CacheRefResolver cacheRefResolver = new CacheRefResolver(builderAssistant,
                    context.getStringAttribute("namespace"));
            try {
                cacheRefResolver.resolveCacheRef();
            } catch (IncompleteElementException e) {
                configuration.addIncompleteCacheRef(cacheRefResolver);
            }
        }
    }

    private void cacheElement(XNode context) {
        if (context != null) {

            Class<? extends Cache> typeClass = PerpetualCache.class;
            Class<? extends Cache> evictionClass = LruCache.class;

            Long flushInterval = context.getLongAttribute("flushInterval");
            Integer size = context.getIntAttribute("size");
            boolean readWrite = !context.getBooleanAttribute("readOnly", false);
            boolean blocking = context.getBooleanAttribute("blocking", false);
            Properties props = context.getChildrenAsProperties();
            builderAssistant.useNewCache(typeClass, evictionClass, flushInterval, size, readWrite, blocking, props);
        }
    }

    private void parameterMapElement(List<XNode> list) {
        for (XNode parameterMapNode : list) {
            String id = parameterMapNode.getStringAttribute("id");
            String type = parameterMapNode.getStringAttribute("type");
            Class<?> parameterClass = resolveClass(type);
            List<XNode> parameterNodes = parameterMapNode.evalNodes("parameter");
            List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
            for (XNode parameterNode : parameterNodes) {
                String property = parameterNode.getStringAttribute("property");
                String resultMap = parameterNode.getStringAttribute("resultMap");
                Integer numericScale = parameterNode.getIntAttribute("numericScale");
                String javaType = parameterNode.getStringAttribute("javaType");
                String typeHandler = parameterNode.getStringAttribute("typeHandler");
                Class<?> javaTypeClass = resolveClass(javaType);
                @SuppressWarnings("unchecked")
                Class<? extends ResultSetHandler.TypeHandler<?>> typeHandlerClass = (Class<? extends ResultSetHandler.TypeHandler<?>>) resolveClass(
                        typeHandler);
                ParameterMapping parameterMapping = builderAssistant.buildParameterMapping(parameterClass, property,
                        resultMap, numericScale, javaTypeClass, typeHandlerClass);
                parameterMappings.add(parameterMapping);
            }
            builderAssistant.addParameterMap(id, parameterClass, parameterMappings);
        }
    }

    private void resultMapElements(List<XNode> list) throws Exception {
        for (XNode resultMapNode : list) {
            try {
                resultMapElement(resultMapNode);
            } catch (IncompleteElementException e) {
            }
        }
    }

    private ResultMap resultMapElement(XNode resultMapNode) throws Exception {
        return resultMapElement(resultMapNode, Collections.<ResultMapping>emptyList());
    }

    private ResultMap resultMapElement(XNode resultMapNode, List<ResultMapping> additionalResultMappings)
            throws Exception {
        String id = resultMapNode.getStringAttribute("id", resultMapNode.getValueBasedIdentifier());
        String type = resultMapNode.getStringAttribute("type", resultMapNode.getStringAttribute("ofType",
                resultMapNode.getStringAttribute("resultType", resultMapNode.getStringAttribute("javaType"))));
        String extend = resultMapNode.getStringAttribute("extends");
        Boolean autoMapping = resultMapNode.getBooleanAttribute("autoMapping");
        Class<?> typeClass = resolveClass(type);
        List<ResultMapping> resultMappings = new ArrayList<ResultMapping>();
        resultMappings.addAll(additionalResultMappings);
        List<XNode> resultChildren = resultMapNode.getChildren();
        for (XNode resultChild : resultChildren) {
            if ("constructor".equals(resultChild.getName())) {
                processConstructorElement(resultChild, typeClass, resultMappings);
            } else {
                List<ResultFlag> flags = new ArrayList<ResultFlag>();
                if ("id".equals(resultChild.getName())) {
                    flags.add(ResultFlag.ID);
                }
                resultMappings.add(buildResultMappingFromContext(resultChild, typeClass, flags));
            }
        }
        ResultMapResolver resultMapResolver = new ResultMapResolver(builderAssistant, id, typeClass, extend,
                resultMappings, autoMapping);
        try {
            return resultMapResolver.resolve();
        } catch (IncompleteElementException e) {
            configuration.addIncompleteResultMap(resultMapResolver);
            throw e;
        }
    }

    private void processConstructorElement(XNode resultChild, Class<?> resultType, List<ResultMapping> resultMappings)
            throws Exception {
        List<XNode> argChildren = resultChild.getChildren();
        for (XNode argChild : argChildren) {
            List<ResultFlag> flags = new ArrayList<ResultFlag>();
            flags.add(ResultFlag.CONSTRUCTOR);
            if ("idArg".equals(argChild.getName())) {
                flags.add(ResultFlag.ID);
            }
            resultMappings.add(buildResultMappingFromContext(argChild, resultType, flags));
        }
    }

    private void sqlElement(List<XNode> list) throws Exception {
        if (configuration.getDatabaseId() != null) {
            sqlElement(list, configuration.getDatabaseId());
        }
        sqlElement(list, null);
    }

    private void sqlElement(List<XNode> list, String requiredDatabaseId) throws Exception {
        for (XNode context : list) {
            String databaseId = context.getStringAttribute("databaseId");
            String id = context.getStringAttribute("id");
            id = builderAssistant.applyCurrentNamespace(id, false);
            if (databaseIdMatchesCurrent(id, databaseId, requiredDatabaseId)) {
                sqlFragments.put(id, context);
            }
        }
    }

    private boolean databaseIdMatchesCurrent(String id, String databaseId, String requiredDatabaseId) {
        if (requiredDatabaseId != null) {
            if (!requiredDatabaseId.equals(databaseId)) {
                return false;
            }
        } else {
            if (databaseId != null) {
                return false;
            }
            if (this.sqlFragments.containsKey(id)) {
                XNode context = this.sqlFragments.get(id);
                if (context.getStringAttribute("databaseId") != null) {
                    return false;
                }
            }
        }
        return true;
    }

    private ResultMapping buildResultMappingFromContext(XNode context, Class<?> resultType, List<ResultFlag> flags)
            throws Exception {
        String property = context.getStringAttribute("property");
        String column = context.getStringAttribute("column");
        String nestedSelect = context.getStringAttribute("select");
        String nestedResultMap = context.getStringAttribute("resultMap",
                processNestedResultMappings(context, Collections.<ResultMapping>emptyList()));
        String notNullColumn = context.getStringAttribute("notNullColumn");
        String columnPrefix = context.getStringAttribute("columnPrefix");
        String resultSet = context.getStringAttribute("resultSet");
        String foreignColumn = context.getStringAttribute("foreignColumn");
        boolean lazy = "lazy".equals(
                context.getStringAttribute("fetchType", configuration.isLazyLoadingEnabled() ? "lazy" : "eager"));
        return builderAssistant.buildResultMapping(resultType, property, column, nestedSelect, nestedResultMap,
                notNullColumn, columnPrefix, flags, resultSet, foreignColumn, lazy);
    }

    private String processNestedResultMappings(XNode context, List<ResultMapping> resultMappings) throws Exception {
        if ("association".equals(context.getName()) || "collection".equals(context.getName())
                || "case".equals(context.getName())) {
            if (context.getStringAttribute("select") == null) {
                ResultMap resultMap = resultMapElement(context, resultMappings);
                return resultMap.getId();
            }
        }
        return null;
    }

    private void bindMapperForNamespace() {
        String namespace = builderAssistant.getCurrentNamespace();
        if (namespace != null) {
            Class<?> boundType = null;
            boundType = resolveClass(namespace);
            if (boundType != null) {
                if (!configuration.hasMapper(boundType)) {
                    configuration.addLoadedResource("namespace:" + namespace);
                    configuration.addMapper(boundType);
                }
            }
        }
    }

}
