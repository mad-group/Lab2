
package group3.myapplicationlab2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.ArrayList;

/**
 * Created by gaeta on 06/04/2017.
 */

public class ExpenseAdapter extends ArrayAdapter<Purchase> {

    private HashMap<String, Bitmap> images  = new HashMap<>();
    private User user;
    //private TextView expense;
    //private TextView expDate;
    //private TextView expAmount;
    private ImageView expViewLeft;
    private ImageView expViewRight;
    private final Util util = new Util(getContext());

    public ExpenseAdapter(Context context, ArrayList<Purchase> expenses, User user) {
        super(context, 0, expenses);
        this.user = user;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Purchase purchase = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.expense_item, parent, false);
        }
        TextView expense = (TextView) convertView.findViewById(R.id.expense_id);
        TextView expDate = (TextView) convertView.findViewById(R.id.expense_date);
        TextView expAmount = (TextView) convertView.findViewById(R.id.expense_amount);
        //expViewRight = (ImageView) convertView.findViewById(R.id.imageViewRight);
        //expViewLeft = (ImageView) convertView.findViewById(R.id.imageViewLeft);

        //expViewLeft.setVisibility(View.GONE);
        //setUserImage(expViewRight, purchase, this.images);
        expense.setText(purchase.getCausal());
        expDate.setText(purchase.getDate());
        expAmount.setText(String.valueOf(purchase.getTotalAmount()) + " \u20ac");
        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
        //        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //params.gravity = Gravity.RIGHT;
        //expense.setLayoutParams(params);
        //expDate.setLayoutParams(params);
        //expAmount.setLayoutParams(params);

/*        if (user.getUid().equals(purchase.getAuthor_id()) ){
            //if the author of the purchase is the current user -> RIGHT
            expViewLeft.setVisibility(View.GONE);
            setUserImage(expViewRight, purchase, this.images);
            expense.setText(purchase.getCausal());
            expDate.setText(purchase.getDate());
            expAmount.setText(String.valueOf(purchase.getTotalAmount()) + " \u20ac");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.RIGHT;
            expense.setLayoutParams(params);
            expDate.setLayoutParams(params);
            expAmount.setLayoutParams(params);

        }
        else{

            expViewRight.setVisibility(View.GONE);
            setUserImage(expViewLeft, purchase, this.images);
            expense.setText(purchase.getCausal());
            expDate.setText(purchase.getDate());
            expAmount.setText(String.valueOf(purchase.getTotalAmount()) + " \u20ac");
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.LEFT;
            expense.setLayoutParams(params);
            expDate.setLayoutParams(params);
            expAmount.setLayoutParams(params);

        }*/

        return convertView;
    }

    private void setUserImage(final ImageView expView, final Purchase purchase, final HashMap<String, Bitmap> images){

        Bitmap image = images.get(purchase.getAuthor_id());
        expView.setImageBitmap(util.getCroppedBitmap(image,200,200));
    }

    public void setImages(HashMap<String,Bitmap> images) {
        this.images = images;
    }
}
