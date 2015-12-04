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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Common super class for requests that return one or more content items.
 */
abstract class CAASAbstractContentRequest<C> extends CAASRequest<C> {
  /**
   * Additional arbitrary parameters that can be added to the query.
   */
  final List<QueryParameter> parameters = new ArrayList<QueryParameter>();
  /**
   * Only serve elements with the given keys.
   */
  final List<String> elements = new ArrayList<String>();
  /**
   * Only serve properties with the given keys.
   */
  final List<String> properties = new ArrayList<String>();
  /**
   * Path to the content item(s) retrieved by path.
   */
  String path = null;
  /**
   * Oid of the content item(s) retrieved by id.
   */
  String oid = null;
  /**
   * The project scope, if any.
   */
  String project;

  /**
   * Initialize this request with the specified identifier and callback.
   * @param callback the callback instance to which the request results will be dispatched asynchronously.
   */
  public CAASAbstractContentRequest(CAASDataCallback<C> callback) {
    super(callback);
  }

  /**
   * Get the <code>path</code> of the content item(s) to retrieve.
   * <p><b>See also</b>: <a href="http://www-01.ibm.com/support/knowledgecenter/SSYK7J_8.5.0/macm/macm_rest_api_sys_cont_items.dita">MACM path structure</a> in the IBM Knowledge Center for MACM.
   * @return the path string.
   */
  public String getPath() {
    return path;
  }

  /**
   * Set the <code>path</code> of the content item(s) to retrieve.
   * The <code>path</code> is mutually exclusive with the {@link #setOid(String) oid}, therefore this method resets the <code>oid</code> to <code>null</code>.
   * <p><b>See also</b>: <a href="http://www-01.ibm.com/support/knowledgecenter/SSYK7J_8.5.0/macm/macm_rest_api_sys_cont_items.dita">MACM path structure</a> in the IBM Knowledge Center for MACM.
   * @param path the path string.
   */
  public void setPath(String path) {
    this.path = path;
    this.oid = null;
  }

  /**
   * Set the <code>oid</code> of the content item(s) to retrieve.
   * @return the oid string.
   */
  public String getOid() {
    return oid;
  }

  /**
   * Set the <code>oid</code> of the content item(s) to retrieve.
   * The <code>oid</code> is mutually exclusive with the {@link #setPath(String) path}, therefore this method resets the <code>path</code> to <code>null</code>.
   * @param oid the oid string.
   */
  public void setOid(String oid) {
    this.oid = oid;
    this.path = null;
  }

  /**
   * Get the project scope, if any.
   */
  public String getProject() {
    return project;
  }

  /**
   * Set the project scope.
   */
  public void setProject(String project) {
    this.project = project;
  }

  /**
   * Get the elements to be served.
   * @return an unmodifiable list of the elements to be served by this request.
   */
  public List<String> getElements() {
    return Collections.unmodifiableList(elements);
  }

  /**
   * Only serve the specified elements.
   */
  public void addElements(String... elements) {
    this.elements.addAll(Arrays.asList(elements));
  }

  /**
   * Get the properties to be served.
   * <p><b>See also</b>: {@link CAASProperties supported properties}.
   * @return an unmodifiable list of the properties to be served by this request.
   */
  public List<String> getProperties() {
    return Collections.unmodifiableList(properties);
  }

  /**
   * Only serve the specified properties.
   * <p><b>See also</b>: {@link CAASProperties supported properties}.
   */
  public void addProperties(String... properties) {
    this.properties.addAll(Arrays.asList(properties));
  }

  /**
   * Add the specified arbitrary request parameter.
   * @param name the name of the parameter to add.
   * @param value the parameter value.
   */
  public void addParameter(String name, String value) {
    parameters.add(new QueryParameter(name, value));
  }

  StringBuilder addPropertiesElementsAndParams(StringBuilder query) throws Exception {
    if (!elements.isEmpty()) {
      query.append('&').append(encodeParam("ibm.element.keys", asCsv(elements)));
      query.append('&').append(encodeParam("ibm.type.information", "true"));
    }
    if (!properties.isEmpty()) {
      query.append('&').append(encodeParam("ibm.property.keys", asCsv(properties)));
    }
    for (QueryParameter param: parameters) {
      query.append('&').append(encodeParam(param.name, param.value));
    }
    return query;
  }

  /**
   * Convert the  specified list of strings into a single string with commma-separated values.
   * @param list the list of strings to convert.
   */
  String asCsv(List<String> list) {
    StringBuilder sb = new StringBuilder();
    int count = 0;
    for (String s: list) {
      if (count > 0) {
        sb.append(',');
      }
      sb.append(s);
      count++;
    }
    return sb.toString();
  }

  /**
   * A simple representation of a key value pair, used for multivalued HTTP request parameters.
   * Multiple <code>QueryParameter</code> objects with the same name can coexist in a list.
   */
  static class QueryParameter {
    public final String name;
    public final String value;

    public QueryParameter(String name, String value) {
      this.name = name;
      this.value = value;
    }
  }
}
