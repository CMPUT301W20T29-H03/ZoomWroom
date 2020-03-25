package com.example.zoomwroom;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zoomwroom.Entities.DriveRequest;
import com.example.zoomwroom.database.MyDataBase;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Create a ride Fragment
 *
 * Author : Amanda Nguyen
 * Creates a ride fragment where destination and pickup markers will be replaced to where the user wants to go
 *
 * March 12, 2020
 *
 * Under the Apache 2.0 license
 */

public class FragmentCreateRide  extends BottomSheetDialogFragment {

    public FragmentCreateRide(){};


    @Override
    public void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_ride, container, false);

        Bundle bundle = getArguments();

        double desLat = bundle.getDouble("desLat");
        double desLon = bundle.getDouble("desLon");
        double depLat = bundle.getDouble("depLat");
        double depLon = bundle.getDouble("depLon");
        double price = bundle.getDouble("price");
        String userID = bundle.getString("userID");

        EditText destination = view.findViewById(R.id.destination_text);
        EditText pickup= view.findViewById(R.id.pickup_text);
        EditText fare = view.findViewById(R.id.fare_text);

        String des = "Lon: " + desLon + " Lat: " + desLat;
        String dep = "Lon: " + depLon + " Lat: " + depLat;
        String fa = Double.toString(price);
        destination.setText(des);
        pickup.setText(dep);
        fare.setText(fa);


        Button cancel = view.findViewById(R.id.cancel_button);
        Button confirm = view.findViewById(R.id.confirm_button);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng departure = new LatLng(depLat, depLon);
                LatLng destination = new LatLng(desLat, desLon);
                DriveRequest newRequest = new DriveRequest(userID, departure, destination);
                newRequest.setSuggestedFare((float) price);
                MyDataBase.addRequest(newRequest);
                Toast.makeText(getContext(), "Successfully create a ride!", Toast.LENGTH_SHORT).show();
                dismiss();





            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}
