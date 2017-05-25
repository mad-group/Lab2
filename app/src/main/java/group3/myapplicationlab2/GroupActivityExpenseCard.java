package group3.myapplicationlab2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class GroupActivityExpenseCard extends AppCompatActivity {

    RecyclerView recyclerView;
    //RecyclerView.LayoutManager layoutManager;
    RecyclerAdapter adapter;
    ArrayList<Purchase> list = new ArrayList<Purchase>();
    private Group group;
    private User user;
    private String gid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_expense_card);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*gid = getIntent().getStringExtra("group_id");
        user = (User)getIntent().getSerializableExtra("user");*/
        adapter = new RecyclerAdapter(list);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        adapter = new RecyclerAdapter();
        recyclerView.setAdapter(adapter);
        Log.d("Activity", "Dentro onCreate activity recycler");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Test", "Button clicked");
                /*Intent i = new Intent(GroupActivityExpenseCard.this, ExpenseInput.class);
                group.setId(getIntent().getStringExtra("group_id"));
                i.putExtra("group", group);
                i.putExtra("user", user);
                i.putExtra("list_pos", getIntent().getStringExtra("list_pos"));

                Log.d("Debug", "pos: " + getIntent().getStringExtra("list_pos") +
                        " group_id: " + getIntent().getStringExtra("group_id"));
                startActivityForResult(i,1);*/
            }
        });
    }

}
