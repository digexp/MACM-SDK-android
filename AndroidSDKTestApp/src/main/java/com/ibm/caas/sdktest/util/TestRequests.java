package com.ibm.caas.sdktest.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.ibm.caas.CAASAssetRequest;
import com.ibm.caas.CAASContentItem;
import com.ibm.caas.CAASContentItemRequest;
import com.ibm.caas.CAASContentItemsList;
import com.ibm.caas.CAASContentItemsRequest;
import com.ibm.caas.CAASDataCallback;
import com.ibm.caas.CAASErrorResult;
import com.ibm.caas.CAASRequestResult;
import com.ibm.caas.CAASService;

import java.util.List;

/**
 * Performs some test requests.
 */
public class TestRequests {
  /**
   * Log tag for this class.
   */
  private final static String LOG_TAG = TestRequests.class.getSimpleName();

  public static void doSomeQueries() {
    CAASService service = GenericCache.getInstance().get(Constants.SERVER);
    //final String path = "MACM Default Application/Content/Data/Offer";
    //final String path = "MACM Default Application/Content Types/Offer";
    //final String path = "MACM Default Application/Views/All";
    final String path = "OOTB Content/Views/All";
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
    request.addElements("author", "title", "publish_date", "isbn", "price", "rating", "cover");
    request.setPageSize(100);
    request.setPageNumber(1);
    service.executeRequest(request);
  }
}
