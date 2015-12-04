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

import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.ibm.caas.CAASContentItem;
import com.ibm.caas.CAASContentItemsList;
import com.ibm.caas.CAASContentItemsRequest;
import com.ibm.caas.CAASService;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.ibm.caas.sdktest.test.TestUtils.getCurrentClassAndMethod;
import static com.ibm.caas.CAASProperties.*;
import static com.ibm.caas.sdktest.test.TestUtils.itemsEqual;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 26 Movie items, 26 Book items
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class CAASContentItemsRequestTest {
  private static final String LOG_TAG = CAASContentItemsRequestTest.class.getSimpleName();

  @Test(timeout=10000)
  public void testViewAllItems() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath(TestUtils.getLibraryName() + "/Views/All");
    request.setPageSize(100);
    request.setPageNumber(1);
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    assertNotNull(callback.result);
    List<CAASContentItem> result = callback.result.getContentItems();
    assertFalse(result.isEmpty());
    assertTrue(result.size() >= 1);
    int movies = 0;
    int books = 0;
    int unexpected = 0;
    for (CAASContentItem item: result) {
      if ("Book".equals(item.getContentType())) {
        books++;
      } else if ("Movie".equals(item.getContentType())) {
        movies++;
      } else {
        unexpected++;
      }
    }
    //assertTrue(movies >= 26);
    //assertTrue(books >= 26);
    assertEquals(0, unexpected);
  }

  /**
   * Test that getting a list by id or by path gives the same results.
   */
  @Test(timeout=20000)
  public void testGetContentById() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath(TestUtils.getLibraryName() + "/Content Types/Book");
    int pgSize = 6;
    // first 10 books in ascending title order
    request.setPageSize(pgSize);
    request.setPageNumber(1);
    request.addSortDescriptor("title", true);
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    assertNotNull(callback.result);
    CAASContentItemsList resultByPath = callback.result;
    List<CAASContentItem> itemsByPath = resultByPath.getContentItems();
    assertFalse(itemsByPath.isEmpty());
    assertEquals(pgSize, itemsByPath.size());
    assertEquals(pgSize, resultByPath.getPageSize());
    callback = new CAASDataCallbackTest<CAASContentItemsList>();
    request = new CAASContentItemsRequest(callback);
    request.setOid(resultByPath.getOid());
    request.setPageSize(pgSize);
    request.setPageNumber(1);
    request.addSortDescriptor("title", true);
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    assertNotNull(callback.result);
    CAASContentItemsList resultById = callback.result;
    List<CAASContentItem> itemsById = resultById.getContentItems();
    assertFalse(itemsByPath.isEmpty());
    assertEquals(pgSize, itemsById.size());
    assertEquals(pgSize, resultById.getPageSize());
    assertEquals(resultByPath.getOid(), resultById.getOid());
    for (int i=0; i<itemsById.size(); i++) {
      assertTrue(itemsEqual(itemsByPath.get(i), itemsById.get(i)));
    }
  }

  @Test(timeout=10000)
  public void testViewOpenProjects() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath("MACM System/Views/Open Projects");
    request.setPageSize(100);
    request.setPageNumber(1);
    String[] properties = {CREATION_DATE, ITEM_COUNT, NAME, STATE, TITLE, UUID};
    request.addProperties(properties);
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    assertNotNull(callback.result);
    List<CAASContentItem> result = callback.result.getContentItems();
    assertFalse("result=" + result, result.isEmpty());
    for (CAASContentItem item: result) {
      for (String prop: properties) {
        assertTrue(String.format("item does not have property '%s' : %s", prop, item), item.hasProperty(prop));
      }
    }
  }

  @Test(timeout=20000)
  public void testProjectScope() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath("MACM System/Views/Open Projects");
    request.setPageSize(100);
    request.setPageNumber(1);
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    assertNotNull(callback.result);
    List<CAASContentItem> result = callback.result.getContentItems();
    assertFalse(result.isEmpty());
    CAASContentItem project = result.get(0);

    CAASDataCallbackTest<CAASContentItemsList> scopeCallback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest scopeRequest = new CAASContentItemsRequest(scopeCallback);
    scopeRequest.setPath(TestUtils.getLibraryName() + "/Views/All");
    scopeRequest.setPageSize(100);
    scopeRequest.setPageNumber(1);
    scopeRequest.setProject(project.getName());
    service.executeRequest(scopeRequest);
    scopeCallback.awaitCompletion();
    assertTrue(scopeCallback.successful);
    assertNotNull(scopeCallback.result);
    List<CAASContentItem> scopeResult = scopeCallback.result.getContentItems();
    assertFalse(scopeResult.isEmpty());
  }

  @Test(timeout=10000)
  public void testMyApprovals() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath(TestUtils.getLibraryName() + "/Views/My Approvals");
    request.setPageSize(100);
    request.setPageNumber(1);
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    assertNotNull(callback.result);
    List<CAASContentItem> result = callback.result.getContentItems();
    assertFalse(result.isEmpty());
  }

  @Test(timeout=20000)
  public void testPaging() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    int pgSize = 6;
    boolean end = false;
    int i = 0;
    while (!end) {
      i++;
      CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
      CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
      request.setPath(TestUtils.getLibraryName() + "/Views/All");
      request.setPageSize(pgSize);
      request.setPageNumber(i);
      service.executeRequest(request);
      callback.awaitCompletion();
      String msg = "page #" + i + " ";
      assertTrue(msg, callback.successful);
      CAASContentItemsList result = callback.result;
      assertNotNull(msg, result);
      List<CAASContentItem> items = result.getContentItems();
      assertNotNull(msg, items);
      assertFalse(msg, items.isEmpty());
      boolean hasNext = result.hasNextPage();
      assertTrue(msg, result.getPageSize() == pgSize);
      if (hasNext) assertEquals(msg, pgSize, items.size());
      else assertTrue(msg, (items.size() > 0) && (items.size() <= pgSize));
      //assertEquals(msg, hasNext ? 10 : items.size(), items.size());
      assertEquals(msg, i, result.getPageNumber());
      assertTrue(msg, (result.getPageSize() > 0) && (result.getPageSize() <= pgSize));
      end = !hasNext;
    }
  }

  /**
   * Test sort of all items by ascending "lastmodifier", then descending "title".
   * @throws Exception if any error occurs.
   */
  @Test(timeout=10000)
  public void testSort() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath(TestUtils.getLibraryName() + "/Views/All");
    String sortCriteria = TITLE;
    request.addProperties(OID, sortCriteria);
    request.setPageSize(100);
    request.setPageNumber(1);
    // sort by categories ascending, then title descending
    request.addSortDescriptor(sortCriteria, true);
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    CAASContentItemsList result = callback.result;
    assertNotNull(result);
    List<CAASContentItem> items = result.getContentItems();
    assertNotNull(items);
    assertFalse(items.isEmpty());
    SortedMap<String, CAASContentItem> map = new TreeMap<String, CAASContentItem>();
    for (CAASContentItem item: items) {
      String contentType = item.getProperty(sortCriteria);
      map.put(contentType, item);
    }
    List<CAASContentItem> myList = new ArrayList<CAASContentItem>(items.size());
    for (Map.Entry<String, CAASContentItem> entry: map.entrySet()) {
      myList.add(entry.getValue());
    }
    assertEquals(myList, items);
  }

  @Test(timeout=10000)
  public void testFilterCategoriesAll() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath(TestUtils.getLibraryName() + "/Views/All");
    request.addProperties(OID, CONTENT_TYPE, TITLE, LAST_MODIFIED_DATE, CATEGORIES, KEYWORDS);
    request.setPageSize(100);
    request.setPageNumber(1);
    String[] categories = {"samples/macm/books/biography"};
    request.addAllCategories(categories);
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    assertNotNull(callback.result);
    List<CAASContentItem> result = callback.result.getContentItems();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    for (CAASContentItem item: result) {
      for (String cat: categories) {
        String msg = String.format("category '%s' is not in item %s", cat, item);
        assertTrue(msg, item.getCategories().contains(cat));
      }
    }
  }

  @Test(timeout=10000)
  public void testFilterCategoriesAny() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath(TestUtils.getLibraryName() + "/Views/All");
    request.addProperties(OID, CONTENT_TYPE, TITLE, LAST_MODIFIED_DATE, CATEGORIES, KEYWORDS);
    request.setPageSize(100);
    request.setPageNumber(1);
    String[] categories = {"samples/macm/books/biography", "samples/macm/books/textbook"};
    request.addAnyCategories(categories);
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    assertNotNull(callback.result);
    List<CAASContentItem> result = callback.result.getContentItems();
    assertFalse(result.isEmpty());
    assertEquals(3, result.size());
    for (CAASContentItem item: result) {
      boolean hasAny = false;
      for (String cat: categories) {
        hasAny |= item.getCategories().contains(cat);
        if (hasAny) break;
      }
      String msg = String.format("none of these categories '%s' is in item %s", Arrays.asList(categories), item);
      assertTrue(msg, hasAny);
    }
  }

  @Test(timeout=10000)
  public void testFilterKeywordsAll() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath(TestUtils.getLibraryName() + "/Views/All");
    request.addProperties(OID, CONTENT_TYPE, TITLE, LAST_MODIFIED_DATE, CATEGORIES, KEYWORDS);
    request.setPageSize(100);
    request.setPageNumber(1);
    String[] keywords = {"bestseller", "special_offer"};
    request.addAllKeywords(keywords);
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    assertNotNull(callback.result);
    List<CAASContentItem> result = callback.result.getContentItems();
    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    for (CAASContentItem item: result) {
      for (String cat: keywords) {
        String msg = String.format("keyword '%s' is not in item %s", cat, item);
        assertTrue(msg, item.getKeywords().contains(cat));
      }
    }
  }

  @Test(timeout=10000)
  public void testFilterKeywordsAny() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath(TestUtils.getLibraryName() + "/Views/All");
    request.addProperties(OID, CONTENT_TYPE, TITLE, LAST_MODIFIED_DATE, CATEGORIES, KEYWORDS);
    request.setPageSize(100);
    request.setPageNumber(1);
    String[] keywords = {"bestseller", "special_offer"};
    request.addAnyKeywords(keywords);
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    assertNotNull(callback.result);
    List<CAASContentItem> result = callback.result.getContentItems();
    assertFalse(result.isEmpty());
    assertEquals(5, result.size());
    for (CAASContentItem item: result) {
      boolean hasAny = false;
      for (String cat: keywords) {
        hasAny |= item.getKeywords().contains(cat);
        if (hasAny) break;
      }
      String msg = String.format("none of these keywords '%s' is in item %s", Arrays.asList(keywords), item);
      assertTrue(msg, hasAny);
    }
  }

  @Test(timeout=10000)
  public void testFilterTitleContains() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath(TestUtils.getLibraryName() + "/Views/All");
    request.addProperties(OID, CONTENT_TYPE, TITLE);
    request.setPageSize(100);
    request.setPageNumber(1);
    String partialTitle = "Lives";
    // should return "Sample Book 1", "Sample Book 10", "Sample Book 11" ...
    request.setTitleContains(partialTitle);
    service.executeRequest(request);
    callback.awaitCompletion();
    assertTrue(callback.successful);
    assertNotNull(callback.result);
    List<CAASContentItem> items = callback.result.getContentItems();
    assertFalse(items.isEmpty());
    assertEquals(1, items.size());
    for (CAASContentItem item: items) {
      String msg = String.format("'%s' is not in the title of item %s", partialTitle, item);
      assertTrue(msg, item.getTitle().contains(partialTitle));
    }
  }

  @Test(timeout=30000)
  public void testFilterWorkflowStatus() throws Exception {
    String cmn = getCurrentClassAndMethod();
    System.out.println("in " + cmn);
    CAASService service = TestUtils.createService();
    Map<CAASContentItemsRequest.WorkflowStatus, Integer> expectedNumbers = new EnumMap<CAASContentItemsRequest.WorkflowStatus, Integer>(CAASContentItemsRequest.WorkflowStatus.class);
    for (CAASContentItemsRequest.WorkflowStatus status: CAASContentItemsRequest.WorkflowStatus.values()) {
      expectedNumbers.put(status, 0);
    }
    CAASDataCallbackTest<CAASContentItemsList> initCallback = new CAASDataCallbackTest<CAASContentItemsList>();
    CAASContentItemsRequest initRequest = new CAASContentItemsRequest(initCallback);
    initRequest.setPath(TestUtils.getLibraryName() + "/Views/All");
    initRequest.addProperties(OID, CONTENT_TYPE, TITLE, STATUS);
    initRequest.setPageSize(100);
    initRequest.setPageNumber(1);
    service.executeRequest(initRequest);
    initCallback.awaitCompletion();
    assertTrue(initCallback.successful);
    assertNotNull(initCallback.result);
    List<CAASContentItem> allItems = initCallback.result.getContentItems();
    for (CAASContentItem item: allItems) {
      CAASContentItemsRequest.WorkflowStatus status = CAASContentItemsRequest.WorkflowStatus.valueOf(item.getStatus());
      int n = expectedNumbers.get(status);
      expectedNumbers.put(status, n + 1);
    }
    Log.v(LOG_TAG, cmn + " got " + allItems.size() + " items, status distribution = " + expectedNumbers);
    for (CAASContentItemsRequest.WorkflowStatus status: CAASContentItemsRequest.WorkflowStatus.values()) {
      CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
      CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
      request.setPath(TestUtils.getLibraryName() + "/Views/All");
      request.addProperties("id", "contenttype", "title", "status");
      request.addProperties(OID, CONTENT_TYPE, TITLE, STATUS);
      request.setPageSize(100);
      request.setPageNumber(1);
      request.setWorkflowStatus(status);
      service.executeRequest(request);
      callback.awaitCompletion();
      assertTrue(callback.successful);
      assertNotNull(callback.result);
      List<CAASContentItem> items = callback.result.getContentItems();
      int expectedNumber = expectedNumbers.get(status);
      assertFalse((expectedNumber > 0) && items.isEmpty());
      String format = "discrepancy for status '%s'";
      assertEquals(String.format(format, status), expectedNumber, items.size());
      for (CAASContentItem item: items) {
        String msg = String.format("'%s' is not the status of item %s", status, item);
        assertTrue(msg, item.getStatus().equalsIgnoreCase(status.toString()));
      }
    }
  }

  @Test(timeout=10000)
  public void testEmptyListInResponse() throws Exception {
    System.out.println("in " + getCurrentClassAndMethod());
    CAASService service = TestUtils.createService();
    for (CAASContentItemsRequest.WorkflowStatus status: CAASContentItemsRequest.WorkflowStatus.values()) {
      CAASDataCallbackTest<CAASContentItemsList> callback = new CAASDataCallbackTest<CAASContentItemsList>();
      CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
      request.setPath(TestUtils.getLibraryName() + "/Views/All");
      request.addProperties(OID, CONTENT_TYPE, TITLE, STATUS);
      // build a random string and add a filter on the title name with this string to ensure no item is found
      request.setTitleContains(TestUtils.generateRandomString(50));
      service.executeRequest(request);
      callback.awaitCompletion();
      assertTrue(callback.successful);
      assertNotNull(callback.result);
      List<CAASContentItem> items = callback.result.getContentItems();
      assertNotNull(items);
      assertTrue(items.isEmpty());
    }
  }
}
