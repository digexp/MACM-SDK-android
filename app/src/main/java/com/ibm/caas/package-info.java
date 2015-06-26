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


/**
 * Android API to request content from an MACM server.
 * <p>Example 1, explictely sign in to the MACM server
 * <pre>
 *   CAASService service = new CAASService("http://www.myhost.com:10039", "MyContextRoot", "MyTenant", "username", "password");
 *
 *   // handle the sign-in result asynchronously
 *   CAASCallback&lt;Void&gt; callback = new CAASCallback&lt;Void&gt;() {
 *     &#64;Override
 *     public void onSuccess(Void result) {
 *       // sign-in successful
 *     }
 *
 *     &#64;Override
 *     public void onError(CAASErrorResult error) {
 *       // handle the error
 *     }
 *   }
 *
 *   CAASRequestResult&lt;Void&gt; result = service.signIn(callback);
 * </pre>
 *
 * <p>Example 2, query the items with content type "Book" by path with implicit authentication
 * <pre>
 *   CAASService service = new CAASService("http://www.myhost.com:10039", "MyContextRoot", "MyTenant", "username", "password");
 *
 *   // handle the request result asynchronously
 *   CAASCallback&lt;CAASContentItemsList&gt; callback = new CAASCallback&lt;CAASContentItemsList&gt;() {
 *     &#64;Override
 *     public void onSuccess(CAASContentItemsList itemsList) {
 *       List&lt;CAASContentItem&gt;&gt; items = itemsList.getContentItems();
 *       // do something with the list of items
 *     }
 *
 *     &#64;Override
 *     public void onError(CAASErrorResult error) {
 *       // handle the error
 *     }
 *   }
 *
 *   CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
 *   request.setPath("MACM/Content Types/Book"); // path to "Book" content items
 *   request.addElements("cover");               // request the url to the cover image
 *   request.addSortDescriptor("title", true);   // sort by ascending title
 *   request.setPageSize(10);                    // request the first 10 items
 *   request.setPageNumber(1);
 *
 *   CAASRequestResult&lt;CAASContentItemsList&gt; result = service.executeRequest(request);
 * </pre>
 */
package com.ibm.caas;
