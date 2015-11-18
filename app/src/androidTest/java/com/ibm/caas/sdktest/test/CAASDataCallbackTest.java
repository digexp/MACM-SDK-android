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

import com.ibm.caas.CAASDataCallback;
import com.ibm.caas.CAASErrorResult;
import com.ibm.caas.CAASRequestResult;

/**
 * A generic callback used for unit-testing.
 */
public class CAASDataCallbackTest<T> implements CAASDataCallback<T> {
  private boolean complete = false;
  boolean successful = false;
  T result;
  CAASRequestResult<T> requestResult;
  CAASErrorResult error;

  @Override
  public final synchronized void onSuccess(CAASRequestResult<T> requestResult) {
    System.out.println("in onSuccess()");
    this.result = requestResult == null ? null : requestResult.getResult();
    this.requestResult = requestResult;
    successful = true;
    synchronized(this) {
      complete = true;
      notifyAll();
    }
  }

  @Override
  public final void onError(CAASErrorResult error) {
    //System.out.println("onError(" + error + ")");
    this.error = error;
    successful = false;
    synchronized(this) {
      complete = true;
      notifyAll();
    }
  }

  public void awaitCompletion() {
    awaitCompletion(-1L);
  }

  public void awaitCompletion(long timeout) {
    long maxTime = timeout <= 0L ? Long.MAX_VALUE : timeout;
    long start = System.nanoTime();
    synchronized(this) {
      try {
        while (((System.nanoTime() - start) / 1000000L < maxTime) && !complete) {
          wait(50L);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
