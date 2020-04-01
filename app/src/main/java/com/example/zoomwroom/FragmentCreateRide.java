package com.example.zoomwroom;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoomwroom.Entities.DriveRequest;
import com.example.zoomwroom.Entities.Driver;
import com.example.zoomwroom.database.MyDataBase;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Create a ride Fragment
 *
 * Author : Amanda Nguyen
 * Fragment that changes dynamically depending on the status of the drive request
 * Functions are created to be called in RiderHomeActivity to hide or show certain buttons/text
 *
 * last update Apr 1, 2020
 *
 * Under the Apache 2.0 license
 */

public class FragmentCreateRide  extends BottomSheetDialogFragment {

    public FragmentCreateRide(){};

    private DriveRequest request;
    private TextView driverName;
    private TextView driverUserName;
    private Button confirm;
    private Button cancel;
    private Button complete;
    private EditText fare;
    private double price;
    private TextView destination;
    private TextView pickup;


    private static final int RESULT_OK = -1;
    private static final int PICKUP_REQUEST = 1;
    private static final int DESTINATION_REQUEST = 2;


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
        destination = view.findViewById(R.id.destination_text);
        pickup = view.findViewById(R.id.pickup_text);
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
        price = FareCalculation.round(price, 2);

        // Setting the textviews
        String des = "Lon: " + FareCalculation.round(desLon, 12) + " Lat: " + FareCalculation.round(desLat, 12);
        String dep = "Lon: " + FareCalculation.round(depLon, 12) + " Lat: " + FareCalculation.round(depLat, 12);
        String fa = Double.toString(price);
        destination.setText(des);
        pickup.setText(dep);
        fare.setText(fa);

        // ****** Setting up information ********


        request = new DriveRequest();
        request.setRiderID(userID);
        request.setSuggestedFare((float) price);
        request.setPickupLocation(new LatLng(depLat, depLon));
        request.setDestination(new LatLng(desLat, desLon));
        // Confirm button in order to send new DriveRequest to Firebase
        confirm.setOnClickListener(v -> {
            if (request == null){
                double offeredFare = Double.parseDouble(fare.getText().toString());
                // Do not accept ride requests where the offer is lower than the suggested price
                if (offeredFare < price){
                    Toast.makeText(getContext(), "Fare must be a minimum of " + price, Toast.LENGTH_SHORT).show();
                    return;
                }
                // grabbing the fare offered by the user
                request.setOfferedFare(Float.parseFloat(fare.getText().toString()));
                fare.setEnabled(false);
                MyDataBase.getInstance().addRequest(request);
                Toast.makeText(getContext(), "Successfully create a ride!", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancel button
        // if a ride request exists, set the status to null
        // restart the activity
        cancel.setOnClickListener(v -> {
            if (request != null){
                request.setStatus(DriveRequest.Status.CANCELLED);
                MyDataBase.getInstance().updateRequest(request);
            }

            Intent intent = new Intent(getActivity(), RiderHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        });

        if (!Places.isInitialized()) {
            Places.initialize(getContext(), getResources().getString(R.string.places_api_key), Locale.CANADA);
        }

        enablePlaceSearch();

        return view;
    }


   /**
    * When the ride request is pending, the confirm button is hidden
    * */
    public void pendingPhase(DriveRequest driveRequest){
        request = driveRequest;
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
        request = driveRequest;
        confirm.setVisibility(View.VISIBLE);
        driverName.setVisibility(View.VISIBLE);
        driverUserName.setVisibility(View.VISIBLE);

        Driver driver = MyDataBase.getInstance().getDriver(driveRequest.getDriverID());
        assert driver != null;

        String stringName = driver.getName();
        driverName.setText(stringName);

        String stringUsername = driver.getUserName();
        driverUserName.setText(stringUsername);

        // overriding confirm button
        confirm.setOnClickListener(v -> {
            driveRequest.setStatus(DriveRequest.Status.CONFIRMED);
            MyDataBase.getInstance().updateRequest(driveRequest);
        });

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
        request = driveRequest;
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
        request = driveRequest;
        cancel.setVisibility(View.GONE);
        confirm.setVisibility(View.GONE);
        complete.setVisibility(View.VISIBLE);
        complete.setOnClickListener(v -> {
            driveRequest.setStatus(DriveRequest.Status.COMPLETED);
            MyDataBase.getInstance().updateRequest(driveRequest);
        });

    }

    /**
     * Allows the user to click on the username and look at driver's profile
     *
     * @param driveRequest
     *    needed to access the driver's name and username
     * */
    public void showDriverProfile(DriveRequest driveRequest){
        final String driverId = driveRequest.getDriverID();
        driverName.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserContactActivity.class);
            intent.putExtra("USER_ID", driverId);
            startActivity(intent);
        });
    }


    private void enablePlaceSearch() {
        destination.setClickable(true);
        pickup.setClickable(true);

        destination.setOnClickListener( v -> {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(getActivity());
            startActivityForResult(intent, DESTINATION_REQUEST);
        });

        pickup.setOnClickListener( v -> {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(getActivity());
            startActivityForResult(intent, PICKUP_REQUEST);
        });
    }

    private void disablePlaceSearch() {
        destination.setClickable(false);
        pickup.setClickable(false);
    }

    private void displayPlaceName() {
        if (request == null) {
            return;
        }

        String pickupLocationName = request.getPickupLocationName();
        String destinationName = request.getDestinationName();

        if (pickupLocationName != null) {
            pickup.setText(pickupLocationName);
        }

        if (destinationName != null) {
            destination.setText(destinationName);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(data);

            if (requestCode == DESTINATION_REQUEST) {
                request.setDestinationName(place.getName());
                request.setDestination(place.getLatLng());
            } else if (requestCode == PICKUP_REQUEST) {
                request.setPickupLocationName(place.getName());
                request.setPickupLocation(place.getLatLng());
            } else {
                return;
            }

            displayPlaceName();

            // update markers
            LatLng destinationLatLng = request.getDestination();
            LatLng pickupLatLng = request.getPickupLocation();

            if (destinationLatLng != null && pickupLatLng != null) {
                ((RiderHomeActivity) getActivity()).setMarkers(destinationLatLng, pickupLatLng);
            }

        } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
            // Handle the error.
            Status status = Autocomplete.getStatusFromIntent(data);
            Log.i("PLACES", status.getStatusMessage());
        }
    }
}
