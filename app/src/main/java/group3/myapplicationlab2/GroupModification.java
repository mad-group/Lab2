package group3.myapplicationlab2;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.Objects;

public class GroupModification extends AppCompatActivity {
    private String group_name;
    private String group_desc;
    private String user_id;
    private String group_id;
    private EditText et_name;
    private EditText et_desc;
    private EditText et_pin;
    private HashMap<String, Object> hm = new HashMap<String, Object>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_modification);


        group_desc = getIntent().getStringExtra("group_desc");
        group_id = getIntent().getStringExtra("group_id");
        group_name = getIntent().getStringExtra("group_name");
        user_id = getIntent().getStringExtra("user_id");

        setTitle(group_name + " - Modify");

        et_name = (EditText) findViewById(R.id.mod_new_group);
        et_desc = (EditText) findViewById(R.id.mod_group_description);
        et_pin = (EditText) findViewById(R.id.mod_group_pin);

        et_name.setText(group_name);
        et_desc.setText(group_desc);

        Button bt = (Button) findViewById(R.id.mod_new_part_btn);
        bt.setText(R.string.bt_modify_group);

    }

    public void modifyUser(View view) {
        boolean allok = false;
        /*name validity*/
        if (et_name.getText().toString().trim().isEmpty()){
            allok = true;
        }
        else if(et_name.getText().toString().trim().length()>0){
            hm.put("name", et_name.getText().toString().trim());
            allok = true;
        }
        else{
            allok =false;
        }

        /*description validity*/
        if (et_desc.getText().toString().trim().isEmpty()){
            allok = true;
        }
        else if(et_desc.getText().toString().trim().length()>0){
            hm.put("description", et_desc.getText().toString().trim());
            allok = true;
        }
        else{
            allok =false;
        }

        /*pin validity*/
        if(et_pin.getText().toString().isEmpty()){
            allok = true;
        }
        else if (et_pin.getText().toString().trim().length() >0 || et_pin.getText().toString().trim().length()<6){
            Snackbar.make(view, "The pin must have at least 6 characters", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            allok = false;
        }
        else{
            hm.put("pin", et_pin.getText().toString().trim());
            allok = true;
        }

/*        Log.d("debug", "-----------");
        for (String k : hm.keySet()){
            Log.d("debug", k +" "+ hm.get(k).toString());
        }*/

        Snackbar.make(view, Boolean.toString(allok), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();

        if (hm.keySet().size()>0) {
            Long ts = System.currentTimeMillis();
            hm.put("lastModifyTimeStamp", (Object)ts);
            final DatabaseReference groups = FirebaseDatabase.getInstance().getReference("Groups")
                    .child(group_id);
            groups.updateChildren(hm);
            hm.remove("lastModifyTimeStamp");
            hm.put("lastModify",ts);
            final DatabaseReference user_groups = FirebaseDatabase.getInstance().getReference("Users")
                    .child(user_id);
            user_groups.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User u = dataSnapshot.getValue(User.class);
                    if (u.getGroups()!=null){
                        List<GroupPreview> l = u.getGroups();
                        for(int i=0; i< l.size(); i++){
                            Log.d("Debug", "get ->" + l.get(i).getId());
                            if (l.get(i).getId().equals(group_id)){
                                Log.d("Debug", "aaa" +l.get(i).getName());
                                user_groups.child("groups").child(Integer.toString(i)).updateChildren(hm);
                                Toast.makeText(getApplicationContext(), "Group Modified", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
}

