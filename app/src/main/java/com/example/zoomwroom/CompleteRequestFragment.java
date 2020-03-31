package com.example.zoomwroom;

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
 * Mar 29, 2020
 */

public class CompleteRequestFragment extends BottomSheetDialogFragment
        implements RateDriverFragment.OnFragmentInteractionListener {

    private Driver driver;
    private DriveRequest driveRequest;

    @Override
    public void onCreate(Bundle savedInstancesState) {
        super.onCreate(savedInstancesState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.complete_request_fragment, container, false);
        Bundle bundle = getArguments();
        assert bundle != null;
        driveRequest = (DriveRequest) bundle.getSerializable("driveRequest");
        assert driveRequest != null;

        driver = MyDataBase.getDriver(driveRequest.getDriverID());
        assert driver != null;

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
            // TODO: complete
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
