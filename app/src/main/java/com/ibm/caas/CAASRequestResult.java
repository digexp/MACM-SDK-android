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

import android.os.AsyncTask;

/**
 * Encapsulates the result of a request.
 * @param <C> the type of result returned by the request.
 */
public class CAASRequestResult<C> {
  /**
   * Reference to the initial request.
   */
  private final CAASRequest<C> request;
  /**
   * The actual result of the request, if any.
   */
  private C result;
  /**
   * Description of the error that occurred, if any.
   */
  private CAASErrorResult error;
  /**
   * The content-type (or mime-type) of the response body.
   */
  private String contentType;
  /**
   * The task which executes the request asynchronously.
   */
  private AsyncTask<Void, Void, CAASRequestResult<C>> asyncTask;

  CAASRequestResult(CAASRequest<C> request) {
    this.request = request;
  }

  /**
   * Get a descritpion of the error that occurred, if any.
   */
  public CAASErrorResult getError() {
    return error;
  }

  void setError(CAASErrorResult error) {
    this.error = error;
  }

  /**
   * Get the original request for this result.
   */
  public CAASRequest<C> getRequest() {
    return request;
  }

  /**
   * Get the content-type (or mime-type) of the response body.
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Set the content-type (or mime-type) of the response body.
   */
  void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   * Wait if necessary and return the result of the request.
   * @return the actual result of the request, if any.
   */
  public C getResult() {
    if (result != null) return result;
    if (!isDone()) {
      try {
        if (asyncTask == null) {
          return null;
        } else {
          asyncTask.get();
          return result;
        }
      } catch(Exception e) {
        if (error == null) {
          error = new CAASErrorResult(-1, e, e.getMessage());
        }
      }
    }
    return result;
  }

  void setResult(C result) {
    this.result = result;
  }

  void setAsyncTask(AsyncTask<Void, Void, CAASRequestResult<C>> asyncTask) {
    this.asyncTask = asyncTask;
  }

  /**
   * Cancel the request.
   */
  public void cancel() {
    asyncTask.cancel(true);
  }

  /**
   * Whether the request if finished.
   * The task is considered finished when it completes successfully or in error or has been cancelled.
   * @return <code>true</code> if the request is finished, <code>false</code> otherwise.
   */
  private boolean isDone() {
    return (asyncTask != null) && (asyncTask.getStatus() == AsyncTask.Status.FINISHED);
  }
}
