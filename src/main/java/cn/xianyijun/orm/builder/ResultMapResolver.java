/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.builder;

import java.util.List;

import cn.xianyijun.orm.mapping.ResultMap;
import cn.xianyijun.orm.mapping.ResultMapping;

/**
 * The type Result map resolver.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class ResultMapResolver {
    private final MapperBuilderAssistant assistant;
    private String id;
    private Class<?> type;
    private String extend;
    private List<ResultMapping> resultMappings;
    private Boolean autoMapping;

    /**
     * Instantiates a new Result map resolver.
     *
     * @param assistant      the assistant
     * @param id             the id
     * @param type           the type
     * @param extend         the extend
     * @param resultMappings the result mappings
     * @param autoMapping    the auto mapping
     */
    public ResultMapResolver(MapperBuilderAssistant assistant, String id, Class<?> type, String extend,
                             List<ResultMapping> resultMappings, Boolean autoMapping) {
        this.assistant = assistant;
        this.id = id;
        this.type = type;
        this.extend = extend;
        this.resultMappings = resultMappings;
        this.autoMapping = autoMapping;
    }

    /**
     * Resolve result map.
     *
     * @return the result map
     */
    public ResultMap resolve() {
        return assistant.addResultMap(this.id, this.type, this.extend, this.resultMappings, this.autoMapping);
    }

}
