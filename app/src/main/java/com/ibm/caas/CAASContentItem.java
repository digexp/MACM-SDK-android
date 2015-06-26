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

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates the response received from the (WCM) server.
 */
public class CAASContentItem implements Serializable {
  enum Property {
    OID("id"),
    NAME("name"),
    AUTHORS("authors"),
    TITLE("title"),
    AUTH_TEMPLATE_ID("authtemplateid"),
    AUTH_TEMPLATE_NAME("authtemplatename"),
    AUTH_TEMPLATE_TITLE("authtemplatetitle"),
    CATEGORIES("categories"),
    CONTENT_TYPE("contenttype"),
    CREATION_DATE("creationdate"),
    CREATOR("creator"),
    CURRENT_STAGE("currentstage"),
    DESCRIPTION("description"),
    EXPIRY_DATE("expirydate"),
    KEYWORDS("keywords"),
    LAST_MODIFIED_DATE("lastmodifieddate"),
    LAST_MODIFIER("lastmodifier"),
    LIBRARY_ID("libraryid"),
    LIBRARY_NAME("libraryname"),
    LIBRARY_TITLE("librarytitle"),
    PARENT_ID("parentid"),
    PARENT_NAME("parentname"),
    PARENT_TITLE("parenttitle"),
    PROJECT_ID("projectid"),
    PROJECT_NAME("projectname"),
    PROJECT_TITLE("projecttitle"),
    PUBLISH_DATE("publishdate"),
    STATUS("status"),
    STATUS_ID("statusid");

    private final String propertyName;

    private Property(String propertyName) {
      this.propertyName = propertyName;
    }

    String getPropertyName() {
      return propertyName;
    }
  }

  /**
   * Convenience mapping of Property elements to their string names.
   */
  final static Map<String, Property> propertiesMap = new HashMap<String, Property>();
  static {
    for (Property p: Property.values()) {
      propertiesMap.put(p.getPropertyName(), p);
    }
  }
  /**
   * The properties of this content item.
   */
  private final GenericMap properties = new GenericMap();
  /**
   * The elements of this content item.
   */
  private final GenericMap elements = new GenericMap();

  /**
   * Get the id of the content item represent by this object.
   * @return the content id, or <code>null</code> if no "id" attribute exists.
   */
  public String getOid() {
    return getProperty(Property.OID);
  }

  public String getName() {
    return getProperty(Property.NAME);
  }

  public String getContentType() {
    return getProperty(Property.CONTENT_TYPE);
  }

  public String getAuthors() {
    return getProperty(Property.AUTHORS);
  }

  public String getTitle() {
    return getProperty(Property.TITLE);
  }

  public String getAuthTemplateId() {
    return getProperty(Property.AUTH_TEMPLATE_ID);
  }

  public String getAuthTemplateName() {
    return getProperty(Property.AUTH_TEMPLATE_NAME);
  }

  public String getAuthTemplateTitle() {
    return getProperty(Property.AUTH_TEMPLATE_TITLE);
  }

  public String getCategories() {
    return getProperty(Property.CATEGORIES);
  }

  public Date getCreationDate() {
    return getProperty(Property.CREATION_DATE);
  }

  public String getCreator() {
    return getProperty(Property.CREATOR);
  }

  public String getCurrentStage() {
    return getProperty(Property.CURRENT_STAGE);
  }

  public String getDescription() {
    return getProperty(Property.DESCRIPTION);
  }

  public Date getExpiryDate() {
    return getProperty(Property.EXPIRY_DATE);
  }

  public String getKeywords() {
    return getProperty(Property.KEYWORDS);
  }

  public Date getLastModifiedDate() {
    return getProperty(Property.LAST_MODIFIED_DATE);
  }

  public String getLastModifier() {
    return getProperty(Property.LAST_MODIFIER);
  }

  public String getLibraryId() {
    return getProperty(Property.LIBRARY_ID);
  }

  public String getLibraryName() {
    return getProperty(Property.LIBRARY_NAME);
  }

  public String getLibraryTitle() {
    return getProperty(Property.LIBRARY_TITLE);
  }

  public String getParentId() {
    return getProperty(Property.PARENT_ID);
  }

  public String getParentName() {
    return getProperty(Property.PARENT_NAME);
  }

  public String getParentTitle() {
    return getProperty(Property.PARENT_TITLE);
  }

  public String getProjectId() {
    return getProperty(Property.PROJECT_ID);
  }

  public String getProjectName() {
    return getProperty(Property.PROJECT_NAME);
  }

  public String getProjectTitle() {
    return getProperty(Property.PROJECT_TITLE);
  }

  public Date getPublishDate() {
    return getProperty(Property.PUBLISH_DATE);
  }

  public String getStatus() {
    return getProperty(Property.STATUS);
  }

  public String getStatusId() {
    return getProperty(Property.STATUS_ID);
  }

  /**
   * Determine whether this content item contains a property with the specified name, no matter what its value is - including <code>null</code>.
   * @param name the name of the element to check.
   * @return <code>true</code> if this content item contains a property with the specified name, including with a <code><null/code> value, <code>false</code> otherwise.
   */
  public boolean hasProperty(String name) {
    return properties.has(name);
  }

  /**
   * Determine whether this content item contains an element with the specified name, no matter what its value is - including <code>null</code>.
   * @param name the name of the element to check.
   * @return <code>true</code> if this content item contains an element with the specified name, including with a <code><null/code> value, <code>false</code> otherwise.
   */
  public boolean hasElement(String name) {
    return elements.has(name);
  }

  public <T> T getElement(String name) {
    return elements.get(name);
  }

  public <T> T getProperty(String property) {
    return properties.get(property);
  }

  <T> T getProperty(Property property) {
    return properties.get(property.getPropertyName());
  }

  <T> void setProperty(String name, T value) {
    properties.set(name, value);
  }

  <T> void setElement(String name, T value) {
    elements.set(name, value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(getClass().getSimpleName()).append('[');
    sb.append("properties=").append(properties);
    sb.append(", elements=").append(elements);
    sb.append(']');
    return sb.toString();
  }
}
