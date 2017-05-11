package group3.myapplicationlab2;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupCreationForm extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = GroupCreationForm.class.getSimpleName();
    private ArrayList<String> membersList;
    private ArrayAdapter<String> membersAdapter;
    private EditText groupName, groupDescription, newParticipant, groupPin;
    private Button btnNewPart;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference user;


    private final DatabaseReference myRef = mDatabase.getReference("Groups");
    private final DatabaseReference myRefUsers = mDatabase.getReference("Users");

    private final List<String> members = new ArrayList<String>();
    private List<String> Mylist = new ArrayList<String>();
    private ListView lw;
    private final int REQUEST_INVITE = 0;

    private GoogleApiClient mGoogleApiClient;

    List<String> users = new ArrayList<String>();

    List<String> myList_notregistered = new ArrayList<String>();
    private String groupNameTmp = null;
    private String groupIdTmp = null;
    private String groupPinTmp = null;
    private String currentUser = null;

    private Group newGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation_form);

        // User input
        groupName = (EditText)findViewById(R.id.new_group);
        groupDescription = (EditText)findViewById(R.id.group_description);
        groupPin = (EditText) findViewById(R.id.group_pin);


        //newParticipant = (EditText)findViewById(R.id.new_participant);
        //btnNewPart = (Button)findViewById(R.id.new_part_btn);


/*        // Insert new participants
        lw = (ListView)findViewById(R.id.list_part);
        String[] participants = {};
        List<String> Mylist = new ArrayList<String>();
        membersList = new ArrayList<>(Arrays.asList(participants));
        membersAdapter = new ArrayAdapter<String>(this, R.layout.new_member_item, R.id.new_member, Mylist);
        lw.setAdapter(membersAdapter);*/

        // Create an auto-managed GoogleApiClient with access to App Invites.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, this)
                .build();

        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult result) {
                                Log.d(TAG, "getInvitation:onResult:" + result.getStatus());
                                if (result.getStatus().isSuccess()) {
                                    // Extract information from the intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    String invitationId = AppInviteReferral.getInvitationId(intent);

                                    // Because autoLaunchDeepLink = true we don't have to do anything
                                    // here, but we could set that to false and manually choose
                                    // an Activity to launch to handle the deep link here.
                                    // ...
                                }
                            }
                        });

        ValueEventListener UsersListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot groupsnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from database snapshot
                    User user = groupsnapshot.getValue(User.class);
                    users.add(user.getEmail());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d("Debug", "loadPost:onCancelled", databaseError.toException());
            }
        };
        myRefUsers.addValueEventListener(UsersListener);

    }/*[END onCreate]*/

    public void prepareUser(View view) {
        // Called when ADD button is pressed
        newGroup = new Group();
        boolean prova = true;
        if (groupName.getText().toString().isEmpty()){
            String err = getResources().getString(R.string.insert_group_name);
            groupName.setError(err);
            prova = false;
        }else {
            newGroup.setName(groupName.getText().toString());
        }

        if (groupDescription.getText().toString().isEmpty()){
            String err = getResources().getString(R.string.insert_group_descr);
            groupDescription.setError(err);
            prova = false;
        }else {
            newGroup.setDescription(groupDescription.getText().toString());
        }

        if (groupPin.getText().toString().isEmpty() || groupPin.getText().toString().length() < 6){
            String err = getResources().getString(R.string.wrong_group_pin);
            groupPin.setError(err);
            prova = false;
        }
        else {
            newGroup.setPin(groupPin.getText().toString());
        }

        //db insert of new group
        if (prova) {
            FirebaseAuth auth = FirebaseAuth.getInstance();

            List<String> l = new ArrayList<String>();
            List<String> lp = new ArrayList<String>();
            l.add(auth.getCurrentUser().getEmail());
            newGroup.setMembers(l);
            this.currentUser = auth.getCurrentUser().getEmail();

            String id = myRef.push().getKey();
            Log.d("PRIMO", id);
            //myRef.push().setValue(newGroup);
            myRef.child(id).setValue(newGroup);

            newGroup.setId(id);
            this.groupPinTmp = newGroup.getPin();
            this.groupNameTmp = newGroup.getName();
            this.groupIdTmp = newGroup.getId();
            //Toast.makeText(getApplicationContext(), R.string.group_created, Toast.LENGTH_SHORT).show();

        }

        if (this.groupNameTmp !=null && this.groupPinTmp != null && this.groupIdTmp != null )
            //Toast.makeText(getApplicationContext(), this.groupNameTmp, Toast.LENGTH_SHORT).show();
            onInviteClicked(this.groupNameTmp, this.groupPinTmp, this.groupIdTmp);
        else
            Toast.makeText(getApplicationContext(), R.string.no_group_creation_and_inviting, Toast.LENGTH_SHORT).show();
    }

    public void onInviteClicked(String groupName, String groupPin, String groupId) {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setEmailHtmlContent(getString(R.string.invitation_email, currentUser, groupIdTmp, groupPinTmp))
                .setEmailSubject(groupName+" "+ getString(R.string.email_subject))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);

                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }

                GroupPreview groupPreview = new GroupPreview();
                groupPreview.setName(newGroup.getName());
                groupPreview.setId(newGroup.getId());
                groupPreview.setDescription(newGroup.getDescription());
                groupPreview.setLastModify(System.currentTimeMillis());

                Intent i = new Intent();
                i.putExtra("new_groupPreview", groupPreview);
                setResult(RESULT_OK, i);
                finish();
            }else {
                // Sending failed or it was canceled, show failure message to the user
                // [START_EXCLUDE]
                Toast.makeText(getApplicationContext(), R.string.invitation_failed, Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        //showMessage(getString(R.string.google_play_services_error));
        Toast.makeText(getApplicationContext(), "failed conn", Toast.LENGTH_SHORT).show();
    }

    public void sendInvitation(View v){

       /* if (this.groupNameTmp !=null && this.groupPinTmp != null && this.groupIdTmp != null )
            //Toast.makeText(getApplicationContext(), this.groupNameTmp, Toast.LENGTH_SHORT).show();
            onInviteClicked(this.groupNameTmp, this.groupPinTmp, this.groupIdTmp);
        else
            Toast.makeText(getApplicationContext(), R.string.no_group_creation_and_inviting, Toast.LENGTH_SHORT).show();*/


/*        //members is a list of mail
        if (members.size()<1){
            String err = getResources().getString(R.string.no_user_insert);
            newParticipant.setError(err);
            prova = false;
        }else{
            newGroup.setMembers(members);
            //Toast.makeText(getApplicationContext(), "invitation here", Toast.LENGTH_SHORT).show();
        }*/

        //for(int i =0; i< members.size(); i++)
        //    Toast.makeText(getApplicationContext(), members.get(i), Toast.LENGTH_LONG).show();

       /* if (prova)

            //for ()
            myList_notregistered.clear();
            for (String i : members){

                if (!(users.contains(i))){
                    myList_notregistered.add(i);
                }
                else{
                    continue;
                }
            }
            Log.d("Deb", myList_notregistered.get(0));

            if (myList_notregistered.size()>0){


                ArrayList<String> arrayOfGEmail = new ArrayList<String>(myList_notregistered);
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayOfGEmail);

                new AlertDialog.Builder(this)
                        .setTitle("Do you want to invite these users?")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                onInviteClicked("Il signore", "PUTTANA LA MADONNA" );
                                adapter.clear();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                adapter.clear();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }*/



    }

}