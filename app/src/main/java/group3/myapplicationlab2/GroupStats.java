package group3.myapplicationlab2;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GroupStats extends AppCompatActivity {

    private Group group;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_stats);
        final Random r = new Random();

        group = (Group) getIntent().getSerializableExtra("group");
        user = (User) getIntent().getSerializableExtra("user");

        ArrayList<GroupMember> members = new ArrayList<GroupMember>(group.getGroupMembers());

        String property = user.getEmail();
        for(int j = 0; j < members.size(); j++)
        {
            GroupMember obj = members.get(j);
            if(obj.getEmail().equals(property)){
                //found, delete.
                members.remove(j);
                break;
            }
        }

        ArrayAdapter<GroupMember> memberArrayAdapter = new ArrayAdapter<GroupMember>(this, R.layout.group_balance_item, members){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // There is nothing to convert --> I need to create extra view
                if (convertView==null) {
                    // Returns a view: inflate is used to populate a view
                    convertView = getLayoutInflater().inflate(R.layout.group_balance_item, parent, false);
                }

                TextView name = (TextView)convertView.findViewById(R.id.group_member_name);
                name.setText(getItem(position).getName());

                TextView debit_credit = (TextView)convertView.findViewById(R.id.expense_amount);
                TextView debit_credit_text = (TextView)convertView.findViewById(R.id.debt_text);
                ImageView mood =(ImageView) convertView.findViewById(R.id.imageView2);

                Double debit = getItem(position).getPayment();

                if (debit > 0){
                    debit_credit.setText("+" + String.format( "%.2f €", debit ));
                    debit_credit.setTextColor(Color.parseColor("#51931b"));
                    debit_credit_text.setText(R.string.owes_you);
                    mood.setBackgroundResource(R.mipmap.ic_smile);
                    mood.setColorFilter(Color.parseColor("#51931b"));
                }
                else if (debit <0) {
                    debit_credit.setText(String.format( "%.2f €", debit ));
                    debit_credit.setTextColor(Color.parseColor("#ff0000"));
                    debit_credit_text.setText(R.string.you_owe);
                    mood.setBackgroundResource(R.mipmap.ic_sad);
                    mood.setColorFilter(Color.parseColor("#ff0000"));

                }
                else {
                    debit_credit.setText(String.format( "%.2f €", debit ));
                    debit_credit.setTextColor(Color.parseColor("#000000"));
                    debit_credit_text.setText(R.string.were_even);
                    mood.setBackgroundResource(R.mipmap.ic_smile);
                    mood.setColorFilter(Color.parseColor("#000000"));

                }
                return convertView;
            }
        };

        final ListView list;
        list = (ListView)findViewById(R.id.group_balance);
        list.setAdapter(memberArrayAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void drawLeavingDialogBox(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text).setTitle(title);
       /* builder.setPositiveButton(getString(R.string.ok));*/
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
