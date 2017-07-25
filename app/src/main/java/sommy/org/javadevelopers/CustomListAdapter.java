package sommy.org.javadevelopers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static android.R.attr.resource;

/**
 * Created by somto on 7/22/17.
 */

public class CustomListAdapter extends ArrayAdapter {

    private final Activity context;
    private final List itemname;
    private final Integer[] imgid;

    public CustomListAdapter(Activity context, List itemname, Integer[] imgid) {
        super(context, R.layout.mylist, itemname);
        this.context = context;
        this.itemname = itemname;
        this.imgid = imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.mylist, null, true);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
        TextView textView = (TextView) rowView.findViewById(R.id.textView);

        textView.setText(itemname.get(position).toString());
        imageView.setImageResource(imgid[position]);
        return rowView;
    }
}
