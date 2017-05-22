package group3.myapplicationlab2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;

/**
 * Created by mc on 21/05/17.
 */

class MembersAdapter extends ArrayAdapter<GroupMember> {
    public MembersAdapter(Context context, ArrayList<GroupMember> groupMembers) {
        super(context, 0, groupMembers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        GroupMember groupMember = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_ei_item, parent, false);
        }
        TextView member = (TextView) convertView.findViewById(R.id.item_member);
        TextView amount = (TextView) convertView.findViewById(R.id.item_amount);
        TextView user_id_tv = (TextView) convertView.findViewById(R.id.item_user_id);

        NumberPicker nb = (NumberPicker) convertView.findViewById(R.id.numberPicker);

        member.setText(groupMember.getName());
        amount.setText("0");
        user_id_tv.setText(groupMember.getUser_id());
        //nb.setMinValue(0);
        nb.setValue(1);

        return convertView;
    }

}
