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
import com.google.firebase.database.ChildEventListener;
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

    ArrayList<Purchase> spese;
    ChildEventListener childEventListener;

    private Util util;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_expense);
        util = new Util(getApplicationContext());

        gid = getIntent().getStringExtra(Constant.ACTIVITYGROUPID);
        user = (User)getIntent().getSerializableExtra(Constant.ACTIVITYUSER);

        final DatabaseReference mGroupReference =  FirebaseDatabase.getInstance()
                                                    .getReference(Constant.REFERENCEGROUPS)
                                                    .child(gid);

        final ValueEventListener GroupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null){

                    Map<String, Object> objectMap = (HashMap<String, Object>)
                            dataSnapshot.getValue();
                    group = new Group();
                    group.GroupConstructor(objectMap);

                    //Collections.sort(group.getPurchases());
                    //Collections.reverse(group.getPurchases());
                    //final HashMap<String, Bitmap> images = new HashMap<String, Bitmap>();
                    //DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
                    /* usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (int i =0; i< group.getGroupMembers().size(); i++){
                                // controllo se la foto è stata aggironata
                                final String key = group.getGroupMembers().get(i).getUser_id();
                                final String user_uri = dataSnapshot.child(key).child("userPathImage").getValue(String.class);
                                String current = dataSnapshot.child(key).child("currentPicsUpload").getValue(String.class);
                                String last = dataSnapshot.child(key).child("lastPicsUpload").getValue(String.class);

                                //se in memoria non c'è path per downloadre l'immagine, ne metto una default
                                if (user_uri.equals("nopath")){
                                    images.put(key, BitmapFactory.decodeResource(getApplicationContext().getResources(),
                                            R.mipmap.ic_launcher));
                                }
                                else{
                                    //se non è stata aggiornata carico da memoira del telefono
                                    if (current.equals(last)){
                                        try {
                                            File f=new File(Environment.getExternalStorageDirectory(), "MoneyTracker/profileImages/"+key+".jpg");
                                            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                                            images.put(key,b );

                                        }
                                        catch (FileNotFoundException e)
                                        {
                                            e.printStackTrace();
                                        }

                                    }
                                    //se è stata aggionrata setto, le segno, scarico da url attraverso appostio listner e salvo in memoira del telefono
                                    else{
                                        Log.d("GAE", "different");
                                        Log.d("GAE", "c: " + current + " l: " + last);
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("lastPicsUpload").setValue(current);
                                        FirebaseStorage.getInstance().getReference("UsersImage").child(key)
                                                .getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                            @Override
                                            public void onSuccess(byte[] bytes) {
                                                Bitmap b = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                                                util.saveImageInMemory(user_uri, key);
                                                images.put(key, b);
                                            }
                                        });

                                    }
                                }
                            }

                            Collections.sort(group.getPurchases());
                            Collections.reverse(group.getPurchases());
                            expenseAdapter = new ExpenseAdapter(GroupActivityExpense.this, new ArrayList<Purchase>(), user);
                            expenseAdapter.setImages(images);

                            expenseAdapter.addAll(group.getPurchases());
                            listView = (ListView) findViewById(R.id.expense_list);
                            listView.setAdapter(expenseAdapter);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });*/

                    if (group.getPurchases().size()<1){
                        findViewById(R.id.content_with_purchases).setVisibility(View.GONE);
                        findViewById(R.id.content_without_purchases).setVisibility(View.VISIBLE);
                    }

                    mGroupReference.child(Constant.REFERENCEGROUPSPURCHASE).addChildEventListener(childEventListener);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        mGroupReference.addListenerForSingleValueEvent(GroupListener);

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                Purchase purchase = new Purchase();
                purchase.PurchaseConstructor(objectMap);

                spese.add(0, purchase);
                group.setPurchases(spese);
                expenseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                Purchase purchase = new Purchase();
                purchase.PurchaseConstructor(objectMap);

                int position;
                for (int ii=0; ii<spese.size(); ii++){

                    if (spese.get(ii).getPurchase_id().equals(purchase.getPurchase_id())){
                        position = ii;
                        spese.remove(position);
                    }
                }

                spese.add(0, purchase);
                group.setPurchases(spese);
                expenseAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                Purchase purchase = new Purchase();
                purchase.PurchaseConstructor(objectMap);

                int position;
                for (int ii=0; ii<spese.size(); ii++){

                    if (spese.get(ii).getPurchase_id().equals(purchase.getPurchase_id())){
                        position = ii;
                        spese.remove(position);
                    }

                }
                group.setPurchases(spese);
                expenseAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        spese = new ArrayList<Purchase>();
        expenseAdapter = new ExpenseAdapter(GroupActivityExpense.this, spese, user);
        listView = (ListView) findViewById(R.id.expense_list);
        listView.setAdapter(expenseAdapter);

        listView.setDivider(null);
        listView.setDividerHeight(0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_exp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra(Constant.ACTIVITYGROUPNAME));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GroupActivityExpense.this, ExpenseInput.class);
                i.putExtra(Constant.ACTIVITYGROUP, group);
                i.putExtra(Constant.ACTIVITYUSER, user);

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

            if (group.getPurchases().size() == 0){
                //drawLeavingDialogBox(getString(R.string.stats_no_pruchases),
                //        getString(R.string.stats_no_pruchases_text));

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
                //group.computePaymentProportion(user);
                group.computePaymentProportionContributors(user);
                Intent i = new Intent(GroupActivityExpense.this, GroupStats.class);
                i.putExtra(Constant.ACTIVITYGROUP, group);
                i.putExtra(Constant.ACTIVITYUSER, user);
                startActivity(i);
            }
            return true;
        }

        if (id == R.id.leave){
            final String uid = user.getUid();
            drawLeavingDialogBox(group.getDescription(), user.getUid());
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

            }
        }
        if (requestCode == PURCHASE_CONTRIBUTOR){
            if(resultCode == RESULT_OK) {
                int i = data.getIntExtra("pos",-1);
                group.getPurchases().get(i).setContributors((List<PurchaseContributor>)data.getSerializableExtra("pcList"));
            }
        }
    }

    public void registerListenerOnListView(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = listView.getItemAtPosition(position);
                Purchase p = (Purchase)o;
                Intent i = new Intent(getApplicationContext(), PurchaseContributors.class);

                i.putExtra(Constant.ACTIVITYGROUP, group);
                i.putExtra(Constant.ACTIVITYGROUPID, gid);
                i.putExtra(Constant.ACTIVITYUSER,user);


                for (Purchase purchase : group.getPurchases()){
                    if (purchase.getPurchase_id().equals(p.getPurchase_id())) {
                        i.putExtra(Constant.ACTIVITYPURCHASE, purchase);
                    }
                }
                i.putExtra("pos", position);
                startActivityForResult(i, PURCHASE_CONTRIBUTOR);
            }
        });
    }

    private void drawLeavingDialogBox(String gid, String uid){
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivityExpense.this);
        builder.setMessage(getString(R.string.grpup_leaving_text)).setTitle(getString(R.string.leaving_group_title));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (userHasDebits(user.getUid())==false){
                        deleteUserFromGroup();
                        Intent i = new Intent();
                        i.putExtra(Constant.ACTIVITYUSERMODIFIED, user);
                        setResult(RESULT_OK, i);
                        finish();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivityExpense.this);
                    builder.setMessage(getString(R.string.user_has_debit_text)).setTitle(getString(R.string.user_has_debits_title));
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    AlertDialog dialog2 = builder.create();
                    dialog2.show();
                    dialog2.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
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
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

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

    private void deleteUserFromGroup(){

        /*Removing fromu group Preview*/
        DatabaseReference user_groups;
        user_groups = FirebaseDatabase.getInstance().getReference()
                .child(Constant.REFERENCEUSERS)
                .child(user.getUid())
                .child(Constant.REFERENCEGROUPSHASH);
        user_groups.child(group.getId()).removeValue();


        /*removing from user groups variable*/
        if (user.getGroups().size()>0) {
            user.getGroups().remove(Integer.parseInt(getIntent().getStringExtra("position")));
        }

        /*Removing from user from groups*/
        DatabaseReference groupsQuery = FirebaseDatabase.getInstance().getReference()
                .child(Constant.REFERENCEGROUPS)
                .child(group.getId())
                .child(Constant.REFERENCEGROUPSMEMBERS)
                .child(user.getUid());
        groupsQuery.removeValue();
    }
}


