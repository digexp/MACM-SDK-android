  package com.ibm.caas.sdktest.activities;

  import android.app.ActionBar;
  import android.app.Activity;
  import android.os.Bundle;

  import com.ibm.caas.CAASContentItem;
  import com.ibm.caas.sdktest.R;
  import com.ibm.caas.sdktest.util.Constants;
  import com.ibm.caas.sdktest.util.GenericCache;


/**
 * An activity representing a single Article detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_article_detail);

    ActionBar bar = getActionBar();
    if (bar != null) {
      CAASContentItem item = GenericCache.getInstance().get(Constants.CURRENT_ITEM, null);
      if (item != null) {
        bar.setTitle(item.getTitle());
      }
      // Show the Up button in the action bar.
      bar.setDisplayHomeAsUpEnabled(true);
    }

    if (savedInstanceState == null) {
      // Create the detail fragment and add it to the activity using a fragment transaction.
      Bundle arguments = new Bundle();
      arguments.putSerializable(ItemDetailFragment.ARG_ITEM_ID, getIntent().getSerializableExtra(ItemDetailFragment.ARG_ITEM_ID));
      ItemDetailFragment fragment = new ItemDetailFragment();
      fragment.setArguments(arguments);
      getFragmentManager().beginTransaction().add(R.id.article_detail_container, fragment).commit();
    }
  }
}
