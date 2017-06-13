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
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
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
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
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

import static group3.myapplicationlab2.Constant.PICK_IMAGE_ID;

public class ExpenseInput extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    Context context;
    Calendar myCalendar = Calendar.getInstance();
    private int year, month, day;
    private EditText dateField;
    private EditText amountField;
    private EditText expenseField;
    private ImageView picsImageView;

    private ListView lv;

    private static final int PICK_IMAGE_ID = 234;
    private File imageOutFile = null;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference(Constant.REFERENCEGROUPS);
    private DatabaseReference users = database.getReference(Constant.REFERENCEUSERS);

    private User user;
    private Group group;
    private MembersAdapter membersAdapter;
    private MembersAdapter3 membersAdapter3;
    private MembersBaseAdapter membersBaseAdapter;


    private int[] arrayParts;
    private ArrayList<PurchaseContributor> pcList;

    private Purchase recPurch;
    private Util util;

    private Switch mySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_input_2);

        user = (User)getIntent().getSerializableExtra(Constant.ACTIVITYUSER);
        group = (Group)getIntent().getSerializableExtra(Constant.ACTIVITYGROUP);
        util = new Util(getApplicationContext());

        //View parentRoot = findViewById(R.id.expense_input_parent);
        //View logo = findViewById(R.id.fab_save);
        //util.desappearViewOnSoftKeyboard(parentRoot, logo);

        setTitle(group.getName() + " - " + getResources().getString(R.string.new_expense));

        dateField = (EditText) findViewById(R.id.ie_tv_date);
        expenseField = (EditText) findViewById(R.id.ie_et_expense);
        amountField = (EditText) findViewById(R.id.ie_et_amount);
        picsImageView = (ImageView) findViewById(R.id.imageViewLeft);


        hideKeyboard(dateField);
        hideKeyboard(expenseField);
        hideKeyboard(amountField);

        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);

        showDate(year, month, day);

        lv = (ListView)findViewById(R.id.list_participants_expense);
        ArrayList<GroupMember> groupMembers = new ArrayList<>();

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

        pcList = new ArrayList<>();
        for (int i=0; i< group.getGroupMembers().size(); i++){
            PurchaseContributor pc = new PurchaseContributor();
            pc.setUser_id((group.getGroupMembers().get(i).getUser_id()));
            if (user.getUid().equals(group.getGroupMembers().get(i).getUser_id())){
                pc.setPayed(true);
            }
            else pc.setPayed(false);
            pc.setUser_name(group.getGroupMembers().get(i).getName());
            pc.setAmount(0);
            pc.setParts(1);
            pcList.add(pc);
        }

        final Purchase recPurch = (Purchase)getIntent().getSerializableExtra(Constant.ACTIVITYPURCHASE);

        if (recPurch==null){
            setTitle(group.getName() + " - " + getResources().getString(R.string.new_expense));


            membersAdapter3 = new MembersAdapter3(getApplicationContext(), pcList);
            membersBaseAdapter = new MembersBaseAdapter(getApplicationContext(), pcList);
            lv.setAdapter(membersAdapter3);

            mySwitch = (Switch) findViewById(R.id.switch1);
            mySwitch.setChecked(true);
            mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        membersAdapter3.recomputeAmounts();
                        lv.setAdapter(membersAdapter3);
                        mySwitch.setText(R.string.automatic_splitting);
                    }else{
                        lv.setAdapter(membersBaseAdapter);
                        mySwitch.setText(R.string.manual_splitting);
                    }

                }
            });

        }
        else{
            setTitle(recPurch.getCausal()+ " - " + getResources().getString(R.string.new_expense));
            dateField.setText(recPurch.getDate());
            expenseField.setText(recPurch.getCausal());
            final String amountString = Double.toString(recPurch.getTotalAmount());

            amountField.setText(amountString);

            membersAdapter3 = new MembersAdapter3(getApplicationContext(), pcList);
            membersBaseAdapter = new MembersBaseAdapter(getApplicationContext(), pcList);
            lv.setAdapter(membersAdapter3);

            mySwitch = (Switch) findViewById(R.id.switch1);
            mySwitch.setChecked(true);
            mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        membersAdapter3.recomputeAmounts();
                        lv.setAdapter(membersAdapter3);
                        mySwitch.setText(R.string.automatic_splitting);
                    }else{
                        lv.setAdapter(membersBaseAdapter);
                        mySwitch.setText(R.string.manual_splitting);
                    }

                }
            });

        }

        setEditTextAmountListener();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (recPurch==null) {
                    saveExpense(view, "");
                }
                else{
                    saveExpense(view, recPurch.getPurchase_id());
                }
            }
        });

    }


    public void saveExpense(View v, String purchase_id) {

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

        if(mySwitch.isChecked()){

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
            else if (numberParts() == 0){
                allOk = false;
                Toast.makeText(ExpenseInput.this, R.string.least_one_pay,Toast.LENGTH_SHORT).show();
            }
            else if(Math.abs(sumAmounts2() - Float.parseFloat(amount))>0.10){
                Toast.makeText(ExpenseInput.this, R.string.sum_partial,Toast.LENGTH_SHORT).show();
                allOk = false;
            }
            else {
                allOk = true;
            }

        }
        else {

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
            else if (numberPartsManual() == 0){
                allOk = false;
                Toast.makeText(ExpenseInput.this, R.string.least_one_pay,Toast.LENGTH_SHORT).show();
            }
            else if(Math.abs(sumAmountsManual() - Float.parseFloat(amount))>0.10){
                Toast.makeText(ExpenseInput.this, R.string.sum_partial,Toast.LENGTH_SHORT).show();
                allOk = false;
            }
            else {
                allOk = true;
            }

        }

        if (allOk){

            String group_id = group.getId();
            Purchase p = new Purchase();
            p.setAuthor_id(author);
            p.setAuthorName(user.getName());
            p.setTotalAmount(Double.parseDouble(amount));
            p.setCausal(expense);
            p.setDateMillis(myd.getTime());
            p.setGroup_id(group_id);
            p.setLastModify(System.currentTimeMillis());
            p.setUser_name(user.getName());
            p.setAuthor_id(user.getUid());

            List<PurchaseContributor> l = createPurchaseContributorsList2();

            Uri resultUri = Uri.parse(p.getPathImage());
            Bitmap expenseImageBitmap;
            String encodedExpenseImage;
            if (this.imageOutFile == null){
                p.setPathImage(Constant.IMAGENOPATH);

                p.setEncodedString(Constant.STRINGNOSTRING);

            }

            else{
                p.setPathImage(this.imageOutFile.getPath());
                expenseImageBitmap = BitmapFactory.decodeFile(p.getPathImage());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                expenseImageBitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                byte[] byteArrayImage = baos.toByteArray();
                encodedExpenseImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                p.setEncodedString(encodedExpenseImage);
            }

            if (purchase_id.isEmpty()) {
                String pid = myRef.push().getKey();
                Long lastModify = System.currentTimeMillis();
                HashMap<String,Object> hm = new HashMap<>();
                hm.put(Constant.GROUPLASTMODIFY, lastModify);
                hm.put(Constant.GROUPSLASTEVENT, Constant.PUSHNEWEXPENSE);
                hm.put(Constant.GROUPSLASTAUTHOR, user.getUid());
                p.setPurchase_id(pid);
                myRef.child(group_id).child(Constant.REFERENCEGROUPSPURCHASE).child(pid).setValue(p);


                for (int indexList=0; indexList<l.size(); indexList++){
                    myRef.child(group_id).child(Constant.REFERENCEGROUPSPURCHASE).child(pid).child(Constant.REFERENCEGROUPSCONTRIBUTORS).child(l.get(indexList).getUser_id()).setValue(l.get(indexList));
                }
                p.setContributors(l);


                myRef.child(group_id).child(Constant.GROUPLASTMODIFYTIMESTAMP).setValue(lastModify);
                users.child(user.getUid())
                        .child(Constant.REFERENCEGROUPSHASH)
                        .child(group.getId())
                        .updateChildren(hm);
                /*end insertions*/


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
            }
            else{
                String pid = purchase_id;
                Long lastModify = System.currentTimeMillis();
                HashMap<String,Object> hm = new HashMap<>();
                hm.put(Constant.GROUPLASTMODIFY, lastModify);
                hm.put(Constant.GROUPSLASTEVENT, Constant.PUSHNEWEXPENSE);
                hm.put(Constant.GROUPSLASTAUTHOR, user.getUid());
                p.setPurchase_id(pid);
                myRef.child(group_id).child(Constant.REFERENCEGROUPSPURCHASE).child(pid).setValue(p);

                for (int indexList=0; indexList<l.size(); indexList++){
                l.get(indexList).setContributor_id(user.getUid());
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
                notification.setEventType(Constant.PUSHMODIFYEXPENSE);
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
            }




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

        String [] toAskArray = util.asksPermissions(ExpenseInput.this);
        if (toAskArray!=null){
            ActivityCompat.requestPermissions(this,toAskArray,
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        else{
            Intent chooseImageIntent = ImagePicker.getPickImageIntent(this);
            startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                if (bitmap != null){
                    picsImageView.setBackground(null);
                    picsImageView.setImageBitmap(bitmap);
                    picsImageView.setVisibility(View.VISIBLE);
                }


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
                    this.imageOutFile = mediaFile;
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                util.checkGrants(ExpenseInput.this,grantResults);
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
                float newAmount;

                if (mySwitch.isChecked()){
                    if (editable.toString().equals("")){
                        newAmount = 0;
                        membersAdapter3.setTotalAmount(newAmount);
                    }
                    else{
                        if (Locale.getDefault().getLanguage().equals("en")) {
                            newAmount = Float.parseFloat(editable.toString());
                            membersAdapter3.setTotalAmount(newAmount);
                        }
                        else {
                            newAmount = Float.parseFloat(editable.toString());
                            membersAdapter3.setTotalAmount(newAmount);
                        }
                    }
                }
            }
        });
    }

    private int numberParts(){
        int parts = membersAdapter3.getTotalParts();
        return parts;
    }

    private int numberPartsManual(){
        return 1;
    }

    private float sumAmounts2(){
        return membersAdapter3.sumAmounts();
    }

    private float sumAmountsManual(){
        return membersBaseAdapter.sumAmounts();
    }

    public List<PurchaseContributor> createPurchaseContributorsList2(){
        return membersAdapter3.getPurchaseContributorsList();
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