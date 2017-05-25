package group3.myapplicationlab2;

import android.content.Context;
import android.media.Image;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.android.gms.common.api.BooleanResult;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;

/**
 * Created by mc on 21/05/17.
 */

class MembersAdapter extends ArrayAdapter<GroupMember>{

    public MembersAdapter(Context context, ArrayList<GroupMember> groupMembers) {
        super(context, 0, groupMembers);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        GroupMember groupMember = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_ei_item, parent, false);
        }
        TextView member = (TextView) convertView.findViewById(R.id.item_member);
        final TextView amount = (TextView) convertView.findViewById(R.id.item_amount);
        TextView user_id_tv = (TextView) convertView.findViewById(R.id.item_user_id);
        final EditText part = (EditText)convertView.findViewById(R.id.item_part);
        final EditText totalAmount = (EditText) convertView.findViewById(R.id.ie_et_amount);

        member.setText(groupMember.getName());
        amount.setText("0");
        user_id_tv.setText(groupMember.getUser_id());


        convertView.findViewById(R.id.item_up_part).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
                int parts = Integer.parseInt(part.getText().toString());
                parts = parts +1;
                part.setText(Integer.toString(parts));
                Log.d("Debug", Integer.toString(parts));
            }
        });

        convertView.findViewById(R.id.item_down_part).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
                int parts = Integer.parseInt(part.getText().toString());
                if (parts == 0){
                    part.setText(Integer.toString(0));
                }else{
                    parts = parts -1;
                    part.setText(Integer.toString(parts));
                    Log.d("Debug", Integer.toString(parts));
                }

            }
        });








        return convertView;
    }


}
