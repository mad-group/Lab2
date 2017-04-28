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

import java.util.List;

public class JoinGroupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private EditText groupID, groupPassword;
    private Button Join;

    private ProgressBar progressBar;

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
                            Group group = dataSnapshot.getValue(Group.class);

                            if (groupPassword.getText().toString().equals(group.getPin())){
                                List<String> members = group.getMembers();
                                auth = FirebaseAuth.getInstance();

                                if (!members.contains(auth.getCurrentUser().getEmail())){
                                    members.add(auth.getCurrentUser().getEmail());
                                    //Log.d("Debug", members.get(1));
                                    mDatabase.child("members").setValue(members);
                                }
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

}
