package group3.myapplicationlab2;

/**
 * Created by anr.putina on 08/05/17.
 */


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by anr.putina on 05/04/17.
 */

public class GroupPreviewAdapter extends ArrayAdapter<GroupPreview> {

    public GroupPreviewAdapter(Context context, ArrayList<GroupPreview> groupPreviews) {
        super(context, 0, groupPreviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        GroupPreview group = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_item, parent, false);
        }

        // Lookup view for data population
        TextView groupName = (TextView) convertView.findViewById(R.id.group_item_name);
        //TextView groupDescription = (TextView) convertView.findViewById(R.id.group_item_component);
        groupName.setText(group.getName());
        //groupDescription.setText(group.getDescription());

        return convertView;
    }
}

