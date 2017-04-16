package group3.myapplicationlab2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.text.DateFormatSymbols;

import java.util.Date;
import java.util.GregorianCalendar;

public class ExpenseInput extends AppCompatActivity {
    Context context;
    Calendar myCalendar = Calendar.getInstance();
    private int year, month, day;
    private EditText dateField;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_input);

        //print current date as default value in Date editText
        dateField = (EditText) findViewById(R.id.ie_tv_date);

        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);

        //showDate(year, month, day);

    }

    public void saveExpense(View v) {
        //tv.setText("mr robot");

        final EditText authorField = (EditText) findViewById(R.id.ie_et_author);
        final EditText expenseField = (EditText) findViewById(R.id.ie_et_expense);
        final EditText amountField = (EditText) findViewById(R.id.ie_et_amount);

        String author = authorField.getText().toString();
        String expense = expenseField.getText().toString();
        String amount = amountField.getText().toString();
        String date = dateField.getText().toString();

        context = v.getContext();
        Intent i = new Intent();
        i.putExtra("author", author);
        i.putExtra("expense", expense);
        i.putExtra("amount",amount);
        i.putExtra("date", date);
        setResult(RESULT_OK, i);

        boolean allOk = true;
        if(author == null || author.isEmpty()){
            Toast.makeText(getApplicationContext(), "Set who does the expense.", Toast.LENGTH_SHORT).show();
            allOk = false;
        }
        if(expense == null || expense.isEmpty()){
            Toast.makeText(getApplicationContext(), "Set what has been bought.", Toast.LENGTH_SHORT).show();
            allOk = false;
        }
        if(amount == null || amount.isEmpty()){
            Toast.makeText(getApplicationContext(), "Set the expense amount.", Toast.LENGTH_SHORT).show();
            allOk = false;
        }
        if (allOk == true){
            finish();
        }


    }
    @SuppressWarnings("deprecation")
    public void dataPicker(View v){
        showDialog(999);
        //Toast.makeText(getApplicationContext(), "ca", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this, myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(arg1, arg2, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        String m = new DateFormatSymbols().getMonths()[month];
        dateField.setText(day + " " + m + " " + year);
    }

}
