package group3.myapplicationlab2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    private static final int PURCHASE_CONTRIBUTOR = 698;
    ExpenseAdapter expenseAdapter;
    Locale l = Locale.ENGLISH;
    private String gid;

    private Group group;
    private User user;
    private ListView listView;

    private int groupListPosition;




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
                    Collections.sort(group.getPurchases());
                    Collections.reverse(group.getPurchases());
                    expenseAdapter.addAll(group.getPurchases());
                    //Log.d("Debug", "purchs dim onCreate " +  group.getPurchases().size() );
                    //paintListViewBackground();

                    if (group.getPurchases().size()<1){
                        findViewById(R.id.content_with_purchases).setVisibility(View.GONE);
                        findViewById(R.id.content_without_purchases).setVisibility(View.VISIBLE);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        mGroupReference.addListenerForSingleValueEvent(GroupListener);

        ArrayList<Purchase> spese = new ArrayList<Purchase>();
        expenseAdapter = new ExpenseAdapter(GroupActivityExpense.this, spese);
        listView = (ListView) findViewById(R.id.expense_list);
        listView.setAdapter(expenseAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_exp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("group_name"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GroupActivityExpense.this, ExpenseInput.class);
                i.putExtra("group", group);
                i.putExtra("user", user);

                startActivityForResult(i,1);
            }
        });

        registerListenerOnListView();



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

            Log.d("DEBUG GROUP MEMBERS", group.getGroupMembers().toString());
            Log.d("DEBUG SIZE", String.valueOf(group.getGroupMembers().size()));
            Log.d("DEGUB PURCHASES", String.valueOf(group.getPurchases().size()));

            if (group.getPurchases().size() == 0){
                //drawLeavingDialogBox(getString(R.string.stats_no_pruchases),
                //        getString(R.string.stats_no_pruchases_text));
                Log.d("DENTRO IF", "IF");

                AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupActivityExpense.this);
                builder1.setMessage("There aren't purchases.");
                builder1.setCancelable(true);

                builder1.setNegativeButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
            else if(group.getGroupMembers() == null || group.getGroupMembers().size()<2){

                Log.d("DENTRO IF", "IF2");

                //drawLeavingDialogBox(getString(R.string.stats_no_members),
                //        getString(R.string.stats_no_members_text));

                AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupActivityExpense.this);
                builder1.setMessage("You are alone in the group");
                builder1.setCancelable(true);

                builder1.setNegativeButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
            else {
                group.resetPaymentProportion();
                group.computePaymentProportion(user);
                Intent i = new Intent(GroupActivityExpense.this, GroupStats.class);
                i.putExtra("group", group);
                i.putExtra("user", user);
                startActivity(i);
            }
            return true;
        }

        if (id == R.id.leave){
            final String uid = user.getUid();
            drawLeavingDialogBox(group.getDescription(), user.getUid(), getIntent().getStringExtra("list_pos"));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {

                findViewById(R.id.content_with_purchases).setVisibility(View.VISIBLE);
                findViewById(R.id.content_without_purchases).setVisibility(View.GONE);
                
                Toast.makeText(getApplicationContext(), R.string.correct_purchase_added, Toast.LENGTH_SHORT).show();
                Purchase new_purchase = (Purchase)data.getSerializableExtra("new_purchase");
                group.getPurchases().add(new_purchase);
                expenseAdapter.clear();
                Collections.sort(group.getPurchases());
                Collections.reverse(group.getPurchases());
                //d("Debug", "size gpr OAR" + group.getPurchases().size());
                expenseAdapter.addAll(group.getPurchases());
            }
        }
        if (requestCode == PURCHASE_CONTRIBUTOR){
            if(resultCode == RESULT_OK) {
                int i = data.getIntExtra("pos",-1);
                group.getPurchases().get(i).setContributors((List<PurchaseContributor>)data.getSerializableExtra("pcList"));
            }

        }
    }

    private void drawLeavingDialogBox(String title, String text) {
        AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext(), android.R.style.Theme_Material_Dialog_Alert).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(text);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,
                getString(R.string.ok),
                new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                return;

            } });
        alertDialog.show();

    }

    public void registerListenerOnListView(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Purchase p = (Purchase)o;
                Intent i = new Intent(getApplicationContext(), PurchaseContributors.class);

                i.putExtra("group", group);
                i.putExtra("group_id", gid);
                i.putExtra("user",user);
                Log.d("Debug", "----------");
                for (Purchase purchase : group.getPurchases()){
                    Log.d("Debug", "size pc: " + purchase.getContributors().size());
                    if (purchase.getPurchase_id().equals(p.getPurchase_id())) {
                        i.putExtra("purchase", purchase);

                    }
                }
                i.putExtra("pos", position);
                startActivityForResult(i, PURCHASE_CONTRIBUTOR);

            }
        });
    }

    private void drawLeavingDialogBox(String gid, String uid, final String position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.grpup_leaving_text)).setTitle(getString(R.string.leaving_group_title));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (userHasDebits(user.getUid())==false){
                    //deleteUserFromGroup(position);
                    deleteUserFromGroup(position);
                    finish();
                }

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private Boolean userHasDebits(String user_id){
        List<Purchase> list = group.getPurchases();
        for (Purchase p: list){
            for(PurchaseContributor pc : p.getContributors()){
                if(pc.getUser_id().equals(user.getUid()) && pc.getPayed()==false) {
                    return true;
                }

            }
        }
        return false;
    }

    private void deleteUserFromGroup(String position){
        DatabaseReference user_groups;
        user_groups = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("groups");
        user_groups.child(position).removeValue();
        if (user.getGroups() != null)
            user.getGroups().remove(position);
        DatabaseReference groupsQuery = FirebaseDatabase.getInstance().getReference()
                .child("Groups")
                .child(group.getId())
                .child("members2")
                .child(user.getUid());
        groupsQuery.removeValue();
    }


}


