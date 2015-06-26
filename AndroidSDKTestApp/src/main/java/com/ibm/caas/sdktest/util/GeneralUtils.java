package com.ibm.caas.sdktest.util;

import android.util.Log;

import com.ibm.caas.CAASErrorResult;

/**
 * .
 */
public class GeneralUtils {
  public static void logErrorResult(String tag, String message, CAASErrorResult error) {
    Exception e = error.getException();
    String stack = (e == null) ? "none" : Log.getStackTraceString(e);
    String msg = String.format("%s: status code = %d, message = %s, exception = %s",
      message, error.getStatusCode(), error.getMessage(), stack);
    Log.e(tag, msg);
  }
}
