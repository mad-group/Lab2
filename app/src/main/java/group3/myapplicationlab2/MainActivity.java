package group3.myapplicationlab2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    private List<GroupPreview> currentGroupPreview;

    private final int CREATE_GROUP = 1;
    private static final int MODIFY_GROUP = 2;
    private static final int REQUEST_INVITE =0;
    private static final int JOIN_GROUP = 3;

    private String pin_str;
    private int pos;

    private File imageOutFile;
    private  ImageView personalPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getApplication().registerActivityLifecycleCallbacks(new ApplicationLifecycleManager());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

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

        mDatabase = FirebaseDatabase.getInstance().getReference(Constant.REFERENCEUSERS);
        user_info = mDatabase.child(auth.getCurrentUser().getUid());
        user_groups = user_info.child(Constant.REFERENCEGROUPS);

        user_info.addValueEventListener(new ValueEventListener() {
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

                personalPhoto = (ImageView) header.findViewById(R.id.imageViewCamera);
                personalPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        takePersonalImage();
                    }
                });
                if (!user.getUserImage().isEmpty()){
                    String encodedImage = user.getUserImage();
                    byte[] decodedImage = Base64.decode(encodedImage, Base64.DEFAULT);
                    Bitmap image = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
                    personalPhoto.setImageBitmap( getCroppedBitmap(image));
                }

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
/*                i.putExtra("group_name", user.getGroups().get(info.position).getName());
                i.putExtra("group_desc", user.getGroups().get(info.position).getDescription());
                i.putExtra("group_id", user.getGroups().get(info.position).getId());
                i.putExtra("user_id", user.getUid());*/
                i.putExtra(Constant.ACTIVITYUSER,user);
                i.putExtra(Constant.ACTIVITYPOSITION, Integer.toString(info.position));
                startActivityForResult(i, MODIFY_GROUP);
                return true;
/*            case R.id.leave:
                final String uid = user.getUid();
                drawLeavingDialogBox(user.getGroups().get(info.position).getId(), uid, info.position);
                return true;*/
            case R.id.add_members:
                DatabaseReference pin = FirebaseDatabase.getInstance().getReference(Constant.REFERENCEGROUPS)
                        .child(user.getGroups().get(info.position).getId())
                        .child(Constant.REFERENCEGROUPSPIN);
                pos = info.position;
                pin.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange (DataSnapshot dataSnapshot){
                        pin_str = dataSnapshot.getValue(String.class);
                        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                                .setMessage(getString(R.string.invitation_message))
                                .setEmailHtmlContent(getString(R.string.invitation_email,
                                        user.getGroups().get(pos).getName(),
                                        user.getGroups().get(pos).getId(),
                                        pin_str))
                                .setEmailSubject(user.getGroups().get(pos).getName()+
                                        " "+ getString(R.string.email_subject))
                                .build();
                        startActivityForResult(intent, REQUEST_INVITE);
                    }

                    @Override
                    public void onCancelled (DatabaseError databaseError){
                        System.out.println("FAIL PIN INFO");
                    }
                });

                return true;
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
            Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
/*            ImageView myImage = (ImageView) findViewById(R.id.ie_iv_from_camera);
            myImage.setImageBitmap(bitmap);*/

            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MoneyTracker");
            mediaStorageDir.mkdirs();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
            try {
                FileOutputStream out = new FileOutputStream(mediaFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                personalPhoto.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] byteArrayImage = baos.toByteArray();
                String encodedExpenseImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                user_info.child("userImage").setValue(encodedExpenseImage);
                user.setUserImage(encodedExpenseImage);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.imageOutFile = mediaFile;
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

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
}