package group3.myapplicationlab2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class GroupActivityExpenseWithCard extends AppCompatActivity {
    private List<Purchase> purchaseList = new ArrayList<>();
    private static final int PURCHASE_CONTRIBUTOR = 698;
    private RecyclerView recyclerView;
    private PurchaseAdapter myAdapter;
    Locale l = Locale.ENGLISH;
    private String gid;
    private Group group;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_espense_with_card);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gid = getIntent().getStringExtra("group_id");
        user = (User)getIntent().getSerializableExtra("user");

        final DatabaseReference mGroupReference =  FirebaseDatabase.getInstance()
                .getReference("Groups")
                .child(gid);

        // Here I am trying to compose the purchaseList with the existing expenses.
        final ValueEventListener GroupListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Map<String, Object> objectMap = (HashMap<String, Object>)dataSnapshot.getValue();
                    group = new Group();
                    group.GroupConstructor(objectMap);
                    Collections.sort(group.getPurchases());
                    Collections.reverse(group.getPurchases());

                    // Qui MI VEDE LA LISTA DELLE PURCHASE PIENA
                    purchaseList.addAll(group.getPurchases());
                    //expenseAdapter.addAll(group.getPurchases());
                    Log.d("Debug", "purchs dim onCreate " +  purchaseList.size());
                    //paintListViewBackground();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mGroupReference.addListenerForSingleValueEvent(GroupListener);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // Qui MI VEDE LA LISTA DELLE PURCHASE VUOTA
        myAdapter = new PurchaseAdapter(purchaseList);
        RecyclerView.LayoutManager myLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(myLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GroupActivityExpenseWithCard.this, ExpenseInput.class);
                group.setId(getIntent().getStringExtra("group_id"));
                i.putExtra("group", group);
                i.putExtra("user", user);
                i.putExtra("list_pos", getIntent().getStringExtra("list_pos"));

                Log.d("Debug", "pos: " + getIntent().getStringExtra("list_pos") +
                        " group_id: " + getIntent().getStringExtra("group_id"));
                startActivityForResult(i,1);
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });
        //registerListenerOnListView();
    }


    private void fakeMethod() {


        /*Purchase prova = new Purchase();
        prova.setAuthorName("Tano");
        prova.setTotalAmount(304);
        purchaseList.add(prova);*/
    }
}
