package com.akarcontrols.akarorders;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class visitorcallregister extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    FloatingActionButton newvisitor;
    TextView totalcount;
    RecyclerView swipeMenuListView;
    EditText searchbar;
    FloatingActionButton addorder;
    DatabaseReference reference;
    int totalint = 0, qty = 0;
    FirebaseRecyclerOptions<UserHelperClass> arrayList;
    FirebaseRecyclerAdapter<UserHelperClass, Listadapteruser> listadapteruser;
    String TAG = "appscontent", capacity, accuracy, phonenumbers, reference_temp;
    int serial_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visitorcallregister);
        newvisitor = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        searchbar = (EditText) findViewById(R.id.searchbar);
        totalcount = (TextView) findViewById(R.id.total);
        swipeMenuListView = (RecyclerView) findViewById(R.id.swipemenulistview);
        addorder = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        swipeMenuListView.setLayoutManager(new LinearLayoutManager(visitorcallregister.this));
        reference = FirebaseDatabase.getInstance().getReference().child("visitor_call_register");

        addorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(visitorcallregister.this, addorders.class);
                startActivity(intent);
            }
        });

        arrayList = new FirebaseRecyclerOptions.Builder<UserHelperClass>().setQuery(reference.child("in"), UserHelperClass.class).build();
        listadapteruser = new FirebaseRecyclerAdapter<UserHelperClass, Listadapteruser>(arrayList) {
            @Override
            protected void onBindViewHolder(@NonNull Listadapteruser holder, int i, @NonNull UserHelperClass userHelperClass) {
                holder.companyname.setText("" + userHelperClass.getCompany_name());
                holder.personname.setText("" + userHelperClass.getPerson_name());
                holder.intime.setText("" + userHelperClass.getIntime());
                final Handler handler = new Handler(Looper.getMainLooper());
            }

            @NonNull
            @Override
            public Listadapteruser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.visitorviewformat, parent, false);
                totalint = 0;
                return new Listadapteruser(v, 2);
            }

        };

        listadapteruser.startListening();
        swipeMenuListView.setAdapter(listadapteruser);

        newvisitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(visitorcallregister.this, addnewvisitor.class);
                startActivity(intent);
            }
        });


        ItemTouchHelper.SimpleCallback itemtouchlistener = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                String companynametemp = ((TextView) swipeMenuListView.findViewHolderForAdapterPosition(viewHolder.getAdapterPosition())
                        .itemView.findViewById(R.id.companyname)).getText().toString();
                String personnametemp = ((TextView) swipeMenuListView.findViewHolderForAdapterPosition(viewHolder.getAdapterPosition())
                        .itemView.findViewById(R.id.personname)).getText().toString();

                LayoutInflater factory = LayoutInflater.from(visitorcallregister.this);
                final View deleteDialogView = factory.inflate(R.layout.giveouttime, null);
                final AlertDialog deleteDialog = new AlertDialog.Builder(visitorcallregister.this).create();
                deleteDialog.setView(deleteDialogView);
                TimePicker outtime = deleteDialogView.findViewById(R.id.outtime);

                Button done = deleteDialogView.findViewById(R.id.done);
                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        reference.child("in").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    Log.e(TAG, "onDataChange: " + ds.child("model"));
                                    if (ds.child("company_name").getValue(String.class).equals(companynametemp) && ds.child("person_name").getValue(String.class).equals(personnametemp)) {
                                        reference_temp = ds.child("reference").getValue(String.class);
                                        String phonenumbertemp = ds.child("phone_number").getValue(String.class);
                                        String reasontemp = ds.child("reason").getValue(String.class);
                                        HashMap<String, Object> productmap = new HashMap<>();
                                        productmap.put("company_name", companynametemp);
                                        productmap.put("person_name", personnametemp);
                                        productmap.put("phone_number", phonenumbertemp);
                                        productmap.put("reason", reasontemp);
                                        productmap.put("type", "visitor");
                                        productmap.put("date", getDateTime());
                                        productmap.put("reference", reference_temp);
                                        int hour = outtime.getCurrentHour();
                                        int min = outtime.getCurrentMinute();
                                        productmap.put("outtime", hour + "" + min);
                                        reference.child(reference_temp).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(visitorcallregister.this, "done", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(visitorcallregister.this, "Check network", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                        reference.child("in").child(reference_temp).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled: ");
                            }

                        });

                    }
                });

                deleteDialog.show();
                listadapteruser.notifyDataSetChanged();

            }
        };
        new ItemTouchHelper(itemtouchlistener).attachToRecyclerView(swipeMenuListView);


    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {

    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd:MM:yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
