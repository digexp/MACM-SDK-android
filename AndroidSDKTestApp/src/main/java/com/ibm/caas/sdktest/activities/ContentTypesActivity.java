package com.ibm.caas.sdktest.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ibm.caas.CAASContentItem;
import com.ibm.caas.CAASContentItemsList;
import com.ibm.caas.CAASContentItemsRequest;
import com.ibm.caas.CAASDataCallback;
import com.ibm.caas.CAASErrorResult;
import com.ibm.caas.CAASService;
import com.ibm.caas.sdktest.R;
import com.ibm.caas.sdktest.util.Constants;
import com.ibm.caas.sdktest.util.GeneralUtils;
import com.ibm.caas.sdktest.util.GenericCache;
import com.ibm.caas.sdktest.util.Settings;

import java.util.List;

public class ContentTypesActivity extends ListActivity {
  private static final String LOG_TAG = ContentTypesActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_content_types);
    String[] items = { "Book", "Movie" };
    ListView listView = (ListView) findViewById(android.R.id.list);
    ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, android.R.id.text1, items);
    this.setListAdapter(adapter);
    listView.setAdapter(adapter);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    //getMenuInflater().inflate(R.menu.menu_content_types, menu);
    return true;
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    ListAdapter adapter = getListAdapter();
    final String contentType = (String) adapter.getItem(position);
    final GenericCache cache = GenericCache.getInstance();
    cache.put(Constants.CONTENT_TYPE, contentType);
    List<CAASContentItem> items = null;
    boolean result = false;
    try {
      CAASService server = cache.get(Constants.SERVER);
      CAASDataCallback<CAASContentItemsList> callback = new CAASDataCallback<CAASContentItemsList>() {
        @Override
        public void onSuccess(CAASContentItemsList dataList) {
          cache.put(Constants.ITEMS, dataList);
          int count = 0;
          for (CAASContentItem item: dataList.getContentItems()) {
            Log.d(LOG_TAG, "onSuccess() : item[" + (count++) + "] = " + item);
          }
          startActivity(new Intent(getApplicationContext(), ItemListActivity.class));
        }

        @Override
        public void onError(CAASErrorResult error) {
          GeneralUtils.logErrorResult(LOG_TAG, "onListItemClick('" + contentType + "') - failed to query item list", error);
        }
      };
      CAASContentItemsRequest request = new CAASContentItemsRequest(callback);
      request.setPath(Settings.macmLib + "/Content Types/" + contentType);
      request.addProperties("id", "title", "categories", "keywords");
      request.addElements("cover", "author", "publish_date", "isbn", "price", "rating");
      request.setPageNumber(1);
      request.setPageSize(10);
      server.executeRequest(request);
    } catch(Exception e) {
      Log.e(LOG_TAG, "error in onListItemClick(): ", e);
    }
  }
}
