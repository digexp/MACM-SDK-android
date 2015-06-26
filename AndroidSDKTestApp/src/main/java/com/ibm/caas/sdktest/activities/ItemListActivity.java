package com.ibm.caas.sdktest.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.ibm.caas.CAASContentItem;
import com.ibm.caas.sdktest.R;
import com.ibm.caas.sdktest.util.Constants;
import com.ibm.caas.sdktest.util.GenericCache;

/**
 * An activity representing a list of Articles. This activity has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched, lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and item details side-by-side using two vertical panes.
 * <p>The activity makes heavy use of fragments. The list of items is a {@link ItemListFragment} and the item details
 * (if present) is a {@link ItemDetailFragment}.
 * <p> This activity also implements the required {@link ItemListFragment.Callbacks} interface to listen for item selections.
 */
public class ItemListActivity extends Activity implements ItemListFragment.Callbacks {
  /**
   * Log tag for this class.
   */
  private final static String LOG_TAG = ItemListActivity.class.getSimpleName();
  /**
   * Whether or not the activity is in two-pane mode, i.e. running on a tablet device.
   */
  private boolean isTwoPane;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i(LOG_TAG, "onCreate()");
    setContentView(R.layout.activity_article_list);
    ActionBar bar = getActionBar();
    if (bar != null) {
      bar.setTitle((String) GenericCache.getInstance().get(Constants.CONTENT_TYPE));
      // Show the Up button in the action bar.
      bar.setDisplayHomeAsUpEnabled(true);
    }
    if (findViewById(R.id.article_detail_container) != null) {
      // The detail container view will be present only in the large-screen layouts (res/values-large and
      // res/values-sw600dp). If this view is present, then the activity should be in two-pane mode.
      isTwoPane = true;
      // In two-pane mode, list items should be given the 'activated' state when touched.
      ((ItemListFragment) getFragmentManager().findFragmentById(R.id.article_list)).setActivateOnItemClick(true);
    }
    // TODO: If exposing deep links into your app, handle intents here.
  }

  /*
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      // This ID represents the Home or Up button. In the case of this activity, the Up button is shown. Use NavUtils to allow users
      // to navigate up one level in the application structure. For more details, see the Navigation pattern on Android Design:
      // http://developer.android.com/design/patterns/navigation.html#up-vs-back
      NavUtils.navigateUpFromSameTask(this);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  */

  /**
   * Callback method from {@link ItemListFragment.Callbacks} indicating that the item with the given ID was selected.
   */
  @Override
  public void onItemSelected(CAASContentItem item) {
    if (item == null) {
      Log.i(LOG_TAG, "item is null");
      return;
    }
    Log.i(LOG_TAG, String.format("onItemSelected(id=%s)", item.getOid()));
    if (isTwoPane) {
      // In two-pane mode, show the detail view in this activity by adding or replacing the detail fragment using a fragment transaction.
      Bundle arguments = new Bundle();
      arguments.putSerializable(ItemDetailFragment.ARG_ITEM_ID, item);
      ItemDetailFragment fragment = new ItemDetailFragment();
      fragment.setArguments(arguments);
      getFragmentManager().beginTransaction().replace(R.id.article_detail_container, fragment).commit();
    } else {
      // In single-pane mode, simply start the detail activity for the selected item ID.
      Intent detailIntent = new Intent(this, ItemDetailActivity.class);
      detailIntent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item);
      startActivity(detailIntent);
    }
  }
}
