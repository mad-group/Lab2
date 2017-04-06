package group3.myapplicationlab2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExpenseInput extends AppCompatActivity {
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_input);





    }

    public void saveExpense(View v) {
        //tv.setText("mr robot");

        final EditText authorField = (EditText) findViewById(R.id.ie_et_author);
        final EditText expenseField = (EditText) findViewById(R.id.ie_et_expense);
        final EditText amountField = (EditText) findViewById(R.id.ie_et_amount);
        final EditText dateField = (EditText) findViewById(R.id.ie_et_date);

        String author = authorField.getText().toString();
        String expense = expenseField.getText().toString();
        String amount = amountField.getText().toString();
        String date = dateField.getText().toString();
/*        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Date parsedDate = null;
        try {
            parsedDate = format.parse(dateField.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());*/



        context = v.getContext();

        Intent i = new Intent();
        i.putExtra("author", author);
        i.putExtra("expense", expense);
        i.putExtra("amount",amount);
        i.putExtra("date", date);
        setResult(RESULT_OK, i);
        finish();


/*        new AlertDialog.Builder(context)
                .setTitle("Delete entry")
                .setMessage(author+"\n"+expense+"\n"+String.valueOf(amount)+"\n")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();*/

    }
}
