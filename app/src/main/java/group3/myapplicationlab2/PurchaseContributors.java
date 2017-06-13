package group3.myapplicationlab2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.IMediaControllerCallback;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.security.AccessController.getContext;

public class PurchaseContributors extends AppCompatActivity {
    private Group group;
    private User user;
    private Purchase purchase;

    private TextView author;
    private TextView totalAmount;
    private TextView date;

    private ListView lv;
    private ScrollPurchaseAdapter scrollPurchaseAdapter;

    private DatabaseReference mGroupReference =  FirebaseDatabase.getInstance().getReference("Groups");

    private List<PurchaseContributor> pcList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase_contributors);
        NestedScrollView nsv = (NestedScrollView)findViewById(R.id.nested_scrollview);
        nsv.setSmoothScrollingEnabled(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.scroll_toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getIntent().getStringExtra("group_name"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        group = (Group) getIntent().getSerializableExtra("group");
        user = (User) getIntent().getSerializableExtra("user");
        purchase = (Purchase) getIntent().getSerializableExtra("purchase");


        if(!purchase.getPathImage().equals("nopath")){

            CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
            Bitmap myBitmap = BitmapFactory.decodeFile(purchase.getPathImage());
            Drawable checkImage = new BitmapDrawable(myBitmap);
            collapsingToolbar.setBackground(checkImage);
        }

        if(!purchase.getEncodedString().equals("nostring")) {
            String encodedImage = purchase.getEncodedString();
            byte[] decodedImage = Base64.decode(encodedImage, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(decodedImage, 0, decodedImage.length);
            CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
 /*           Bitmap myBitmap = BitmapFactory.decodeFile(purchase.getPathImage());*/
            Drawable checkImage = new BitmapDrawable(image);
            collapsingToolbar.setBackground(checkImage);
            try {
                FileOutputStream fos = new FileOutputStream(purchase.getPathImage());
                image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (FileNotFoundException e) {
                Log.d("ERROR", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("Error", "Error accessing file: " + e.getMessage());
            }
        }
        toolbar.setTitle(purchase.getCausal());



        author = (TextView) findViewById(R.id.scroll_tv_author);
        author.setText(purchase.getUser_name());

        totalAmount = (TextView) findViewById(R.id.scroll_tv_amount);
        totalAmount.setText(new DecimalFormat("##.##").format(purchase.getTotalAmount()) + "€");

        date = (TextView) findViewById(R.id.scroll_tv_date);
        Date d = new Date(purchase.getDateMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        date.setText(formatter.format(d).toString());

        pcList = purchase.getContributors();
        //Toast.makeText(getApplicationContext(), "aaa "+pcList.size(), Toast.LENGTH_SHORT).show();

        for (int i = 0; i< purchase.getContributors().size(); i++){
            drawNewCotnributor(purchase.getContributors().get(i));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.purchase_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.modify_purchase) {

            if(purchase.getAuthor_id().equals(user.getUid())){
                Intent i = new Intent(this, ExpenseInput.class);
                i.putExtra(Constant.ACTIVITYGROUP, group);
                i.putExtra(Constant.ACTIVITYUSER, user);
                i.putExtra(Constant.ACTIVITYPURCHASE, purchase);
                startActivityForResult(i,1);
            }
            else{
                onlyAuthorDialogBox(getString(R.string.purch_mod_err_title), getString(R.string.purch_mod_err_text));
            }

            return true;

        }

        if (id == R.id.delete_purchase){

            if(purchase.getAuthor_id().equals(user.getUid())){

                HashMap<String, Double> owers = new HashMap<>();
                for (PurchaseContributor pc : purchase.getContributors()){
                    if (!pc.getPayed()){
                        owers.put(pc.getUser_name(),pc.getAmount());
                    }
                }

                if(owers.size()>0){
                    DecimalFormat df = new DecimalFormat("##.##");
                    String mex = getResources().getString(R.string.delete_expense);
                    for (String key : owers.keySet()){
                        mex += key + " " + getResources().getString(R.string.owes_you_small) + " " + df.format(owers.get(key)) + "€\n";
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseContributors.this);
                    builder.setMessage(mex).setTitle(getString(R.string.purch_mod_del_title));
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deletePurchase(group, purchase.getPurchase_id());
                            finish();
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseContributors.this);
                    builder.setMessage(mex).setTitle(getString(R.string.purch_mod_del_title));
                    builder.setMessage(mex).setTitle(getString(R.string.purch_mod_del_title));
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            deletePurchase(group, purchase.getPurchase_id());
                            finish();
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
                onlyAuthorDialogBox(getString(R.string.purch_del_err_title), getString(R.string.purch_del_err_text));
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    public void onlyAuthorDialogBox(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(PurchaseContributors.this);
        builder.setMessage(message).setTitle(title);
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

    private void drawNewCotnributor(PurchaseContributor pc_){
        final PurchaseContributor pc = pc_;
        if (!pc.getUser_id().equals(purchase.getAuthor_id())) {
            //Log.d("debug", "pc_id: " + pc.getUser_id() + "p_auth_id: "+purchase.getAuthorName());
            View linearLayout = findViewById(R.id.ll_scroll_3);
            DecimalFormat df = new DecimalFormat("##.##");
            final TextView tv = new TextView(this);
            if (!pc.getPayed()) {
                tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.btn_logut_bg));
                tv.setText(pc.getUser_name() + " " + getString(R.string.owes_to) + " " + purchase.getUser_name() + " " +
                        df.format(pc.getAmount()) + "€");
            }
            else {
                tv.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                tv.setText(pc.getUser_name() + " " + getString(R.string.payed_to) + " " + purchase.getUser_name() + " " +
                        df.format(pc.getAmount()) + "€");


            }
            tv.setTextSize((float) 25);
            //tv.setTextColor(Color.parseColor("#000000"));
            tv.setClickable(true);
            int[] attrs = new int[]{android.R.attr.selectableItemBackground /* index 0 */};
            TypedArray ta = obtainStyledAttributes(attrs);
            Drawable drawableFromTheme = ta.getDrawable(0);
            ta.recycle();
            tv.setBackground(drawableFromTheme);
            tv.setPadding(0, 10, 0, 10);

            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            llp.setMargins((int) getResources().getDimension(R.dimen.text_margin), 0, 0, 0);
            //llp.setMargins(16,0,0,0);
            tv.setLayoutParams(llp);


            ((LinearLayout) linearLayout).addView(tv);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user.getUid().equals(purchase.getAuthor_id()) &&
                            tv.getText().toString().contains(getResources().getString(R.string.owes_to))) {
                        //Log.d("Debug","dialog p_author_id" +  purchase.getAuthorName());
                        DecimalFormat df = new DecimalFormat("##.##");
                        String text = pc.getUser_name() + getResources().getString(R.string.ispaying_you) +
                                df.format(pc.getAmount()) + "€?";
                        drawLeavingDialogBox(getResources().getString(R.string.extinguish_debt), text, pc);

                    }

                }
            });
        }
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent();
        i.putExtra("pcList",(Serializable)pcList);
        i.putExtra("pos", getIntent().getIntExtra("pos",-1));
        setResult(RESULT_OK,i);
        finish();
    }

    private void drawLeavingDialogBox(String title, String text, PurchaseContributor pc_) {
        final PurchaseContributor pc = pc_;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text).setTitle(title);
        builder.setNegativeButton(getString(R.string.no),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        builder.setPositiveButton(getString(R.string.yes),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                pc.setPayed(true);
               // Log.d("debug", "--->" + group.getId()+" ----- "+purchase.getPurchase_id() + " ---- " +pc.getContributor_id() );
                mGroupReference
                        .child(getIntent().getStringExtra("group_id"))
                        .child("purchases")
                        .child(purchase.getPurchase_id())
                        .child("contributors")
                        .child(pc.getUser_id()).child("payed").setValue("true");

                /*int index =0;
                int falseCounter=0;
                int authorIndex =0;
                String authorContributorId="";
                for (PurchaseContributor element : pcList){
                    Log.d("debug", "nel for");
                    if (element.getPayed()==false){

                        falseCounter = falseCounter +1;
                        authorIndex = index;
                        authorContributorId = element.getUser_id();
                    }
                    index = index+1;
                }

                Log.d("debug", "fc: " + falseCounter);
                Log.d("debug", "ai: " + authorIndex);
                Log.d("debug", "aci: " + authorContributorId);

                if (falseCounter == 1) {
                    Log.d("debug", "nellif");
                    PurchaseContributor pcAuthor = pcList.remove(authorIndex);
                    pc.setPayed(true);
                    pcList.add(pcAuthor);

                    mGroupReference
                            .child(getIntent().getStringExtra("group_id"))
                            .child("purchases")
                            .child(purchase.getPurchase_id())
                            .child("contributors")
                            .child(authorContributorId).child("payed").setValue("true");
                }*/


                //method to clean the textview
                View linearLayout = findViewById(R.id.ll_scroll_3);
                if(((LinearLayout) linearLayout).getChildCount() > 0)
                    ((LinearLayout) linearLayout).removeAllViews();
                
                for (int i = 0; i< pcList.size(); i++){
                        drawNewCotnributor(pcList.get(i));
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        dialog.getButton(dialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }
}
