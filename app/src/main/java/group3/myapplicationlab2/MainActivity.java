package group3.myapplicationlab2;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.CollationElementIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public static final int GROUP_CLICKED = 10;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private ListView list;
    private ArrayList<String> groups_ids = new ArrayList<>();

    private User user;
    private  DatabaseReference user_groups;
    private DatabaseReference user_info;
    private GroupPreviewAdapter adapter;
    private GroupPreviewAdapterHashMap groupPreviewAdapterHashMap;

    private List<GroupPreview> currentGroupPreview;

    private final int CREATE_GROUP = 1;
    private static final int MODIFY_GROUP = 2;
    private static final int REQUEST_INVITE =0;
    private static final int JOIN_GROUP = 3;

    private String pin_str;
    private int pos;

    private DataBaseProxy dataBaseProxy;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getApplication().registerActivityLifecycleCallbacks(new ApplicationLifecycleManager());

        dataBaseProxy = new DataBaseProxy();

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
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

        //NEW ADAPTER
        //HashMap<String, GroupPreview> groupPreviewHashMap = new HashMap<String, GroupPreview>();
        //GroupPreview groupPreview = new GroupPreview();
        //groupPreview.GroupPreviewConstructor("Nome", "id", "description", 124155135, "Evento", "Author");
        //groupPreviewHashMap.put("QUALCOSA", groupPreview);

        /*groupPreviewAdapterHashMap = new GroupPreviewAdapterHashMap(groupPreviewHashMap);
        final ListView listView = (ListView) findViewById(R.id.group_list);
        listView.setAdapter(groupPreviewAdapterHashMap);*/

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        user_info = mDatabase.child(auth.getCurrentUser().getUid());
        user_groups = user_info.child("groups");

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

                //HASHMAP v2
                /*user = dataSnapshot.getValue(User.class);
                if (user.getGroupsHash()!=null){
                    groupPreviewAdapterHashMap.add(user.getGroupsHash());
                    groupPreviewAdapterHashMap.notifyDataSetChanged();
                }*/

                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(MainActivity.this);
                View header=navigationView.getHeaderView(0);
                TextView user_email = (TextView)header.findViewById(R.id.user_email);
                user_email.setText(user.getEmail());
                TextView user_name = (TextView)header.findViewById(R.id.username);
                user_name.setText(user.getName());

                Intent serviceIntent = new Intent(MainActivity.this, GroupPreviewService.class);
                serviceIntent.putExtra("user", user);
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
                //Toast.makeText(MainActivity.this, "Clicked group: " + String.valueOf(position), Toast. LENGTH_SHORT).show();
                Intent i=new Intent(MainActivity.this, GroupActivityExpense.class);
                i.putExtra("user_id", user.getUid());
                i.putExtra("group_name", user.getGroups().get(position).getName());
                i.putExtra("position", Integer.toString(position));
                i.putExtra("group_id", user.getGroups().get(position).getId());
                i.putExtra("user", user);

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
                i.putExtra("user", user);
                startActivityForResult(i, CREATE_GROUP);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();

        /*user_info.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                adapter.clear();

                user = dataSnapshot.getValue(User.class);
                if (user.getGroups() != null){
                    //Collections.sort(user.getGroups(), Collections.<GroupPreview>reverseOrder());
                    adapter.clear();
                    for (int i=0; i<user.getGroups().size(); i++){
                        adapter.add(user.getGroups().get(i));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("FAIL USER INFO");
            }
        });*/

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
                i.putExtra("group_name", user.getGroups().get(info.position).getName());
                i.putExtra("group_desc", user.getGroups().get(info.position).getDescription());
                i.putExtra("group_id", user.getGroups().get(info.position).getId());
                i.putExtra("user_id", user.getUid());
                startActivityForResult(i, MODIFY_GROUP);
                return true;
/*            case R.id.leave:
                final String uid = user.getUid();
                drawLeavingDialogBox(user.getGroups().get(info.position).getId(), uid, info.position);
                return true;*/
            case R.id.add_members:
                DatabaseReference pin = FirebaseDatabase.getInstance().getReference("Groups")
                        .child(user.getGroups().get(info.position).getId())
                        .child("pin");
                pos = info.position;
                pin.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange (DataSnapshot dataSnapshot){
                        pin_str = dataSnapshot.getValue(String.class);
                        Log.d("debug", "aaa + " + pin_str);
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

        Log.d("GROUP", "GROUP CREATED");
        if (requestCode == CREATE_GROUP) {
            if(resultCode == RESULT_OK) {
                //Toast.makeText(getApplicationContext(), R.string.correct_purchase_added, Toast.LENGTH_SHORT).show();

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
                //user_info.child("groups").setValue(currentGroupPreview);
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
                user = (User)data.getSerializableExtra("modified_user");
                reDrawGroupList();
            }

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

        //VERSION 2 HASHMAP
        /*if (requestCode == CREATE_GROUP) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), R.string.correct_purchase_added, Toast.LENGTH_SHORT).show();

                String groupPreviewKey = (String) data.getStringExtra("groupPreviewKey");
                GroupPreview groupPreview = (GroupPreview) data.getSerializableExtra("new_groupPreview");

                //Add GroupPreview in User
                user.insertGroupPreviewInHashMap(groupPreviewKey, groupPreview);

                HashMap<String, GroupPreview> groupPreviewHashMap = new HashMap<String, GroupPreview>();
                groupPreviewHashMap.put(groupPreviewKey, groupPreview);

                groupPreviewAdapterHashMap.add(groupPreviewHashMap);
                groupPreviewAdapterHashMap.notifyDataSetChanged();

                for (Map.Entry<String,GroupPreview> entry : user.getGroupsHash().entrySet()) {
                    String key = entry.getKey();
                    GroupPreview groupPreview1 = entry.getValue();
                    Log.d("NAME", groupPreview1.getName());
                    Log.d("ID", groupPreview1.getId());
                }
            }
        }*/
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
            i.putExtra("user", user);
            //startActivity(i);
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
        for(int i=0; i< user.getGroups().size(); i++){
            adapter.add(user.getGroups().get(i));
        }
        user_groups.setValue(user.getGroups());
        /*qui alla fine potrebbero non esserci più gruppi,occorrebbe risettare l'immagine No grpups*/
    }

}















