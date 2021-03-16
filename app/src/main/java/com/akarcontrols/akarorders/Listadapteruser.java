package com.akarcontrols.akarorders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.data.DataHolder;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class Listadapteruser extends RecyclerView.ViewHolder {
    TextView custname, model, capacity_accuracy, brand, reference, quantity, capacity, date,personname,companyname,intime;
    ImageView status;

    public Listadapteruser(View itemview) {
        super(itemview);
        custname = (TextView) itemView.findViewById(R.id.custname);
        model = (TextView) itemView.findViewById(R.id.model);
        capacity_accuracy = (TextView) itemView.findViewById(R.id.capacity_accuracy);
        brand = (TextView) itemView.findViewById(R.id.brand);
        quantity = (TextView) itemView.findViewById(R.id.qty);
        reference = (TextView) itemView.findViewById(R.id.referenceid);
        status = (ImageView) itemView.findViewById(R.id.complete_icon);

    }

    public Listadapteruser(View v, int i) {
        super(v);
        if (i == 1) {
            custname = (TextView) itemView.findViewById(R.id.custname);
            capacity = (TextView) itemView.findViewById(R.id.capacity);
            date = (TextView) itemView.findViewById(R.id.datereturn);
            reference = (TextView) itemView.findViewById(R.id.referenceid);
            model = (TextView) itemView.findViewById(R.id.model);
            status = (ImageView) itemView.findViewById(R.id.complete_icon);
        }else if(i==2){
            companyname = (TextView) itemView.findViewById(R.id.companyname);
            personname = (TextView) itemView.findViewById(R.id.personname);
            intime = (TextView) itemView.findViewById(R.id.intime);
        }
    }

}
