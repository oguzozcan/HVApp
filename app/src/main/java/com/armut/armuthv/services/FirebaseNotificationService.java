package com.armut.armuthv.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.armut.armuthv.ArmutHVApp;
import com.armut.armuthv.MainActivity;
import com.armut.armuthv.R;
import com.armut.armuthv.busevents.JobEvents;
import com.armut.armuthv.busevents.MiscEvents;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by oguzemreozcan on 24/01/17.
 */

public class FirebaseNotificationService extends FirebaseMessagingService {

    private final String TAG = "FBNotificationService";
    public final int FB_NOTIFICATION_REQUEST_CODE = 985;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        try{
            Log.d(TAG, "From: " + remoteMessage.getFrom());
            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Map<String, String> data = remoteMessage.getData();
                Log.d(TAG, "Message data payload: " + data);
                if(data != null){
                    sendNotification(data.get("alert"), data.get("action"), data.get("job_id"), data.get("job_quotes_id"));
                }
            }
            // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            String message = remoteMessage.getNotification().getBody();
//            remoteMessage.get
//            Log.d(TAG, "Message Notification Body: " + message);
//            sendNotification(message);
//        }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody, String action, String jobId, String jobQuoteId) {
        ArmutHVApp app = (ArmutHVApp) getApplication();
        int uniqueJobId = 0;
        int uniquejobQuoteId = 0;
        if(jobId == null){
            Log.d(TAG, "JOB ID NULL");
            return;
        }
        try{
            uniqueJobId = Integer.parseInt(jobId);
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
        if(jobQuoteId != null){
            try{
                uniquejobQuoteId = Integer.parseInt(jobQuoteId);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        app.addToNotificationGroup(uniqueJobId);
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("ACTION", action);
        intent.putExtra(JobEvents.PARAM_JOB_ID, uniqueJobId);
        intent.putExtra(JobEvents.PARAM_JOB_QUOTE_ID, uniquejobQuoteId);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);//Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, FB_NOTIFICATION_REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        int count = app.getNotificationGroupCount(uniqueJobId);
        String title = "Armut'tan yeni bildirimin var";
        if(action.equals("quoteSelected")){
            title = "Tebrikler! Teklifin seçildi, işi kazandın.";
            app.getDataSaver().putBoolean("QUOTE_SELECTED_MAY_SHOW_RATE_DIALOG", true);
            app.getDataSaver().save();
        }

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.appicon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                //.setStyle(new NotificationCompat.InboxStyle())
                .setSound(defaultSoundUri)
                .setGroup("Armut Hizmet Veren")
                .setGroupSummary(true)
                .setDefaults(android.app.Notification.DEFAULT_SOUND | android.app.Notification.DEFAULT_VIBRATE)
                .setLights(Color.GREEN, 500,500)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(count > 1){
            title = "Armut'tan " + (count) +" yeni bildirimin var";
            notificationBuilder.setContentTitle(title).setNumber(count);
        }
        Log.d(TAG, "NOTIFY : " + uniqueJobId);
        notificationManager.notify(uniqueJobId, notificationBuilder.build());
        app.getBus().post(new MiscEvents.NotificationReceivedEvent(uniqueJobId,uniquejobQuoteId, action));
    }
}
