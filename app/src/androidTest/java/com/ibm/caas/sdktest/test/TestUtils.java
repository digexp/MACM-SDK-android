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

import android.util.Log;

import com.ibm.caas.CAASContentItem;
import com.ibm.caas.CAASErrorResult;
import com.ibm.caas.CAASService;

import java.util.Random;

/**
 *
 */
public class TestUtils {
  /**
   * A global PRNG.
   */
  private static final Random RAND = new Random(System.nanoTime());

  /**
   * Get the name of the method that called this one.
   * @return the name of the invoking method as a string.
  */
  public static String getCurrentClassAndMethod() {
    StackTraceElement[] elements = new Exception().getStackTrace();
    if (elements.length < 2) {
      return "could not find current class/method name";
    }
    String s = elements[1].getClassName();
    int idx = s.lastIndexOf('.');
    return new StringBuilder(idx >= 0 ? s.substring(idx + 1) : s).append('.').append(elements[1].getMethodName()).append("()").toString();
  }

  public static boolean itemsEqual(CAASContentItem item1, CAASContentItem item2) {
    if (item1 == item2) return true;
    if ((item1 == null) || (item2 == null)) return false;
    return item1.getOid().equals(item2.getOid());
  }

  /**
   * Genrate a random string of the specified length.
   * @param length the length of the string to generate.
   * @return A string with <code>length</code> random upper case alphabetic characters.
   */
  public static String generateRandomString(int length) {
    StringBuilder randomString = new StringBuilder();
    for (int i=0; i<length; i++) randomString.append((char) ('A' + RAND.nextInt(26)));
    return randomString.toString();
  }

  public static CAASService createService() {
    //return new CAASService("http://macm-mobile-nightly.rtp.raleigh.ibm.com:10039", "wps", "", "wpsadmin", "wpsadmin");
    return new CAASService("http://macm-daily-us.rtp.raleigh.ibm.com:10039", "wps", "macm1", "wpsadmin", "wpsadmin");
  }

  public static CAASService createServiceHTTPS() {
    return new CAASService("https://macm-daily-us.rtp.raleigh.ibm.com:10041", "wps", "macm1", "wpsadmin", "wpsadmin");
  }

  public static String getLibraryName() {
    return "Samples";
  }

  public static String toErrorString(CAASErrorResult error) {
    if (error == null) return "null";
    StringBuilder sb = new StringBuilder();
    sb.append("statusCode=").append(error.getStatusCode());
    sb.append(", message=").append(error.getMessage());
    Throwable t = error.getException();
    sb.append(", exception=").append(t == null ? "null" : Log.getStackTraceString(t));
    return sb.toString();
  }
}
