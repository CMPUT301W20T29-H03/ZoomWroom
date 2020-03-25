package com.example.zoomwroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DriverInfosForContactActivitys extends AppCompatActivity {
    TextView driverphonenumber;
    TextView driveremailaddress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_infos_for_contact_activitys);

        driverphonenumber = findViewById(R.id.driver_info_phonenumber_content);
        driveremailaddress = findViewById(R.id.driver_info_email_content);

    }

    public void makecalltodriver(View view){
        String phone = driverphonenumber.getText().toString();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);

    }

    public void sendemailtodriver(View view){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        System.out.println(driveremailaddress.getText().toString());
        i.putExtra(Intent.EXTRA_EMAIL  ,new String[]{driveremailaddress.getText().toString()});

        try {
            startActivity(i);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(DriverInfosForContactActivitys.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
