package group3.myapplicationlab2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.security.Key;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class GroupActivityExpense extends AppCompatActivity {

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    ExpenseAdapter expenseAdapter;
    Locale l = Locale.ENGLISH;
    private String gid;

    private FirebaseAuth auth;
    private DatabaseReference mGroupReference;
    List<Purchase> purchases = new ArrayList<Purchase>();

    private String QUI = "GroupActivity";

    private Group group;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_expense);
        gid = getIntent().getStringExtra("group_id");
        user = (User)getIntent().getSerializableExtra("user");


        final DatabaseReference mGroupReference =  FirebaseDatabase.getInstance()
                                                    .getReference("Groups")
                                                    .child(gid);

        final ValueEventListener GroupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null){

                    Map<String, Object> objectMap = (HashMap<String, Object>)
                            dataSnapshot.getValue();
                    group = new Group();
                    group.GroupConstructor(objectMap);
                    expenseAdapter.addAll(group.getPurchases());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        mGroupReference.addListenerForSingleValueEvent(GroupListener);

        ArrayList<Purchase> spese = new ArrayList<Purchase>();
        expenseAdapter = new ExpenseAdapter(GroupActivityExpense.this, spese);
        ListView listView = (ListView) findViewById(R.id.expense_list);
        listView.setAdapter(expenseAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_exp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("group_name"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(GroupActivityExpense.this, ExpenseInput.class);
                i.putExtra("user_id", getIntent().getStringExtra("user_id"));
                i.putExtra("group_id", getIntent().getStringExtra("group_id"));
                i.putExtra("list_pos", getIntent().getStringExtra("list_pos"));
                i.putExtra("user", user);

                Log.d("Debug", "pos: " + getIntent().getStringExtra("list_pos") +
                        " group_id: " + getIntent().getStringExtra("group_id"));
                startActivityForResult(i,1);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.group_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.group_Stats) {
            //group.computeTotalExpense();
            //group.computeAggregateExpenses(user);

            group.computePaymentProportion(user);

            if (group.getPurchases().size() == 0){
                drawLeavingDialogBox(getString(R.string.stats_no_pruchases),
                        getString(R.string.stats_no_pruchases_text));
            }
            else if(group.getMembers().size() ==0){
                drawLeavingDialogBox(getString(R.string.stats_no_members),
                        getString(R.string.stats_no_members_text));

            }
            else {
                Intent i = new Intent(GroupActivityExpense.this, GroupStats.class);
                i.putExtra("group", group);
                i.putExtra("user", user);
                //startActivityForResult(i,1);
                startActivity(i);
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), R.string.correct_purchase_added, Toast.LENGTH_SHORT).show();
                Purchase new_purchase = (Purchase)data.getSerializableExtra("new_purchase");
                purchases.add(new_purchase);
                Collections.sort(purchases, Collections.<Purchase>reverseOrder());
                expenseAdapter.clear();
                expenseAdapter.addAll(purchases);
            }
        }
    }

    private void drawLeavingDialogBox(String title, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text).setTitle(title);
        builder.setPositiveButton(getString(R.string.ok),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}


