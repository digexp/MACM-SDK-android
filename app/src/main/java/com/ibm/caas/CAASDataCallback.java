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
 * Callback handler invoked once the server's response has been received.
 * @param <T> the type of results returned by the request.
 */
public interface CAASDataCallback<T> {
  /**
   * Called when a request to the MACM service succeeded.
   * @param requestResult the data received from the server.
   */
  void onSuccess(CAASRequestResult<T> requestResult);

  /**
   * Called when an querying data from the server results in an error.
   * @param error encapsulates information about the error.
   */
  void onError(CAASErrorResult error);
}
