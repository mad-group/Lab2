package group3.myapplicationlab2;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class GroupCreationForm extends AppCompatActivity {

    private EditText groupName, groupDescription, newParticipant;
    private FirebaseDatabase mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation_form);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = mDatabase.getReference("Groups");
        // User input
        groupName = (EditText)findViewById(R.id.new_group);
        groupDescription = (EditText)findViewById(R.id.group_description);
        newParticipant = (EditText)findViewById(R.id.new_participant);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.new_group_btn);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> members = new ArrayList<>();
                members.add(newParticipant.getText().toString());

                final Group newGroup = new Group();
                newGroup.setDescription(groupDescription.getText().toString());
                newGroup.setName(groupName.getText().toString());
                newGroup.setMembers(members);
                myRef.push().setValue(newGroup);
            }
        });
    }
}
