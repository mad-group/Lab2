package group3.myapplicationlab2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Contacts;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.IntegerRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
    private EditText expenseField;
    private ImageView mImageView;
    private ListView lv;

    private static final int PICK_IMAGE_ID = 234;
    private File imageOutFile = null;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference(Constant.REFERENCEGROUPS);
    private DatabaseReference users = database.getReference(Constant.REFERENCEUSERS);

    private User user;
    private Group group;
    private MembersAdapter membersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_input);

        user = (User)getIntent().getSerializableExtra(Constant.ACTIVITYUSER);
        group = (Group)getIntent().getSerializableExtra(Constant.ACTIVITYGROUP);

        setTitle(group.getName() + " - New expense");

        dateField = (EditText) findViewById(R.id.ie_tv_date);
        expenseField = (EditText) findViewById(R.id.ie_et_expense);
        amountField = (EditText) findViewById(R.id.ie_et_amount);
        hideKeyboard(dateField);
        hideKeyboard(expenseField);
        hideKeyboard(amountField);

        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);

        mImageView = (ImageView) findViewById(R.id.ie_iv_from_camera);
        showDate(year, month, day);

        setEditTextAmountListener();

        lv = (ListView)findViewById(R.id.list_participants_expense);
        ArrayList<GroupMember> groupMembers = new ArrayList<>();
        membersAdapter = new MembersAdapter(ExpenseInput.this, groupMembers, 0);
        lv.setAdapter(membersAdapter);
        membersAdapter.addAll(group.getGroupMembers());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveExpense(view);
            }
        });

    }


    public void saveExpense(View v) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

        String author = user.getUid();
        String expense = expenseField.getText().toString().trim();
        String amount = amountField.getText().toString().trim();
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


        if (author == null || author.isEmpty()){
            allOk = false;
        }
        else if (expense == null || expense.isEmpty()){
            expenseField.setError(getResources().getString(R.string.miss_expense));
            expenseField.requestFocus();
            allOk = false;
        }
        else if (amount == null || amount.isEmpty()){
            amountField.setError(getResources().getString(R.string.miss_amount));
            amountField.requestFocus();
            allOk = false;
        }
        else if (sumParts() == 0){
            allOk = false;
        }
        else {
            allOk = true;
        }

        /*if(author == null || author.isEmpty()){
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

        if(sumParts()==0){
            allOk = false;
        }*/

        if (allOk){
            String group_id = group.getId();

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
            List<PurchaseContributor> l = createPurchaseContributorsList();


            Uri resultUri = Uri.parse(p.getPathImage());
            Bitmap expenseImageBitmap;
            String encodedExpenseImage;
            if (this.imageOutFile == null){
                p.setPathImage(Constant.IMAGENOPATH);

                p.setEncodedString(Constant.STRINGNOSTRING);

            }

            else{
                p.setPathImage(this.imageOutFile.getPath());
                //expenseImageBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(resultUri));
                expenseImageBitmap = BitmapFactory.decodeFile(p.getPathImage());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                expenseImageBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] byteArrayImage = baos.toByteArray();
                encodedExpenseImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                p.setEncodedString(encodedExpenseImage);
                //Log.d("Debug", "aaaaaaaaaaa " + encodedExpenseImage);
            }

            String pid = myRef.push().getKey();
            Long lastModify = System.currentTimeMillis();
            HashMap<String,Object> hm = new HashMap<>();
            hm.put(Constant.GROUPLASTMODIFY, lastModify);
            hm.put(Constant.GROUPSLASTEVENT, Constant.PUSHNEWEXPENSE);
            hm.put(Constant.GROUPSLASTAUTHOR, user.getUid());
            p.setPurchase_id(pid);
            myRef.child(group_id).child(Constant.REFERENCEGROUPSPURCHASE).child(pid).setValue(p);

            String key;

            for (int indexList=0; indexList<l.size(); indexList++){
                /*l.get(indexList).setContributor_id(user.getUid());*/
                myRef.child(group_id).child(Constant.REFERENCEGROUPSPURCHASE).child(pid).child(Constant.REFERENCEGROUPSCONTRIBUTORS).child(l.get(indexList).getUser_id()).setValue(l.get(indexList));
            }
            p.setContributors(l);


            myRef.child(group_id).child(Constant.GROUPLASTMODIFYTIMESTAMP).setValue(lastModify);
            users.child(user.getUid())
                    .child(Constant.REFERENCEGROUPSHASH)
                    .child(group.getId())
                    .updateChildren(hm);

            Notification notification = new Notification();
            notification.setAuthorName(user.getName());
            notification.setAuthorId(user.getUid());
            notification.setEventType(Constant.PUSHNEWEXPENSE);
            notification.setGroupName(group.getName());
            notification.setGroupId(group.getId());
            notification.setId(group.getNumeric_id());

            // SEND NOTIFICATION
            for (GroupMember groupMember: group.getGroupMembers()){
                if (!groupMember.getUser_id().equals(user.getUid())){
                    users.child(groupMember.getUser_id())
                            .child(Constant.PUSH)
                            .push()
                            .setValue(notification);
                    SystemClock.sleep(20);
                }
            }
            // END NOTIFICATION

            context = v.getContext();

            Intent i = new Intent();
            i.putExtra(Constant.GROUPNEWPURCHASE, p);
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

    private void setEditTextAmountListener(){
        amountField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                TextView tv;
                View v;
                float fraction;
                for (int i =0; i<lv.getCount(); i++){
                    v = lv.getChildAt(i);
                    tv = (TextView) v.findViewById(R.id.item_amount);
                    if(editable.toString().isEmpty()){
                        membersAdapter.setTotAmount((float)0);
                        tv.setText("0");
                    }
                    else {
                        //fraction = Float.parseFloat(editable.toString()) * Integer.parseInt(et.getText().toString())/ sumParts();
                        membersAdapter.setTotAmount(Float.parseFloat(editable.toString()));
                        fraction = Float.parseFloat(editable.toString()) / sumParts();
                        NumberFormat format = NumberFormat.getInstance(Locale.US);
                        //String floatString = Float.toString(fraction);
                        tv.setText(format.format(fraction));
                    }
                }

            }
        });
    }



    private int sumParts(){
        View v;
        EditText et;
        int parts = 0;
        for (int i=0; i<lv.getCount(); i++){
            v = lv.getChildAt(i);
            et = (EditText) v.findViewById(R.id.item_part);
            parts += Integer.parseInt(et.getText().toString());
        }
        return parts;
    }

    private List<PurchaseContributor> createPurchaseContributorsList() {
        List<PurchaseContributor> l = new ArrayList<>();
        TextView et_amount;
        TextView et_id;
        TextView et_userName;
        View vv;
        for (int ii = 0; ii < lv.getCount(); ii++) {
            vv = lv.getChildAt(ii);
            et_amount = (TextView) vv.findViewById(R.id.item_amount);
            et_id = (TextView) vv.findViewById(R.id.item_user_id);
            et_userName = (TextView) vv.findViewById(R.id.item_member);
            PurchaseContributor pc = new PurchaseContributor();
            pc.setUser_id(et_id.getText().toString().trim());
            pc.setUser_name(et_userName.getText().toString().trim());
            Double amount = Double.parseDouble(et_amount.getText().toString());
            pc.setAmount(amount);
            if (et_amount.getText().toString().equals("0")){
                pc.setPayed(true);
            }
            else{
                pc.setPayed(false);
            }
            l.add(pc);

        }
        return l;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void setLostFocus(View v){
        v.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus ) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

}

