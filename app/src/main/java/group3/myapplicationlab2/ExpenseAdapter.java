package group3.myapplicationlab2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
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
        TextView expDate = (TextView) convertView.findViewById(R.id.expense_date);
        ImageView expView = (ImageView) convertView.findViewById(R.id.imageView2);

        // Qui dobbiamo prendere gli inserimenti dell'utente dalla nuova activity.
        expAuthor.setText(purchase.getUser_name());
        expAmount.setText(String.valueOf(purchase.getTotalAmount()) + " \u20ac");
        expense.setText(purchase.getCausal());
        expDate.setText(purchase.getDate());


        if (!purchase.getPathImage().equals("nopath")) {
            File imgFile = new File(purchase.getPathImage());
            if(imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                expView.setImageBitmap(myBitmap);
            }
            else {
                expView.setImageResource(R.drawable.ic_menu_gallery);
            }
        }
        else{
            expView.setImageResource(R.drawable.ic_menu_gallery);
        }


        return convertView;
    }
}
