package group3.myapplicationlab2;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mc on 23/05/17.
 */

public class ScrollPurchaseAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private Map<String,Object> contributors = new HashMap<>();
    private String[] keys;
    Context context;

    public ScrollPurchaseAdapter(Context context, Map<String,Object> contributors) {
        this.context = context;
        this.contributors = contributors;
        this.keys = contributors.keySet().toArray(new String[contributors.size()]);
        this.layoutInflater = LayoutInflater.from(context);
        }

    @Override
    public int getCount() {
        return contributors.size();
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int position) {
        return contributors.get(keys[position]);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView= layoutInflater.inflate(R.layout.scroll_expense_item, null);
        }
        String key = keys[position];
        String value = getItem(position).toString();

        TextView contributor = (TextView) convertView.findViewById(R.id.scroll_item_user);
        contributor.setText(key);
        TextView amount = (TextView) convertView.findViewById(R.id.scroll_item_debit);
        amount.setText(value);

        return convertView;
    }
}
