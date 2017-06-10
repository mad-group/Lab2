package group3.myapplicationlab2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.util.Random;

public class GroupCreationForm extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = GroupCreationForm.class.getSimpleName();
    private EditText groupName, groupPin;

    private final int REQUEST_INVITE = 0;

    private GoogleApiClient mGoogleApiClient;

    private String groupNameTmp = null;
    private String groupIdTmp = null;
    private String groupPinTmp = null;
    private String currentUser = null;

    private Group newGroup;
    private User user;

    private DataBaseProxy dataBaseProxy;
    private DatabaseReference groupRef;

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_creation_form);

        Util util = new Util(getApplicationContext());


        View parentRoot = findViewById(R.id.group_creation_parent);
        View logo = findViewById(R.id.icon_group_creation);
        util.desappearViewOnSoftKeyboard(parentRoot, logo);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        groupRef = FirebaseDatabase.getInstance().getReference("Groups");
        user = (User)getIntent().getSerializableExtra("user");
        dataBaseProxy = new DataBaseProxy();

        // User input
        groupName = (EditText)findViewById(R.id.new_group);
        groupPin = (EditText) findViewById(R.id.group_pin);


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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.new_part_btn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                prepareUser(view);
            }
        });

    }/*[END onCreate]*/

    public void prepareUser(View view) {
        // Called when ADD button is pressed
        newGroup = new Group();
        boolean prova = true;

        if (groupName.getText().toString().isEmpty()){
            groupName.setError(getResources().getString(R.string.insert_group_name), null);
            groupName.requestFocus();
            prova = false;

            progressBar.setVisibility(View.GONE);
        }
        else if (groupPin.getText().toString().isEmpty() || groupPin.getText().toString().length() < 6){
            groupPin.setError(getResources().getString(R.string.wrong_group_pin), null);
            groupPin.requestFocus();
            prova=false;

            progressBar.setVisibility(View.GONE);
        }
        else{
            newGroup.setName(groupName.getText().toString());
            newGroup.setPin(groupPin.getText().toString());
            prova = true;
        }


        /*if (groupName.getText().toString().isEmpty()){
            String err = getResources().getString(R.string.insert_group_name);
            groupName.setError(err);
            prova = false;
        }else {
            newGroup.setName(groupName.getText().toString());
        }

        if (groupPin.getText().toString().isEmpty() || groupPin.getText().toString().length() < 6){
            String err = getResources().getString(R.string.wrong_group_pin);
            groupPin.setError(err);
            prova = false;
        }
        else {
            newGroup.setPin(groupPin.getText().toString());
        }*/

        //db insert of new group
        if (prova) {

            FirebaseAuth auth = FirebaseAuth.getInstance();

            this.currentUser = auth.getCurrentUser().getEmail();

            //INSERT GROUP IN DB
            newGroup.setDescription("DefaultDescription");
            newGroup.setLastEvent("GroupCreation");
            newGroup.setLastAuthor(user.getUid());
            newGroup.setNumeric_id(new Random().nextInt(1000007));
            String groupId = dataBaseProxy.insertGroup(newGroup);

            //INSERT MEMBER IN GROUP CREATED BEFORE
            GroupMember groupMember = new GroupMember();
            groupMember.GroupMemberConstructor(user.getName(), user.getEmail(), user.getUid());
            dataBaseProxy.insertMemberInGroup(groupId, user.getUid(), groupMember);

            newGroup.setId(groupId);
            this.groupPinTmp = newGroup.getPin();
            this.groupNameTmp = newGroup.getName();
            this.groupIdTmp = newGroup.getId();
            //Toast.makeText(getApplicationContext(), R.string.group_created, Toast.LENGTH_SHORT).show();

            if (this.groupNameTmp !=null && this.groupPinTmp != null && this.groupIdTmp != null ){
                //Toast.makeText(getApplicationContext(), this.groupNameTmp, Toast.LENGTH_SHORT).show();
                onInviteClicked(this.groupNameTmp, this.groupPinTmp, this.groupIdTmp);
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.error_group_creation, Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void onInviteClicked(final String groupName, String groupPin, final String groupId) {
        final Util util = new Util(getApplicationContext());
        try {
            String mex = "##"+groupId+"##||##"+groupPin +"##";
            Bitmap QR = util.encodeAsBitmap(mex);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            QR.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            final byte[] data = baos.toByteArray();

            final StorageReference groupQRref = FirebaseStorage.getInstance().getReference("QR");
            UploadTask uploadTask = groupQRref.child(groupId).putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d("URI", downloadUrl.toString());

                    groupRef.child(groupId).child("QRpath").setValue(downloadUrl.toString());
                    newGroup.setQRpath(downloadUrl.toString());
                    Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                            .setMessage(getString(R.string.invitation_message))
                            .setEmailHtmlContent(getString(R.string.invitation_email, currentUser, groupIdTmp, groupPinTmp, downloadUrl.toString()))
                            .setEmailSubject(groupName+" "+ getString(R.string.email_subject))
                            .build();
                    startActivityForResult(intent, REQUEST_INVITE);
                }

            });

        } catch (WriterException e) {
            e.printStackTrace();
        }
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
                groupPreview.GroupPreviewConstructor(
                        newGroup.getName(),
                        newGroup.getId(),
                        newGroup.getDescription(),
                        System.currentTimeMillis(),
                        "GroupCreation",
                        user.getUid()
                );

                dataBaseProxy.insertGroupPreview(groupPreview, user.getUid(), newGroup.getId());

                Intent i = new Intent();
                i.putExtra("groupPreviewKey", newGroup.getId());
                i.putExtra("new_groupPreview", groupPreview);
                setResult(RESULT_OK, i);
                finish();
            }else {
                Toast.makeText(getApplicationContext(), R.string.invitation_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        //showMessage(getString(R.string.google_play_services_error));
        Toast.makeText(getApplicationContext(), "failed conn", Toast.LENGTH_SHORT).show();
    }

}