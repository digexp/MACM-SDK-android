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

/**
 * This class provides an enumeration of the names of the properties supported for content items and project items.
 * <p>Content items support the following properties:
 * <ul>
 * <li>{@link #OID id}</li>
 * <li>{@link #NAME name}</li>
 * <li>{@link #AUTHORS authors}</li>
 * <li>{@link #TITLE title}</li>
 * <li>{@link #AUTH_TEMPLATE_ID authtemplateid}</li>
 * <li>{@link #AUTH_TEMPLATE_NAME authtemplatename}</li>
 * <li>{@link #AUTH_TEMPLATE_TITLE authtemplatetitle}</li>
 * <li>{@link #CATEGORIES categories}</li>
 * <li>{@link #CONTENT_TYPE contenttype}</li>
 * <li>{@link #CREATION_DATE creationdate}</li>
 * <li>{@link #CREATOR creator}</li>
 * <li>{@link #CURRENT_STAGE currentstage}</li>
 * <li>{@link #DESCRIPTION description}</li>
 * <li>{@link #EXPIRY_DATE expirydate}</li>
 * <li>{@link #KEYWORDS keywords}</li>
 * <li>{@link #LAST_MODIFIED_DATE lastmodifieddate}</li>
 * <li>{@link #LAST_MODIFIER lastmodifier}</li>
 * <li>{@link #LIBRARY_ID libraryid}</li>
 * <li>{@link #LIBRARY_NAME libraryname}</li>
 * <li>{@link #LIBRARY_TITLE librarytitle}</li>
 * <li>{@link #PARENT_ID parentid}</li>
 * <li>{@link #PARENT_NAME parentname}</li>
 * <li>{@link #PARENT_TITLE parenttitle}</li>
 * <li>{@link #PROJECT_ID projectid}</li>
 * <li>{@link #PROJECT_NAME projectname}</li>
 * <li>{@link #PROJECT_TITLE projecttitle}</li>
 * <li>{@link #PUBLISH_DATE publishdate}</li>
 * <li>{@link #STATUS status}</li>
 * <li>{@link #STATUS_ID statusid}</li>
 * </ul>
 * <p>Project items support the following properties:
 * <ul>
 * <li>{@link #NAME name}</li>
 * <li>{@link #TITLE title}</li>
 * <li>{@link #CREATION_DATE creationdate}</li>
 * <li>{@link #CREATOR creator}</li>
 * <li>{@link #ITEM_COUNT itemcount}</li>
 * <li>{@link #LAST_MODIFIER lastmodifier}</li>
 * <li>{@link #STATE state}</li>
 * <li>{@link #UUID uuid}</li>
 * </ul>
 */
public class CAASProperties {
  public static final String OID = "id";
  public static final String NAME = "name";
  public static final String AUTHORS = "authors";
  public static final String TITLE = "title";
  public static final String AUTH_TEMPLATE_ID = "authtemplateid";
  public static final String AUTH_TEMPLATE_NAME = "authtemplatename";
  public static final String AUTH_TEMPLATE_TITLE = "authtemplatetitle";
  public static final String CATEGORIES = "categories";
  public static final String CONTENT_TYPE = "contenttype";
  public static final String CREATION_DATE = "creationdate";
  public static final String CREATOR = "creator";
  public static final String CURRENT_STAGE = "currentstage";
  public static final String DESCRIPTION = "description";
  public static final String EXPIRY_DATE = "expirydate";
  public static final String ITEM_COUNT = "itemcount";
  public static final String KEYWORDS = "keywords";
  public static final String LAST_MODIFIED_DATE = "lastmodifieddate";
  public static final String LAST_MODIFIER = "lastmodifier";
  public static final String LIBRARY_ID = "libraryid";
  public static final String LIBRARY_NAME = "libraryname";
  public static final String LIBRARY_TITLE = "librarytitle";
  public static final String PARENT_ID = "parentid";
  public static final String PARENT_NAME = "parentname";
  public static final String PARENT_TITLE = "parenttitle";
  public static final String PROJECT_ID = "projectid";
  public static final String PROJECT_NAME = "projectname";
  public static final String PROJECT_TITLE = "projecttitle";
  public static final String PUBLISH_DATE = "publishdate";
  public static final String STATUS = "status";
  public static final String STATUS_ID = "statusid";
  public static final String STATE = "state";
  public static final String UUID = "uuid";
}
