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

import java.util.Date;
import java.util.List;

/**
 * Instances of this class provide access to a list of items resulting from a request, along with access to the properties of this list.
 */
public class CAASContentItemsList {
  /**
   * The list of content items.
   */
  private List<CAASContentItem> contentItems;
  /**
   * The id of the list.
   */
  private String oid;
  /**
   * The last modified date of the list.
   */
  private Date lastModifiedDate;
  /**
   * The page size of the list.
   */
  private int pageSize;
  /**
   * The page number of the list.
   */
  private int pageNumber;
  /**
   * The whether there is at least one more page in the list.
   */
  private boolean hasNext;

  /**
   * Determine whether there is at least one more page to receive.
   */
  public boolean hasNextPage() {
    return hasNext;
  }

  void setHasNextPage(boolean hasNext) {
    this.hasNext = hasNext;
  }

  /**
   * Get the list of items received in the response.
   */
  public List<CAASContentItem> getContentItems() {
    return contentItems;
  }

  void setContentItems(List<CAASContentItem> contentItems) {
    this.contentItems = contentItems;
  }

  /**
   * Get the oid of the received list.
   */
  public String getOid() {
    return oid;
  }

  void setOid(String oid) {
    this.oid = oid;
  }

  /**
   * Get the date of last modification of the received list.
   */
  public Date getLastModifiedDate() {
    return lastModifiedDate;
  }

  void setLastModifiedDate(Date lastModifiedDate) {
    this.lastModifiedDate = lastModifiedDate;
  }

  /**
   * Get the list's page size, that is, the maximum number of items in the list.
   */
  public int getPageSize() {
    return pageSize;
  }

  void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * Get the current page number for the list.
   */
  public int getPageNumber() {
    return pageNumber;
  }

  void setPageNumber(int pageNumber) {
    this.pageNumber = pageNumber;
  }
}
