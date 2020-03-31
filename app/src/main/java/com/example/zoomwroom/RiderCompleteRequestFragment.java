package com.example.zoomwroom;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.zoomwroom.Entities.DriveRequest;
import com.example.zoomwroom.Entities.Driver;
import com.example.zoomwroom.database.MyDataBase;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * @author Dulong Sang
 * last update: Mar 31, 2020
 */

public class RiderCompleteRequestFragment extends BottomSheetDialogFragment
        implements RateDriverFragment.OnFragmentInteractionListener {

    private Driver driver;
    private DriveRequest driveRequest;

    public RiderCompleteRequestFragment(DriveRequest driveRequest) {
        this.driveRequest = driveRequest;
    }

    @Override
    public void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.complete_request_fragment, container, false);

        // findViews
        Button generateQRButton;
        Button rateDriverButton;
        Button completeButton;

        generateQRButton = view.findViewById(R.id.complete_request_generate_qr_button);
        rateDriverButton = view.findViewById(R.id.complete_request_rate_driver_button);
        completeButton = view.findViewById(R.id.complete_request_complete_button);

        generateQRButton.setOnClickListener(v -> {
            QRDisplayFragment qrDisplayFragment = new QRDisplayFragment(driveRequest.toQRBucksString());
            qrDisplayFragment.show(getFragmentManager(), "QR_Display");
        });

        rateDriverButton.setOnClickListener(v -> {
            RateDriverFragment rateDriverFragment = new RateDriverFragment(driver);
            rateDriverFragment.show(getFragmentManager(), "Rate_Driver");
        });

        completeButton.setText(String.format("Rate %s", driver.getName()));
        completeButton.setOnClickListener(v -> {
            // update the request status
            driveRequest.setStatus(DriveRequest.Status.COMPLETED);
            MyDataBase.updateRequest(driveRequest);

            // close this fragment
            Intent intent = new Intent(getActivity(), RiderHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onConfirmPressed(int rateValue) {
        if (rateValue == RateDriverFragment.NO_RATE) {
            return;
        }

        driver = MyDataBase.getDriver(driveRequest.getDriverID());
        assert driver != null;
        if (rateValue == RateDriverFragment.POSITIVE_RATE) {
            driver.getRating().addRating(true);
        } else if (rateValue == RateDriverFragment.NEGATIVE_RATE) {
            driver.getRating().addRating(false);
        }
        MyDataBase.updateDriver(driver);
    }
}
