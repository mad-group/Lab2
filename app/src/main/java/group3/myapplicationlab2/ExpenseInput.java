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
import android.provider.Contacts;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
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
    Calendar myCalendar = Calendar.getInstance();
    private int year, month, day;
    private EditText dateField;
    private EditText amountField;
    private ImageView mImageView;
    private ListView lv;

    private static final int PICK_IMAGE_ID = 234;
    private File imageOutFile = null;
    private String author_key;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("Groups");
    private DatabaseReference users = database.getReference("Users");

    private User user;
    private Group group;
    private MembersAdapter membersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_input);
        user = (User)getIntent().getSerializableExtra("user");
        group = (Group)getIntent().getSerializableExtra("group");

        setTitle(group.getName() + " - New expense");

        //print current date as default value in Date editText
        dateField = (EditText) findViewById(R.id.ie_tv_date);

        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);

        mImageView = (ImageView) findViewById(R.id.ie_iv_from_camera);
        showDate(year, month, day);

        author_key = user.getUid();

        setEditTextAmountListener();

        lv = (ListView)findViewById(R.id.list_participants_expense);
        ArrayList<GroupMember> groupMembers = new ArrayList<>();
        membersAdapter = new MembersAdapter(ExpenseInput.this, groupMembers);
        lv.setAdapter(membersAdapter);
        membersAdapter.addAll(group.getGroupMembers());




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveExpense(view);
            }
        });

    }


    public void saveExpense(View v) {


        Map<String,Object> map = createContributorsMap();
        for(String key : map.keySet()){
            Log.d("Debug", "key: " + key + " Price: " + map.get(key));
        }

        final EditText expenseField = (EditText) findViewById(R.id.ie_et_expense);

       // String author = authorField.getText().toString();
        String author = author_key;
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
            //Toast.makeText(getApplicationContext(), R.string.miss_author, Toast.LENGTH_SHORT).show();
/*            String err = getResources().getString(R.string.miss_author);
            author.setError(err);*/
            allOk = false;
        }
        if(expense == null || expense.isEmpty()){
            //Toast.makeText(getApplicationContext(), R.string.miss_amount, Toast.LENGTH_SHORT).show();
            String err = getResources().getString(R.string.miss_expense);
            expenseField.setError(err);
            allOk = false;
        }
        if(amount == null || amount.isEmpty()){
            String err = getResources().getString(R.string.miss_amount);
            amountField.setError(err);
            allOk = false;
        }



        if (allOk){
            String group_id = group.getId();
            Log.d("Debug", group_id);

            Purchase p = new Purchase();
            p.setAuthor_id(author);
            p.setAuthorName(author);
            p.setTotalAmount(Double.parseDouble(amount));
            p.setCausal(expense);
            p.setDateMillis(myd.getTime());
            p.setGroup_id(group_id);
            p.setLastModify(System.currentTimeMillis());
            p.setUser_name(user.getName());
            p.setAuthor_id(user.getUid());
            p.setContributors(createContributorsMap());

            Log.d("Debug", "dim " + p.getContributors().size());


            if (this.imageOutFile == null)
                p.setPathImage("nopath");
            else
                p.setPathImage(this.imageOutFile.getPath());

            String pid = myRef.push().getKey();
            Long lastModify = System.currentTimeMillis();
            HashMap<String,Object> hm = new HashMap<>();
            hm.put("lastModify", lastModify);
            myRef.child(group_id).child("purchases").child(pid).setValue(p);
            myRef.child(group_id).child("lastModifyTimeStamp").setValue(lastModify);
            users.child(user.getUid())
                    .child("groups")
                    .child(getIntent().getStringExtra("list_pos"))
                    .updateChildren(hm);


            context = v.getContext();
            Intent i = new Intent();
            Log.d("debug", getIntent().getStringExtra("list_pos"));
            i.putExtra("new_purchase", p);
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

    private void drawDialogBoxEqual(String title, float amount) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle(title);
        String str="";
        for (int i =0; i<group.getGroupMembers().size();i++){
            Log.d("Debug", "insideFor");
            str += group.getGroupMembers().get(i).getName() + "\t" + Float.toString(amount/group.getGroupMembers().size()) + "€\n";
        }
        builder.setMessage(str);
        builder.setPositiveButton(getString(R.string.ok),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setEditTextAmountListener(){
        amountField = (EditText) findViewById(R.id.ie_et_amount);
        amountField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                TextView et;
                View v;
                float fraction;
                for (int i =0; i<lv.getCount(); i++){
                    v = lv.getChildAt(i);
                    et = (TextView) v.findViewById(R.id.item_amount);
                    if(editable.toString().isEmpty()){
                        et.setText("0");
                    }
                    else {
                        fraction = Float.parseFloat(editable.toString()) / sumParts();
                        et.setText(new DecimalFormat("##.##").format(fraction));
                    }
                }

            }
        });
    }

    private int sumParts(){
        return group.getGroupMembers().size();
    }

    private Map<String, Object> createContributorsMap() {
        Map<String, Object> map = new HashMap<>();
        TextView et_amount;
        TextView et_id;
        View vv;
        for (int ii = 0; ii < lv.getCount(); ii++) {
            vv = lv.getChildAt(ii);
            et_amount = (TextView) vv.findViewById(R.id.item_amount);
            et_id = (TextView) vv.findViewById(R.id.item_user_id);
            map.put(et_id.getText().toString(), Double.parseDouble(et_amount.getText().toString()));

        }
        return map;
    }
}

