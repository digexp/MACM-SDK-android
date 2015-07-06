package com.ibm.caas.sdktest.activities;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.ibm.caas.CAASContentItem;
import com.ibm.caas.CAASContentItemRequest;
import com.ibm.caas.CAASContentItemsList;
import com.ibm.caas.CAASDataCallback;
import com.ibm.caas.CAASErrorResult;
import com.ibm.caas.CAASService;
import com.ibm.caas.sdktest.util.Constants;
import com.ibm.caas.sdktest.util.GeneralUtils;
import com.ibm.caas.sdktest.util.GenericCache;

import java.util.List;

/**
 * A list fragment representing a list of content items. This fragment also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is currently being viewed in a {@link ItemDetailFragment}.
 * <p>Activities containing this fragment MUST implement the {@link Callbacks} interface.
 */
public class ItemListFragment extends ListFragment {
  /**
   * The serialization (saved instance state) Bundle key representing the activated item position. Only used on tablets.
   */
  private static final String STATE_ACTIVATED_POSITION = "activated_position";
  /**
   * The fragment's current callback object, which is notified of list item clicks.
   */
  private Callbacks callbacks = dummyCallbacks;
  /**
   * The current activated item position. Only used on tablets.
   */
  private int activatedPosition = ListView.INVALID_POSITION;
  private static GenericCache cache = GenericCache.getInstance();
  private static final String LOG_TAG = ItemListFragment.class.getSimpleName();

  /**
   * A callback interface that all activities containing this fragment must implement. This mechanism allows activities to be notified of item selections.
   */
  public interface Callbacks {
    /**
     * Callback for when an item has been selected.
     */
    public void onItemSelected(CAASContentItem item);
  }

  /**
   * A dummy implementation of the {@link Callbacks} interface that does
   * nothing. Used only when this fragment is not attached to an activity.
   */
  private static Callbacks dummyCallbacks = new Callbacks() {
    @Override
    public void onItemSelected(CAASContentItem item) {
    }
  };

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public ItemListFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    CAASContentItemsList list = GenericCache.getInstance().get(Constants.ITEMS);
    List<CAASContentItem> items = list.getContentItems();
    Log.i(LOG_TAG, "onCreate() : nb items = " + items.size());
    //setListAdapter(new ArrayAdapter<CAASContentItem>(getActivity(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, items));
    setListAdapter(new ItemListAdapter(getActivity(), items));
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Log.i(LOG_TAG, "onViewCreated()");
    // Restore the previously serialized activated item position.
    if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
      setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
    }
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Log.i(LOG_TAG, "onAttach()");
    // Activities containing this fragment must implement its callbacks.
    if (!(activity instanceof Callbacks)) {
      throw new IllegalStateException("Activity must implement fragment's callbacks.");
    }
    callbacks = (Callbacks) activity;
  }

  @Override
  public void onDetach() {
    super.onDetach();
    Log.i(LOG_TAG, "onDetach()");
    // Reset the active callbacks interface to the dummy implementation.
    callbacks = dummyCallbacks;
  }

  @Override
  public void onListItemClick(ListView listView, View view, int position, long id) {
    super.onListItemClick(listView, view, position, id);
    // Notify the active callbacks interface (the activity, if the fragment is attached to one) that an item has been selected.
    ListAdapter adapter = getListAdapter();
    CAASContentItem item = (CAASContentItem) adapter.getItem(position);
    GenericCache.getInstance().put(Constants.CURRENT_ITEM, item);
    Log.d(LOG_TAG, "onListItemClick(position=" + position + ") item = " + item);
    try {
      CAASService server = cache.get(Constants.SERVER);
      CAASDataCallback<CAASContentItem> callback = new CAASDataCallback<CAASContentItem>() {
        @Override
        public void onSuccess(CAASContentItem data) {
          callbacks.onItemSelected(data);
        }

        @Override
        public void onError(CAASErrorResult error) {
          GeneralUtils.logErrorResult(LOG_TAG, "onListItemClick() - failed to query item", error);
        }
      };
      CAASContentItemRequest request = new CAASContentItemRequest(callback);
      request.setOid(item.getOid());
      request.addProperties("id", "title", "categories", "keywords");
      server.executeRequest(request);
    } catch(Exception e) {
      Log.e(LOG_TAG, "error in onListItemClick(): ", e);
    }
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (activatedPosition != ListView.INVALID_POSITION) {
      // Serialize and persist the activated item position.
      outState.putInt(STATE_ACTIVATED_POSITION, activatedPosition);
    }
  }

  /**
   * Turns on activate-on-click mode. When this mode is on, list items will be given the 'activated' state when touched.
   */
  public void setActivateOnItemClick(boolean activateOnItemClick) {
    // When setting CHOICE_MODE_SINGLE, ListView will automatically give items the 'activated' state when touched.
    getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
  }

  private void setActivatedPosition(int position) {
    if (position == ListView.INVALID_POSITION) {
      getListView().setItemChecked(activatedPosition, false);
    } else {
      getListView().setItemChecked(position, true);
    }
    activatedPosition = position;
  }
}
