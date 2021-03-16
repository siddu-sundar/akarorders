package com.akarcontrols.akarorders;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    AppCompatButton visitorregister, current_order, reports, service;
    DatabaseReference neworderreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        visitorregister = (AppCompatButton) findViewById(R.id.visitor_call_register);
        service = (AppCompatButton) findViewById(R.id.service_machine);
        current_order = (AppCompatButton) findViewById(R.id.current_orders);
        reports = (AppCompatButton) findViewById(R.id.reports);
        Log.e("SMS", "onClick: clicked");
        neworderreference = FirebaseDatabase.getInstance().getReference().child("orders").child("neworder");

        visitorregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.e("SMS", "onClick: clicked");
                Intent intent = new Intent(MainActivity.this, visitorcallregister.class);
                startActivity(intent);
            }
        });


        neworderreference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                neworderreference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String temp1 = snapshot.child("customer_name").getValue(String.class);
                        String temp2 = snapshot.child("model").getValue(String.class);
                        Log.e("checking", "onChildChanged: " + temp1 + temp2);
                        notification(temp1, temp2);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        current_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, currentorders.class);
                startActivity(intent);
                Log.e("SMS", "onClick: clicked");
//                sendmsg();

            }
        });

        service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, service.class);
                startActivity(intent);
            }
        });

        reports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, reportsmain.class);
                startActivity(intent);
            }
        });


    }
//
//    private void sendmsg() {
//        Log.e("SMS", "onClick: clicked");
//        try {
//            // Construct data
//            String apiKey = "apikey=" + "JqLIK9rS6bg-vxDNHSwCoUAB7fGGdwTk2ixkZ9RQnI";
//            String message = "&message=" + "Dear user, Your recent order with AKAR CONTROLS %n" +
//                    "MODEL: TMPG %n" +
//                    "QUANTITY: 2 %n" +
//                    "is ready for delivery and will be shipped today.";
//            String sender = "&sender=" + "AKARCH";
//            String numbers = "&numbers=" + "919445423684";
//
//            // Send data
//            HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
//            String data = apiKey + numbers + message + sender;
//            conn.setDoOutput(true);
//            conn.setRequestMethod("POST");
//            conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
//            conn.getOutputStream().write(data.getBytes("UTF-8"));
//            final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            final StringBuffer stringBuffer = new StringBuffer();
//            String line;
//            while ((line = rd.readLine()) != null) {
//                stringBuffer.append(line);
//            }
//            Log.e("SMS", "onClick: done");
//            rd.close();
//        } catch (Exception e) {
//            System.out.println("Error SMS " + e);
//            Log.e("SMS", "onClick: no" + e);
//        }
//
//    }


    private void notification(String temp1, String temp2) {
        Log.e("TAG", "notification: comes to notifications");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_001");
        Intent ii = new Intent(this.getApplicationContext(), currentorders.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
//        bigText.bigText(verseurl);
        bigText.setBigContentTitle("New Order");
        bigText.setSummaryText("Click to view order from " + temp1 + ", Model : " + temp2);
        Uri sounduri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        grantUriPermission("com.android.systemui", sounduri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("New Order");
        mBuilder.setContentText("Click to view order from " + temp1 + ", Model : " + temp2);
        mBuilder.setSound(sounduri);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(0, mBuilder.build());

    }

}