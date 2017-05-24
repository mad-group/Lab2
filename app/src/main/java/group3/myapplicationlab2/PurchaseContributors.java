package group3.myapplicationlab2;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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

        Map<String, Object> map = new HashMap<>();
        map.put("aaa", Float.parseFloat("1"));
        map.put("bbb", Float.parseFloat("2"));
        map.put("bbs", Float.parseFloat("3"));
        map.put("bab", Float.parseFloat("4"));
        map.put("abb", Float.parseFloat("5"));
//        Log.d("Debug", "dim " + purchase.getContributors().size());
        scrollPurchaseAdapter = new ScrollPurchaseAdapter(PurchaseContributors.this, map);
/*        lv.setAdapter(scrollPurchaseAdapter);*/


        for (int i = 0; i< 150; i++){
            drawNewCotnributor(map);
        }












/*        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        })*/;
    }

    public void onClickTest(){
        Log.d("debug", "clicked");

    }

    private void drawNewCotnributor(Map<String,Object> map){
        View linearLayout = findViewById(R.id.ll_scroll_2);
        TextView tv = new TextView(this);
        tv.setText("key: aaa cercasi - value: " + map.get("aaa"));
        tv.setTextSize((float)25);
        tv.setTextColor(Color.parseColor("#000000"));
        tv.setClickable(true);
        TypedValue outValue = new TypedValue();
        int[] attrs = new int[] { android.R.attr.selectableItemBackground /* index 0 */};
        TypedArray ta = obtainStyledAttributes(attrs);
        Drawable drawableFromTheme = ta.getDrawable(0 /* index */);
        ta.recycle();
        tv.setBackground(drawableFromTheme);
        tv.setPadding(0,10,0,10);

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        llp.setMargins((int)getResources().getDimension(R.dimen.text_margin),0,0,0);
        //llp.setMargins(16,0,0,0);
        tv.setLayoutParams(llp);


        ((LinearLayout) linearLayout).addView(tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Debug", "test clicked yeee");
            }
        });
    }
}
