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

package com.ibm.caas.sdktest.test;

import com.ibm.caas.CAASContentItem;

import java.util.Comparator;

/**
 * Comparator that uses a single string field to compare two {@link CAASContentItem} objects.
 */
public class ContentItemComparator implements Comparator<CAASContentItem> {
  /**
   * The field on which to compare.
   */
  private final String field;
  /**
   * Whether to perform an ascending or descending sort.
   */
  private final boolean ascending;

  /**
   * Initialize this comparator with the field name and sort direction.
   * @param field the field on which to compare.
   * @param ascending whether to perform an ascending or descending sort.
   */
  public ContentItemComparator(final String field, final boolean ascending) {
    this.field = field;
    this.ascending = ascending;
  }

  @Override
  public int compare(CAASContentItem item1, CAASContentItem item2) {
    if (item2 == item1) return 0; // covers case with both items null
    if (item1 == null) return lessThan();
    if (item2 == null) return moreThan();
    String value1 = item1.getProperty(field);
    String value2 = item2.getProperty(field);
    if (value1 == value2) return 0;
    if (value1 == null) return lessThan();
    if (value2 == null) return moreThan();
    return adjustDirection(value1.compareTo(value2));
  }

  /**
   * Return a value meaning "less than" according to the sort direction.
   * @return -1 for an ascending sort, +1 for descending.
   */
  private int lessThan() {
    return ascending ? -1 : 1;
  }

  /**
   * Return a value meaning "greater than" according to the sort direction.
   * @return +1 for an ascending sort, -1 for descending.
   */
  private int moreThan() {
    return ascending ? 1 : -1;
  }

  /**
   * Eventually reverse a comparison result, based ont he sort direction.
   * @param value the comparison value to process.
   * @return the value itself for an ascending sort, or its opposite for a descending sort.
   */
  private int adjustDirection(int value) {
    return ascending ? value : -value;
  }
}
