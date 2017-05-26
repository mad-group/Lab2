package group3.myapplicationlab2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by anr.putina on 26/05/17.
 */

public class GroupPreviewAdapterHashMap extends BaseAdapter {

    private final ArrayList mData;

    public GroupPreviewAdapterHashMap(HashMap<String, GroupPreview> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
        Log.d("DEBUG", mData.toString());
    }

    public void add(HashMap<String, GroupPreview> map){
        mData.addAll(map.entrySet());
        Log.d("IN ADD", "add");
        Log.d("DEBUG", mData.toString());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, GroupPreview> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        final View result;

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item, parent, false);
        }

        Map.Entry<String, GroupPreview> groupPreviewEntry = (Map.Entry<String, GroupPreview>)getItem(pos);

        // Lookup view for data population
        TextView groupName = (TextView) convertView.findViewById(R.id.group_item_name);
        TextView groupDescription = (TextView) convertView.findViewById(R.id.group_item_component);

        groupName.setText(groupPreviewEntry.getValue().getName());
        groupDescription.setText(groupPreviewEntry.getValue().getDescription());

        return convertView;
    }


}
