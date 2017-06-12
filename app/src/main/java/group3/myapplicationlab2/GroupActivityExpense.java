package group3.myapplicationlab2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupActivityExpense extends AppCompatActivity {

    private static final int PURCHASE_CONTRIBUTOR = 698;
    ExpenseAdapter expenseAdapter;
    Locale l = Locale.ENGLISH;
    private String gid;

    private Group group;
    private User user;
    private ListView listView;
    private Util util;

    private Toolbar toolbar;
    ArrayList<Purchase> expenseList;
    ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_expense);

        util = new Util(getApplicationContext());
        gid = getIntent().getStringExtra(Constant.ACTIVITYGROUPID);
        user = (User)getIntent().getSerializableExtra(Constant.ACTIVITYUSER);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar_GAE);
        progressBar.setVisibility(View.VISIBLE);

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

                    final HashMap<String, Bitmap> images = new HashMap<String, Bitmap>();

                    final File cacheDir = getBaseContext().getCacheDir();
                    for (int i=0; i<group.getGroupMembers().size(); i++){
                        final String key = group.getGroupMembers().get(i).getUser_id();
                        File f = new File(cacheDir, key);
                        FileInputStream fis = null;
                        try {
                            fis = new FileInputStream(f);
                            Bitmap bitmap = BitmapFactory.decodeStream(fis);
                            images.put(key, bitmap);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    expenseAdapter.setImages(images);
                    expenseAdapter.notifyDataSetChanged();


                    for (int i=0; i < group.getGroupMembers().size(); i++){
                        final String key = group.getGroupMembers().get(i).getUser_id();

                        FirebaseStorage.getInstance().getReference(Constant.REFERENCEUSERIMAGE).child(key)
                                .getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap b = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);

                                File f = new File(cacheDir, key);

                                try {
                                    FileOutputStream out = new FileOutputStream(f);
                                    b.compress(Bitmap.CompressFormat.JPEG, 100, out);
                                    out.flush();
                                    out.close();

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                images.put(key, b);
                                expenseAdapter.setImages(images);
                                expenseAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                    if (group.getPurchases().size()<1){
                        findViewById(R.id.content_with_purchases).setVisibility(View.GONE);
                        findViewById(R.id.content_without_purchases).setVisibility(View.VISIBLE);
                    }

                    mGroupReference.child(Constant.REFERENCEGROUPSPURCHASE).addChildEventListener(childEventListener);
                    progressBar.setVisibility(View.GONE);

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

                expenseList.add(0, purchase);
                group.setPurchases(expenseList);
                expenseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                Purchase purchase = new Purchase();
                purchase.PurchaseConstructor(objectMap);

                int position;
                for (int ii=0; ii<expenseList.size(); ii++){

                    if (expenseList.get(ii).getPurchase_id().equals(purchase.getPurchase_id())){
                        position = ii;
                        expenseList.remove(position);
                    }
                }

                expenseList.add(0, purchase);
                group.setPurchases(expenseList);
                expenseAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                Map<String, Object> objectMap = (HashMap<String, Object>) dataSnapshot.getValue();
                Purchase purchase = new Purchase();
                purchase.PurchaseConstructor(objectMap);

                int position;
                for (int ii=0; ii<expenseList.size(); ii++){

                    if (expenseList.get(ii).getPurchase_id().equals(purchase.getPurchase_id())){
                        position = ii;
                        expenseList.remove(position);
                    }

                }

                group.setPurchases(expenseList);

                if (group.getPurchases().size()==0){
                    findViewById(R.id.content_with_purchases).setVisibility(View.GONE);
                    findViewById(R.id.content_without_purchases).setVisibility(View.VISIBLE);
                }

                expenseAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        expenseList = new ArrayList<Purchase>();
        expenseAdapter = new ExpenseAdapter(GroupActivityExpense.this, expenseList, user);
        listView = (ListView) findViewById(R.id.expense_list);
        registerForContextMenu(listView);
        listView.setAdapter(expenseAdapter);

        listView.setDivider(null);
        listView.setDividerHeight(0);

        toolbar = (Toolbar) findViewById(R.id.toolbar_exp);
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
        if (id == R.id.group_Stats_GAE) {

            if (group.getPurchases().size() == 0){

                AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupActivityExpense.this);
                builder1.setMessage(R.string.stats_no_pruchases);
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
                alert11.getButton(alert11.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));


            }
            else if(group.getGroupMembers() == null || group.getGroupMembers().size()<2){

                AlertDialog.Builder builder1 = new AlertDialog.Builder(GroupActivityExpense.this);
                builder1.setMessage(R.string.stats_no_members);
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();
                alert11.getButton(alert11.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

            }
            else {
                group.resetPaymentProportion();
                group.computePaymentProportionContributors(user);
                Intent i = new Intent(GroupActivityExpense.this, GroupStats.class);
                i.putExtra(Constant.ACTIVITYGROUP, group);
                i.putExtra(Constant.ACTIVITYUSER, user);
                startActivityForResult(i,Constant.STATSGROUP);
            }
            return true;
        }

        if (id == R.id.leave_group_GAE){
            drawLeavingDialogBox(group.getDescription(), user.getUid());
            return true;
        }

        if (id == R.id.modify_GAE) {
            Intent i = new Intent(GroupActivityExpense.this, GroupModification.class);

            i.putExtra(Constant.ACTIVITYUSER, user);
            i.putExtra(Constant.ACTIVITYGROUPID, group.getId());
            i.putExtra(Constant.ACTIVITYGROUPNAME, group.getName());
            startActivityForResult(i, Constant.MODIFY_GROUP);
            return true;
        }

        if (id == R.id.add_members_GAE) {
            final String groupPin = group.getPin();
            final String groupId = group.getId();
            final String QRpath = group.getQRpath().toString();
            final String groupName = group.getName();

            try {
                String mex = "##" + groupId + "##||##" + groupPin + "##";
                Bitmap QR = util.encodeAsBitmap(mex);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                QR.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] data = baos.toByteArray();
                Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                        .setMessage(getString(R.string.invitation_message))
                        .setEmailHtmlContent(getString(R.string.invitation_email, user.getName(),
                                groupId, groupPin, QRpath))
                        .setEmailSubject(groupName + " " + getString(R.string.email_subject))
                        .build();
                startActivityForResult(intent, Constant.REQUEST_INVITE);


            } catch (WriterException e) {
                e.printStackTrace();
            }

            return true;

        }
        if (id == R.id.generate_QR_GAE){

            DatabaseReference group = FirebaseDatabase.getInstance().getReference(Constant.REFERENCEGROUPS)
                    .child(gid);
            group.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange (DataSnapshot dataSnapshot){
                    final String QRpath = dataSnapshot.child(Constant.REFERENCEGROUPSQR).getValue(String.class);
                    final String groupName = dataSnapshot.child(Constant.REFERENCEGROUPSNAME).getValue(String.class);
                    Bitmap QR = util.downloadImage(QRpath);
                    drawQRDialogBox(QR,groupName);
                }

                @Override
                public void onCancelled (DatabaseError databaseError){
                    System.out.println("FAIL PIN INFO");
                }
            });

        }

        return super.onOptionsItemSelected(item);
    }

 /*   @Override
    public void onResume(){
        super.onResume();


    }*/


    private void drawQRDialogBox(Bitmap bitmap, String groupName){
        ImageView imageView = new ImageView(GroupActivityExpense.this);
        imageView.setImageBitmap(bitmap);
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivityExpense.this);
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

    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
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

        if (requestCode == Constant.MODIFY_GROUP) {
            if (resultCode == RESULT_OK) {

                //String newName = intent.getStringExtra("newName");
                String newName = data.getStringExtra("newName");
                getSupportActionBar().setTitle(newName);

            }
        }



    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent();
        i.putExtra(Constant.ACTIVITYUSER,user);
        //here send back the the suer to redraw in main
        finish();

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
                if (util.userHasDebits(group, user)==false){
                        util.deleteUserFromGroup(group, user, Integer.parseInt(getIntent().getStringExtra(Constant.ACTIVITYPOSITION)));
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





    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.purchase_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.modify_purchase:
                if(group.getPurchases().get(info.position).getAuthor_id().equals(user.getUid())){
                    Intent i = new Intent(this, ExpenseInput.class);
                    i.putExtra(Constant.ACTIVITYGROUP, group);
                    i.putExtra(Constant.ACTIVITYUSER, user);
                    i.putExtra(Constant.ACTIVITYPURCHASE, group.getPurchases().get(info.position));
                    startActivityForResult(i,1);
                }
                else{
                    onlyAuthorDialogBox();
            }

                return true;
            case R.id.delete_purchase:

                if(group.getPurchases().get(info.position).getAuthor_id().equals(user.getUid())){

                    HashMap<String, String> owers = new HashMap<>();
                    for (PurchaseContributor pc : group.getPurchases().get(info.position).getContributors()){
                        if (!pc.getPayed()){
                            owers.put(pc.getUser_name(),Double.toString(pc.getAmount()));
                        }
                    }

                    if(owers.size()>0){
                        String mex = getResources().getString(R.string.delete_expense);
                        for (String key : owers.keySet()){
                            mex += key + " " + getResources().getString(R.string.owes_you_small) + " " + owers.get(key) + "€\n";
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivityExpense.this);
                        builder.setMessage(mex).setTitle(getString(R.string.purch_mod_del_title));
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deletePurchase(group, group.getPurchases().get(info.position).getPurchase_id());
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                return;
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                    else {
                        String mex = getResources().getString(R.string.delete_expense);
                        AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivityExpense.this);
                        builder.setMessage(mex).setTitle(getString(R.string.purch_mod_del_title));
                        builder.setMessage(mex).setTitle(getString(R.string.purch_mod_del_title));
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deletePurchase(group, group.getPurchases().get(info.position).getPurchase_id());
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                return;
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                }
                else{
                    onlyAuthorDialogBox();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onlyAuthorDialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivityExpense.this);
        builder.setMessage(getString(R.string.purch_mod_err_text)).setTitle(getString(R.string.purch_mod_err_title));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    public void deletePurchase(Group group, String pid){

        FirebaseDatabase.getInstance().getReference(Constant.REFERENCEGROUPS)
                .child(group.getId())
                .child(Constant.REFERENCEGROUPSPURCHASE)
                .child(pid).removeValue();

        Notification notification = new Notification();
        notification.setAuthorName(user.getName());
        notification.setAuthorId(user.getUid());
        notification.setEventType(Constant.PUSHDELETEEXPENSE);
        notification.setGroupName(group.getName());
        notification.setGroupId(group.getId());
        notification.setId(group.getNumeric_id());

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference users = database.getReference(Constant.REFERENCEUSERS);

        // SEND NOTIFICATION
        for (GroupMember groupMember: group.getGroupMembers()){
            if (!groupMember.getUser_id().equals(user.getUid())){
                users.child(groupMember.getUser_id())
                        .child(Constant.PUSH)
                        .push()
                        .setValue(notification);
                SystemClock.sleep(20);
            }
        }
        // END NOTIFICATION
    }


}


