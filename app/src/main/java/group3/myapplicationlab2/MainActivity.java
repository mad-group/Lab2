package group3.myapplicationlab2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private ListView list;
    private ArrayList<String> groups_ids = new ArrayList<>();

    private User user;
    private DatabaseReference user_info;
    private GroupPreviewAdapter adapter;

    private List<GroupPreview> currentGroupPreview;

    private final int CREATE_GROUP = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        final ArrayList<GroupPreview> groupPreviews = new ArrayList<GroupPreview>();
        adapter = new GroupPreviewAdapter(this, groupPreviews);
        final ListView listView = (ListView) findViewById(R.id.group_list);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        user_info = mDatabase.child(auth.getCurrentUser().getUid());

        user_info.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
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
        });




        //Value event listener for realtime data update
        /*mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Log.d("Debug", postSnapshot.getValue(""));
                    //Getting the data from snapshot
                    //Group group = postSnapshot.getValue(Group.class);

                    //Log.d("Debug", group.getName());
                    //Adding it to a string
                    //String string = "Name: "+person.getName()+"\nAddress: "+person.getAddress()+"\n\n";

                    //Displaying it on textview
                    //textViewPersons.setText(string);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });*/

        //final ArrayList<Group> arrayOfGroups = new ArrayList<Group>();
        //final GroupAdapter adapter = new GroupAdapter(this, arrayOfGroups);

        //ListView listView = (ListView) findViewById(R.id.group_list);
        //listView.setAdapter(adapter);
        //adapter.clear();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, "Clicked group: " + String.valueOf(position), Toast. LENGTH_SHORT).show();
                Intent i=new Intent(MainActivity.this, GroupActivityExpense.class);
                i.putExtra("user_id", user.getUid());

                for (int pos =0; pos < user.getGroups().size(); pos++){

                }
                i.putExtra("list_pos", Integer.toString(position));
                i.putExtra("group_id", user.getGroups().get(position).getId());
                Log.d("Debug", "pos: " + position +
                        " group_id: " + user.getGroups().get(position).getId() +
                        " group_na: " + user.getGroups().get(position).getName());
                startActivityForResult(i,10);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_group);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Debug", "QUI");
                Intent i = new Intent(MainActivity.this, GroupCreationForm.class);
                startActivityForResult(i, CREATE_GROUP);
            }
        });

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
                Log.d("debug", "aaaaa " + info.id + " " + info.position);
                Log.d("debug", "modfy " + user.getGroups().get(info.position).getId());
                return true;
            case R.id.leave:
                Log.d("debug", "Leave " + user.getGroups().get(info.position).getId());
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_GROUP) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), R.string.correct_purchase_added, Toast.LENGTH_SHORT).show();

                GroupPreview groupPreview = (GroupPreview) data.getSerializableExtra("new_groupPreview");
                adapter.add(groupPreview);

                if (user.getGroups() != null){
                    currentGroupPreview = user.getGroups();
                }
                else{
                    currentGroupPreview = new ArrayList<GroupPreview>();
                }

                currentGroupPreview.add(groupPreview);
                user_info.child("groups").setValue(currentGroupPreview);
                user.setGroups(currentGroupPreview);

            }
        }
        else {
            if (user.getGroups() != null) {
                currentGroupPreview = user.getGroups();
                for (int j = 0; j < user.getGroups().size(); j++)
                    //Log.d("debug", "first" + user.getGroups().get(j).getName());
                Collections.sort(currentGroupPreview, Collections.<GroupPreview>reverseOrder());
                adapter.clear();
                for (int i = 0; i<currentGroupPreview.size(); i++) {
                    //Log.d("debug", "after" + user.getGroups().get(i).getName());
                    adapter.add(user.getGroups().get(i));
                }
                user_info.child("groups").setValue(currentGroupPreview);
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
            startActivity(i);

        } else if (id == R.id.logout) {
            auth.signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void onClick(View view) {
        //Intent i = new Intent(view.getContext(), ExpensesReports.class);
        //startActivity(i);
    }
}
