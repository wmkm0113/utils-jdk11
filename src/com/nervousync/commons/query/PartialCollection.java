/*
 * Copyright © 2003 Nervousync Studio, Inc. All rights reserved.
 * This software is the confidential and proprietary information of 
 * Nervousync Studio, Inc. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the 
 * license agreement you entered into with Nervousync Studio.
 */
package com.nervousync.commons.query;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.nervousync.interceptor.beans.BaseHandlerInterceptor;
import com.nervousync.utils.StringUtils;

/**
 * Partial collection
 * @author Steven Wee	<a href="mailto:wmkm0113@Hotmail.com">wmkm0113@Hotmail.com</a>
 * @version $Revision: 1.0 $ $Date: Jan 13, 2010 4:07:14 PM $
 */
public final class PartialCollection<T> implements Serializable {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 2086690645677391624L;

	/**
	 * Collection of entities (part of some another collection)
	 */
	private final List<T> resultList;

	/**
	 * Total number of elements in collection this collection is part of
	 */
	private final long totalCount;

	/**
	 * Creates an empty instance of PartialCollection
	 */
	public PartialCollection() {
		this(new ArrayList<T>(), 0);
	}

	/**
	 * Creates new instance of PartialCollection with specified collection and total
	 *
	 * @param resultList	Result list
	 * @param totalCount   	Total size of collection, which part is contained in this instance
	 */
	public PartialCollection(List<T> resultList, long totalCount) {
		this.resultList = resultList;
		this.totalCount = totalCount;
	}
	
	public List<T> asList() {
		return this.resultList;
	}

    /**
     * Gets the size of part of initial collection that is contained here
     *
     * @return number of elements in partial collection
     */
	public int size() {
		return this.resultList.size();
	}

    /**
     * Clears the partial collection
     */
	public void clear() {
		this.resultList.clear();
	}

    /**
     * Figures out is partial collection empty
     *
     * @return <code>true</code> if this collection is empty
     */
	public boolean isEmpty() {
		return this.resultList.isEmpty();
	}

	public Object[] toArray() {
		return this.resultList.toArray();
	}

	public boolean add(T o) {
		return this.resultList.add(o);
	}

	public boolean contains(Map<String, Object> o) {
		return this.resultList.contains(o);
	}

	public boolean remove(Map<String, Object> o) {
		return this.resultList.remove(o);
	}

	public boolean addAll(Collection<T> c) {
		return this.resultList.addAll(c);
	}

	public boolean containsAll(Collection<Map<String, Object>> c) {
		return this.resultList.containsAll(c);
	}

	public boolean removeAll(Collection<Map<String, Object>> c) {
		return this.resultList.removeAll(c);
	}

	public boolean retainAll(Collection<Map<String, Object>> c) {
		return this.resultList.retainAll(c);
	}

	public Iterator<T> iterator() {
		return this.resultList.iterator();
	}
	
    /**
     * Gets total number of elements in initial collection
     *
     * @return total number of elements
     */
	public long getTotalCount() {
		return this.totalCount;
	}
	
	public static <T> PartialCollection<T> parse(String cacheData, Class<T> entityClass) {
		return PartialCollection.parse(cacheData, entityClass, null);
	}
	
	public static <T> PartialCollection<T> parse(String cacheData, Class<T> entityClass, BaseHandlerInterceptor methodInterceptor) {
		if (cacheData == null || entityClass == null) {
			return null;
		}
		
		Map<String, Object> convertMap = StringUtils.convertJSONStringToMap(cacheData);

		long totalCount = Long.parseLong((String)convertMap.get("totalCount"));
		List<T> objectList = StringUtils.convertJSONStringToList((String)convertMap.get("objectList"), entityClass);

		if (objectList != null) {
			return new PartialCollection<>(objectList, totalCount);
		}
		return null;
	}
	
	public String cacheData() {
		Map<String, String> convertMap = new HashMap<>();
		convertMap.put("totalCount", Long.valueOf(this.totalCount).toString());
		convertMap.put("objectList", StringUtils.convertObjectToJSONString(this.resultList));
		
		return StringUtils.convertObjectToJSONString(convertMap);
	}
}
