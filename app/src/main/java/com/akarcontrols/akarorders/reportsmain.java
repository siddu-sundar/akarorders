package com.akarcontrols.akarorders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class reportsmain extends AppCompatActivity {
    Button order,production,service,visitor_call;
    DatabaseReference neworderreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportsmain);
        order=(Button)findViewById(R.id.order);
        production=(Button)findViewById(R.id.production);
        service=(Button)findViewById(R.id.service);
        visitor_call=(Button)findViewById(R.id.visitor_call_register);

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(reportsmain.this,orderreport.class);
                startActivity(intent);
            }
        });
    }
}
