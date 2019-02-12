package com.michaelcerne.delvaldays;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    
    static final String TAG = "MsgFirebaseServ";
    
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        JSONArray mData;
        List<String> mFinal = new ArrayList<>();
        Date mDate = Calendar.getInstance().getTime();
        String mTimeStamp;
        String mListString;

        createNotificationChannel();
        mTimeStamp = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(mDate);
        try {
            mData = (new JSONObject(remoteMessage.getData().toString())).getJSONArray("returned");
            for (int i = 0; i < mData.length(); i++) {
                if (mData.getJSONObject(i).getString("date").equals(mTimeStamp)) {
                    mFinal.add(mData.getJSONObject(i).getString("summary"));
                }
            }
        } catch(JSONException e) {
            Log.e(TAG, "JSON error occurred");
        }
        if (mFinal.size() > 0) {
            mListString = TextUtils.join(", ", mFinal);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1000")
                    .setSmallIcon(R.drawable.michaelcerne_icon)
                    .setContentTitle(new SimpleDateFormat("EEEE MM/dd", Locale.US).format(mDate))
                    .setContentText(mListString)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(Integer.parseInt(new SimpleDateFormat("ddHHmmss",  Locale.US).format(mDate)), mBuilder.build());
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1000", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}