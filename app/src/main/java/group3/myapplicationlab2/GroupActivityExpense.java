package group3.myapplicationlab2;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class GroupActivityExpense extends AppCompatActivity {

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_expense);

        final int passedVar = getIntent().getIntExtra("groupNumber", 0);
        Log.d("Debug", String.valueOf(passedVar));

        //String[] spese = {"Expense 1", "Expense 2", "Expense 3"};
        ArrayList<Purchase> spese = new ArrayList<Purchase>();
        final ExpenseAdapter expenseAdapter = new ExpenseAdapter(this, spese);

        ListView listView = (ListView) findViewById(R.id.expense_list);
        listView.setAdapter(expenseAdapter);
        expenseAdapter.clear();
        Purchase newPurchase = new Purchase("Gaetano", 50, "Compleanno Michele");
        //Unica spesa aggiunta alle spese:
        expenseAdapter.add(newPurchase);

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // QUI SI DEVE ISTANZIARE L'OGGETTO newInsert CON I PARAMETRI PRESI DALL'INPUT UTENTE

                Purchase newInsert = new Purchase("Ciccio", 12, "Motivo sconosciuto");
                expenseAdapter.add(newInsert);
                expenseAdapter.notifyDataSetChanged();

                //String newItem = "New Expense";
                //arrayList.add(newItem);
                //adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.group_activity, menu);
        return true;
    }

}
