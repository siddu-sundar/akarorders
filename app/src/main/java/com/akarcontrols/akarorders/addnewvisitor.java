package com.akarcontrols.akarorders;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class addnewvisitor extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    TimePicker intime;
    String date;
    EditText custnameedit, personnameedit, reason, phonenumberedit;
    SearchableSpinner custnamesearch;
    AppCompatButton goback, placeorder;
    TextView reference_id;
    ArrayList<String> thelist;
    RadioButton visitorradio, callradio;
    ArrayAdapter<String> listAdapter;
    String phonenumber, TAG = "addnewvisitor";
    String new_id;
    DatabaseReference reference, custref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnewvisitor);

        custnameedit = (EditText) findViewById(R.id.custnameedit);
        personnameedit = (EditText) findViewById(R.id.personname);
        reason = (EditText) findViewById(R.id.reason);
        phonenumberedit = (EditText) findViewById(R.id.phonenumber);
        custnamesearch = (SearchableSpinner) findViewById(R.id.custnamesearch);
        intime = (TimePicker) findViewById(R.id.intime);
        visitorradio = (RadioButton) findViewById(R.id.visitorradio);
        callradio = (RadioButton) findViewById(R.id.callradio);

        goback = (AppCompatButton) findViewById(R.id.goback);
        placeorder = (AppCompatButton) findViewById(R.id.placeorder);
        reference_id = (TextView) findViewById(R.id.referenceid);

        thelist = new ArrayList<>();
        listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, thelist);

        visitorradio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (visitorradio.isChecked() == true) {
                    visitorradio.setChecked(true);
                    callradio.setChecked(false);
                } else if (visitorradio.isChecked() == false) {
                    callradio.setChecked(true);
                    visitorradio.setChecked(false);
                }
            }

        });

        callradio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (callradio.isChecked() == true) {
                    callradio.setChecked(true);
                    visitorradio.setChecked(false);
                } else if (callradio.isChecked() == false) {
                    callradio.setChecked(false);
                    visitorradio.setChecked(true);
                }
            }

        });

        Calendar c = Calendar.getInstance();
        int date = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int minutes = c.get(Calendar.MINUTE);
        int seconds = c.get(Calendar.SECOND);
        int year = c.get(Calendar.YEAR);
        new_id = minutes + "" + seconds + "" + date + "" + month + "" + year;
//        reference_id.setText(new_id);

        reference = FirebaseDatabase.getInstance().getReference().child("visitor_call_register");
        thelist.add("Select customer");

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

            }

        });

        custnamesearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Toast.makeText(addnewvisitor.this, "Please select another value", Toast.LENGTH_SHORT).show();
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

                        }

                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done();
                Intent intent = new Intent(addnewvisitor.this, visitorcallregister.class);
                startActivity(intent);
                finish();
            }
        });

        placeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                done();
                Intent intent = new Intent(addnewvisitor.this, addorders.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd:MM:yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

    }


    public void done() {
        HashMap<String, Object> productmap = new HashMap<>();
        productmap.put("company_name", custnameedit.getText().toString());
        productmap.put("person_name", personnameedit.getText().toString());
        productmap.put("phone_number", phonenumberedit.getText().toString());
        productmap.put("reason", reason.getText().toString());
        if (visitorradio.isChecked() == true) {
            productmap.put("type", "visitor");
        } else {
            productmap.put("type", "call");
        }
        productmap.put("date", getDateTime());
        int hour = intime.getCurrentHour();
        int min = intime.getCurrentMinute();
        productmap.put("intime", hour + "" + min);
        productmap.put("reference", new_id);
        if (visitorradio.isChecked() == true) {
            reference.child("in").child(new_id).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(addnewvisitor.this, "done", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(addnewvisitor.this, "Check network", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            reference.child(new_id).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(addnewvisitor.this, "done", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(addnewvisitor.this, "Check network", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
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
                                Toast.makeText(addnewvisitor.this, "Check network", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
