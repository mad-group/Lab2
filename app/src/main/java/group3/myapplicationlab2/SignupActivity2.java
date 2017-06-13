package group3.myapplicationlab2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static group3.myapplicationlab2.Constant.PICK_IMAGE_ID;

public class SignupActivity2 extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private ImageView picsImageView;

    private EditText inputEmail, inputPassword, inputName;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private DatabaseReference mdatabase;

    private File imageOutFile = null;

    String email, password, name;

    private StorageReference user_image;

    private Util util;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_2);

        email = getIntent().getStringExtra("email");
        password = getIntent().getStringExtra("password");

        picsImageView = (ImageView) findViewById(R.id.imageViewLeft);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputName = (EditText) findViewById(R.id.name);

        util = new Util(getApplicationContext());
        auth = FirebaseAuth.getInstance();

        user_image = FirebaseStorage.getInstance().getReference("UsersImage");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = inputName.getText().toString().trim();

                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), R.string.enter_name, Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity2.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //Toast.makeText(SignupActivity2.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity2.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    mdatabase = FirebaseDatabase.getInstance().getReference("Users");
                                    String user_id = auth.getCurrentUser().getUid();
                                    final DatabaseReference current_user =  mdatabase.child(user_id);
                                    current_user.child("name").setValue(name);
                                    current_user.child("email").setValue(auth.getCurrentUser().getEmail());
                                    current_user.child("uid").setValue(auth.getCurrentUser().getUid());

                                    try {
                                        if (bitmap != null) {

                                            byte[] byteArrayImage = util.bitmap2byte(bitmap);

                                            UploadTask uploadTask = user_image.child(auth.getCurrentUser().getUid()).putBytes(byteArrayImage);
                                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                                    Log.d("URI", downloadUrl.toString());

                                                    current_user.child("userPathImage").setValue(downloadUrl.toString());
                                                    String ts = Long.toString(System.currentTimeMillis());
                                                    FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid()).child("currentPicsUpload").setValue(ts);

                                                    startActivity(new Intent(SignupActivity2.this, MainActivity.class));
                                                    finish();

                                                }
                                            });
                                        }
                                        else {
                                            startActivity(new Intent(SignupActivity2.this, MainActivity.class));
                                            finish();
                                        }
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }
                            }
                        });

            }
        });


    }


    public void takeImage(View v){

        String [] toAskArray = util.asksPermissions(SignupActivity2.this);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                util.checkGrants(SignupActivity2.this,grantResults);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PICK_IMAGE_ID:
                //here i should insert the bitmap
                bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
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
}
