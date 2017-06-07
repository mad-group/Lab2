package group3.myapplicationlab2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.WriterException;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static final int GROUP_CLICKED = 10;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_ID = 234;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private User user;
    private DatabaseReference user_groups;
    private DatabaseReference user_info;
    private GroupPreviewAdapter adapter;
    private StorageReference user_image;


    private List<GroupPreview> currentGroupPreview;

    private final int CREATE_GROUP = 1;
    private static final int MODIFY_GROUP = 2;
    private static final int REQUEST_INVITE =0;
    private static final int JOIN_GROUP = 3;

    private String pin_str;
    private int pos;

    private File imageOutFile;
    private  ImageView personalPhoto;

    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApplication().registerActivityLifecycleCallbacks(new ApplicationLifecycleManager());
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        util = new Util (getApplicationContext());
        auth = FirebaseAuth.getInstance();
        // this listener will be called when there is change in firebase user session
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        auth.addAuthStateListener(authListener);
        setContentView(R.layout.activity_main);

        //OLD ADAPTER
        final ArrayList<GroupPreview> groupPreviews = new ArrayList<GroupPreview>();
        adapter = new GroupPreviewAdapter(this, groupPreviews);
        final ListView listView = (ListView) findViewById(R.id.group_list);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(MainActivity.this);
        final View header=navigationView.getHeaderView(0);
        personalPhoto = (ImageView) header.findViewById(R.id.imageViewCamera);
        personalPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePersonalImage();
            }
        });


        mDatabase = FirebaseDatabase.getInstance().getReference(Constant.REFERENCEUSERS);
        user_info = mDatabase.child(auth.getCurrentUser().getUid());
        user_groups = user_info.child(Constant.REFERENCEGROUPS);
        user_image = FirebaseStorage.getInstance().getReference("UsersImage");


        user_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                adapter.clear();
                user = dataSnapshot.getValue(User.class);
                if (user.getGroupsHash()!=null){
                    Collections.sort(user.getGroups(), Collections.<GroupPreview>reverseOrder());
                    adapter.clear();
                    for (int i=0; i<user.getGroups().size(); i++){
                        adapter.add(user.getGroups().get(i));
                    }
                }
                else {
                    findViewById(R.id.content_with_groups).setVisibility(View.GONE);
                    findViewById(R.id.content_without_groups).setVisibility(View.VISIBLE);
                }

                Bitmap image = util.downloadImage(user.getUserPathImage());
                if (image != null)
                    personalPhoto.setImageBitmap(util.getCroppedBitmap(image,200,200));
                final TextView userNameTv = (TextView) findViewById(R.id.username);
                userNameTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeUserName(user);
                    }
                });

                TextView user_email = (TextView)header.findViewById(R.id.user_email);
                user_email.setText(user.getEmail());
                TextView user_name = (TextView)header.findViewById(R.id.username);
                user_name.setText(user.getName());


                Intent serviceIntent = new Intent(MainActivity.this, GroupPreviewService.class);
                serviceIntent.putExtra(Constant.ACTIVITYUSER, user);
                startService(serviceIntent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("FAIL USER INFO");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(MainActivity.this, GroupActivityExpense.class);
                i.putExtra(Constant.ACTIVITYUSERID, user.getUid());
                i.putExtra(Constant.ACTIVITYGROUPNAME, user.getGroups().get(position).getName());
                i.putExtra(Constant.ACTIVITYPOSITION, Integer.toString(position));
                i.putExtra(Constant.ACTIVITYGROUPID, user.getGroups().get(position).getId());
                i.putExtra(Constant.ACTIVITYUSER, user);

                startActivityForResult(i, GROUP_CLICKED);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_group);
        fab.setImageResource(R.drawable.ic_new_group);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, GroupCreationForm.class);
                i.putExtra(Constant.ACTIVITYUSER, user);
                startActivityForResult(i, CREATE_GROUP);
            }
        });

    }

    private void changeUserName(User user) {
        final EditText editText = new EditText(MainActivity.this);
        editText.setText(user.getName());
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.scan_Modify_username));
        builder.setView(editText);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_modify, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.modify:
                Intent i = new Intent(MainActivity.this, GroupModification.class);

                i.putExtra(Constant.ACTIVITYUSER,user);
                i.putExtra(Constant.ACTIVITYPOSITION, Integer.toString(info.position));
                startActivityForResult(i, MODIFY_GROUP);
                return true;

            case R.id.add_members:
                DatabaseReference pin = FirebaseDatabase.getInstance().getReference(Constant.REFERENCEGROUPS)
                        .child(user.getGroups().get(info.position).getId())
                        .child(Constant.REFERENCEGROUPSPIN);
                pos = info.position;
                pin.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange (DataSnapshot dataSnapshot){
                        final String groupPin  = dataSnapshot.child("pin").getValue(String.class);
                        final String groupId = dataSnapshot.child("id").getValue(String.class);
                        final String QRpath = dataSnapshot.child("QRpath").getValue(String.class);
                        final String groupName = dataSnapshot.child("name").getValue(String.class);

                        try {
                            String mex = "##"+groupId+"##||##"+groupPin +"##";
                            Bitmap QR = util.encodeAsBitmap(mex);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            QR.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            final byte[] data = baos.toByteArray();
                            Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                                    .setMessage(getString(R.string.invitation_message))
                                    .setEmailHtmlContent(getString(R.string.invitation_email, user.getName(),
                                            groupId, groupPin, QRpath))
                                    .setEmailSubject(groupName+" "+ getString(R.string.email_subject))
                                    .build();
                            startActivityForResult(intent, REQUEST_INVITE);


                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onCancelled (DatabaseError databaseError){
                        System.out.println("FAIL PIN INFO");
                    }
                });

                return true;

            case R.id.generate_QR:
                DatabaseReference group = FirebaseDatabase.getInstance().getReference("Groups")
                        .child(user.getGroups().get(info.position).getId());
                pos = info.position;
                group.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange (DataSnapshot dataSnapshot){
                        final String QRpath = dataSnapshot.child("QRpath").getValue(String.class);
                        final String groupName = dataSnapshot.child("name").getValue(String.class);
                        Bitmap QR = util.downloadImage(QRpath);
                        drawQRDialogBox(QR,groupName);
                    }

                    @Override
                    public void onCancelled (DatabaseError databaseError){
                        System.out.println("FAIL PIN INFO");
                    }
                });
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_GROUP) {
            if(resultCode == RESULT_OK) {

                findViewById(R.id.content_with_groups).setVisibility(View.VISIBLE);
                findViewById(R.id.content_without_groups).setVisibility(View.GONE);

                GroupPreview groupPreview = (GroupPreview) data.getSerializableExtra("new_groupPreview");
                adapter.insert(groupPreview,0);

                if (user.getGroups() != null){
                    currentGroupPreview = user.getGroups();
                }
                else{
                    currentGroupPreview = new ArrayList<GroupPreview>();
                }

                currentGroupPreview.add(groupPreview);
                Collections.sort(currentGroupPreview, Collections.<GroupPreview>reverseOrder());
                user.setGroups(currentGroupPreview);

            }
        }
        else if (requestCode == JOIN_GROUP){
            if (resultCode == RESULT_OK) {

                findViewById(R.id.content_with_groups).setVisibility(View.VISIBLE);
                findViewById(R.id.content_without_groups).setVisibility(View.GONE);

                GroupPreview groupPreview = (GroupPreview) data.getSerializableExtra("new_groupPreview");
                adapter.insert(groupPreview,0);

                if (user.getGroups() != null){
                    currentGroupPreview = user.getGroups();
                }
                else{
                    currentGroupPreview = new ArrayList<GroupPreview>();
                }

                currentGroupPreview.add(groupPreview);
                Collections.sort(currentGroupPreview, Collections.<GroupPreview>reverseOrder());
                user.setGroups(currentGroupPreview);

            }
        }
        else if (requestCode == GROUP_CLICKED){
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "aaaaa", Toast. LENGTH_SHORT).show();
                user = (User)data.getSerializableExtra(Constant.ACTIVITYUSERMODIFIED);
                reDrawGroupList();
            }

        }
        else if (requestCode == MODIFY_GROUP){
            if (resultCode == RESULT_OK){
                reloadUser();
            }
        }
        else if (requestCode == PICK_IMAGE_ID){
            try {
                /*After to take a new photo, set it as personal image*/
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                final File mediaFile = util.fileProfileImageCreator(user.getUid());
                FileOutputStream out = new FileOutputStream(mediaFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                personalPhoto.setImageBitmap(util.getCroppedBitmap(bitmap,200,200));

                /*saving photo for for uploading*/
                byte[] byteArrayImage = util.bitmap2byte(bitmap);

                UploadTask uploadTask = user_image.child(user.getUid()).putBytes(byteArrayImage);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.d("URI", downloadUrl.toString());

                        user.setUserPathImage(downloadUrl.toString());
                        String ts = Long.toString(System.currentTimeMillis());
                        user.setCurrentPicsUpload(ts);
                        FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("currentPicsUpload").setValue(ts);
                        user_info.child("userPathImage").setValue(downloadUrl.toString());
                        /*user_image_link.child(user.getUid()).setValue(downloadUrl);*/

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            //this.imageOutFile = mediaFile;
        }
        else {
            if (user.getGroups() != null) {
                currentGroupPreview = user.getGroups();
                for (int j = 0; j < user.getGroups().size(); j++)
                    Collections.sort(currentGroupPreview, Collections.<GroupPreview>reverseOrder());
                adapter.clear();
                for (int i = 0; i<currentGroupPreview.size(); i++) {
                    //Log.d("debug", "after" + user.getGroups().get(i).getName());
                    adapter.add(user.getGroups().get(i));
                }
                user.setGroups(currentGroupPreview);
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.join_group) {

            Intent i = new Intent(MainActivity.this, JoinGroupActivity.class);
            i.putExtra(Constant.ACTIVITYUSER, user);
            startActivityForResult(i, JOIN_GROUP);

        } else if (id == R.id.logout) {
            auth.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void reDrawGroupList(){
        adapter.clear();

        if (user.getGroups().size()>0){
            findViewById(R.id.content_with_groups).setVisibility(View.VISIBLE);
            findViewById(R.id.content_without_groups).setVisibility(View.GONE);
        }
        else{
            findViewById(R.id.content_with_groups).setVisibility(View.GONE);
            findViewById(R.id.content_without_groups).setVisibility(View.VISIBLE);
        }

        for(int i=0; i< user.getGroups().size(); i++){
            adapter.add(user.getGroups().get(i));
        }
        user_groups.setValue(user.getGroups());

    }

    private void reloadUser(){
        user_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                adapter.clear();
                user = dataSnapshot.getValue(User.class);
                if (user.getGroupsHash()!=null){
                    Collections.sort(user.getGroups(), Collections.<GroupPreview>reverseOrder());
                    adapter.clear();
                    for (int i=0; i<user.getGroups().size(); i++){
                        adapter.add(user.getGroups().get(i));
                    }
                }
                else {
                    findViewById(R.id.content_with_groups).setVisibility(View.GONE);
                    findViewById(R.id.content_without_groups).setVisibility(View.VISIBLE);
                }

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(MainActivity.this);
                View header=navigationView.getHeaderView(0);
                TextView user_email = (TextView)header.findViewById(R.id.user_email);
                user_email.setText(user.getEmail());
                TextView user_name = (TextView)header.findViewById(R.id.username);
                user_name.setText(user.getName());

                Intent serviceIntent = new Intent(MainActivity.this, GroupPreviewService.class);
                serviceIntent.putExtra(Constant.ACTIVITYUSER, user);
                startService(serviceIntent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("FAIL USER INFO");
            }
        });
    }

    public void takePersonalImage() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
            startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);


        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
                    startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, R.string.camera_permission_denied, Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void drawQRDialogBox(Bitmap bitmap, String groupName){
        ImageView imageView = new ImageView(MainActivity.this);
        imageView.setImageBitmap(bitmap);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(groupName + " - " +  getString(R.string.scan_QR_title));
        builder.setView(imageView);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

    }
}