package com.ibm.caas.sdktest.util;

import android.util.Log;

import com.ibm.caas.CAASContentItemsList;
import com.ibm.caas.CAASContentItemsRequest;
import com.ibm.caas.CAASDataCallback;
import com.ibm.caas.CAASErrorResult;
import com.ibm.caas.CAASService;

/**
 * Performs some test requests.
 */
public class TestRequests {
  /**
   * Log tag for this class.
   */
  private final static String LOG_TAG = TestRequests.class.getSimpleName();
  /**
   * Synchronization lock.
   */
  private final static Object lock = TestRequests.class;

  public static void doSomeQueries() {
    CAASService service = GenericCache.getInstance().get(Constants.SERVER);
    final String path = Settings.macmLib + "/Views/All";
    CAASDataCallback<CAASContentItemsList> callback = new CAASDataCallback<CAASContentItemsList>() {
      @Override
      public void onSuccess(CAASContentItemsList caasContentItems) {
        Log.d(LOG_TAG, "onSuccess(" + path + ") got " + caasContentItems.getContentItems().size() + " items");
      }

      @Override
      public void onError(CAASErrorResult error) {
        GeneralUtils.logErrorResult(LOG_TAG, "error in query for '" + path + "'", error);
      }
    };
    CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
    request.setPath(path);
    request.addProperties("id", "contenttype", "title", "lastmodifieddate", "categories", "keywords");
    request.addElements("author", "title", "publish_date", "isbn", "price", "cover");
    request.setPageSize(100);
    request.setPageNumber(1);
    service.executeRequest(request);
  }

  /**
   * Test the performance for the specified number of requests.
   * @param nbRequests the number of requests to eprform in this test.
   */
  public static void testPerformance(final int nbRequests) {
    Log.v(LOG_TAG, "performance test ...");
    CAASService service = GenericCache.getInstance().get(Constants.SERVER);
    final String path = Settings.macmLib + "/Views/All";
    for (int i=0; i<nbRequests; i++) {
      Log.v(LOG_TAG, "performance test, request #" + (i+1));
      CAASDataCallback<CAASContentItemsList> callback = new CAASDataCallback<CAASContentItemsList>() {
        @Override
        public void onSuccess(CAASContentItemsList caasContentItems) {
          notifyRequestDone();
        }

        @Override
        public void onError(CAASErrorResult error) {
          GeneralUtils.logErrorResult(LOG_TAG, "error in query for '" + path + "'", error);
          notifyRequestDone();
        }

        private void notifyRequestDone() {
          try {
            synchronized(lock) {
              lock.notifyAll();
            }
          } catch(Exception ignore) {
          }
        }
      };
      CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
      request.setPath(path);
      request.addProperties("id", "contenttype", "title", "lastmodifieddate", "categories", "keywords");
      request.addElements("author", "title", "publish_date", "isbn", "price", "cover");
      request.setPageSize(50);
      request.setPageNumber(1);
      service.executeRequest(request);
      try {
        synchronized(lock) {
          lock.wait();
        }
      } catch(Exception ignore) {
      }
    }
    Log.v(LOG_TAG, "measured performance: " + service.getPerformanceStats());
  }
}
