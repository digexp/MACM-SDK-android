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

import android.util.Log;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This class is package-protected so as not to be exposed to clients.
 */
final class Utils {
  /**
   * Log tag for this class.
   */
  private final static String LOG_TAG = Utils.class.getSimpleName();

  // Android system properties
  static final String PROPERTY_HTTP_AGENT = "http.agent";

  // HTTP connection stuff
  static final String HTTP_HEADER_USER_AGENT = "User-Agent";
  static final String HTTP_HEADER_ACCEPT_LANGUAGE = "Accept-Language";

  // CaaS stuff
  static final String CAAS_QUERY_PARAMETER_URILE = "urile";
  static final String CAAS_URILE_BY_ID = "wcm:oid:";
  static final String CAAS_URILE_BY_PATH = "wcm:path:";
  static final String CAAS_QUERY_PARAMETER_MIME_TYPE = "mime-type";
  static final String CAAS_QUERY_PARAMETER_MIME_TYPE_JSON = "application/json";

  /**
   * Default resource bundle base name for localization.
   */
  static final String DEFAULT_LOCALIZATION_BASE_NAME = "com.ibm.caas.Messages";

  /**
   * The UTF-8 charset string.
   */
  static final String UTF_8 = "utf-8";

  static String localize(String messageId, Object...parameters) {
    return localize(Locale.getDefault(), DEFAULT_LOCALIZATION_BASE_NAME, messageId, parameters);
  }

  static String localize(Locale locale, String baseName, String messageId, Object...parameters) {
    //Log.v(LOG_TAG, String.format("locale=%s, baseName=%s, messageId=%s, parameters=%s", locale, baseName, messageId, Arrays.asList(parameters)));
    if (locale == null) {
      locale = Locale.getDefault();
    }
    try {
      ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
      String source = bundle.getString(messageId);
      return MessageFormat.format(source, parameters);
    } catch(MissingResourceException e) {
      return "message '" + messageId + "' could not be localized (" + e.getMessage() + ")";
    }
  }
}
