package group3.myapplicationlab2;

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

public class GroupAdapter extends ArrayAdapter<Group> {

    public GroupAdapter(Context context, ArrayList<Group> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Group group = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.group_item, parent, false);
        }
        // Lookup view for data population
        TextView groupName = (TextView) convertView.findViewById(R.id.group_item_name);
        TextView groupDescription = (TextView) convertView.findViewById(R.id.group_item_component);
        //TextView tvHome = (TextView) convertView.findViewById(R.id.tvHome);
        // Populate the data into the template view using the data object
        groupName.setText(group.getName());
        groupDescription.setText(group.getDescription());
        //tvHome.setText(user.hometown);

        // Return the completed view to render on screen
        return convertView;
    }
}