package com.akarcontrols.akarorders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.InputType;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class currentorders extends AppCompatActivity {
    TextView totalcount;
    RecyclerView swipeMenuListView;
    EditText searchbar;
    FloatingActionButton addorder;
    String lrnum, boxc, phonenumbertemp, transport;
    DatabaseReference reference, snoref, deliveredref, loadcellref, cancelledref;
    int totalint = 0, qty = 0;
    FirebaseRecyclerOptions<UserHelperClass> arrayList;
    FirebaseRecyclerAdapter<UserHelperClass, Listadapteruser> listadapteruser;
    String TAG = "appscontent", capacity, accuracy, phonenumbers, reference_temp, sendlr;
    int serial_no;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currentorder);

        searchbar = (EditText) findViewById(R.id.searchbar);
        totalcount = (TextView) findViewById(R.id.total);
        swipeMenuListView = (RecyclerView) findViewById(R.id.swipemenulistview);
        addorder = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        swipeMenuListView.setLayoutManager(new LinearLayoutManager(currentorders.this));
        reference = FirebaseDatabase.getInstance().getReference().child("orders").child("pending");
        cancelledref = FirebaseDatabase.getInstance().getReference().child("orders").child("cancelled");
        deliveredref = FirebaseDatabase.getInstance().getReference().child("orders").child("delivered");
        loadcellref = FirebaseDatabase.getInstance().getReference().child("orders").child("loadcell sales");
//        snoref = FirebaseDatabase.getInstance().getReference().child("orders").child("serial_number");
        snoref = FirebaseDatabase.getInstance().getReference().child("orders").child("production_record").child("February").child("01:02:2021");
        snoref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                    serial_no = Integer.parseInt(ds.child("brand").getValue(String.class));
                    String serial_no =ds.child("brand").getValue(String.class);
                    Log.e(TAG, "onDataChange: "+serial_no );
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ");
            }

        });

        addorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(currentorders.this, addorders.class);
                startActivity(intent);
            }
        });

        arrayList = new FirebaseRecyclerOptions.Builder<UserHelperClass>().setQuery(reference, UserHelperClass.class).build();
        listadapteruser = new FirebaseRecyclerAdapter<UserHelperClass, Listadapteruser>(arrayList) {
            @Override
            protected void onBindViewHolder(@NonNull Listadapteruser holder, int i, @NonNull UserHelperClass userHelperClass) {
                qty = Integer.parseInt(userHelperClass.getQuantity());
                totalint = totalint + qty;
                Log.e(TAG, "onBindViewHolder: " + totalint + "." + qty);
                holder.custname.setText("" + userHelperClass.getCustomer_name());
                holder.model.setText("" + userHelperClass.getModel());
                holder.capacity_accuracy.setText("" + userHelperClass.getCapacity() + "/" + userHelperClass.getAccuracy());
                holder.brand.setText("" + userHelperClass.getBrand());
                holder.quantity.setText("Qty: " + userHelperClass.getQuantity());
                holder.reference.setText("" + userHelperClass.getReference());
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
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listviewformat, parent, false);
                return new Listadapteruser(v);
            }

        };

        listadapteruser.startListening();
        swipeMenuListView.setAdapter(listadapteruser);


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
                reference = FirebaseDatabase.getInstance().getReference().child("orders").child("pending");
                LayoutInflater factory = LayoutInflater.from(currentorders.this);
                final View deleteDialogView = factory.inflate(R.layout.viewdetails_layout, null);
                final AlertDialog deleteDialog = new AlertDialog.Builder(currentorders.this).create();
                deleteDialog.setView(deleteDialogView);
                TextView modeltext = deleteDialogView.findViewById(R.id.modeltext);
                TextView capacity_accuracytext = deleteDialogView.findViewById(R.id.capacity_accuracytext);
                TextView brandtext = deleteDialogView.findViewById(R.id.brandtext);
                TextView stampingtext = deleteDialogView.findViewById(R.id.stampingtext);
                TextView phonenumbertext = deleteDialogView.findViewById(R.id.phonenumbertext);
                TextView deliverytext = deleteDialogView.findViewById(R.id.deliverytext);
                TextView descriptiontext = deleteDialogView.findViewById(R.id.descriptiontext);
                TextView loadcelltext = deleteDialogView.findViewById(R.id.loadcelltext);
                TextView datetext = deleteDialogView.findViewById(R.id.datetext);

                TextView model = deleteDialogView.findViewById(R.id.model);
                TextView capacity_accuracy = deleteDialogView.findViewById(R.id.capacity_accuracy);
                TextView brand = deleteDialogView.findViewById(R.id.brand);
                TextView stamping = deleteDialogView.findViewById(R.id.stamping);
                TextView phonenumber = deleteDialogView.findViewById(R.id.phonenumber);
                TextView delivery = deleteDialogView.findViewById(R.id.delivery);
                TextView description = deleteDialogView.findViewById(R.id.description);
                TextView loadcell = deleteDialogView.findViewById(R.id.loadcell);
                TextView custname = deleteDialogView.findViewById(R.id.custname);
                TextView qty = deleteDialogView.findViewById(R.id.qty);
                TextView date = deleteDialogView.findViewById(R.id.date);

                Button completed = deleteDialogView.findViewById(R.id.completed);
                if (statusicon.getVisibility() == View.VISIBLE || loadcell.getText().toString().equals("Loadcell")) {
                    completed.setText("DELIVERED");
                } else {
                    completed.setText("Completed");
                }


                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.e(TAG, "onDataChange: " + ds.child("model"));
                            if (ds.child("reference").getValue(String.class).equals(ref)) {
                                model.setText(ds.child("model").getValue(String.class));
                                capacity_accuracy.setText(ds.child("capacity").getValue(String.class) + "/" + ds.child("accuracy").getValue(String.class));
                                brand.setText(ds.child("brand").getValue(String.class));
                                capacity = ds.child("capacity").getValue(String.class);
                                accuracy = ds.child("accuracy").getValue(String.class);
                                phonenumbers = ds.child("phone_number").getValue(String.class);
                                sendlr = ds.child("send_lr").getValue(String.class);
                                reference_temp = ds.child("reference").getValue(String.class);
                                stamping.setText(ds.child("stamping").getValue(String.class));
                                phonenumber.setText("" + ds.child("phone_number").getValue(String.class));
                                delivery.setText(ds.child("delivery").getValue(String.class));
                                description.setText(ds.child("description").getValue(String.class));
                                loadcell.setText(ds.child("loadcell").getValue(String.class));
                                custname.setText(ds.child("customer_name").getValue(String.class));
                                qty.setText(ds.child("quantity").getValue(String.class));
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
                        Intent intent = new Intent(currentorders.this, addorders.class);
                        intent.putExtra("model", model.getText().toString());
                        intent.putExtra("capacity", capacity);
                        intent.putExtra("accuracy", accuracy);
                        intent.putExtra("brand", brand.getText().toString());
                        intent.putExtra("stamping", stamping.getText().toString());
                        intent.putExtra("description", description.getText().toString());
                        intent.putExtra("loadcell", loadcell.getText().toString());
                        intent.putExtra("customer_name", custname.getText().toString());
                        intent.putExtra("qty", qty.getText().toString());
                        intent.putExtra("serial_number", serial_no);
                        intent.putExtra("phone_number", phonenumbers);
                        intent.putExtra("delivery", delivery.getText().toString());
                        intent.putExtra("from", 1);
                        intent.putExtra("reference", reference_temp);
                        intent.putExtra("sendlr", sendlr);

                        startActivity(intent);
                    }
                });
                deleteDialogView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LayoutInflater factory = LayoutInflater.from(currentorders.this);
                        final View deleteDialogView = factory.inflate(R.layout.sendlrlayout, null);
                        final AlertDialog deleteDialogin = new AlertDialog.Builder(currentorders.this).create();
                        deleteDialogin.setView(deleteDialogView);
                        TextView maintext = deleteDialogView.findViewById(R.id.textmain);
                        EditText lrnumber = deleteDialogView.findViewById(R.id.lrnumber);
                        lrnumber.setHint("Enter reason for order cancellation");
                        EditText boxcount = deleteDialogView.findViewById(R.id.boxes);
                        boxcount.setInputType(InputType.TYPE_CLASS_NUMBER);
                        Button cancel = deleteDialogView.findViewById(R.id.cancel);
                        Button send = deleteDialogView.findViewById(R.id.sendlr);
                        send.setText("Back");
                        maintext.setText("Are you sure to cancel the order?");
                        boxcount.setVisibility(View.GONE);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (!lrnumber.getText().toString().equals("")) {
                                    reference.child(reference_temp).removeValue();
                                    HashMap<String, Object> productmap = new HashMap<>();
                                    productmap.put("customer_name", custname.getText().toString());
                                    productmap.put("model", model.getText().toString());
                                    productmap.put("quantity", qty.getText().toString());
                                    productmap.put("description", description.getText().toString());
                                    productmap.put("phone_number", phonenumber.getText().toString());
                                    productmap.put("brand", brand.getText().toString());
                                    productmap.put("date_ordered", date.getText().toString());
                                    productmap.put("stamping", stamping.getText().toString());
                                    productmap.put("reference", reference_temp);
                                    productmap.put("status", "cancelled");
                                    productmap.put("reason", lrnumber.getText().toString());
                                    cancelledref.child(reference_temp).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(currentorders.this, "cancelled", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(currentorders.this, "Check network", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    deleteDialogin.dismiss();
                                    deleteDialog.dismiss();


                                }
                            }
                        });
                        send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteDialogin.dismiss();
                            }
                        });
                        deleteDialogin.show();
                        listadapteruser.notifyDataSetChanged();
                    }
                });

                completed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //your business logic loge
                        Log.e(TAG, "onClick: worked");
                        if (completed.getText().toString().equals("DELIVERED")) {
                            HashMap<String, Object> productmap = new HashMap<>();
                            productmap.put("customer_name", custname.getText().toString());
                            productmap.put("model", model.getText().toString());
                            productmap.put("capacity_accuracy", capacity_accuracy.getText().toString());
                            productmap.put("quantity", qty.getText().toString());
                            productmap.put("loadcell", loadcell.getText().toString());
                            productmap.put("delivery", delivery.getText().toString());
                            productmap.put("description", description.getText().toString());
                            productmap.put("phone_number", phonenumber.getText().toString());
                            productmap.put("brand", brand.getText().toString());
                            productmap.put("date_ordered", date.getText().toString());
                            productmap.put("date_completed", getDateTime());
                            productmap.put("stamping", stamping.getText().toString());
                            productmap.put("reference", reference_temp);
                            productmap.put("status", "delivered");
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
                            String month_name = month_date.format(cal.getTime());
                            int year = Calendar.getInstance().get(Calendar.YEAR);

                            deliveredref.child(String.valueOf(year)).child(month_name).child(getDateTime()).child(reference_temp).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
//                                        Toast.makeText(currentorders.this, "done", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(currentorders.this, "Check network", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            if (sendlr.equals("yes")) {


                                LayoutInflater factory = LayoutInflater.from(currentorders.this);
                                final View deleteDialogView = factory.inflate(R.layout.sendlrlayout, null);
                                final AlertDialog deleteDialog = new AlertDialog.Builder(currentorders.this).create();
                                deleteDialog.setView(deleteDialogView);
                                TextView maintext = deleteDialogView.findViewById(R.id.textmain);
                                EditText lrnumber = deleteDialogView.findViewById(R.id.lrnumber);
                                lrnumber.setInputType(InputType.TYPE_CLASS_NUMBER);
                                EditText boxcount = deleteDialogView.findViewById(R.id.boxes);
                                boxcount.setInputType(InputType.TYPE_CLASS_NUMBER);
                                Button cancel = deleteDialogView.findViewById(R.id.cancel);
                                Button send = deleteDialogView.findViewById(R.id.sendlr);
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        deleteDialog.dismiss();
                                    }
                                });
                                send.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        lrnum = lrnumber.getText().toString();
                                        boxc = boxcount.getText().toString();
                                        phonenumbertemp = "91" + phonenumber.getText().toString();
                                        transport = delivery.getText().toString();
                                        phonenumber.setEnabled(false);
                                        delivery.setEnabled(false);
                                        boxcount.setEnabled(false);
                                        lrnumber.setEnabled(false);
                                        deleteDialog.dismiss();
//                                        new MyTask().execute();
                                    }
                                });
                                deleteDialog.show();
                                listadapteruser.notifyDataSetChanged();
                            }
                            reference.child(reference_temp).removeValue();
                            listadapteruser.notifyDataSetChanged();
                            deleteDialog.dismiss();
                        } else if (loadcell.getText().toString().equals("Loadcell")) {
                            HashMap<String, Object> productmap = new HashMap<>();
                            productmap.put("customer_name", custname.getText().toString());
                            productmap.put("model", model.getText().toString());
                            productmap.put("capacity", capacity_accuracy.getText().toString());
                            productmap.put("quantity", qty.getText().toString());
                            productmap.put("delivery", delivery.getText().toString());
                            productmap.put("description", description.getText().toString());
                            productmap.put("phone_number", phonenumber.getText().toString());
                            productmap.put("date_ordered", date.getText().toString());
                            productmap.put("date_completed", getDateTime());
                            productmap.put("reference", reference_temp);
                            productmap.put("status", "delivered");
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat month_date = new SimpleDateFormat("MMMM");
                            String month_name = month_date.format(cal.getTime());
                            int year = Calendar.getInstance().get(Calendar.YEAR);
                            loadcellref.child(String.valueOf(year)).child(month_name).child(getDateTime()).child(reference_temp).updateChildren(productmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(currentorders.this, "done", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(currentorders.this, "Check network", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            reference.child(reference_temp).removeValue();
                            listadapteruser.notifyDataSetChanged();
                            deleteDialog.dismiss();

                        } else {
                            Intent intent = new Intent(currentorders.this, enterserialnumber.class);
                            intent.putExtra("model", model.getText().toString());
                            intent.putExtra("capacity_accuracy", capacity_accuracy.getText().toString());
                            intent.putExtra("brand", brand.getText().toString());
                            intent.putExtra("stamping", stamping.getText().toString());
                            intent.putExtra("description", description.getText().toString());
                            intent.putExtra("loadcell", loadcell.getText().toString());
                            intent.putExtra("custname", custname.getText().toString());
                            intent.putExtra("qty", qty.getText().toString());
                            intent.putExtra("serial_number", serial_no);
                            intent.putExtra("reference", reference_temp);
                            intent.putExtra("send_lr", sendlr);
                            startActivity(intent);
                        }
                    }
                });
                deleteDialog.show();
                listadapteruser.notifyDataSetChanged();

            }
        };


        new

                ItemTouchHelper(itemtouchlistener).

                attachToRecyclerView(swipeMenuListView);

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e(TAG, "onTextChanged: " + charSequence.toString());
//                if (!charSequence.toString().equals("")) {
//                    firebasesearch(charSequence.toString());
//                    listadapteruser.notifyDataSetChanged();
//                }else{
//                    listadapteruser.notifyDataSetChanged();
//                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().equals("")) {
                    firebasesearch(editable.toString());
                    listadapteruser.notifyDataSetChanged();
                }else{
                    listadapteruser.notifyDataSetChanged();
                }
            }
        });
    }

    private void firebasesearch(String toString) {
        totalint = 0;

        arrayList = new FirebaseRecyclerOptions.Builder<UserHelperClass>().setQuery(reference, UserHelperClass.class).build();
        listadapteruser = new FirebaseRecyclerAdapter<UserHelperClass, Listadapteruser>(arrayList) {
            @Override
            protected void onBindViewHolder(@NonNull Listadapteruser holder, int i, @NonNull UserHelperClass userHelperClass) {
                String custname = userHelperClass.getCustomer_name();
                if (custname.contains(toString)) {
                    qty = Integer.parseInt(userHelperClass.getQuantity());
                    totalint = totalint + qty;
                    Log.e(TAG, "onBindViewHolder: " + totalint + "." + qty);
                    holder.custname.setText("" + userHelperClass.getCustomer_name());
                    holder.model.setText("" + userHelperClass.getModel());
                    holder.capacity_accuracy.setText("" + userHelperClass.getCapacity() + "/" + userHelperClass.getAccuracy());
                    holder.brand.setText("" + userHelperClass.getBrand());
                    holder.quantity.setText("Qty: " + userHelperClass.getQuantity());
                    holder.reference.setText("" + userHelperClass.getReference());
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
                    holder.itemView.setVisibility(View.VISIBLE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,225));
                } else {
//                    holder.itemView.setVisibility(View.GONE);
//                    swipeMenuListView.removeViewAt(0);
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                    Log.e(TAG, "onBindViewHolder: " + holder.getAdapterPosition());

                }


            }

            @NonNull
            @Override
            public Listadapteruser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listviewformat, parent, false);
                return new Listadapteruser(v);
            }

        };
        listadapteruser.startListening();
        swipeMenuListView.setAdapter(listadapteruser);

    }


    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd:MM:yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    private class MyTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // Construct data
                String apiKey = "apikey=" + "JqLIK9rS6bg-vxDNHSwCoUAB7fGGdwTk2ixkZ9RQnI";
                Log.e(TAG, "doInBackground: " + lrnum + "." + boxc + "." + phonenumbertemp + "." + transport);
                String message = "&message="
                        + "Dear user,%nYour package has been booked with " + transport + ".%n %nLR number : " + lrnum + "%nNo. of boxes : " + boxc + "%n %nThanks for choosing Akar Controls,%nPh : 9444027384";


                String sender = "&sender=" + "AKARCH";
                String numbers = "&numbers=" + phonenumbertemp;
                Log.e(TAG, "doInBackground: " + message);
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
