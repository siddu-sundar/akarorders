package com.akarcontrols.akarorders;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class enterserialnumber extends AppCompatActivity {

    private ListView myList;
    private MyAdapter myAdapter;
    int qty, serial_number, completed;
    EditText edit;
    Button refresh, done;
    DatabaseReference reference, snoref, referencestatusupdate;
    String capacity_Accuracy, brand, stamping, description, loadcell, custname, model, referencetemp, sendlr;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enterserialnumber);
        refresh = (Button) findViewById(R.id.refresh);
        myList = (ListView) findViewById(R.id.MyList);
        myList.setItemsCanFocus(true);
        edit = (EditText) findViewById(R.id.edit);
        done = (Button) findViewById(R.id.done);
        Intent intent = getIntent();
        completed = 0;
        qty = Integer.parseInt(intent.getStringExtra("qty"));
        capacity_Accuracy = intent.getStringExtra("capacity_accuracy");
        brand = intent.getStringExtra("brand");
        stamping = intent.getStringExtra("stamping");
        description = intent.getStringExtra("description");
        loadcell = intent.getStringExtra("loadcell");
        custname = intent.getStringExtra("custname");
        model = intent.getStringExtra("model");
        referencetemp = intent.getStringExtra("reference");
        sendlr = intent.getStringExtra("sendlr");

        serial_number = intent.getIntExtra("serial_number", 0);
        myAdapter = new MyAdapter();
        myList.setAdapter(myAdapter);
        edit.setText(serial_number + "");
        reference = FirebaseDatabase.getInstance().getReference().child("orders").child("production_record");
        referencestatusupdate = FirebaseDatabase.getInstance().getReference().child("orders").child("pending");

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serial_number = Integer.parseInt(edit.getText().toString());
                myAdapter = new MyAdapter();
                myList.setAdapter(myAdapter);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v;
//                ArrayList<String> mannschaftsnamen = new ArrayList<String>();
                EditText et;
                for (int i = 0; i < myList.getCount(); i++) {
                    v = myList.getAdapter().getView(i, null, null);
                    et = (EditText) v.findViewById(i);
                    String mcno = et.getText().toString();
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.hasChild(mcno)) {
                                Toast.makeText(enterserialnumber.this, "Machine number already used", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("checking", "onClick: " + mcno);
                                HashMap<String, Object> productmap = new HashMap<>();
                                productmap.put("customer_name", custname);
                                productmap.put("model", model);
                                productmap.put("capacity_accuracy", capacity_Accuracy);
                                productmap.put("loadcell", loadcell);
                                productmap.put("description", description);
                                productmap.put("brand", brand);
                                productmap.put("stamping", "S");
                                productmap.put("machine_number", mcno);
                                productmap.put("date", getDateTime());
                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
                                String month_name = month_date.format(cal.getTime());
                                int year = Calendar.getInstance().get(Calendar.YEAR);
                                reference.child(String.valueOf(year)).child(month_name).child(getDateTime()).child(mcno).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            completed++;
                                            if (completed == qty) {
                                                goback();
                                                Log.e("checkingnew", "onComplete: "+completed+qty );
                                            }
                                        } else {
                                        }
                                    }
                                });
                                if (Integer.parseInt(mcno) > serial_number) {
                                    snoref = FirebaseDatabase.getInstance().getReference().child("orders").child("serial_number");
                                    HashMap<String, Object> snomap = new HashMap<>();
                                    int temp = Integer.parseInt(mcno) + 1;
                                    snomap.put("serial_number", temp + "");
                                    Log.e("checking", "onDataChange: comes inside" + mcno);
                                    snoref.child("serial_number").updateChildren(snomap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                            } else {
                                            }
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });
    }

    private void goback() {
        HashMap<String, Object> productmap = new HashMap<>();
        productmap.put("status", "completed");
        referencestatusupdate.child(referencetemp).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
//                    if (sendlr.equals("yes")) {
//                    new MyTask().execute();
//                    } else {
//                    new MyTask2().execute();
//                    }
                    Intent intent = new Intent(enterserialnumber.this, currentorders.class);
                    startActivity(intent);
                    finish();
                } else {
                }
            }
        });
    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        public ArrayList myItems = new ArrayList();

        @RequiresApi(api = Build.VERSION_CODES.M)
        public MyAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i = 0; i < qty; i++) {
                ListItem listItem = new ListItem();
                listItem.caption = "Caption" + i;
                myItems.add(listItem);
            }
            notifyDataSetChanged();
        }

        public int getCount() {
            return myItems.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.serialnumber_layout, null);
                holder.caption = (EditText) convertView
                        .findViewById(R.id.ItemCaption);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //Fill EditText with the value you have in data source
            holder.caption.setText((serial_number + position) + "");
            holder.caption.setId(position);

            //we need to update adapter once we finish with editing
            holder.caption.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        final int position = v.getId();
                        final EditText Caption = (EditText) v;
                        myItems.set(position, Caption.getText().toString());
                        Log.e("checking", "onFocusChange: " + Caption.getText().toString());
                    }
                }
            });

            return convertView;
        }
    }

    class ViewHolder {
        EditText caption;
    }

    class ListItem {
        String caption;

    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd:MM:yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
//
//    Thread thread = new Thread(new Runnable() {
//
//        @Override
//        public void run() {
//
//            try {
//                // Construct data
//                String apiKey = "apikey=" + "JqLIK9rS6bg-vxDNHSwCoUAB7fGGdwTk2ixkZ9RQnI";
//                String message = "&message=" + "Dear user, Your recent order with AKAR CONTROLS %n MODEL: " + model + " %n QUANTITY: " + qtytemp + " %n is ready for delivery and will be shipped today.";
//                String sender = "&sender=" + "AKARCH";
//                String numbers = "&numbers=" + "919445423684";
//
//                // Send data
//                HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
//                String data = apiKey + numbers + message + sender;
//                conn.setDoOutput(true);
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
//                conn.getOutputStream().write(data.getBytes("UTF-8"));
//                final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                final StringBuffer stringBuffer = new StringBuffer();
//                String line;
//                while ((line = rd.readLine()) != null) {
//                    stringBuffer.append(line);
//                }
//                Log.e("checking", "sendsms: success");
//                rd.close();
//
//
//            } catch (Exception e) {
//                System.out.println("Error SMS " + e);
//                Log.e("checking", "sendsms: failed" + e);
//            }
//
//        }
//    });
//
//    Thread thread1 = new Thread(new Runnable() {
//
//        @Override
//        public void run() {
//
//            try {
//                // Construct data
//                String apiKey = "apikey=" + "JqLIK9rS6bg-vxDNHSwCoUAB7fGGdwTk2ixkZ9RQnI";
//                String model = "TMPR";
//                String qtytemp = "2";
//                String sender = "&sender=" + "AKARCH";
//                String numbers = "&numbers=" + "919445423684";
//
//                // Send data
//                HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
//                String data = apiKey + numbers + message + sender;
//                conn.setDoOutput(true);
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
//                conn.getOutputStream().write(data.getBytes("UTF-8"));
//                final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                final StringBuffer stringBuffer = new StringBuffer();
//                String line;
//                while ((line = rd.readLine()) != null) {
//                    stringBuffer.append(line);
//                }
//                Log.e("checking", "sendsms: success");
//                rd.close();
//
//
//            } catch (Exception e) {
//                System.out.println("Error SMS " + e);
//                Log.e("checking", "sendsms: failed" + e);
//            }
//
//        }
//    });

    private class MyTask extends AsyncTask<Void, Void, Void> {
        String result;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Construct data
                String apiKey = "apikey=" + "JqLIK9rS6bg-vxDNHSwCoUAB7fGGdwTk2ixkZ9RQnI";
                String message = "&message=" + "Dear user, Your recent order with AKAR CONTROLS %n MODEL: " + model +
                        " %n QUANTITY: " + qty + " %n is ready for delivery and will be shipped today.";
                String sender = "&sender=" + "AKARCH";
                String numbers = "&numbers=" + "919445423684";

                // Send data
                HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
                String data = apiKey + numbers + message + sender;
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
                conn.getOutputStream().write(data.getBytes("UTF-8"));
                final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                final StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null) {
                    stringBuffer.append(line);
                }
                Log.e("SMS", "onClick: worked");
                rd.close();
            } catch (Exception e) {
                System.out.println("Error SMS " + e);
                Log.e("SMS", "onClick: no" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class MyTask2 extends AsyncTask<Void, Void, Void> {
        String result;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Construct data
                String apiKey = "apikey=" + "JqLIK9rS6bg-vxDNHSwCoUAB7fGGdwTk2ixkZ9RQnI";
                String message = "&message=" + "Dear user,%n%n" + "Your recent order with AKAR CONTROLS %n" +
                        "MODEL: " + model + " %nQUANTITY: " + qty + "%nis ready for delivery.%n" +
                        "You can pick up the package anytime.";
                String sender = "&sender=" + "AKARCH";
                String numbers = "&numbers=" + "919445423684";

                // Send data
                HttpURLConnection conn = (HttpURLConnection) new URL("https://api.textlocal.in/send/?").openConnection();
                String data = apiKey + numbers + message + sender;
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
                conn.getOutputStream().write(data.getBytes("UTF-8"));
                final BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                final StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = rd.readLine()) != null) {
                    stringBuffer.append(line);
                }
                Log.e("SMS", "onClick: worked");
                rd.close();
            } catch (Exception e) {
                System.out.println("Error SMS " + e);
                Log.e("SMS", "onClick: no" + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
