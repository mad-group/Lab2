package group3.myapplicationlab2;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class GroupStats extends AppCompatActivity {

    private Group group;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_stats);


        final Random r = new Random();

        group = (Group) getIntent().getSerializableExtra(Constant.ACTIVITYGROUP);
        user = (User) getIntent().getSerializableExtra(Constant.ACTIVITYUSER);

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

        final HashMap<String, Bitmap> images = new HashMap<String, Bitmap>();

        final File cacheDir = getBaseContext().getCacheDir();
        for (int i=0; i<group.getGroupMembers().size(); i++){
            final String key = group.getGroupMembers().get(i).getUser_id();
            File f = new File(cacheDir, key);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                images.put(key, bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
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
                //ImageView mood =(ImageView) convertView.findViewById(R.id.balance_face);

                ImageView expViewLeft = (ImageView) convertView.findViewById(R.id.imageViewLeft);

                if (images.get(getItem(position).getUser_id()) != null){

                    Bitmap image = images.get(getItem(position).getUser_id());
                    expViewLeft.setImageBitmap(image);

                }
                else
                {
                    expViewLeft.setImageResource(R.mipmap.ic_standard_user);
                }


                Double debit = getItem(position).getPayment();

                if (debit > 0){
                    debit_credit.setText("+" + String.format( "%.2f €", debit ));
                    debit_credit.setTextColor(getResources().getColor(R.color.mood_fine));
                    debit_credit_text.setText(R.string.owes_you);
                }
                else if (debit <0) {
                    debit_credit.setText(String.format( "%.2f €", debit ));
                    debit_credit.setTextColor(getResources().getColor(R.color.mood_sad));
                    debit_credit_text.setText(R.string.you_owe);

                }
                else {
                    debit_credit.setText(String.format( "%.2f €", debit ));
                    debit_credit.setTextColor(getResources().getColor(R.color.black));
                    debit_credit_text.setText(R.string.were_even);

                }
                return convertView;
            }
        };

        final ListView list;
        list = (ListView)findViewById(R.id.group_balance);
        list.setAdapter(memberArrayAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_stats);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra(Constant.ACTIVITYGROUPNAME));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        toolbar.setTitle(group.getName()+" - " + getResources().getString(R.string.statistics));
    }

}
