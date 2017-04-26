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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        // this listener will be called when there is change in firebase user session
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("Debug", "In AuthListener");
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



        //mDatabase = FirebaseDatabase.getInstance().getReference();

        //mDatabase.child("Groups");

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

        ArrayList<Group> arrayOfGroups = new ArrayList<Group>();
        final GroupAdapter adapter = new GroupAdapter(this, arrayOfGroups);

        ListView listView = (ListView) findViewById(R.id.group_list);
        listView.setAdapter(adapter);

        //String[] nomi = {"Andrian", "Flavia", "Michele", "Gaetano"};

        adapter.clear();

        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference myRef = database.getReference("Groups");

        mDatabase = FirebaseDatabase.getInstance().getReference("Groups");


        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adapter.clear();
                for (DataSnapshot groupsnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from database snapshot
                    Group group = groupsnapshot.getValue(Group.class);
                    adapter.add(group);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.d("Debug", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(postListener);


        /*List<String> Mylist = new ArrayList<String>();
        Mylist.add("Flavia");
        Mylist.add("Andrian");
        Mylist.add("Michele");

        Group newGroup = new Group("Gruppo 2", 1, Mylist, "Gruppo Bello");*/

        //Group newGroup = new Group("Gruppo 1", 1, nomi, "Gruppo Bello");
        //Group newGroup2 = new Group("Gruppo 2", 2, nomi, "Gruppo Carino");
        //Group newGroup3 = new Group("Gruppo 3", 3, nomi, "Gruppo Brutto");

        //adapter.add(newGroup);
        //adapter.add(newGroup2);
        //adapter.add(newGroup3);

        /*adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);*/

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Debug", String.valueOf(position));
                //Toast.makeText(MainActivity.this, "Clicked group: " + String.valueOf(position), Toast. LENGTH_SHORT).show();

                Intent i=new Intent(MainActivity.this, GroupActivityExpense.class);
                i.putExtra("groupNumber", position);
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
        View header=navigationView.getHeaderView(0);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView user_email = (TextView)header.findViewById(R.id.user_email);
        user_email.setText(user.getEmail());

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

        if (id == R.id.new_group) {
            // Handles the new group form
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

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
