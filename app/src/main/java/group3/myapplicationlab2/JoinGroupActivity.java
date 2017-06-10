package group3.myapplicationlab2;

import android.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinGroupActivity extends AppCompatActivity {

    private static final int READ_QR = 235;
    private static final int ZXING_CAMERA_PERMISSION = 1;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private DatabaseReference user_info;


    private EditText groupID, groupPassword;
    private Button Join;

    private ProgressBar progressBar;

    private List<GroupPreview> currentGroupPreview;

    private User user;

    private DataBaseProxy dataBaseProxy;

    private GroupPreview groupPreviewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
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

        Button readQR = (Button)findViewById(R.id.readQR);
        readQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readFromQR(view);
            }
        });

        dataBaseProxy = new DataBaseProxy();

        groupID = (EditText)findViewById(R.id.Group_Id);
        groupPassword = (EditText)findViewById(R.id.Group_password);
        Join = (Button)findViewById(R.id.btn_join);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        user = (User) getIntent().getSerializableExtra("user");
        user_info = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        Join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                mDatabase = FirebaseDatabase.getInstance().getReference("Groups").child(groupID.getText().toString());

                ValueEventListener GroupListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            Map<String, Object> objectMap = (HashMap<String, Object>)
                                    dataSnapshot.getValue();

                            Group group = new Group();
                            group.GroupConstructor(objectMap);

                            if (groupPassword.getText().toString().equals(group.getPin())){
                                auth = FirebaseAuth.getInstance();

                                if (!dataSnapshot.hasChild(auth.getCurrentUser().getUid())){

                                    GroupMember groupMember = new GroupMember();
                                    groupMember.setName(user.getName());
                                    groupMember.setEmail(user.getEmail());
                                    groupMember.setUser_id(user.getUid());

                                    mDatabase.child("members2").child(user.getUid()).setValue(groupMember);

                                    groupPreviewResult = new GroupPreview();
                                    groupPreviewResult.GroupPreviewConstructor(
                                            group.getName(),
                                            groupID.getText().toString(),
                                            group.getDescription(),
                                            System.currentTimeMillis(),
                                            "GroupJoin",
                                            user.getUid()
                                    );


                                    dataBaseProxy.insertGroupPreview(groupPreviewResult, user.getUid(), groupID.getText().toString());
                                }

                                Intent i = new Intent();
                                i.putExtra("new_groupPreview", groupPreviewResult);
                                setResult(RESULT_OK, i);
                                finish();

                            }

                            else{
                                Toast.makeText(JoinGroupActivity.this, "Incorrect GroupID or Password", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                        else{
                            Toast.makeText(JoinGroupActivity.this, "Incorrect GroupID or Password", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Getting Post failed, log a message
                        Log.d("Debug", "loadPost:onCancelled", databaseError.toException());
                    }
                };

                mDatabase.addListenerForSingleValueEvent(GroupListener);

            }
        });

    }

    private void readFromQR(View view){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA}, ZXING_CAMERA_PERMISSION);
        } else {
            Intent intent = new Intent(this, SimpleScannerActivity.class);
            startActivityForResult(intent,READ_QR);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_QR) {

            if (resultCode== RESULT_OK) {

                groupID.setText( data.getStringExtra("gid"));
                groupPassword.setText(data.getStringExtra("pin"));
                Join.performClick();

            }
            if(resultCode == RESULT_CANCELED){
                //handle cancel
            }
        }
    }

}
