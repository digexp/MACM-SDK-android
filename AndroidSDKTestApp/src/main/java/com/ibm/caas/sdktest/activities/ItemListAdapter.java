package com.ibm.caas.sdktest.activities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ibm.caas.CAASAssetRequest;
import com.ibm.caas.CAASContentItem;
import com.ibm.caas.CAASDataCallback;
import com.ibm.caas.CAASErrorResult;
import com.ibm.caas.CAASService;
import com.ibm.caas.sdktest.R;
import com.ibm.caas.sdktest.util.Constants;
import com.ibm.caas.sdktest.util.GeneralUtils;
import com.ibm.caas.sdktest.util.GenericCache;

import java.util.List;

/**
 * An [@link ArrayAdapter} which displays for each row an optional image beside the content item's title.
 */
public class ItemListAdapter extends ArrayAdapter<CAASContentItem> {
  /**
   * Log tag for this class.
   */
  private final static String LOG_TAG = ItemListAdapter.class.getSimpleName();
  /**
   * The context in which this adapter is used.
   */
  private final Context context;
  /**
   * The list of content items to display.
   */
  private final List<CAASContentItem> items;

  /**
   * Initialize this adapter with the specified context and items.
   * @param context the context in which this adapter is used.
   * @param items the list of content items to display.
   */
  public ItemListAdapter(Context context, List<CAASContentItem> items) {
    super(context, R.layout.items_list_row, items);
    this.context = context;
    this.items = items;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View rowView = inflater.inflate(R.layout.items_list_row, parent, false);
    TextView textView = (TextView) rowView.findViewById(R.id.item_label);
    final ImageView imageView = (ImageView) rowView.findViewById(R.id.item_image);
    String title = items.get(position).getTitle();
    textView.setText(title);
    //URL imageURL = items.get(position).getElement("cover");
    CAASContentItem item = items.get(position);
    String imageURL = item.getElement("cover");
    boolean displayImages = true;
    if ((imageURL != null) && displayImages) {
      CAASService service = GenericCache.getInstance().get(Constants.SERVER);
      Log.v(LOG_TAG, "getView(): title='" + item.getTitle() + "', imageURL='" + imageURL + "', server url='" + service.getServerURL() + "'");
      final String fullURL = service.getServerURL() + imageURL.toString();
      Bitmap bitmap = GenericCache.getInstance().get(fullURL);
      if (bitmap == null) {
        CAASDataCallback<byte[]> callback = new CAASDataCallback<byte[]>() {
          @Override
          public void onSuccess(byte[] bytes) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            GenericCache.getInstance().put(fullURL, bitmap); // cache the image
            setDrawable(imageView, bitmap);
          }

          @Override
          public void onError(CAASErrorResult error) {
            GeneralUtils.logErrorResult(LOG_TAG, "failed to load image at " + fullURL, error);
          }
        };
        CAASAssetRequest request = new CAASAssetRequest(fullURL, callback);
        service.executeRequest(request);
      } else {
        setDrawable(imageView, bitmap);
      }
    }
    return rowView;
  }

  /**
   * Set the specified image onto the current list item.
   * @param imageView the view containing the image.
   * @param bitmap the image to set.
   */
  private void setDrawable(ImageView imageView, Bitmap bitmap) {
    BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
    imageView.setImageDrawable(drawable);
  }
}
