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

import java.net.HttpURLConnection;

/**
 * Encapsulates the error result of a failed query.
 */
public class CAASErrorResult {
  /**
   * The HTTP status code of the qsery.
   */
  private final int statusCode;
  /**
   * The exception for this error, if any.
   */
  private final Exception exception;
  /**
   * The error message, if any.
   */
  private final String message;
  /**
   * The URL connection used to send the request and receive the response.
   */
  private final HttpURLConnection httpURLConnection;

  /**
   * Initialize this error result with the specified status code, optional exception and error message.
   * @param statusCode the HTTP status code from the HTTP response.
   * @param exception an optional exception that was raised while executing the reqquest.
   * @param message the error message.
   * @param httpURLConnection the URL connection used to send the request and receive the response.
   */
  CAASErrorResult(int statusCode, Exception exception, String message, HttpURLConnection httpURLConnection) {
    this.statusCode = statusCode;
    this.exception = exception;
    this.message = message;
    this.httpURLConnection = httpURLConnection;
  }

  /**
   * Return the status code from the HTTP response.
   * A value of -1 or less indicates that the request did not go through and therefore no status code could be captured.
   */
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * Get the exception that occurred while executing the request, if any.
   */
  public Exception getException() {
    return exception;
  }

  /**
   * Return the error message.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Get the URL connection used to send the request and receive the response.
   * @return an {@link HttpURLConnection} instance.
   */
  public HttpURLConnection getHttpURLConnection() {
    return httpURLConnection;
  }
}
