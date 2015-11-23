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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Instances of this class represent content items retrieved from the server.
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
    ITEM_COUNT("itemcount"),
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

  /**
   * Return this content item's name as a string,
   * or <code>null</code> if the name is not defined.
   */
  public String getName() {
    return getProperty(Property.NAME);
  }

  /**
   * Return this content item's "contenttype" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getContentType() {
    return getProperty(Property.CONTENT_TYPE);
  }

  /**
   * Return this content item's "authors" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getAuthors() {
    return getProperty(Property.AUTHORS);
  }

  /**
   * Return this content item's "title" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getTitle() {
    return getProperty(Property.TITLE);
  }

  /**
   * Return this content item's "authtemplateid" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getAuthTemplateId() {
    return getProperty(Property.AUTH_TEMPLATE_ID);
  }

  /**
   * Return this content item's "authtemplatename" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getAuthTemplateName() {
    return getProperty(Property.AUTH_TEMPLATE_NAME);
  }

  /**
   * Return this content item's "authtemplatetitle" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getAuthTemplateTitle() {
    return getProperty(Property.AUTH_TEMPLATE_TITLE);
  }

  /**
   * Return this content item's "categories" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getCategories() {
    return getProperty(Property.CATEGORIES);
  }

  /**
   * Return this content item's "creationdate" property as a {@link Date},
   * or <code>null</code> if the property is not defined.
   */
  public Date getCreationDate() {
    return getProperty(Property.CREATION_DATE);
  }

  /**
   * Return this content item's "creator" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getCreator() {
    return getProperty(Property.CREATOR);
  }

  /**
   * Return this content item's "currentstage" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getCurrentStage() {
    return getProperty(Property.CURRENT_STAGE);
  }

  /**
   * Return this content item's "description" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getDescription() {
    return getProperty(Property.DESCRIPTION);
  }

  /**
   * Return this content item's "expirydate" property as a {@link Date},
   * or <code>null</code> if the property is not defined.
   */
  public Date getExpiryDate() {
    return getProperty(Property.EXPIRY_DATE);
  }

  /**
   * Return this content item's "keywords" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getKeywords() {
    return getProperty(Property.KEYWORDS);
  }

  /**
   * Return this content item's "lastmodifieddate" property as a {@link Date},
   * or <code>null</code> if the property is not defined.
   */
  public Date getLastModifiedDate() {
    return getProperty(Property.LAST_MODIFIED_DATE);
  }

  /**
   * Return this content item's "lastmodifier" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getLastModifier() {
    return getProperty(Property.LAST_MODIFIER);
  }

  /**
   * Return this content item's "libraryid" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getLibraryId() {
    return getProperty(Property.LIBRARY_ID);
  }

  /**
   * Return this content item's "libraryname" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getLibraryName() {
    return getProperty(Property.LIBRARY_NAME);
  }

  /**
   * Return this content item's "librarytitle" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getLibraryTitle() {
    return getProperty(Property.LIBRARY_TITLE);
  }

  /**
   * Return this content item's "parentid" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getParentId() {
    return getProperty(Property.PARENT_ID);
  }

  /**
   * Return this content item's "parentname" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getParentName() {
    return getProperty(Property.PARENT_NAME);
  }

  /**
   * Return this content item's "parenttitle" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getParentTitle() {
    return getProperty(Property.PARENT_TITLE);
  }

  /**
   * Return this content item's "projectid" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getProjectId() {
    return getProperty(Property.PROJECT_ID);
  }

  /**
   * Return this content item's "projectname" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getProjectName() {
    return getProperty(Property.PROJECT_NAME);
  }

  /**
   * Return this content item's "projecttitle" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getProjectTitle() {
    return getProperty(Property.PROJECT_TITLE);
  }

  /**
   * Return this content item's "publishdate" property as a {@link Date},
   * or <code>null</code> if the property is not defined.
   */
  public Date getPublishDate() {
    return getProperty(Property.PUBLISH_DATE);
  }

  /**
   * Return this content item's "status" property as a string,
   * or <code>null</code> if the property is not defined.
   */
  public String getStatus() {
    return getProperty(Property.STATUS);
  }

  /**
   * Return this content item's "statusid" property as a string,
   * or <code>null</code> if the property is not defined.
   */
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

  /**
   * Get the value of this content item's element
   * <p>The returned value type is one of the following, depending on which element is looked up:
   * <ul>
   *   <li>number values: <code>double</code> or <code>java.lang.Double</code></li>
   *   <li>string values: <code>java.lang.String</code></li>
   *   <li>date values: <code>java.util.Date</code></li>
   *   <li>url values: <code>java.lang.String</code></li>
   * </ul>
   * @param name the name of the element to retrieve.
   * @param <T> the type of the element's value.
   * @return the element's value or <code>null</code> if the element is not defined.
   */
  public <T> T getElement(String name) {
    return elements.get(name);
  }

  /**
   * Get the value of this content item's property.
   * <p>The returned value type is one of the following, depending on which property is looked up:
   * <ul>
   *   <li>number values: <code>double</code> or <code>java.lang.Double</code></li>
   *   <li>string values: <code>java.lang.String</code></li>
   *   <li>date values: <code>java.util.Date</code></li>
   *   <li>url values: <code>java.lang.String</code></li>
   * </ul>
   * @param name the name of the property to retrieve.
   * @param <T> the type of the property's value.
   * @return the property's value or <code>null</code> if the property is not defined.
   */
  public <T> T getProperty(String name) {
    return properties.get(name);
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
