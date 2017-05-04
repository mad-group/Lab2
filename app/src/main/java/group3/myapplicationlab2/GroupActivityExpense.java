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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.GregorianCalendar;
import java.util.Locale;

public class GroupActivityExpense extends AppCompatActivity {

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    ExpenseAdapter expenseAdapter;
    Locale l = Locale.ENGLISH;
    private String gid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_expense);
        gid = getIntent().getStringExtra("group_id");
        final DatabaseReference mPurchaseReference =
                FirebaseDatabase.getInstance().getReference("Groups")
                        .child(gid)
                        .child("Purchases");


        //String[] spese = {"Expense 1", "Expense 2", "Expense 3"};
        ArrayList<Purchase> spese = new ArrayList<Purchase>();
        //final ExpenseAdapter expenseAdapter = new ExpenseAdapter(this, spese);
        expenseAdapter = new ExpenseAdapter(this, spese);

        ListView listView = (ListView) findViewById(R.id.expense_list);
        listView.setAdapter(expenseAdapter);


/*        Calendar c = new GregorianCalendar();
        c.set(Calendar.DAY_OF_MONTH,20);
        c.set(Calendar.MONTH, Calendar.AUGUST);
        c.set(Calendar.YEAR, 2016);
        Date d = c.getTime();*/

        //Toast.makeText(getApplicationContext(), String.valueOf(d.getTime()), Toast.LENGTH_SHORT).show();

/*        DateFormat df = new SimpleDateFormat("dd MMM yyyy",new Locale(Locale.getDefault().getDisplayLanguage()));
        String indate = df.format(d);*/

/*        Purchase newPurchase = new Purchase(Locale.ENGLISH + " " + Locale.ITALIAN,
                50,
                Locale.getDefault().getDisplayLanguage(),
                c.getTimeInMillis());
        newPurchase.setPathImage(null);
        //Unica spesa aggiunta alle spese:
        expenseAdapter.add(newPurchase);*/

        //arrayList = new ArrayList<>(Arrays.asList(spese));
        //      adapter = new ArrayAdapter<String>(this, R.layout.expense_item, arrayList){
/*            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // There is nothing to convert --> I need to create extra view
                if (convertView==null) {
                    // Returns a view: inflate is used to populate a view
                    convertView = getLayoutInflater().inflate(R.layout.expense_item, parent, false);
                }
                TextView tv = (TextView)convertView.findViewById(R.id.expense_id);
                tv.setText(getItem(position).toString());

                TextView author = (TextView) convertView.findViewById(R.id.expense_author);
                author.setText("Author " + (position+1) + " of group " + (passedVar + 1));

                TextView amount = (TextView) convertView.findViewById(R.id.expense_amount);
                amount.setText("Amount random");

                TextView date = (TextView) convertView.findViewById(R.id.expense_date);
                date.setText("Date random");

                return convertView;
            }*/
        //};

/*        final ListView list;
        list = (ListView)findViewById(R.id.expense_list);
        list.setAdapter(adapter);*/

        /*
        final ListView list;
        list = (ListView)findViewById(R.id.expense_list);

        list.setAdapter(new BaseAdapter() {

            String[] spese = {"Expense 1", "Expense 2", "Expense 3"};

            @Override
            public int getCount() {return spese.length;}

            @Override
            public Object getItem(int position) {return spese[position];}

            @Override
            public long getItemId(int position) {return 0;}

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // There is nothing to convert --> I need to create extra view
                if (convertView==null) {
                    // Returns a view: inflate is used to populate a view
                    convertView = getLayoutInflater().inflate(R.layout.expense_item, parent, false);
                }
                TextView tv = (TextView)convertView.findViewById(R.id.expense_id);
                tv.setText(getItem(position).toString());

                TextView author = (TextView) convertView.findViewById(R.id.expense_author);
                author.setText("Author " + (position+1) + " of group " + (passedVar + 1));

                TextView amount = (TextView) convertView.findViewById(R.id.expense_amount);
                amount.setText("Amount random");

                TextView date = (TextView) convertView.findViewById(R.id.expense_date);
                date.setText("Date random");

                return convertView;
            }

        });*/

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_exp);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(GroupActivityExpense.this, ExpenseInput.class);
                i.putExtra("group_id", getIntent().getStringExtra("group_id"));
                startActivityForResult(i,1);
            }
        });

        //listener on purchases
        final ValueEventListener purchaseListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                expenseAdapter.clear();
                for (DataSnapshot purSnapshot : dataSnapshot.getChildren()) {
                    //Getting the data from database snapshot
                    Purchase p = purSnapshot.getValue(Purchase.class);
                    expenseAdapter.add(p);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("Debug", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        mPurchaseReference.addValueEventListener(purchaseListener);
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
            Log.d("Debug", "Stats clicked");

            Intent i =new Intent(GroupActivityExpense.this, GroupStats.class);

            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
/*                String author = data.getStringExtra("author");
                String expense = data.getStringExtra("expense");
                String amount = data.getStringExtra("amount");
                Long date = data.getLongExtra("date", System.currentTimeMillis());*//*


                String author = "a";
                String expense = "b";
                String amount = "12";
                Long date = Long.getLong("0");
                Purchase newInsert = new Purchase(author, Float.parseFloat(amount),expense, date);
                if (data.getStringExtra("filepath") == "nopath"){
                    newInsert.setPathImage(null);
                }
                else{
                    newInsert.setPathImage(data.getStringExtra("filepath"));
                }

                //Toast.makeText(getApplicationContext(), newInsert.getPathImage().toString(), Toast.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), date, Toast.LENGTH_LONG).show();


                //Purchase newInsert = new Purchase("aaaa", 12,"bbbb");
                expenseAdapter.add(newInsert);
                expenseAdapter.notifyDataSetChanged();

*/

            }
        }
    }

}


