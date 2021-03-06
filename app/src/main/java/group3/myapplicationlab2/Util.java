package group3.myapplicationlab2;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.api.BooleanResult;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static group3.myapplicationlab2.Constant.PICK_IMAGE_ID;

/**
 * Created by mc on 03/06/17.
 */

public class Util {
    private Context context;
    private boolean result = true;

    public Util(Context c){
        context = c;
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap, int w, int h_) {


        int W = bitmap.getWidth();
        int H = bitmap.getHeight();
        int h = (H*w)/W;
        int cx = W/2;
        int cy = H/2;
        int left = cx - (w/2);
        int top = cy - (h/2);
        int right = cx + (w/2);
        int bottom = cy +(h/2);

        Bitmap sizedBmp = bitmap.createScaledBitmap(bitmap,w,h,false);
        Bitmap croppedBmp = squareBitmap(sizedBmp);
        Bitmap output = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, w);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        //canvas.drawRoundRect(rectF, h, h, paint);
        canvas.drawCircle((w/2), (w/2), (w/2), paint);


        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(croppedBmp, rect, rect, paint);

        return output;
    }

    private int radius(int w, int h){
        if (w>h) return h/2;
        else return w/2;
    }

    private static int dp2px(Resources resource, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,   dp,resource.getDisplayMetrics());
    }

    public static float px2dp(Resources resource, float px)  {
        return (float) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px,resource.getDisplayMetrics());
    }

    public Bitmap squareBitmap(Bitmap srcBmp){
        Bitmap dstBmp;
/*        if (srcBmp.getWidth() >= srcBmp.getHeight()){
            //orizzontale
            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        }
        else{
            //verticale
            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }*/
        dstBmp = Bitmap.createBitmap(
                srcBmp,
                0,
                srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                srcBmp.getWidth(),
                srcBmp.getWidth());
        return dstBmp;
    }


    public Bitmap downloadImage(String uri) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);

            try {
                URL url = new URL(uri);
                Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return image;
            } catch(IOException e) {
                System.out.println(e);
            }
        }
        return null;
    }

    public File fileImageCreator(){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MoneyTracker");
        mediaStorageDir.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }

    public File fileProfileImageCreator(String user_id){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "MoneyTracker/profileImages");
        mediaStorageDir.mkdirs();
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator+ user_id + ".jpg");
        return mediaFile;
    }

    public String bitmap2string (Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] byteArrayImage = baos.toByteArray();
        return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

    }

    public byte[] bitmap2byte(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
        byte[] byteArrayImage = baos.toByteArray();
        return byteArrayImage;
    }


    public Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    str,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    320, 320, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        this.context.getResources().getColor(R.color.black):this.context.getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_8888);

        bitmap.setPixels(pixels, 0, 320, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }

    public void downloadImageInMemory(String uri, String key){
        File pictureFile = this.fileProfileImageCreator(key);
        if (pictureFile == null)
            Log.d("PATH_FIG", "null");
        else
            Log.d("PATH_FIG", pictureFile.getAbsolutePath());
        try{
            FileOutputStream fos = new FileOutputStream(pictureFile);
            this.downloadImage(uri).compress(Bitmap.CompressFormat.PNG, 50, fos);
            fos.close();

        } catch (FileNotFoundException e) {
            Log.d("ERROR", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("ERROR", "Error accessing file: " + e.getMessage());
        }
    }

    public void saveImageInMemory(Bitmap b, String key){
        File pictureFile = this.fileProfileImageCreator(key);
        if (pictureFile == null)
            Log.d("PATH_FIG", "null");
        else
            Log.d("PATH_FIG", pictureFile.getAbsolutePath());
        try{
            FileOutputStream fos = new FileOutputStream(pictureFile);
            b.compress(Bitmap.CompressFormat.PNG, 50, fos);
            fos.close();

        } catch (FileNotFoundException e) {
            Log.d("ERROR", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("ERROR", "Error accessing file: " + e.getMessage());
        }
    }

    public void getGrantedPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.CAMERA}
                ,1);

    }

    public Boolean userHasDebits(Group group, User user){
        List<Purchase> list = group.getPurchases();
        for (Purchase p: list){
            for(PurchaseContributor pc : p.getContributors()){
                if(pc.getUser_id().equals(user.getUid()) && pc.getPayed()==false) {
                    return true;
                }

            }
        }
        return false;
    }

    public void deleteUserFromGroup(Group group, User user, int pos){

        /*Removing fromu group Preview*/
        DatabaseReference user_groups;
        user_groups = FirebaseDatabase.getInstance().getReference()
                .child(Constant.REFERENCEUSERS)
                .child(user.getUid())
                .child(Constant.REFERENCEGROUPSHASH);
        user_groups.child(group.getId()).removeValue();


        /*removing from user groups variable*/
        if (user.getGroups().size()>0) {
            user.getGroups().remove(pos);
        }

        /*Removing from user from groups*/
        DatabaseReference groupsQuery = FirebaseDatabase.getInstance().getReference()
                .child(Constant.REFERENCEGROUPS)
                .child(group.getId())
                .child(Constant.REFERENCEGROUPSMEMBERS)
                .child(user.getUid());
        groupsQuery.removeValue();
    }

    public void checkGrants(Activity activity, int [] grantResults){
        Log.d("GRANT", "dim "+grantResults.length);
        boolean permex=true;
        for (int i=0; i<grantResults.length; i++){
            if(grantResults[i] == PackageManager.PERMISSION_DENIED){
                permex = false;
            }

        }
        if (permex) {
            // Permission Granted
            Intent chooseImageIntent = ImagePicker.getPickImageIntent(activity);
            activity.startActivityForResult(chooseImageIntent, Constant.PICK_IMAGE_ID);
        } else {
            // Permission Denied
            Toast.makeText(activity, R.string.camera_permission_denied, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public  String[] asksPermissions(Activity activity){
        ArrayList<String> toAsk = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            toAsk.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            toAsk.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            toAsk.add(android.Manifest.permission.CAMERA);
        }

        if (toAsk.size()>0){
            String [] toAskArray = new String[toAsk.size()];
            for (int i=0; i<toAsk.size(); i++){
                toAskArray[i] = toAsk.get(i);
            }
            return toAskArray;

        }
        else{
            return null;
        }

    }
    public void desappearViewOnSoftKeyboard(final View activityRootView, final View view) {
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
                if (heightDiff > dpToPx(context, 200)) {
                    view.setVisibility(View.GONE);
                } else {
                    view.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public static float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }






}