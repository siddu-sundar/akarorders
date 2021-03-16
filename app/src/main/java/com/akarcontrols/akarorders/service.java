package com.akarcontrols.akarorders;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

public class service extends AppCompatActivity {
    TextView totalcount;
    RecyclerView swipeMenuListView;
    EditText searchbar;
    DatabaseReference reference, snoref, deliveredref, loadcellref;
    int totalint = 0, qty = 0;
    FloatingActionButton addservice;
    FirebaseRecyclerOptions<UserHelperClass> arrayList;
    FirebaseRecyclerAdapter<UserHelperClass, Listadapteruser> listadapteruser;
    String TAG = "appscontent", capacity, accuracy, phonenumbers, reference_temp;
    int serial_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.servicemain);

        searchbar = (EditText) findViewById(R.id.searchbar);
        totalcount = (TextView) findViewById(R.id.total);
        swipeMenuListView = (RecyclerView) findViewById(R.id.swipemenulistview);
        addservice = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        swipeMenuListView.setLayoutManager(new LinearLayoutManager(service.this));
        reference = FirebaseDatabase.getInstance().getReference().child("service").child("pending");
        deliveredref = FirebaseDatabase.getInstance().getReference().child("service").child("service delivered");
        loadcellref = FirebaseDatabase.getInstance().getReference().child("service").child("loadcell service");

        arrayList = new FirebaseRecyclerOptions.Builder<UserHelperClass>().setQuery(reference, UserHelperClass.class).build();
        listadapteruser = new FirebaseRecyclerAdapter<UserHelperClass, Listadapteruser>(arrayList) {
            @Override
            protected void onBindViewHolder(@NonNull Listadapteruser holder, int i, @NonNull UserHelperClass userHelperClass) {
                totalint++;
                holder.custname.setText("" + userHelperClass.getCustomer_name());
                holder.model.setText("" + userHelperClass.getModel());
                holder.capacity.setText(userHelperClass.getCapacity() + "");
                holder.reference.setText("" + userHelperClass.getReference());
                holder.date.setText("" + userHelperClass.getDate());
                String status = userHelperClass.getStatus();
                if (status.equals("completed")) {
                    holder.status.setVisibility(View.VISIBLE);
                } else holder.status.setVisibility(View.INVISIBLE);

                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        totalcount.setText(totalint + "");
                    }
                }, 2000);
            }

            @NonNull
            @Override
            public Listadapteruser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.servicelayoout, parent, false);
                totalint = 0;
                return new Listadapteruser(v, 1);
            }

        };


        listadapteruser.startListening();
        swipeMenuListView.setAdapter(listadapteruser);

        addservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(service.this, addservice.class);
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
                String ref = ((TextView) swipeMenuListView.findViewHolderForAdapterPosition(viewHolder.getAdapterPosition())
                        .itemView.findViewById(R.id.referenceid)).getText().toString();
                ImageView statusicon = (ImageView) swipeMenuListView.findViewHolderForAdapterPosition(viewHolder.getAdapterPosition())
                        .itemView.findViewById(R.id.complete_icon);
                reference = FirebaseDatabase.getInstance().getReference().child("service").child("pending");
                LayoutInflater factory = LayoutInflater.from(service.this);
                final View deleteDialogView = factory.inflate(R.layout.viewdetails_servicelayout, null);
                final AlertDialog deleteDialog = new AlertDialog.Builder(service.this).create();
                deleteDialog.setView(deleteDialogView);
                TextView modeltext = deleteDialogView.findViewById(R.id.modeltext);
                TextView capacitytext = deleteDialogView.findViewById(R.id.capacitytext);
                TextView phonenumbertext = deleteDialogView.findViewById(R.id.phonenumbertext);
                TextView problemtext = deleteDialogView.findViewById(R.id.problemtext);
                TextView descriptiontext = deleteDialogView.findViewById(R.id.descriptiontext);
                TextView datetext = deleteDialogView.findViewById(R.id.datetext);
                TextView machinenumbertext = deleteDialogView.findViewById(R.id.machine_numbertext);


                TextView model = deleteDialogView.findViewById(R.id.model);
                TextView capacity = deleteDialogView.findViewById(R.id.capacity);
                TextView phonenumber = deleteDialogView.findViewById(R.id.phonenumber);
                TextView description = deleteDialogView.findViewById(R.id.description);
                TextView custname = deleteDialogView.findViewById(R.id.custname);
                TextView date = deleteDialogView.findViewById(R.id.date);
                TextView machinenumber = deleteDialogView.findViewById(R.id.machine_number);
                TextView problem = deleteDialogView.findViewById(R.id.problem);

                Button completed = deleteDialogView.findViewById(R.id.completed);
                if (statusicon.getVisibility() == View.VISIBLE || machinenumber.getText().toString().equals("LOADCELL")) {
                    completed.setText("Delivered");
                }


                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.e(TAG, "onDataChange: " + ds.child("model"));
                            if (ds.child("reference").getValue(String.class).equals(ref)) {
                                model.setText(ds.child("model").getValue(String.class));
                                capacity.setText(ds.child("capacity").getValue(String.class));
                                machinenumber.setText(ds.child("machine_number").getValue(String.class));
                                phonenumbers = ds.child("phone_number").getValue(String.class);
                                reference_temp = ds.child("reference").getValue(String.class);
                                problem.setText(ds.child("problem_reported").getValue(String.class));
                                phonenumber.setText("" + ds.child("phone_number").getValue(String.class));
                                description.setText(ds.child("description").getValue(String.class));
                                custname.setText(ds.child("customer_name").getValue(String.class));
                                date.setText(ds.child("date").getValue(String.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "onCancelled: ");
                    }

                });

                deleteDialogView.findViewById(R.id.change).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(service.this, addservice.class);
                        intent.putExtra("model", model.getText().toString());
                        intent.putExtra("capacity", capacity.getText().toString());
                        intent.putExtra("description", description.getText().toString());
                        intent.putExtra("custname", custname.getText().toString());
                        intent.putExtra("problem_reported", problem.getText().toString());
                        intent.putExtra("phone_number", phonenumbers);
                        intent.putExtra("from", 1);
                        intent.putExtra("reference", reference_temp);
                        intent.putExtra("date", date.getText().toString());
                        intent.putExtra("date_returned", date.getText().toString());

                        startActivity(intent);
                    }
                });
                completed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //your business logic loge
                        Log.e(TAG, "onClick: worked");
                        if (statusicon.getVisibility() == View.VISIBLE) {
                            HashMap<String, Object> productmap = new HashMap<>();
                            productmap.put("customer_name", custname.getText().toString());
                            productmap.put("model", model.getText().toString());
                            productmap.put("capacity", capacity.getText().toString());
                            productmap.put("description", description.getText().toString());
                            productmap.put("phone_number", phonenumber.getText().toString());
                            productmap.put("date_reported", date.getText().toString());
                            productmap.put("date_completed", getDateTime());
                            productmap.put("reference", reference_temp);
                            productmap.put("status", "delivered");
                            deliveredref.child(reference_temp).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(service.this, "done", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(service.this, "Check network", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            reference.child(reference_temp).removeValue();
                            listadapteruser.notifyDataSetChanged();
                            deleteDialog.dismiss();
                        } else if (machinenumber.getText().toString().equals("LOADCELL")) {
                            HashMap<String, Object> productmap = new HashMap<>();
                            productmap.put("customer_name", custname.getText().toString());
                            productmap.put("model", model.getText().toString());
                            productmap.put("capacity", capacity.getText().toString());
                            productmap.put("description", description.getText().toString());
                            productmap.put("phone_number", phonenumber.getText().toString());
                            productmap.put("date_reported", date.getText().toString());
                            productmap.put("date_completed", getDateTime());
                            productmap.put("reference", reference_temp);
                            productmap.put("status", "delivered");
                            loadcellref.child(reference_temp).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(service.this, "done", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(service.this, "Check network", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            reference.child(reference_temp).removeValue();
                            listadapteruser.notifyDataSetChanged();
                            deleteDialog.dismiss();
//
                        } else {
                            LayoutInflater factory = LayoutInflater.from(service.this);
                            final View deleteDialogView = factory.inflate(R.layout.service_completedlayout, null);
                            final AlertDialog deleteDialog = new AlertDialog.Builder(service.this).create();
                            deleteDialog.setView(deleteDialogView);
                            EditText problem_identified = deleteDialogView.findViewById(R.id.problem_identified);
                            EditText parts_changed = deleteDialogView.findViewById(R.id.parts_changed);

                            Button completed = deleteDialogView.findViewById(R.id.done);

                            completed.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (problem_identified.getText().toString().equals("") || parts_changed.getText().toString().equals("")) {
                                        Toast.makeText(service.this, "type the blanks", Toast.LENGTH_SHORT).show();
                                    } else {
//                                        deliveredref.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot snapshot) {
//                                                if (snapshot.hasChild(mcno)) {
//                                                    Toast.makeText(enterserialnumber.this, "Machine number already used", Toast.LENGTH_SHORT).show();
//                                                } else {
//                                                    Log.e("checking", "onClick: " + mcno);
//                                                    HashMap<String, Object> productmap = new HashMap<>();
//                                                    productmap.put("customer_name", custname);
//                                                    productmap.put("model", model);
//                                                    productmap.put("capacity_accuracy", capacity_Accuracy);
//                                                    productmap.put("loadcell", loadcell);
//                                                    productmap.put("description", description);
//                                                    productmap.put("brand", brand);
//                                                    productmap.put("stamping", "S");
//                                                    productmap.put("machine_number", mcno);
//                                                    productmap.put("date", getDateTime());
//                                                    reference.child(mcno).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            if (task.isSuccessful()) {
//                                                                completed++;
////                                            goback();
//                                                            } else {
//                                                            }
//                                                        }
//                                                    });
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                            }
//                                        });
                                    }

                                }
                            });



                        deleteDialog.show();


                    }
                }
            });
                deleteDialog.show();
                listadapteruser.notifyDataSetChanged();

        }
    }

    ;


        new

    ItemTouchHelper(itemtouchlistener).

    attachToRecyclerView(swipeMenuListView);

        searchbar.addTextChangedListener(new

    TextWatcher() {
        @Override
        public void beforeTextChanged (CharSequence charSequence,int i, int i1, int i2){

        }

        @Override
        public void onTextChanged (CharSequence charSequence,int i, int i1, int i2){
            Log.e(TAG, "onTextChanged: " + charSequence.toString());
            if (!charSequence.toString().equals("")) {
//                    firebasesearch(charSequence.toString());
                listadapteruser.notifyDataSetChanged();
            }

        }

        @Override
        public void afterTextChanged (Editable editable){
        }
    });
}

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd:MM:yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
