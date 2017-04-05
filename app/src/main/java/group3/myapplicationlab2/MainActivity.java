package group3.myapplicationlab2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ArrayList<Group> arrayOfGroups = new ArrayList<Group>();
        GroupAdapter adapter = new GroupAdapter(this, arrayOfGroups);

        ListView listView = (ListView) findViewById(R.id.group_list);
        listView.setAdapter(adapter);

        String[] nomi = {"Andrian", "Flavia", "Michele", "Gaetano"};

        adapter.clear();
        Group newGroup = new Group("Gruppo 1", 1, nomi, "Gruppo Bello");
        Group newGroup2 = new Group("Gruppo 2", 2, nomi, "Gruppo Carino");
        Group newGroup3 = new Group("Gruppo 3", 3, nomi, "Gruppo Brutto");

        adapter.add(newGroup);
        adapter.add(newGroup2);
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
        adapter.add(newGroup3);
        adapter.add(newGroup3);
        adapter.add(newGroup3);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Debug", String.valueOf(position));
                Toast.makeText(MainActivity.this, "Clicked group: " + String.valueOf(position), Toast. LENGTH_SHORT).show();
            }
        });

        /*ListView myListView = (ListView)findViewById(R.id.group_list);

        BaseAdapter baseAdapter = new BaseAdapter() {
            String[] groups = { "First group", "Second group", "Third group", "Fourth group",
                                "First group", "Second group", "Third group", "Fourth group",
                                "First group", "Second group", "Third group", "Fourth group" };

            @Override
            public int getCount() {
                return groups.length;
            }

            @Override
            public Object getItem(int position) {
                return groups.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return null;
            }
        };

        myListView.setAdapter(baseAdapter);
        myListView.setOnItemClickListener(onListClick);*/

        /*
        list=(ListView)findViewById(R.id.group_list);
        list.setAdapter(new BaseAdapter() {
            String[] groups = {"First group", "Second group", "Third group", "Fourth group"};

            @Override
            public int getCount() {return groups.length;}
            @Override
            public Object getItem(int position) {return groups[position];}
            @Override
            public long getItemId(int position) {return 0;}
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // There is nothing to convert --> I need to create extra view
                if (convertView==null) {
                    // Returns a view: inflate is used to populate a view
                    convertView = getLayoutInflater().inflate(R.layout.group_item, parent, false);
                }
                TextView tv = (TextView)convertView.findViewById(R.id.group_item);
                tv.setText(getItem(position).toString());
                return convertView;
            }
        });
        */


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
