/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.parse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cn.xianyijun.orm.io.Resources;

/**
 * The type Xml mapper entity resolver.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class XMLMapperEntityResolver implements EntityResolver {
	private static final Logger logger = LoggerFactory.getLogger(XMLMapperEntityResolver.class);

	private static final String IBATIS_CONFIG_SYSTEM = "ibatis-3-config.dtd";
	private static final String IBATIS_MAPPER_SYSTEM = "ibatis-3-mapper.dtd";
	private static final String MYBATIS_CONFIG_SYSTEM = "mybatis-3-config.dtd";
	private static final String MYBATIS_MAPPER_SYSTEM = "mybatis-3-mapper.dtd";

	// dtd path
	private static final String MYBATIS_CONFIG_DTD = "cn/xianyijun/orm/builder/mybatis-3-config.dtd";
	private static final String MYBATIS_MAPPER_DTD = "cn/xianyijun/orm/builder/mybatis-3-mapper.dtd";

	/**
	 * Resolve entity input source.
	 *
	 * @param publicId the public id
	 * @param systemId the system id
	 * @return the input source
	 * @throws SAXException the sax exception
	 * @throws IOException  the io exception
	 */
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		try {
			if (systemId != null) {
				String lowerCaseSystemId = systemId.toLowerCase(Locale.ENGLISH);
				if (lowerCaseSystemId.contains(MYBATIS_CONFIG_SYSTEM)
						|| lowerCaseSystemId.contains(IBATIS_CONFIG_SYSTEM)) {
					return getInputSource(MYBATIS_CONFIG_DTD, publicId, systemId);
				} else if (lowerCaseSystemId.contains(MYBATIS_MAPPER_SYSTEM)
						|| lowerCaseSystemId.contains(IBATIS_MAPPER_SYSTEM)) {
					return getInputSource(MYBATIS_MAPPER_DTD, publicId, systemId);
				}
			}
			return null;
		} catch (Exception e) {
			throw new SAXException(e.toString());
		}
	}

	private InputSource getInputSource(String path, String publicId, String systemId) {
		InputSource source = null;
		if (path != null) {
			InputStream in;
			try {
				in = Resources.getResourceAsStream(path);
				source = new InputSource(in);
				source.setSystemId(systemId);
				source.setPublicId(publicId);
			} catch (IOException e) {
				logger.debug(e.getMessage());
			}
		}
		return source;
	}
}
