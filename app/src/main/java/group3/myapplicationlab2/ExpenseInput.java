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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
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
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
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

    private static final int PICK_IMAGE_ID = 234;
    private File imageOutFile = null;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("Groups");


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
            String group_id = getIntent().getStringExtra("group_id");
            //Purchase p = new Purchase(author, Float.parseFloat(amount),expense, myd.getTime());
            Purchase p = new Purchase();
            p.setAuthorName(author);
            Log.d("Debug", p.getAuthorName());
            p.setTotalAmount(Float.parseFloat(amount));
            p.setCausal(expense);
            p.setDateMillis(myd.getTime());
            p.setGroup_id(group_id);

            if (this.imageOutFile == null)
                p.setPathImage("nopath");
            else
                p.setPathImage(this.imageOutFile.getPath());

            String pid = myRef.push().getKey();
            myRef.child(group_id).child("Purchases").child(pid).setValue(p);
            context = v.getContext();
            Intent i = new Intent();
/*            i.putExtra("author", author);
            i.putExtra("expense", expense);
            i.putExtra("amount",amount);
            i.putExtra("date", myd.getTime());*/
           // i.putExtra("filepath", p.getPathImage());
          //  i.putExtra("pid",pid);
            setResult(RESULT_OK, i);
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

    public void takeImage(View v){

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        else {
            Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
            startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                ImageView myImage = (ImageView) findViewById(R.id.ie_iv_from_camera);
                myImage.setImageBitmap(bitmap);

                File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MoneyTracker");
                mediaStorageDir.mkdirs();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_" + timeStamp + ".jpg");
                try {
                    FileOutputStream out = new FileOutputStream(mediaFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.imageOutFile = mediaFile;
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
                    startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
                } else {
                    // Permission Denied
                    Toast.makeText(ExpenseInput.this, R.string.camera_permission_denied, Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

/*    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            // Get Post object and use the values to update the UI
            Post post = dataSnapshot.getValue(Post.class);
            // ...
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            // ...
        }
    };*/

}

