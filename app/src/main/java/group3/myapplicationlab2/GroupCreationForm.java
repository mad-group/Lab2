package group3.myapplicationlab2;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupCreationForm extends AppCompatActivity {

    private ArrayList<String> membersList;
    private ArrayAdapter<String> membersAdapter;
    private EditText groupName, groupDescription, newParticipant;
    private List<String> members;
    private FirebaseDatabase mDatabase;
    private Button btnNewPart;
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
        btnNewPart = (Button)findViewById(R.id.new_part_btn);
        final List<String> members = new ArrayList<String>();

        // Insert new participants
        ListView lw = (ListView)findViewById(R.id.list_part);
        String[] participants = {};
        membersList = new ArrayList<>(Arrays.asList(participants));
        membersAdapter = new ArrayAdapter<String>(this, R.layout.new_member_item, R.id.new_member, participants);
        lw.setAdapter(membersAdapter);
        btnNewPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newP = newParticipant.getText().toString();
                members.add(newP);
                /*membersList.add(newP);
                membersAdapter.notifyDataSetChanged();*/
                Log.d("Debug", newP);
            }
        });

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.new_group_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //List<String> members = new ArrayList<>();
                //members.add(newParticipant.getText().toString());

                final Group newGroup = new Group();
                newGroup.setDescription(groupDescription.getText().toString());
                newGroup.setName(groupName.getText().toString());
                newGroup.setMembers(members);
                myRef.push().setValue(newGroup);
            }
        });
    }
}
