package com.example.zoomwroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoomwroom.Entities.DriveRequest;
import com.example.zoomwroom.database.MyDataBase;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Create a ride Fragment
 *
 * Author : Amanda Nguyen
 * Fragment that changes dynamically depending on the status of the drive request
 * Functions are created to be called in RiderHomeActivity to hide or show certain buttons/text
 *
 * March 27, 2020
 *
 * Under the Apache 2.0 license
 */

public class FragmentCreateRide  extends BottomSheetDialogFragment {

    public FragmentCreateRide(){};

    private DriveRequest newRequest;
    private TextView driverName;
    private TextView driverUserName;
    private Button confirm;
    private Button cancel;
    private Button complete;
    private EditText fare;
    private double price;


    @Override
    public void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_ride, container, false);

        // ****** Setting up information ********

        // finding views in the XML file
        driverName = view.findViewById(R.id.driver_name);
        driverUserName = view.findViewById(R.id.driver_username);
        TextView destination = view.findViewById(R.id.destination_text);
        TextView pickup = view.findViewById(R.id.pickup_text);
        fare = view.findViewById(R.id.fare_text);
        cancel = view.findViewById(R.id.cancel_button);
        confirm = view.findViewById(R.id.confirm_button);
        complete = view.findViewById(R.id.complete_button);

        // grabbing info needed from bundle passed into the fragment
        Bundle bundle = getArguments();
        double desLat = bundle.getDouble("desLat");
        double desLon = bundle.getDouble("desLon");
        double depLat = bundle.getDouble("depLat");
        double depLon = bundle.getDouble("depLon");
        String userID = bundle.getString("userID");
        price = bundle.getDouble("price");
        price = RiderHomeActivity.round(price, 2);

        // Setting the textviews
        String des = "Lon: " + RiderHomeActivity.round(desLon,12) + " Lat: " + RiderHomeActivity.round(desLat,12);
        String dep = "Lon: " + RiderHomeActivity.round(depLon,12) + " Lat: " + RiderHomeActivity.round(depLat,12);
        String fa = Double.toString(price);
        destination.setText(des);
        pickup.setText(dep);
        fare.setText(fa);

        // ****** Setting up information ********


        // Confirm button in order to send new DriveRequest to Firebase
        confirm.setOnClickListener(v -> {
            if (newRequest == null){
                double offeredFare = Double.valueOf(fare.getText().toString());
                price = RiderHomeActivity.round(price,2);
                // Do not accept ride requests where the offer is lower than the suggested price
                if (offeredFare < price){
                    Toast.makeText(getContext(), "Fare must be a minimum of " + price, Toast.LENGTH_SHORT).show();
                }
                else{
                    LatLng departure = new LatLng(depLat, depLon);
                    LatLng destination1 = new LatLng(desLat, desLon);
                    newRequest = new DriveRequest(userID, departure, destination1);

                    // grabbing the fare offered by the user
                    newRequest.setOfferedFare(Float.valueOf(fare.getText().toString()));
                    fare.setEnabled(false);
                    MyDataBase.addRequest(newRequest);
                    Toast.makeText(getContext(), "Successfully create a ride!", Toast.LENGTH_SHORT).show();
                }

            }

        });

        // Cancel button
        // if a ride request exists, set the status to null
        // restart the activity
        cancel.setOnClickListener(v -> {
            if (newRequest != null){
                newRequest.setStatus(DriveRequest.Status.CANCELLED);
                MyDataBase.updateRequest(newRequest);
            }

            Intent intent = new Intent(getActivity(), RiderHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        });


        return view;
    }


   /**
    * When the ride request is pending, the confirm button is hidden
    * */
    public void pendingPhase(DriveRequest driveRequest){
        newRequest = driveRequest;
        confirm.setVisibility(View.GONE);
    }

    /**
     *
     * Have the driver's name, username and confirm button appear
     * Override confirm button to update the drive request
     * Driver's username is also now clickable to view
     *
     * @param driveRequest
     *  needed to access the driver's name and username
     *
     * */
    public void DriverAcceptedPhase(DriveRequest driveRequest){
        newRequest = driveRequest;
        confirm.setVisibility(View.VISIBLE);
        driverName.setVisibility(View.VISIBLE);
        driverUserName.setVisibility(View.VISIBLE);


        String stringName = MyDataBase.getDriver(driveRequest.getDriverID()).getName();
        driverName.setText(stringName);

        String stringUsername = MyDataBase.getDriver(driveRequest.getDriverID()).getUserName();
        driverUserName.setText(stringUsername);

        // overriding confirm button
        confirm.setOnClickListener(v -> confirmRidePhase(driveRequest));

        // activate function to show driver profile
        showDriverProfile(driveRequest);

    }

    /**
     *
     * Called when user wants to accept the ride from driver
     * Confirm button is invisible again
     * @param driveRequest
     *  driveRequest to update
     *
     * */
    public void confirmRidePhase(DriveRequest driveRequest){
        newRequest = driveRequest;
        driveRequest.setStatus(DriveRequest.Status.CONFIRMED);
        MyDataBase.updateRequest(driveRequest);
        confirm.setVisibility(View.GONE);
    }

    /**
     * Ride is now in progress
     * User is no longer able to cancel the ride
     * Complete the ride button is now active
     *
     * @param driveRequest
     *     driveRequest to update
     * */
    public void ridingPhase(DriveRequest driveRequest){
        newRequest = driveRequest;
        cancel.setVisibility(View.GONE);
        complete.setVisibility(View.VISIBLE);
        complete.setOnClickListener( v -> {
            driveRequest.setStatus(DriveRequest.Status.COMPLETED1);
            MyDataBase.updateRequest(driveRequest);
        });

    }

    /**
     * Allows the user to click on the username and look at driver's profile
     *
     * @param driveRequest
     *    needed to access the driver's name and username
     * */
    public void showDriverProfile(DriveRequest driveRequest){
        String driverId = driveRequest.getDriverID();
        driverUserName.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserInfoForContactActivity.class);
            intent.putExtra("USER_ID",driverId);
            startActivity(intent);
        });
    }


}
