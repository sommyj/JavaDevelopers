package sommy.org.javadevelopers;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

/**
 * Created by somto on 7/22/17.
 */

public class CustomListAdapter extends ArrayAdapter {

    private final Activity context;
    private final List<String> itemName;
    private final List<String> imageUri;
    private ImageView imageView;
    private TextView textView;

    public CustomListAdapter(Activity context, List<String> itemName, List<String> imageUri) {
        super(context, R.layout.user_list, itemName);
        this.context = context;
        this.itemName = itemName;
        this.imageUri = imageUri;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public TextView getTextView() {
        return textView;
    }

    /**
     * This method is used to set the image and text view with data gotten from MainActivity class
     * @param position in the list view
     * @param view
     * @param parent
     * @return the an inflated view to the parent adapter class to be passed to the list view
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.user_list, null, true);

        imageView = (ImageView) rowView.findViewById(R.id.imageView);
        textView = (TextView) rowView.findViewById(R.id.textView);

        textView.setText(itemName.get(position).toString());
        new DownloadImageTask(imageView).execute(imageUri.get(position));
        return rowView;
    }

    /**
     *This class downloads the image of the URL retrieved from MainActivity class
     */
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
