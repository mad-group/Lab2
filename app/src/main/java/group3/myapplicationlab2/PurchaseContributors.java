package group3.myapplicationlab2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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



        group = (Group) getIntent().getSerializableExtra("group");
        user = (User) getIntent().getSerializableExtra("user");
        purchase = (Purchase) getIntent().getSerializableExtra("purchase");

        setTitle(purchase.getCausal());

        author = (TextView) findViewById(R.id.scroll_tv_author);
        author.setText(purchase.getUser_name());

        totalAmount = (TextView) findViewById(R.id.scroll_tv_amount);
        totalAmount.setText(new DecimalFormat("##.##").format(purchase.getTotalAmount()) + "€");

        date = (TextView) findViewById(R.id.scroll_tv_date);
        Date d = new Date(purchase.getDateMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        date.setText(formatter.format(d).toString());

        pcList = purchase.getContributors();



        for (int i = 0; i< pcList.size(); i++){
            drawNewCotnributor(pcList.get(i));
        }

    }

    private void drawNewCotnributor(PurchaseContributor pc_){
        final PurchaseContributor pc = pc_;
        if (!pc.getUser_id().equals(purchase.getAuthorName())) {
            //Log.d("debug", "pc_id: " + pc.getUser_id() + "p_auth_id: "+purchase.getAuthorName());
            View linearLayout = findViewById(R.id.ll_scroll_3);
            DecimalFormat df = new DecimalFormat("##.##");
            final TextView tv = new TextView(this);
            if (!pc.getPayed())
                tv.setText(pc.getUser_name() +" "+getString(R.string.owes_to) +" "+purchase.getUser_name() + " " +
                        df.format(pc.getAmount()) + "€");
            else
                tv.setText(pc.getUser_name() +" "+ getString(R.string.payed_to)+ " " + purchase.getUser_name() + " "
                        + df.format(pc.getAmount()) + "€");
            tv.setTextSize((float) 25);
            tv.setTextColor(Color.parseColor("#000000"));
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
                    if (user.getUid().equals(purchase.getAuthorName())) {
                        //Log.d("Debug","dialog p_author_id" +  purchase.getAuthorName());
                        String text = pc.getUser_name() + " is paying to you " + pc.getAmount() + "€?";
                        drawLeavingDialogBox("Debt extinguishing", text, pc);

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
                        .child(pc.getContributor_id()).child("payed").setValue("true");

                int index =0;
                int falseCounter=0;
                int authorIndex =0;
                String authorContributorId="";
                for (PurchaseContributor element : pcList){
                    Log.d("debug", "nel for");
                    if (element.getPayed()==false){

                        falseCounter = falseCounter +1;
                        authorIndex = index;
                        authorContributorId = element.getContributor_id();
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
                }


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
    }
}
