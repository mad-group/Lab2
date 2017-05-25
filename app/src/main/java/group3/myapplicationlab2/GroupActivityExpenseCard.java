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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GroupActivityExpenseCard extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerAdapter adapter;
    ArrayList<Purchase> list = new ArrayList<>();
    private Group group;
    private User user;
    private String gid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_expense_card);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        /*final DatabaseReference mGroupReference =  FirebaseDatabase.getInstance()
                .getReference("Groups")
                .child(gid);


        final ValueEventListener GroupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null){

                    Map<String, Object> objectMap = (HashMap<String, Object>)dataSnapshot.getValue();
                    group = new Group();
                    group.GroupConstructor(objectMap);
                    Collections.sort(group.getPurchases());
                    Collections.reverse(group.getPurchases());
                    list.addAll(group.getPurchases());
                    //adapter.addAll(group.getPurchases());
                    //paintListViewBackground();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };

        mGroupReference.addListenerForSingleValueEvent(GroupListener); */
        //ArrayList<Purchase> provaLista = new ArrayList<Purchase>();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // Here I fill the adapter with the empty list:
        adapter = new RecyclerAdapter(list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        addPurchaseStatic();
        addPurchaseStatic();
        addPurchaseStatic();
        addPurchaseStatic();
        addPurchaseStatic();
        addPurchaseStatic();
        addPurchaseStatic();
        addPurchaseStatic();
        addPurchaseStatic();
    }

        /*Purchase spesa = new Purchase();
        spesa.setAuthorName("Tano");
        spesa.setTotalAmount(10);
        list.add(spesa);
        /*gid = getIntent().getStringExtra("group_id");
        user = (User)getIntent().getSerializableExtra("user");
        adapter = new RecyclerAdapter(list);
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
                Intent i = new Intent(GroupActivityExpenseCard.this, ExpenseInput.class);
                group.setId(getIntent().getStringExtra("group_id"));
                i.putExtra("group", group);
                i.putExtra("user", user);
                i.putExtra("list_pos", getIntent().getStringExtra("list_pos"));

                Log.d("Debug", "pos: " + getIntent().getStringExtra("list_pos") +
                        " group_id: " + getIntent().getStringExtra("group_id"));
                startActivityForResult(i,1);
            }
        });
    }*/

    private void addPurchaseStatic() {
        Purchase purc = new Purchase();
        purc.setAuthorName("Tano");
        purc.setTotalAmount(10);
        list.add(purc);
    }

}
