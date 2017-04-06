package group3.myapplicationlab2;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.*;
import java.util.ArrayList;

/**
 * Created by gaeta on 06/04/2017.
 */

public class ExpenseAdapter extends ArrayAdapter<Purchase> {

    public ExpenseAdapter(Context context, ArrayList<Purchase> expenses) {super(context, 0, expenses);}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Purchase purchase = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.expense_item, parent, false);
        }
        TextView expAuthor = (TextView) convertView.findViewById(R.id.expense_author);
        TextView expense = (TextView) convertView.findViewById(R.id.expense_id);
        TextView expAmount = (TextView) convertView.findViewById(R.id.expense_amount);

        // Qui dobbiamo prendere gli inserimenti dell'utente dalla nuova activity.
        expAuthor.setText(purchase.getAuthorAmount());
        expAmount.setText(String.valueOf(purchase.getTotalAmount()));
        expense.setText(purchase.getCausal());


        return convertView;
    }
}