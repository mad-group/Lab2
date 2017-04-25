package group3.myapplicationlab2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.text.DateFormatSymbols;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.*;

public class ExpenseInput extends AppCompatActivity {
    Context context;
    Uri path_image = null;
    Calendar myCalendar = Calendar.getInstance();
    private int year, month, day;
    private EditText dateField;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Bitmap mImageBitmap;
    private ImageView mImageView;
    static int test = 0;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_input);

        //print current date as default value in Date editText
        dateField = (EditText) findViewById(R.id.ie_tv_date);

        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);

        mImageView = (ImageView) findViewById(R.id.ie_iv_from_camera);
        showDate(year, month, day);

    }

    public void saveExpense(View v) {
        final EditText authorField = (EditText) findViewById(R.id.ie_et_author);
        final EditText expenseField = (EditText) findViewById(R.id.ie_et_expense);
        final EditText amountField = (EditText) findViewById(R.id.ie_et_amount);

        String author = authorField.getText().toString();
        String expense = expenseField.getText().toString();
        String amount = amountField.getText().toString();
        String date = dateField.getText().toString();


        Date myd = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        try{
            myd = formatter.parse(date);
        }
        catch(ParseException | NullPointerException e){
            e.printStackTrace();
        }

        boolean allOk = true;
        if(author == null || author.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.miss_author, Toast.LENGTH_SHORT).show();
            allOk = false;
        }
        if(expense == null || expense.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.miss_amount, Toast.LENGTH_SHORT).show();
            allOk = false;
        }
        if(amount == null || amount.isEmpty()){
            Toast.makeText(getApplicationContext(), R.string.miss_expense, Toast.LENGTH_SHORT).show();
            allOk = false;
        }

        if (allOk == true){
            context = v.getContext();
            Intent i = new Intent();
            i.putExtra("author", author);
            i.putExtra("expense", expense);
            i.putExtra("amount",amount);
            i.putExtra("date", myd.getTime());
            if (this.path_image == null)
                i.putExtra("filepath", "nopath");
            else
                i.putExtra("filepath", this.path_image.getPath());
            setResult(RESULT_OK, i);
            TextView tv = (TextView) findViewById(R.id.debug_tv1);
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
            DatePickerDialog dp =  new DatePickerDialog(this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT,myDateListener, year, month, day);
            //dp.getWindow().setFeatureDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
            return dp;
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

    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type) {
        /*pointer to the dir "money tarcker". if it does not exits, it creates*/
        boolean res=true;
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MoneyTracker");
        if (mediaStorageDir.exists() == false){
            res=mediaStorageDir.mkdirs();
        }
        if (res == false){
            return null;
        }

        /*adds a tumestamp and a type to the new file*/
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }
    ;

    public void takeImage(View v){
        //check if device has avaiable phocamera
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            Toast.makeText(getApplicationContext(), R.string.has_not_camera, Toast.LENGTH_LONG).show();
            return;
        }

        /*if it hase, start the new intent, which refers to "MediaStore.ACTION_IMAGE_CAPUTRE"*/
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager())!=null){
            File photoFile = null;
            photoFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            //Toast.makeText(getApplicationContext(), Uri.fromFile(photoFile).toString(), Toast.LENGTH_LONG).show();

            //continue only if the file is correctly created
            if (photoFile!=null){
                /*save the photo in the path WITHOUT TO STRING*/
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                TextView tv = (TextView) findViewById(R.id.debug_tv1);
                this.path_image =  Uri.fromFile(photoFile);
                //tv.setText( this.path_image.toString());
                startActivityForResult(cameraIntent,REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            TextView tv = (TextView) findViewById(R.id.debug_tv1);
            tv.setText(this.path_image.toString());

            File imgFile = new File(this.path_image.getPath());
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ImageView myImage = (ImageView) findViewById(R.id.ie_iv_from_camera);
                myImage.setImageBitmap(myBitmap);
            }
        }
    }

}

