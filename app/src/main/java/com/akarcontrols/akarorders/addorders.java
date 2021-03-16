package com.akarcontrols.akarorders;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Member;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class addorders extends AppCompatActivity {
    EditText custnameedit, modelnameedit, capacityedit, accuracyedit, quantityedit, loadcelledit, descriptionedit, phonenumberedit, delivery;
    SearchableSpinner custnamesearch, modelnamesearch, deliverysearch;
    AppCompatButton brand, done;
    Switch lrnumberswitch;
    TextView reference_id;
    ArrayList<String> thelist, thelist1, thelist2;
    RadioButton machineradio, loadcellradio;
    String brands = "";
    ArrayAdapter<String> listAdapter, listAdapter1, listAdapter2;
    Switch stamping;
    String new_id;
    String phonenumber;
    //    sendSMS sendSMS;
    DatabaseReference reference, custref, modelref, deliveryref, neworderreference;
    String TAG = "addorders class";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addorders);

        custnameedit = (EditText) findViewById(R.id.custnameedit);
        modelnameedit = (EditText) findViewById(R.id.modeledit);
        capacityedit = (EditText) findViewById(R.id.capacityedit);
        accuracyedit = (EditText) findViewById(R.id.accuracyedit);
        quantityedit = (EditText) findViewById(R.id.qty);
        loadcelledit = (EditText) findViewById(R.id.loadcelledit);
        descriptionedit = (EditText) findViewById(R.id.description);
        phonenumberedit = (EditText) findViewById(R.id.phonenumber);
        delivery = (EditText) findViewById(R.id.delivery);
        custnamesearch = (SearchableSpinner) findViewById(R.id.custnamesearch);
        deliverysearch = (SearchableSpinner) findViewById(R.id.deliverysearch);
        modelnamesearch = (SearchableSpinner) findViewById(R.id.modelsearch);
        brand = (AppCompatButton) findViewById(R.id.brand);
        stamping = (Switch) findViewById(R.id.stampingswitch);
        lrnumberswitch = (Switch) findViewById(R.id.lrnumber_switch);
        machineradio = (RadioButton) findViewById(R.id.machineradio);
        loadcellradio = (RadioButton) findViewById(R.id.loadcellradio);
//        sendSMS=new sendSMS();
        done = (AppCompatButton) findViewById(R.id.done);
        reference_id = (TextView) findViewById(R.id.referenceid);

        Log.e(TAG, "log works");


        Intent intent = getIntent();
        String model = intent.getStringExtra("model");
        String capacity = intent.getStringExtra("capacity");
        String accuracy = intent.getStringExtra("accuracy");
        String brandt = intent.getStringExtra("brand");
        String phonenumbers = intent.getStringExtra("phone_number");
        String stampingt = intent.getStringExtra("stamping");
        String description = intent.getStringExtra("description");
        String loadcell = intent.getStringExtra("loadcell");
        String custname = intent.getStringExtra("customer_name");
        String qty = intent.getStringExtra("qty");
        String serial_not = intent.getStringExtra("serial_number");
        String deliveryt = intent.getStringExtra("delivery");
        String referencetemp = intent.getStringExtra("reference");
        String sendlr = intent.getStringExtra("sendlr");

        thelist = new ArrayList<>();
        thelist1 = new ArrayList<>();
        thelist2 = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, thelist);
        listAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, thelist1);
        listAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, thelist2);

        machineradio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (machineradio.isChecked() == true) {
                    machineradio.setChecked(true);
                    loadcellradio.setChecked(false);
                    accuracyedit.setText("");
                    loadcelledit.setText("");
                    brands = "";
                } else if (machineradio.isChecked() == false) {
                    loadcellradio.setChecked(true);
                    machineradio.setChecked(false);
                    accuracyedit.setText("0");
                    loadcelledit.setText("Loadcell");
                    brands = "LOADCELL";
                    stamping.setChecked(false);
                }
            }

        });

        loadcellradio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (loadcellradio.isChecked() == true) {
                    loadcellradio.setChecked(true);
                    machineradio.setChecked(false);
                    accuracyedit.setText("0");
                    loadcelledit.setText("Loadcell");
                    brands = "LOADCELL";
                    stamping.setChecked(false);
                } else if (loadcellradio.isChecked() == false) {
                    loadcellradio.setChecked(false);
                    machineradio.setChecked(true);
                    accuracyedit.setText("");
                    loadcelledit.setText("");
                    brands = "";
                }
            }

        });

        brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "log works");

                LayoutInflater factory = LayoutInflater.from(addorders.this);
                final View deleteDialogView = factory.inflate(R.layout.brandselection_layout, null);
                final AlertDialog deleteDialog = new AlertDialog.Builder(addorders.this).create();
                deleteDialog.setView(deleteDialogView);
                Button atlas = deleteDialogView.findViewById(R.id.atlas);
                Button premier = deleteDialogView.findViewById(R.id.premier);
                Button legend = deleteDialogView.findViewById(R.id.legend);
                Button plain = deleteDialogView.findViewById(R.id.plain);
                atlas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        brands = "ATLAS";
                        brand.setText(brands);
                        deleteDialog.dismiss();
                    }
                });
                premier.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //your business logic loge
                        brands = "PREMIER";
                        brand.setText(brands);
                        deleteDialog.dismiss();
                    }
                });
                legend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //your business logic loge
                        brands = "LEGEND";
                        brand.setText(brands);
                        deleteDialog.dismiss();
                    }
                });
                plain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //your business logic loge
                        brands = "PLAIN";
                        brand.setText(brands);
                        deleteDialog.dismiss();
                    }
                });
                deleteDialog.show();
            }
        });

        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);
        int year = c.get(Calendar.YEAR);
        new_id = minutes + "" + seconds + "" + date + "" + month + "" + year;
        reference_id.setText(new_id);

        reference = FirebaseDatabase.getInstance().getReference().child("orders").child("pending");
        neworderreference = FirebaseDatabase.getInstance().getReference().child("orders").child("neworder");
        thelist.add("Select customer");
        thelist1.add("Select Model");
        thelist2.add("Select Delivery mode");

        custref = FirebaseDatabase.getInstance().getReference().child("customers");
        custref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String county = ds.child("customer_name").getValue(String.class);
                    thelist.add(county);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, thelist);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                custnamesearch.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ");
            }

        });

        custnamesearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Toast.makeText(addorders.this, "Please select another value", Toast.LENGTH_SHORT).show();
                } else {
                    String custnametemp = adapterView.getItemAtPosition(i).toString();
                    custnameedit.setText(custnametemp);
                    custref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (custnametemp.equals(ds.child("customer_name").getValue(String.class))) {
                                    phonenumber = ds.child("phone_number").getValue(String.class);
                                    phonenumberedit.setText(phonenumber);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(TAG, "onCancelled: ");
                        }

                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        deliveryref = FirebaseDatabase.getInstance().getReference().child("delivery");
        deliveryref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String county = ds.child("delivery_mode").getValue(String.class);
                    thelist2.add(county);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, thelist2);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                deliverysearch.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ");
            }

        });

        deliverysearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Toast.makeText(addorders.this, "Please select another value", Toast.LENGTH_SHORT).show();
                } else {
                    String custnametemp = adapterView.getItemAtPosition(i).toString();
                    delivery.setText(custnametemp);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        modelref = FirebaseDatabase.getInstance().getReference().child("models");
        modelref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String county = ds.child("model").getValue(String.class);
                    thelist1.add(county);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, thelist1);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                modelnamesearch.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ");
            }

        });

                modelnamesearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Toast.makeText(addorders.this, "Please select another value", Toast.LENGTH_SHORT).show();
                } else {
                    String custnametemp = adapterView.getItemAtPosition(i).toString();
                    modelnameedit.setText(custnametemp);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        int from = intent.getIntExtra("from", 0);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!(custnameedit.getText().toString().equals("")) && !(modelnameedit.getText().toString().equals("")) && !(capacityedit.getText().toString().equals(""))
                        && !(accuracyedit.getText().toString().equals("")) && !(loadcelledit.getText().toString().equals("")) && !(quantityedit.getText().toString().equals(""))
                        && !(brands.equals("")) && !(descriptionedit.getText().toString().equals(""))
                        && !(delivery.getText().toString().equals("")) && !(phonenumberedit.getText().toString().equals(""))) {
                    HashMap<String, Object> productmap = new HashMap<>();
                    productmap.put("customer_name", custnameedit.getText().toString());
                    productmap.put("model", modelnameedit.getText().toString());
                    productmap.put("capacity", capacityedit.getText().toString());
                    productmap.put("accuracy", accuracyedit.getText().toString());
                    productmap.put("quantity", quantityedit.getText().toString());
                    productmap.put("loadcell", loadcelledit.getText().toString());
                    productmap.put("delivery", delivery.getText().toString());
                    productmap.put("description", descriptionedit.getText().toString());
                    productmap.put("phone_number", phonenumberedit.getText().toString());
                    productmap.put("brand", brands);
                    productmap.put("date", getDateTime());
                    if (stamping.isChecked()) {
                        productmap.put("stamping", "S");
                    } else {
                        productmap.put("stamping", "NS");
                    }
                    if (lrnumberswitch.isChecked()) {
                        productmap.put("send_lr", "yes");
                    } else {
                        productmap.put("send_lr", "no");
                    }
                    productmap.put("reference", new_id);
                    productmap.put("status", "pending");
                    reference.child(new_id).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(addorders.this, "done", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(addorders.this, "Check network", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    custref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.hasChild(custnameedit.getText().toString())) {
                                Log.e(TAG, "onDataChange: has child");
                            } else {
                                HashMap<String, Object> productmap = new HashMap<>();
                                productmap.put("customer_name", custnameedit.getText().toString());
                                productmap.put("phone_number", phonenumberedit.getText().toString());
                                custref.child(custnameedit.getText().toString()).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.e(TAG, "onComplete: done");
                                        } else {
                                            Toast.makeText(addorders.this, "Check network", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    deliveryref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.hasChild(delivery.getText().toString())) {
                                Log.e(TAG, "onDataChange: has child");
                            } else {
                                HashMap<String, Object> productmap = new HashMap<>();
                                productmap.put("delivery_mode", delivery.getText().toString());
                                deliveryref.child(delivery.getText().toString()).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.e(TAG, "onComplete: done");
                                        } else {
                                            Toast.makeText(addorders.this, "Check network", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    HashMap<String, Object> productmap2 = new HashMap<>();
                    productmap2.put("customer_name", custnameedit.getText().toString());
                    productmap2.put("model", modelnameedit.getText().toString());
                    productmap2.put("reference", new_id);
                    productmap2.put("quantity", quantityedit.getText().toString());

                    neworderreference.updateChildren(productmap2).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(addorders.this, "done", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(addorders.this, "Check network", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    modelref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.hasChild(modelnameedit.getText().toString())) {
                                Log.e(TAG, "onDataChange: has child");
                            } else {
                                HashMap<String, Object> productmap = new HashMap<>();
                                productmap.put("model", modelnameedit.getText().toString());
                                modelref.child(modelnameedit.getText().toString()).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.e(TAG, "onComplete: done");
                                        } else {
                                            Toast.makeText(addorders.this, "Check network", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    if (!(from == 1)) {
                        Log.e(TAG, "onClick: " + from);
                        new MyTask().execute();
                    }
                    Intent intent = new Intent(addorders.this, MainActivity.class);
                    startActivity(intent);


                }
            }

        });

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (from == 1) {
                    modelnameedit.setText(model);
                    capacityedit.setText(capacity);
                    accuracyedit.setText(accuracy);
                    brands = brandt;
                    descriptionedit.setText(description);
                    phonenumberedit.setText(phonenumbers);
                    loadcelledit.setText(loadcell);
                    custnameedit.setText(custname);
                    quantityedit.setText(qty);
                    delivery.setText(deliveryt);
                    brand.setText(brands);
                    new_id = referencetemp;
                    reference_id.setText(new_id);
                    if (stampingt.equals("S")) {
                        stamping.setChecked(true);
                    } else stamping.setChecked(false);
                    if (sendlr.equals("yes")) {
                        lrnumberswitch.setChecked(true);
                    } else lrnumberswitch.setChecked(false);
                }
            }
        }, 200);


    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd:MM:yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {
        String result;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Construct data

                String apiKey = "apikey=" + "JqLIK9rS6bg-vxDNHSwCoUAB7fGGdwTk2ixkZ9RQnI";
                String qtytemp = quantityedit.getText().toString();
                String modeltemp = modelnameedit.getText().toString();
                String phtemp = "91" + phonenumberedit.getText().toString();
                Log.e(TAG, "doInBackground: " + qtytemp + modeltemp + phtemp);
                String message = "&message=" + "Dear user,%n" +
                        "Thanks for choosing Akar Controls. Received your order,%n" +
                        "Model: " + modeltemp +
                        "%nQuantity : " + qtytemp +
                        "%nYou will receive a message on completion.%n" +
                        "Ph:9444027384";

                String sender = "&sender=" + "AKARCH";
                String numbers = "&numbers=" + phtemp;
                Log.e(TAG, "doInBackground: "+message+phtemp );
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
