package com.ibm.caas.sdktest.activities;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ibm.caas.CAASContentItem;
import com.ibm.caas.sdktest.R;

import static com.ibm.caas.CAASProperties.*;

/**
 * A fragment representing a single content item detail screen.
 * This fragment is either contained in a {@link ItemListActivity} in two-pane mode (on tablets) or a {@link ItemDetailActivity} on handsets.
 */
public class ItemDetailFragment extends Fragment {
  /**
   * The fragment argument representing the item ID that this fragment represents.
   */
  public static final String ARG_ITEM_ID = "item_id";
  /**
   * The content this fragment is presenting.
   */
  private CAASContentItem item;
  private ViewGroup rootView = null;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon screen orientation changes).
   */
  public ItemDetailFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments().containsKey(ARG_ITEM_ID)) {
      item = (CAASContentItem) getArguments().get(ARG_ITEM_ID);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    rootView = (ViewGroup) inflater.inflate(R.layout.fragment_article_detail, container, false);
    rootView.findViewById(0);
    // update the text fields values
    if (item != null) {
      setViewText(R.id.title_detail, TITLE);
      setViewText(R.id.author, "author");
      setViewText(R.id.published, PUBLISH_DATE);
      setViewText(R.id.isbn, "isbn");
      setViewText(R.id.price, "price");
      setViewText(R.id.categories, CATEGORIES);
      setViewText(R.id.keywords, KEYWORDS);
    }
    return rootView;
  }

  /**
   * Set the text of a text view specified by its resource id.
   * @param resource the id of the resource.
   * @param attribute the name of a content item element whose value is set as the view's text.
   */
  private void setViewText(int resource, String attribute) {
    TextView view = (TextView) rootView.findViewById(resource);
    Object value = item.getElement(attribute);
    if (value == null) value = item.getProperty(attribute);
    if (value == null) value = "";
    view.setText(value.toString());
  }
}
