/*
 * Created by XianYiJun 8/31/16 7:36 PM
 * Copyright (c) 2016. All rights reserved.
 *
 * Last modified 8/31/16 7:31 PM
 */

package cn.xianyijun.orm.reflection.property;

import java.util.Iterator;

/**
 * The type Property tokenizer.
 *
 * @author xianyijun xianyijun0@gmail.com
 */
public class PropertyTokenizer implements Iterable<PropertyTokenizer>, Iterator<PropertyTokenizer> {
	  private String name;
	  private String indexedName;
	  private String index;
	  private String children;

	/**
	 * Instantiates a new Property tokenizer.
	 *
	 * @param fullName the full name
	 */
	public PropertyTokenizer(String fullName) {
	    int delim = fullName.indexOf('.');
	    if (delim > -1) {
	      name = fullName.substring(0, delim);
	      children = fullName.substring(delim + 1);
	    } else {
	      name = fullName;
	      children = null;
	    }
	    indexedName = name;
	    delim = name.indexOf('[');
	    if (delim > -1) {
	      index = name.substring(delim + 1, name.length() - 1);
	      name = name.substring(0, delim);
	    }
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
	 * Gets index.
	 *
	 * @return the index
	 */
	public String getIndex() {
	    return index;
	  }

	/**
	 * Gets indexed name.
	 *
	 * @return the indexed name
	 */
	public String getIndexedName() {
	    return indexedName;
	  }

	/**
	 * Gets children.
	 *
	 * @return the children
	 */
	public String getChildren() {
	    return children;
	  }

	/**
	 * Has next boolean.
	 *
	 * @return the boolean
	 */
	@Override
	  public boolean hasNext() {
	    return children != null;
	  }

	/**
	 * Next property tokenizer.
	 *
	 * @return the property tokenizer
	 */
	@Override
	  public PropertyTokenizer next() {
	    return new PropertyTokenizer(children);
	  }

	/**
	 * Remove.
	 */
	@Override
	  public void remove() {
	    throw new UnsupportedOperationException("Remove is not supported, as it has no meaning in the context of properties.");
	  }

	/**
	 * Iterator iterator.
	 *
	 * @return the iterator
	 */
	@Override
	  public Iterator<PropertyTokenizer> iterator() {
	    return this;
	  }
	}
