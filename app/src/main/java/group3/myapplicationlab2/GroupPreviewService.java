package group3.myapplicationlab2;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Created by anr.putina on 25/05/17.
 */

public class GroupPreviewService extends Service{

    private DatabaseReference groupPreviewReference;
    private User user;

    private Integer notificationNumber=0;

    @Override
    public void onCreate() {
        super.onCreate();
        groupPreviewReference = FirebaseDatabase.getInstance()
                .getReference("Users");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        user = (User)intent.getSerializableExtra("user");

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                 Notification notification = dataSnapshot.getValue(Notification.class);
                    if (!notification.getAuthorId().equals(user.getUid())){

                        String lastEvent = notification.getEventType();
                        String message = getResources().getString(R.string.probably_changes) + " ";
                        if (lastEvent.equals("expenseInput")){
                            message = getResources().getString(R.string.new_expense_notification) + " ";
                        }
                        if (lastEvent.equals("changeName")){
                            message = getResources().getString(R.string.change_group_name_notification) + " ";
                        }
                        if (lastEvent.equals("newMember")){
                            message = getResources().getString(R.string.new_member_in_group_notification) + " ";
                        }
                        if (lastEvent.equals(Constant.PUSHDELETEEXPENSE)){
                            message = getResources().getString(R.string.remove_expense_notification) + " ";
                        }
                        if (lastEvent.equals(Constant.PUSHMODIFYEXPENSE)){
                            message = getResources().getString(R.string.modified_expense_notification) + " ";
                        }

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(GroupPreviewService.this)
                                        .setSmallIcon(R.drawable.ic_new_group)
                                        .setContentTitle(getResources().getText(R.string.app_name))
                                        .setContentText(message+notification.getGroupName())
                                        .setAutoCancel(true)
                                        .setVibrate(new long[] { 100, 500})
                                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                        .setGroup(notification.getGroupId())
                                        .setGroupSummary(true);

                        // Creates an explicit intent for an Activity in your app
                        Intent resultIntent = new Intent(GroupPreviewService.this, GroupActivityExpense.class);
                        resultIntent.putExtra("group_id", notification.getGroupId());
                        resultIntent.putExtra("group_name", notification.getGroupName());
                        resultIntent.putExtra("user", user);

                        // The stack builder object will contain an artificial back stack for the
                        // started Activity.
                        // This ensures that navigating backward from the Activity leads out of
                        // your application to the Home screen.
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(GroupPreviewService.this);
                        // Adds the back stack for the Intent (but not the Intent itself)
                        stackBuilder.addParentStack(GroupActivityExpense.class);
                        // Adds the Intent that starts the Activity to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent =
                                stackBuilder.getPendingIntent(
                                        0,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                        mBuilder.setContentIntent(resultPendingIntent);
                        NotificationManager mNotificationManager =
                                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        // mId allows you to update the notification later on.

                        mNotificationManager.notify(notification.getId(), mBuilder.build());
                    }
                groupPreviewReference.child(user.getUid()).child("push").child(dataSnapshot.getKey()).removeValue();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        groupPreviewReference.child(user.getUid()).child("push").addChildEventListener(childEventListener);
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Wont be called as service is not bound
        return null;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
