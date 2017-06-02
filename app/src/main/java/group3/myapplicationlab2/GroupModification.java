package group3.myapplicationlab2;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupModification extends AppCompatActivity {
    private String group_name;
    private String user_id;
    private String group_id;
    private int pos;
    private EditText et_name;
    private EditText et_pin;
    private HashMap<String, Object> hm = new HashMap<String, Object>();

    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_modification);

        user = (User)getIntent().getSerializableExtra("user");
        pos = Integer.parseInt(getIntent().getStringExtra("position"));
        group_id = user.getGroups().get(pos).getId();
        group_name = user.getGroups().get(pos).getName();
        user_id = user.getUid();


        setTitle(group_name + " - Modify");

        et_name = (EditText) findViewById(R.id.mod_new_group);
        et_pin = (EditText) findViewById(R.id.mod_group_pin);
        et_name.setText(group_name);

        Button bt = (Button) findViewById(R.id.mod_new_part_btn);
        bt.setText(R.string.bt_modify_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    public void modifyUser(View view) {
        /*name validity*/
        if(et_name.getText().toString().trim().length()>0){
            hm.put("name", et_name.getText().toString().trim());
        }
        else{
            String err = getResources().getString(R.string.miss_name_mod);
            et_name.setError(err);
        }

        /*pin validity*/
        if (et_pin.getText().toString().trim().length()<=5){
            String err2 = getResources().getString(R.string.miss_pin_mod);
            et_pin.setError(err2);
        }
        else{
            hm.put("pin", et_pin.getText().toString().trim());
        }

/*        Log.d("debug", "-----------");
        for (String k : hm.keySet()){
            Log.d("debug", k +" "+ hm.get(k).toString());
        }*/

/*        Snackbar.make(view, Boolean.toString(allok), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/

/*        if (hm.keySet().size()>0) {
                //aggiorno dati nel gruppo
                Long ts = System.currentTimeMillis();
                hm.put("lastModifyTimeStamp", (Object)ts);
                final DatabaseReference groups = FirebaseDatabase.getInstance().getReference("Groups")
                        .child(group_id);
                groups.updateChildren(hm);
                hm.remove("lastModifyTimeStamp");
                hm.put("lastModify",ts);


                //listener sui membri del gruppo, dove mi salvo le key di tutti i componenti
                final DatabaseReference group_members = FirebaseDatabase.getInstance()
                        .getReference("Groups")
                        .child(group_id)
                        .child("members2");
                Log.d("Debug", "aaa");

                group_members.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> objMember = (HashMap<String, Object>) dataSnapshot.getValue();

                        for (String key : objMember.keySet()){
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(key)
                                    .child("groupsHash")
                                    .child(group_id)
                                    .updateChildren(hm);
                            SystemClock.sleep(20);
                            Log.d("Debug", "usr - " + key);

                        }
                        Intent i = new Intent();
                        setResult(RESULT_OK, i);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        }*/
    }
}


