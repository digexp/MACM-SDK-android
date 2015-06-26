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
 * A request object for querying details of a single content item.
 */
public class CAASContentItemRequest extends CAASAbstractContentRequest<CAASContentItem> {
  /**
   * Initialize this request the specified callback.
   * @param callback the callback instance to which the request results will be dispatched asynchronously.
   */
  public CAASContentItemRequest(CAASDataCallback<CAASContentItem> callback) {
    super(callback);
  }

  @Override
  String buildQuery(CAASService service) throws Exception {
    StringBuilder sb = new StringBuilder();
    sb.append(service.getBaseQueryURI());
    if (project != null) {
      sb.append("/$project/").append(project);
    }
    sb.append("/caas");
    sb.append('?').append(encodeParam("current", "true"));
    sb.append('&').append(encodeParam(Utils.CAAS_QUERY_PARAMETER_MIME_TYPE, Utils.CAAS_QUERY_PARAMETER_MIME_TYPE_JSON));
    if (oid != null) {
      sb.append('&').append(encodeParam(Utils.CAAS_QUERY_PARAMETER_URILE, Utils.CAAS_URILE_BY_ID + oid));
    } else if (path != null) {
      sb.append('&').append(encodeParam(Utils.CAAS_QUERY_PARAMETER_URILE, Utils.CAAS_URILE_BY_PATH + path));
    }
    addPropertiesElementsAndParams(sb);
    sb.append('&').append(encodeParam("ibm.type.information", "true"));
    return sb.toString();
  }

  @Override
  CAASContentItem resultFromResponse(byte[] source) throws Exception {
    return new JSONParser(bytesToString(source)).getResult().getContentItems().get(0);
  }
}
