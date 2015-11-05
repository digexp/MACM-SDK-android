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
 * Request implementation for fetching images, PDF files or other assets from the server
 * and returning them as byte arrays that can be converted to appropriate types for the Android platform.
 */
public class CAASAssetRequest extends CAASRequest<byte[]> {
  private final String url;
  /**
   * Initialize this request with the specified URL and callback.
   * @param url the URL of the image ot fetch. This can be a full, absolute URL or a relative one.
   * @param callback the callback to which result notifications are dispatched.
   */
  public CAASAssetRequest(String url, CAASDataCallback<byte[]> callback) {
    super(callback);
    this.url = url;
  }

  @Override
  String buildQuery(CAASService service) throws Exception {
    if (!url.startsWith("http")) {
      return service.getServerURL() + url;
    }
    return url;
  }

  @Override
  byte[] resultFromResponse(byte[] source) throws Exception {
    //return BitmapFactory.decodeByteArray(source, 0, source.length);
    return source;
  }
}
