package group3.myapplicationlab2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GroupStats extends AppCompatActivity {

    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_stats);
        final Random r = new Random();

        String[] spese = {"Andrian", "Flavia", "Michele", "Gaetano", "Diletta"};
        arrayList = new ArrayList<>(Arrays.asList(spese));
        adapter = new ArrayAdapter<String>(this, R.layout.group_balance_item, arrayList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // There is nothing to convert --> I need to create extra view
                if (convertView==null) {
                    // Returns a view: inflate is used to populate a view
                    convertView = getLayoutInflater().inflate(R.layout.group_balance_item, parent, false);
                }

                TextView name = (TextView)convertView.findViewById(R.id.group_member_name);
                name.setText(getItem(position).toString());

                TextView debit_credit = (TextView)convertView.findViewById(R.id.expense_amount);
                int i1 = r.nextInt(2 + 5)-2;

                if (i1 > 0){
                    debit_credit.setText("+" + String.valueOf(i1)+" €");
                    debit_credit.setTextColor(Color.parseColor("#51931b"));
                }
                else if (i1 <0) {
                    debit_credit.setText(String.valueOf(i1)+" €");
                    debit_credit.setTextColor(Color.parseColor("#ff0000"));
                }
                else {
                    debit_credit.setText(String.valueOf(i1)+" €");
                    debit_credit.setTextColor(Color.parseColor("#000000"));
                }
                return convertView;
            }
        };

        final ListView list;
        list = (ListView)findViewById(R.id.group_balance);
        list.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

}
