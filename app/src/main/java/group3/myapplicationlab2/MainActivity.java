package group3.myapplicationlab2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
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
        final GroupPreviewAdapter adapter = new GroupPreviewAdapter(this, groupPreviews);
        ListView listView = (ListView) findViewById(R.id.group_list);
        listView.setAdapter(adapter);

        //mDatabase = FirebaseDatabase.getInstance().getReference();

        //mDatabase.child("Groups");

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference user_info = mDatabase.child(auth.getCurrentUser().getUid());

        user_info.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(User.class);

                if (user.getGroups() != null){
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



        /*FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("Groups");

        ValueEventListener GroupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("debug", "***********");
                adapter.clear();
                int index = 0;
                HashMap<Long, Group> hm_groups = new HashMap<>();
                HashMap <Long, String> hm_keys = new HashMap<>();
                ArrayList<Long> ts = new ArrayList<>();
                for (DataSnapshot groupSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from database snapshot
                    Group group = groupSnapshot.getValue(Group.class);
                    List<String> members = group.getMembers();

                    if (members.contains(auth.getCurrentUser().getUid()) ||
                            members.contains(auth.getCurrentUser().getEmail())){
                        //
                        //Save the records:
                        //<timestamp, groups> (to visualzie)
                        //<timestamp, firebase groupID>, to allow put in the Expense input intent the GID
                        //arraylist of ts, to order
                        //
                        hm_groups.put(group.getLastModifyTimeStamp(), group);
                        hm_keys.put(group.getLastModifyTimeStamp(), groupSnapshot.getKey());
                        ts.add(group.getLastModifyTimeStamp());
                    }
                }
                //ordering in reverse order
                Collections.sort(ts);
                Collections.reverse(ts);
                //
                //For all the timestamps, i take the keys (the ts) and i put it in a collection which map
                //listview position - group id
                //put in the adpater the group
                //
                for (Long k : ts){
                    groups_ids.add(index,hm_keys.get(k));
                    Log.d("debug", k.toString());
                    index +=1;
                    adapter.add(hm_groups.get(k));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d("Debug", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(GroupListener);*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MainActivity.this, "Clicked group: " + String.valueOf(position), Toast. LENGTH_SHORT).show();
                Intent i=new Intent(MainActivity.this, GroupActivityExpense.class);
                //i.putExtra("group_id", groups_ids.get(position));
                i.putExtra("group_id", user.getGroups().get(position).getId());
                startActivity(i);
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
        /*View header=navigationView.getHeaderView(0);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView user_email = (TextView)header.findViewById(R.id.user_email);
        user_email.setText(user.getEmail());*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add_group);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("Debug", "QUI");
                Intent i = new Intent(MainActivity.this, GroupCreationForm.class);
                startActivity(i);
                // Write a message to the database
                /*FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Groups");

                //myRef.setValue("Hello, World!");


                //Creating firebase
                //mDatabase = FirebaseDatabase.getInstance().getReference();

                //Getting values to store
                //String[] nomi = {"Andrian", "Flavia", "Michele", "Gaetano"};
                List<String> Mylist = new ArrayList<String>();
                Mylist.add("Flavia");
                Mylist.add("Andrian");
                Mylist.add("Michele");
                Mylist.add("Gaetano");

                Group newGroup = new Group();

                newGroup.setDescription("Descrizione Seria");
                newGroup.setId(2);
                newGroup.setMembers(Mylist);
                newGroup.setName("Gruppo 2");
                //myRef.child("gruppo2").setValue(newGroup);

                myRef.push().setValue(newGroup);
*/
                //Storing values to firebase
                //mDatabase.child("Groups").setValue(newGroup);

                //Intent i = new Intent(GroupActivityExpense.this, ExpenseInput.class);
                //startActivityForResult(i,1);

                //String newItem = "New Expense";
                //arrayList.add(newItem);
                //adapter.notifyDataSetChanged();
            }
        });

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
