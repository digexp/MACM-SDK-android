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

import com.ibm.caas.CAASAssetRequest;
import com.ibm.caas.CAASContentItem;
import com.ibm.caas.CAASContentItemRequest;
import com.ibm.caas.CAASContentItemsList;
import com.ibm.caas.CAASContentItemsRequest;
import com.ibm.caas.CAASRequestResult;
import com.ibm.caas.CAASService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.ibm.caas.sdktest.test.TestUtils.getCurrentClassAndMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 26 Movie items, 26 Book items
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CAASAssetRequestTest {
  @Test(timeout=20000)
  public void testFindAsset() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath("OOTB Content/Views/All");
    request.addProperties("id");
    request.addElements("cover");
    request.setPageSize(100);
    request.setPageNumber(1);
    CAASRequestResult<CAASContentItemsList> requestResult = service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    assertNotNull(callback.result);
    List<CAASContentItem> items = callback.result.getContentItems();
    String imageURL = null;
    for (CAASContentItem item: items) {
      String s = item.getElement("cover");
      if ((s != null) && s.contains(".png")) {
        imageURL = s;
        break;
      }
    }
    assertNotNull(imageURL);
    CAASDataCallbackTest<byte[]> assetCallback = new CAASDataCallbackTest<byte[]>();
    CAASAssetRequest assetRequest = new CAASAssetRequest(imageURL, assetCallback);
    CAASRequestResult<byte[]> assetResult = service.executeRequest(assetRequest);
    assetCallback.awaitCompletion();
    assertTrue(assetCallback.successful);
    assertNotNull(assetCallback.result);
    assertTrue(assetCallback.result.length > 0);
  }

  @Test(timeout=20000)
  public void testAssetNotFound() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    // cover = /wps/wcm/myconnect/a541995c-ae76-4ff8-95da-e5b83851a00a/cover?MOD=AJPERES
    CAASDataCallbackTest<byte[]> assetCallback = new CAASDataCallbackTest<byte[]>();
    CAASAssetRequest assetRequest = new CAASAssetRequest("/wps/wcm/myconnect" + TestUtils.generateRandomString(50), assetCallback);
    CAASRequestResult<byte[]> assetResult = service.executeRequest(assetRequest);
    assetCallback.awaitCompletion();
    assertFalse(assetCallback.successful);
    assertNull(assetCallback.result);
    assertNotNull(assetCallback.error);
    assertEquals(404, assetCallback.error.getStatusCode());
  }
}
