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

import com.ibm.caas.CAASContentItemsList;
import com.ibm.caas.CAASContentItemsRequest;
import com.ibm.caas.CAASErrorResult;
import com.ibm.caas.CAASService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.ibm.caas.sdktest.test.TestUtils.getCurrentClassAndMethod;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CAASServiceTest {
  @Before
  public void setUp() throws Exception {
  }

  @Test(timeout=10000)
  public void testSignInSuccess() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService tmp = TestUtils.createService();
    //CAASService service = new CAASService(tmp.getServerURL(), tmp.getContextRoot(), tmp.getInstance());
    CAASService service = new CAASService(tmp.getServerURL(), tmp.getContextRoot(), tmp.getInstance(), tmp.getUserName(), tmp.getPassword());
    CAASDataCallbackTest<Void> callback = new CAASDataCallbackTest<Void>();
    service.signIn(tmp.getUserName(), tmp.getPassword(), callback);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    assertNull(callback.error);
    //assertTrue("unsuccessful request: " + TestUtils.toErrorString(callback.error).replace("\n", "\\n"), callback.successful);
  }

  /**
   * Sign in with incorrect credentials and check that an error was returned.
   */
  @Test(timeout=10000)
  public void testSignInFailure() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService tmp = TestUtils.createService();
    CAASService service = new CAASService(tmp.getServerURL(), tmp.getContextRoot(), tmp.getInstance());
    CAASDataCallbackTest<Void> callback = new CAASDataCallbackTest<Void>();
    service.signIn(tmp.getUserName(), "incorrect_password", callback);
    callback.awaitCompletion();
    System.out.println("result: " + callback.requestResult);
    assertFalse(callback.successful);
    CAASErrorResult error = callback.error;
    System.out.println("error: "+ error);
    assertNotNull(error);
    assertEquals(401, error.getStatusCode());
  }

  @Test(timeout=10000)
  public void testAbbreviatedSignInSuccess() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath(TestUtils.getLibraryName() + "/Views/All");
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    assertNotNull(callback.result);
    CAASContentItemsList result = callback.result;
    assertNotNull(result);
    assertNotNull(result.getContentItems());
    assertFalse(result.getContentItems().isEmpty());
  }

  /**
   * Sign in with incorrect credentials and check that an error was returned.
   */
  @Test(timeout=10000)
  public void testAbbreviatedSignInFailure() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService tmp = TestUtils.createService();
    CAASService service = new CAASService(tmp.getServerURL(), tmp.getContextRoot(), tmp.getInstance(), tmp.getUserName(), "incorrect_password");
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath(TestUtils.getLibraryName() + "/Views/All");
    service.executeRequest(request);
    callback.awaitCompletion();
    assertFalse(callback.successful);
    CAASErrorResult error = callback.error;
    assertNotNull(error);
    //assertTrue((error.getStatusCode() != -1) && (error.getStatusCode() != 200));
    assertEquals(401, error.getStatusCode());
  }

  /**
   * Test that HTTPS connection succeeds when allowUntrustedCertificates == true.
   */
  @Test(timeout=10000)
  public void testHTTPSSuccess() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    //CAASService service = new CAASService("https://macm-mobile-nightly.rtp.raleigh.ibm.com:10042", "wps", "", "wpsadmin", "wpsadmin");
    CAASService service = TestUtils.createServiceHTTPS();
    service.setAllowUntrustedCertificates(true);
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath(TestUtils.getLibraryName() + "/Views/All");
    service.executeRequest(request);
    callback.awaitCompletion();
    if (!callback.successful) {
      CAASErrorResult error = callback.error;
      if (error != null) {
        if (error.getException() != null) {
          throw error.getException();
        }
      }
    }
    assertTrue(callback.successful);
    assertNotNull(callback.result);
    CAASContentItemsList result = callback.result;
    assertNotNull(result);
    assertNotNull(result.getContentItems());
    assertFalse(result.getContentItems().isEmpty());
  }

  /**
   * Test that HTTPS connection fails when allowUntrustedCertificates == false.
   */
  @Test(timeout=10000, expected = javax.net.ssl.SSLHandshakeException.class)
  public void testHTTPSFailure() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createServiceHTTPS();
    service.setAllowUntrustedCertificates(false);
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath(TestUtils.getLibraryName() + "/Views/All");
    service.executeRequest(request);
    callback.awaitCompletion();
    assertFalse(callback.successful);
    CAASErrorResult error = callback.error;
    assertNotNull(error);
    assertNotNull(error.getException());
    throw error.getException();
  }
}
