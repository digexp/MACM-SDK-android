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

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.ibm.caas.CAASContentItem;
import com.ibm.caas.CAASContentItemRequest;
import com.ibm.caas.CAASService;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.ibm.caas.sdktest.test.TestUtils.getCurrentClassAndMethod;
import static com.ibm.caas.CAASProperties.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * 26 Movie items, 26 Book items
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CAASContentItemRequestTest {
  @Test(timeout=20000)
  public void testByIdAndPath() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItem> callback = new CAASDataCallbackTest<CAASContentItem>();
    String[] properties = { OID, CONTENT_TYPE, TITLE, LAST_MODIFIED_DATE, CATEGORIES, KEYWORDS, STATUS };
    String[] elements = { "author", "title", "publish_date", "isbn", "price", "rating", "cover" };
    CAASContentItemRequest request = new CAASContentItemRequest(callback);
    request.setPath(TestUtils.getLibraryName() + "/Content Types/Book");
    request.addProperties(properties);
    request.addElements(elements);
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    CAASContentItem resultByPath = callback.result;
    assertNotNull(resultByPath);
    callback = new CAASDataCallbackTest<CAASContentItem>();
    request = new CAASContentItemRequest(callback);
    request.addProperties(properties);
    request.addElements(elements);
    request.setOid(resultByPath.getOid());
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    CAASContentItem resultById = callback.result;
    assertNotNull(resultById);
    String format = "difference on the value of %s '%s'";
    for (String prop: properties) {
      assertEquals(String.format(format, "property", prop), resultByPath.getProperty(prop), resultById.getProperty(prop));
    }
    for (String elt: elements) {
      assertEquals(String.format(format, "element", elt), resultByPath.getElement(elt), resultById.getElement(elt));
    }
  }

  @Test(timeout=20000)
  public void testItemNotFound() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItem> callback = new CAASDataCallbackTest<CAASContentItem>();
    String[] properties = { OID, CONTENT_TYPE, TITLE, LAST_MODIFIED_DATE, CATEGORIES, KEYWORDS};
    CAASContentItemRequest request = new CAASContentItemRequest(callback);
    request.setOid(TestUtils.generateRandomString(32));
    request.addProperties(properties);
    service.executeRequest(request);
    callback.awaitCompletion();
    assertFalse(callback.successful);
    assertNull(callback.result);
    assertNotNull(callback.error);
    assertEquals(404, callback.error.getStatusCode());
  }
}
