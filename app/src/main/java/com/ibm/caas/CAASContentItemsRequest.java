/*
 ********************************************************************
 * Licensed Materials - Property of IBM                             *
 *                                                                  *
 * Copyright IBM Corp. 2015 All rights reserved.                    *
 *                                                                  *
 * US Government Users Restricted Rights - Use, duplication or      *
 * disclosure restricted by GSA ADP Schedule Contract with          *
 * IBM Corp.                                                        *
 *                                                                  *
 * DISCLAIMER OF WARRANTIES. The following [enclosed] code is       *
 * sample code created by IBM Corporation. This sample code is      *
 * not part of any standard or IBM product and is provided to you   *
 * solely for the purpose of assisting you in the development of    *
 * your applications. The code is provided "AS IS", without         *
 * warranty of any kind. IBM shall not be liable for any damages    *
 * arising out of your use of the sample code, even if they have    *
 * been advised of the possibility of such damages.                 *
 ********************************************************************
 */

package com.ibm.caas;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A request object for querying a list of content items.
 */
public class CAASContentItemsRequest extends CAASAbstractContentRequest<CAASContentItemsList> {
  /**
   * Possible values for the workflow status.
   */
  public enum WorkflowStatus {
    Published,
    Draft,
    Expired,
    Deleted
  }
  /**
   * Max number of items to be served.
   */
  private int pageSize = -1;
  /**
   * Page index to be served (starts at 1).
   */
  private int pageNumber = -1;
  /**
   * Map of sort criterias to "asc" or "desc" qualifier.
   * Possible criterias: one of "title", "datemodified", "datecreated", "datepublished", "author"
   * <p>Note: {@link LinkedHashMap} guarantees that the iteration order is the same as the insertion order.
   */
  private final Map<String, Boolean> sortDescriptors = new LinkedHashMap<String, Boolean>();
  /**
   * Case insensitive exact match on category names.
   * The returned items must have all specified categories.
   */
  private final List<String> categoriesAll = new ArrayList<String>();
  /**
   * Case insensitive exact match on category name.
   * The returned items must have any of the specified categories.
   */
  private final List<String> categoriesAny = new ArrayList<String>();
  /**
   * Case insensitive exact match on keyword.
   * The returned items must have all specified keywords.
   */
  private final List<String> keywordsAll = new ArrayList<String>();
  /**
   * Case insensitive exact match on keyword.
   * The returned items must have any of the specified keywords.
   */
  private final List<String> keywordsAny = new ArrayList<String>();
  /**
   * Case sensitive "contains" match.
   */
  private String titleContains;
  /**
   * Case insensitive exact match on one of "draft", "published", "expired", "deleted".
   */
  private WorkflowStatus workflowStatus;

  /**
   * Initialize this request with the specified callback.
   * @param callback the callback instance to which the request results will be dispatched asynchronously.
   */
  public CAASContentItemsRequest(CAASDataCallback<CAASContentItemsList> callback) {
    super(callback);
  }

  /**
   * Max number of items to be served.
   */
  public int getPageSize() {
    return pageSize;
  }

  /**
   * Max number of items to be served.
   */
  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * Page index to be served (starts at 1)
   */
  public int getPageNumber() {
    return pageNumber;
  }

  /**
   * Page index to be served (starts at 1)
   */
  public void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
  }

  /**
   * Add a sort criteria on the specified key and with the specified ordering.
   * @param key one of "lastmodifieddate", "creationdate", "position", "title", "authors", "expirydate", "publishdate", "status".
   * @param ascending <code>true</code> for ascending order, <code>false</code> for descending order.
   */
  public void addSortDescriptor(String key, boolean ascending) {
    sortDescriptors.put(key, ascending);
  }

  /**
   * One of "title", "datemodified", "datecreated", "datepublished", "authors".
   */
  public Map<String, Boolean> getSortDescriptors() {
    return sortDescriptors;
  }

  /**
   * Case insensitive exact match on category name.
   */
  public List<String> getAllCategories() {
    return categoriesAll;
  }

  /**
   * Case insensitive exact match on category name(s).
   * The returned items must have all specified categories.
   */
  public void addAllCategories(String... categories) {
    addToList(this.categoriesAll, categories);
  }

  /**
   * Case insensitive exact match on category name.
   */
  public List<String> getAnyCategories() {
    return categoriesAny;
  }

  /**
   * Case insensitive exact match on category name.
   * The returned items must have any of the specified categories.
   */
  public void addAnyCategories(String... categories) {
    addToList(this.categoriesAny, categories);
  }

  /**
   * Case insensitive exact match on keyword.
   */
  public List<String> getAllKeywords() {
    return keywordsAll;
  }

  /**
   * Case insensitive exact match on keyword.
   * The returned items must have all specified keywords.
   */
  public void addAllKeywords(String...keywords) {
    addToList(this.keywordsAll, keywords);
  }

  /**
   * Case insensitive exact match on keyword.
   */
  public List<String> getAnyKeywords() {
    return keywordsAny;
  }

  /**
   * Case insensitive exact match on keyword.
   * The returned items must have any of the specified keywords.
   */
  public void addAnyKeywords(String...keywords) {
    addToList(this.keywordsAny, keywords);
  }

  /**
   * Case sensitive "contains" match on title.
   */
  public String getTitleContains() {
    return titleContains;
  }

  /**
   * Case sensitive "contains" match on title.
   */
  public void setTitleContains(String partialTitle) {
    this.titleContains = partialTitle;
  }

  /**
   * Case insensitive exact match on one of "draft", "published", "expired", "deleted".
   */
  public WorkflowStatus getWorkflowStatus() {
    return workflowStatus;
  }

  /**
   * Case insensitive exact match on one of "draft", "published", "expired", "deleted".
   */
  public void setWorkflowStatus(WorkflowStatus workflowStatus) {
    this.workflowStatus = workflowStatus;
  }

  @Override
  String buildQuery(CAASService service) throws Exception {
    StringBuilder sb = new StringBuilder();
    sb.append(service.getBaseQueryURI());
    if (project != null) {
      sb.append("/$project/").append(project);
    }
    sb.append("/caas");
    sb.append('?').append(encodeParam("current", "true"));
    sb.append('&').append(encodeParam(Utils.CAAS_QUERY_PARAMETER_MIME_TYPE, Utils.CAAS_QUERY_PARAMETER_MIME_TYPE_JSON));
    if (oid != null) {
      sb.append('&').append(encodeParam(Utils.CAAS_QUERY_PARAMETER_URILE, Utils.CAAS_URILE_BY_ID + oid));
    } else if (path != null) {
      sb.append('&').append(encodeParam(Utils.CAAS_QUERY_PARAMETER_URILE, Utils.CAAS_URILE_BY_PATH + path));
    }
    if (pageSize > 0) {
      sb.append('&').append(encodeParam("ibm.pageSize", pageSize));
    }
    if (pageNumber > 0) {
      sb.append('&').append(encodeParam("ibm.pageNumber", pageNumber));
    }
    if (!sortDescriptors.isEmpty()) {
      StringBuilder criterias = new StringBuilder();
      int count = 0;
      for (Map.Entry<String, Boolean> entry: sortDescriptors.entrySet()) {
        if (count > 0) criterias.append(',');
        count++;
        criterias.append(entry.getValue() ? '+' : '-').append(entry.getKey());
      }
      sb.append('&').append(encodeParam("ibm.sortCriteria", criterias.toString()));
    }
    if (!categoriesAll.isEmpty()) {
      sb.append('&').append(encodeParam("ibm.filter.categories.all", asCsv(categoriesAll)));
    }
    if (!categoriesAny.isEmpty()) {
      sb.append('&').append(encodeParam("ibm.filter.categories.any", asCsv(categoriesAny)));
    }
    if (!keywordsAll.isEmpty()) {
      sb.append('&').append(encodeParam("ibm.filter.keywords.all", asCsv(keywordsAll)));
    }
    if (!keywordsAny.isEmpty()) {
      sb.append('&').append(encodeParam("ibm.filter.keywords.any", asCsv(keywordsAny)));
    }
    if (titleContains != null) {
      sb.append('&').append(encodeParam("ibm.filter.title.contains", titleContains));
    }
    if (workflowStatus != null) {
      sb.append('&').append(encodeParam("ibm.filter.workflowstatus", workflowStatus));
    }
    addPropertiesElementsAndParams(sb);
    return sb.toString();
  }

  @Override
  CAASContentItemsList resultFromResponse(byte[] source) throws Exception {
    return new JSONParser(bytesToString(source)).getResult();
  }

  private void addToList(List<String> list, String...toAdd) {
    for (String s: toAdd) {
      if (s != null) {
        list.add(s);
      }
    }
  }
}
