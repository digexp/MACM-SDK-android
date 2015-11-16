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

import java.net.URLEncoder;

/**
 * Abstract super class common to all requests.
 */
public abstract class CAASRequest<C> {
  private static final String LOG_TAG = CAASRequest.class.getSimpleName();
  /**
   * The callback associated with the request.
   */
  protected final CAASDataCallback<C> callback;

  /**
   * Initialize this request with the specified identifier and callback.
   * @param callback the callback instance to which the request results will be dispatched asynchronously.
   */
  public CAASRequest(CAASDataCallback<C> callback) {
    this.callback = callback;
  }

  /**
   * Build the query part of the HTTP request.
   * @param service the service performing the request.
   * @return the query part of the HTTP request as a string.
   * @throws Exception if any error occurs while building the query.
   */
  abstract String buildQuery(CAASService service) throws Exception;

  /**
   * Parse the raw response received from the server into an objet of the type handled by this request.
   * @param source the raw bytes of the server response.
   * @return an object of the type handled by this request.
   * @throws Exception if any error occurs while parse the JSON.
   */
  abstract C resultFromResponse(byte[] source) throws Exception;

  /**
   * Get the callback associated with this request.
   * @return an instance of an implementation of {@link CAASDataCallback}.
   */
  public CAASDataCallback<C> getCallback() {
    return callback;
  }

  /**
   * Encode the specified key and value so they can be used as a request parameter.
   * @param key the parmeter key.
   * @param value the parmeter value.
   * @return the encoded parmeter in form key=value.
   * @throws Exception if any error occurs.
   */
  String encodeParam(String key, Object value) throws Exception {
    StringBuilder sb = new StringBuilder(URLEncoder.encode(key, Utils.UTF_8).replace("+", "%20"));
    sb.append('=').append(value == null ? "" : URLEncoder.encode(value.toString(), Utils.UTF_8).replace("+", "%20"));
    return sb.toString();
  }

  /**
   * Convert the specified bytes into a string using UTF-8 encoding.
   * @param bytes the bytes to convert.
   * @return a string built from the input bytes.
   * @throws Exception if an encoding error occurs.
   */
  String bytesToString(byte[] bytes) throws Exception {
    return new String(bytes, Utils.UTF_8);
  }
}
