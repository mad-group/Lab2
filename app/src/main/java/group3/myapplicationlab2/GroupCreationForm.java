package group3.myapplicationlab2;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupCreationForm extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = GroupCreationForm.class.getSimpleName();
    private ArrayList<String> membersList;
    private ArrayAdapter<String> membersAdapter;
    private EditText groupName, groupDescription, newParticipant;
    private Button btnNewPart;
    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private final DatabaseReference myRef = mDatabase.getReference("Groups");
    private final List<String> members = new ArrayList<String>();
    private List<String> Mylist = new ArrayList<String>();
    private ListView lw;
    private final int REQUEST_INVITE = 0;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation_form);

        // User input
        groupName = (EditText)findViewById(R.id.new_group);
        groupDescription = (EditText)findViewById(R.id.group_description);
        newParticipant = (EditText)findViewById(R.id.new_participant);
        btnNewPart = (Button)findViewById(R.id.new_part_btn);

        // Insert new participants
        lw = (ListView)findViewById(R.id.list_part);
        //String[] participants = {};
        //List<String> Mylist = new ArrayList<String>();
        //membersList = new ArrayList<>(Arrays.asList(participants));
        membersAdapter = new ArrayAdapter<String>(this, R.layout.new_member_item, R.id.new_member, Mylist);
        lw.setAdapter(membersAdapter);

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
    }/*[END onCreate]*/

    public void prepareUser(View view) {
        String newP = newParticipant.getText().toString();
        if (!newP.matches("[a-zA-Z0-9._-]+@[a-z]+.[a-z]+") || newP.isEmpty()) {
            String err = getResources().getString(R.string.invalid_mail_address);
            newParticipant.setError(err);
        }
        else {
            members.add(newP);
            membersAdapter.add(newP);
            newParticipant.setText("");
        }
    }


    private void onInviteClicked(String groupName, String token) {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setEmailHtmlContent("<html>You are invited to join to a Money Tracker <b>"+groupName+"</b> group.</br>"+
                        "Please log or sing in (with this email) to fully operate with your friends!</br>"+
                        "Use this to join token: <b>" + token +"</b>.</br></br>"+
                         "MT crew</html>")
                .setEmailSubject(groupName + " MoneyTracker group Invitation")
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
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // [START_EXCLUDE]
                Toast.makeText(getApplicationContext(), "failed send", Toast.LENGTH_SHORT).show();
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

    /*
    at fab pressing, doing this cotnrols and if all right creates the groups
    * */
    public void sendInvitation(View v){
        final Group newGroup = new Group();
        if (groupName.getText().toString().isEmpty()){
            String err = getResources().getString(R.string.insert_group_name);
            groupName.setError(err);
        }else {
            newGroup.setName(groupName.getText().toString());
        }

        if (groupDescription.getText().toString().isEmpty()){
            String err = getResources().getString(R.string.insert_group_descr);
            groupName.setError(err);
        }else {
            newGroup.setDescription(groupDescription.getText().toString());
        }

        //members is a list of mail
        if (members.size()<1){
            String err = getResources().getString(R.string.no_user_insert);
            groupName.setError(err);
        }else{
            newGroup.setMembers(members);
            //Toast.makeText(getApplicationContext(), "invitation here", Toast.LENGTH_SHORT).show();
        }
        for(int i =0; i< members.size(); i++)
            Toast.makeText(getApplicationContext(), members.get(i), Toast.LENGTH_SHORT).show();

        //db isnert of new group
        //myRef.push().setValue(newGroup);
        onInviteClicked("PORCO DIO", "PUTTANA LA MADONNA" );
    }




}
