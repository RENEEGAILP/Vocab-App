package edu.neu.madcourse.vocab;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class NotificationService extends FirebaseMessagingService {

    static private final String FIREBASE_CLOUD_MESSAGING_SERVER_KEY = "key=AAAAU9ojm5Y:APA91bHNoX9GbB0UaEQs4hcHawvTrVP00M6eR1n_3dKK-tJJ9Xx0pTMpeKj2JW56mlfEEcgqXXyf1arDC4M3SeHoW8wk6MOaZtpzMx93DH90XKC-a0NYsTUuvZUWUF_WhqAZy7HGkHYb";
    private static final String CHANNEL_ID  = "VOCAB_CHANNEL_ID";
    private static final String CHANNEL_NAME  = "VOCAB_CHANNEL_NAME";
    private static final String CHANNEL_DESCRIPTION  = "VOCAB_CHANNEL_DESCRIPTION";

    static private FirebaseFirestore m_firestore = FirebaseFirestore.getInstance();
    static private FirebaseAuth m_auth = FirebaseAuth.getInstance();

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d("Refresh Token", "Received refresh token: " + token);
        FirebaseUser user = m_auth.getCurrentUser();
        if (user != null){
            String user_id =user.getUid();
            m_firestore.collection( "users" ).document(user_id).update("device_token", token);
        }

    }

    /**
     * Push a notification to a device with the targetToken from firebase.
     */
    static void sendMessageToDevice(String targetToken, String notificationText) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jdata = new JSONObject();
        try {
            jNotification.put("title", "Vocab");
            jNotification.put("body", notificationText);
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");

            jdata.put("title","data title");
            jdata.put("content","data content");

            jPayload.put("to", targetToken); // CLIENT_REGISTRATION_TOKEN);

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data",jdata);


            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", FIREBASE_CLOUD_MESSAGING_SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Log.e("Notification Service", "run: " + resp);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage);
        }
    }

    private void showNotification(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Notification notification;
        NotificationCompat.Builder builder;
        NotificationManager notificationManager = getSystemService(NotificationManager.class);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH);
            // Configure the notification channel
            notificationChannel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(notificationChannel);
            builder = new NotificationCompat.Builder(this,CHANNEL_ID);
        }
        else {
            builder = new NotificationCompat.Builder(this);
        }
        notification = builder.setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(0,notification);

    }

    static private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    /**
     * Send notification to all users
     */
    static public void sendNotificationToAllUsers(String notification_message){
        m_firestore.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()){
                                Log.d("NOTIFICATION","User id: " + documentSnapshot.getId());
                                String token = (String) documentSnapshot.getData().get("device_token");
                                Log.d("NOTIFICATION",  " Device token =>  " + token);
                                if (token != null){
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            NotificationService.sendMessageToDevice(token,
                                                    notification_message);
                                        }
                                    }).start();
                                }
                            }
                        }
                        else{
                            Log.d("NOTIFICATION", "Error getting users: ", task.getException());
                        }
                    }
                });
    }

}
