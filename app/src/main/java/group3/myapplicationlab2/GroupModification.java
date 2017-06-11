package group3.myapplicationlab2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
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

        Util util = new Util(getApplicationContext());

        View parentRoot = findViewById(R.id.group_modification_parent);
        View logo = findViewById(R.id.group_modification_logo);
        util.desappearViewOnSoftKeyboard(parentRoot, logo);

        user = (User)getIntent().getSerializableExtra("user");
        String posStr = getIntent().getStringExtra("position");

        if (posStr != null){
            pos = Integer.parseInt(posStr);
            group_id = user.getGroups().get(pos).getId();
            group_name = user.getGroups().get(pos).getName();
        }
        else{
            group_id = getIntent().getStringExtra(Constant.ACTIVITYGROUPID);
            group_name = getIntent().getStringExtra(Constant.ACTIVITYGROUPNAME);
        }
        user_id = user.getUid();


        setTitle(group_name + " - " + getResources().getString(R.string.modify));

        et_name = (EditText) findViewById(R.id.mod_new_group);
        //et_pin = (EditText) findViewById(R.id.mod_group_pin);
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

        if (et_name.getText().toString().trim().length()==0){
            et_name.setError(getResources().getString(R.string.miss_name_mod), null);
            et_name.requestFocus();
        }
        /*else if(et_pin.getText().toString().trim().length()<=5){
            et_pin.setError(getResources().getString(R.string.miss_pin_mod), null);
        }*/
        else{
            hm.put("name", et_name.getText().toString().trim());
            //hm.put("pin", et_pin.getText().toString().trim());
        }

        /*if(et_name.getText().toString().trim().length()>0){
            hm.put("name", et_name.getText().toString().trim());
        }
        else{
            String err = getResources().getString(R.string.miss_name_mod);
            et_name.setError(err, null);
        }

        if (et_pin.getText().toString().trim().length()<=5){
            String err2 = getResources().getString(R.string.miss_pin_mod);
            et_pin.setError(err2, null);
        }
        else{
            hm.put("pin", et_pin.getText().toString().trim());
        }*/


/*        Snackbar.make(view, Boolean.toString(allok), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/

        if (hm.keySet().size()>0) {

            Util util = new Util (getApplicationContext());
            /*try {
                //aggirono il QR
                String mex = "##" + group_id + "##||##" + hm.get("pin") + "##";
                Bitmap QR = util.encodeAsBitmap(mex);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                QR.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] data = baos.toByteArray();

                final StorageReference groupQRref = FirebaseStorage.getInstance().getReference("QR");
                UploadTask uploadTask = groupQRref.child(group_id).putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        hm.put("QRpath", downloadUrl.toString());
                        Log.d("URI", downloadUrl.toString());
                        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups");
                        groupRef.child(group_id).child("QRpath").setValue(downloadUrl.toString());
                    }

                });
            }catch (WriterException e) {
                e.printStackTrace();
            }*/

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
                        //Log.d("Debug", "usr - " + key);
                    }

                    Intent i = new Intent();
                    i.putExtra("newName", hm.get("name").toString());
                    //i.putExtra("map", hm);
/*                    Log.d("NAME", "mod " + (String)hm.get("name"));
                    i.putExtra(Constant.ACTIVITYGROUPPIN, (String)hm.get("pin"));
                    i.putExtra(Constant.ACTIVITYGROUPQRPATH, (String)hm.get("QRpath"));*/
                    setResult(RESULT_OK, i);
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}


