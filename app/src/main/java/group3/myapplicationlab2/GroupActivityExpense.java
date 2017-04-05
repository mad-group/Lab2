package group3.myapplicationlab2;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class GroupActivityExpense extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_expense);

        final int passedVar = getIntent().getIntExtra("groupNumber", 0);
        Log.d("Debug", String.valueOf(passedVar));

        ListView list;
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
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
