package com.akarcontrols.akarorders;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class addservice extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Button datePicker;
    String date;
    EditText custnameedit, modelnameedit, capacityedit, problem_reported, machinenumber, descriptionedit, phonenumberedit;
    SearchableSpinner custnamesearch, modelnamesearch;
    AppCompatButton done;

    TextView reference_id;
    ArrayList<String> thelist, thelist1;
    RadioButton machineradio, loadcellradio;
    ArrayAdapter<String> listAdapter, listAdapter1;
    String phonenumber;
    String new_id;
    DatabaseReference reference, custref, modelref;
    String TAG = "addorders class";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service);

        custnameedit = (EditText) findViewById(R.id.custnameedit);
        modelnameedit = (EditText) findViewById(R.id.modeledit);
        capacityedit = (EditText) findViewById(R.id.capacityedit);
        descriptionedit = (EditText) findViewById(R.id.description);
        phonenumberedit = (EditText) findViewById(R.id.phonenumber);
        custnamesearch = (SearchableSpinner) findViewById(R.id.custnamesearch);
        modelnamesearch = (SearchableSpinner) findViewById(R.id.modelsearch);
        machineradio = (RadioButton) findViewById(R.id.machineradio);
        loadcellradio = (RadioButton) findViewById(R.id.loadcellradio);
        machinenumber = (EditText) findViewById(R.id.machine_number);
        problem_reported = (EditText) findViewById(R.id.problem);

        done = (AppCompatButton) findViewById(R.id.done);
        reference_id = (TextView) findViewById(R.id.referenceid);


        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);
        int year = c.get(Calendar.YEAR);
        new_id = minutes + "" + seconds + "" + date + "" + month + "" + year;
        reference_id.setText(new_id);

        Intent intent = getIntent();
        String model = intent.getStringExtra("model");
        String capacity = intent.getStringExtra("capacity");
        String description = intent.getStringExtra("description");
        String custname = intent.getStringExtra("custname");
        String problem = intent.getStringExtra("problem_reported");
        String phonenumbers = intent.getStringExtra("phone_number");
        int from = intent.getIntExtra("from", 0);
        String referencetemp = intent.getStringExtra("reference");

        datePicker = (Button) findViewById(R.id.date_picker);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (from == 1) {
                    modelnameedit.setText(model);
                    capacityedit.setText(capacity);
                    descriptionedit.setText(description);
                    phonenumberedit.setText(phonenumbers);
                    custnameedit.setText(custname);
                    problem_reported.setText(problem);
                    new_id = referencetemp;
                    reference_id.setText(new_id);
                }
            }
        }, 200);


        thelist = new ArrayList<>();
        thelist1 = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, thelist);
        listAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, thelist1);

        machineradio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (machineradio.isChecked() == true) {
                    machineradio.setChecked(true);
                    loadcellradio.setChecked(false);
                } else if (machineradio.isChecked() == false) {
                    loadcellradio.setChecked(true);
                    machineradio.setChecked(false);
                    machinenumber.setText("LOADCELL");
                }
            }

        });

        loadcellradio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (loadcellradio.isChecked() == true) {
                    loadcellradio.setChecked(true);
                    machineradio.setChecked(false);
                    machinenumber.setText("LOADCELL");
                } else if (loadcellradio.isChecked() == false) {
                    loadcellradio.setChecked(false);
                    machineradio.setChecked(true);
                }
            }

        });


        reference = FirebaseDatabase.getInstance().getReference().child("service").child("pending");
        thelist.add("Select customer");
        thelist1.add("Select Model");

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
                    Toast.makeText(addservice.this, "Please select another value", Toast.LENGTH_SHORT).show();
                    custnameedit.setText("");
                } else {
                    String custnametemp = adapterView.getItemAtPosition(i).toString();
                    custnameedit.setText(custnametemp);
                    custref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                phonenumber = ds.child("phone_number").getValue(String.class);
                                phonenumberedit.setText(phonenumber);
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
                    Toast.makeText(addservice.this, "Please select another value", Toast.LENGTH_SHORT).show();
                    modelnameedit.setText("");
                } else {
                    String custnametemp = adapterView.getItemAtPosition(i).toString();
                    modelnameedit.setText(custnametemp);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> productmap = new HashMap<>();
                productmap.put("customer_name", custnameedit.getText().toString());
                productmap.put("model", modelnameedit.getText().toString());
                productmap.put("capacity", capacityedit.getText().toString());
                productmap.put("machine_number", machinenumber.getText().toString());
                productmap.put("problem_reported", problem_reported.getText().toString());
                productmap.put("description", descriptionedit.getText().toString());
                productmap.put("phone_number", phonenumberedit.getText().toString());
                productmap.put("date_return", date);
                productmap.put("date", getDateTime());
                productmap.put("reference", new_id);
                productmap.put("status", "pending");
                reference.child(new_id).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(addservice.this, "done", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(addservice.this, "Check network", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(addservice.this, "Check network", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                if (from == 1) {
                    new MyTask().execute();
                }

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
                                        Toast.makeText(addservice.this, "Check network", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Intent intent = new Intent(addservice.this, MainActivity.class);
                startActivity(intent);


            }
        });

    }

    public void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


    @Override
    public void onDateSet(DatePicker datePicker1, int year, int month, int dayOfMonth) {
        if ((dayOfMonth < 10) && (month <= 8)) {
            date = "0" + dayOfMonth + "0" + (month + 1) + year;
            datePicker.setText(dayOfMonth + "-" + (1 + month) + "-" + year);
        } else if ((dayOfMonth < 10) && (month >= 9)) {
            date = "0" + dayOfMonth + "" + (month + 1) + year;
            datePicker.setText(dayOfMonth + "-" + (1 + month) + "-" + year);

        } else if ((dayOfMonth >= 10) && (month >= 9)) {
            date = dayOfMonth + "" + (month + 1) + "" + year;
            datePicker.setText(dayOfMonth + "-" + (1 + month) + "-" + year);

        } else {
            date = dayOfMonth + "0" + (month + 1) + "" + year;
            datePicker.setText(dayOfMonth + "-" + (1 + month) + "-" + year);
        }
        Log.e("checking", "onDateSet: " + date);
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
                String modeltemp = modelnameedit.getText().toString();
                String phtemp = "91" + phonenumberedit.getText().toString();
                Log.e(TAG, "doInBackground: " + modeltemp + phtemp);

                String message = "&message=" +
                        "Dear user,%n" +
                        " %n" +
                        "We have received your machine for service.%n" +
                        "Model: " + modeltemp + "%n" +
                        "You will receive a message shortly once the problems are rectified.%n" +
                        " %n" +
                        "Akar Controls";

                String sender = "&sender=" + "AKARCH";
                String numbers = "&numbers=" + phtemp;
                Log.e(TAG, "doInBackground: " + message + phtemp);
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
