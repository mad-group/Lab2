package group3.myapplicationlab2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private DatabaseReference user_info;


    private EditText groupID, groupPassword;
    private Button Join;

    private ProgressBar progressBar;

    private List<GroupPreview> currentGroupPreview;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                            //Getting the data from database snapshot

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

                                    mDatabase.child("members2").child(auth.getCurrentUser().getUid()).setValue(groupMember);

                                    if (user.getGroups() != null){
                                        currentGroupPreview = user.getGroups();
                                    }
                                    else{
                                        currentGroupPreview = new ArrayList<GroupPreview>();
                                    }

                                    GroupPreview groupPreview = new GroupPreview();
                                    groupPreview.setName(group.getName());
                                    groupPreview.setDescription(group.getDescription());
                                    groupPreview.setId(groupID.getText().toString());
                                    groupPreview.setLastAuthor(group.getLastAuthor());
                                    groupPreview.setLastEvent(group.getLastEvent());
                                    groupPreview.setLastModify(group.getLastModifyTimeStamp());

                                    currentGroupPreview.add(groupPreview);
                                    user_info.child("groups").setValue(currentGroupPreview);

                                    user.setGroups(currentGroupPreview);
                                }

                                finish();
                            }


                            /*if (groupPassword.getText().toString().equals(group.getPin())){
                                List<String> members = group.getMembers();
                                auth = FirebaseAuth.getInstance();

                                if (!members.contains(auth.getCurrentUser().getEmail())){
                                    members.add(auth.getCurrentUser().getEmail());
                                    //Log.d("Debug", members.get(1));
                                    mDatabase.child("members").setValue(members);

                                    if (user.getGroups() != null){
                                        currentGroupPreview = user.getGroups();
                                    }
                                    else{
                                        currentGroupPreview = new ArrayList<GroupPreview>();
                                    }

                                    GroupPreview groupPreview = new GroupPreview();
                                    groupPreview.setName(group.getName());
                                    groupPreview.setDescription(group.getDescription());
                                    groupPreview.setId(groupID.getText().toString());

                                    currentGroupPreview.add(groupPreview);
                                    user_info.child("groups").setValue(currentGroupPreview);

                                    user.setGroups(currentGroupPreview);

                                }


                                finish();
                            }*/
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

}
