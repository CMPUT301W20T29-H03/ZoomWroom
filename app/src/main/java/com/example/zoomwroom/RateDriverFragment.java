/**
 * @author Dulong Sang
 *
 * To pop out this fragment, simply add this two lines of code
 * RateDriverFragment rateDriverFragment = new RateDriverFragment(driver);
 * rateDriverFragment.show(getSupportFragmentManager(), "RATE_DRIVER");
 *
 * the caller class should implement RateDriverFragment.OnFragmentInteractionListener
 * after the user clicks the complete button, it will call listener.onConfirmedPress(int rateValue)
 * where the caller should handle three values of rateValue,
 * RateDriverFragment.NO_RATE, RateDriverFragment.POSITIVE_RATE, RateDriverFragment.NEGATIVE_RATE.
 * It is caller's responsiblity to update the driver's rating!
 */

package com.example.zoomwroom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.example.zoomwroom.Entities.Driver;

public class RateDriverFragment extends DialogFragment {
    public interface OnFragmentInteractionListener {
        void onConfirmPressed(int rateValue);   // receive the rate value
    }

    public static final int NO_RATE = 0;
    public static final int POSITIVE_RATE = 1;
    public static final int NEGATIVE_RATE = 2;

    private int rateValue = NO_RATE;
    private OnFragmentInteractionListener listener;
    private ImageView positiveButton;
    private ImageView negativeButton;

    private Driver driver;

    public RateDriverFragment(Driver driver) {
        this.driver = driver;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.rate_driver_fragment, null);
        TextView titleTextView = view.findViewById(R.id.rate_driver_title);
        positiveButton = view.findViewById(R.id.rate_driver_positive);
        negativeButton = view.findViewById(R.id.rate_driver_negative);
        Button completeButton = view.findViewById(R.id.rate_driver_complete_button);

        String driverName = driver.getUserName();
        titleTextView.setText(String.format("How did %s do?", driverName));
        positiveButton.setOnClickListener(v -> clickPositive());
        negativeButton.setOnClickListener(v -> clickNegative());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(view);
        final Dialog dialog = builder.create();
        dialog.show();
        completeButton.setOnClickListener(v -> {
            dialog.dismiss();
            listener.onConfirmPressed(rateValue);
        });

        return dialog;
    }

    private void clickPositive() {
        if (rateValue == NEGATIVE_RATE) {
            negativeButton.setImageDrawable(ResourcesCompat.getDrawable(
                    getResources(), R.drawable.thumbs_down_unsel, null));
            positiveButton.setImageDrawable(ResourcesCompat.getDrawable(
                    getResources(), R.drawable.thumbs_up_sel, null));
        } else if (rateValue == NO_RATE) {
            positiveButton.setImageDrawable(ResourcesCompat.getDrawable(
                    getResources(), R.drawable.thumbs_up_sel, null));
        }
        rateValue = POSITIVE_RATE;
    }

    private void clickNegative() {
        if (rateValue == POSITIVE_RATE) {
            negativeButton.setImageDrawable(ResourcesCompat.getDrawable(
                    getResources(), R.drawable.thumbs_down_sel, null));
            positiveButton.setImageDrawable(ResourcesCompat.getDrawable(
                    getResources(), R.drawable.thumbs_up_unsel, null));
        } else if (rateValue == NO_RATE) {
            negativeButton.setImageDrawable(ResourcesCompat.getDrawable(
                    getResources(), R.drawable.thumbs_down_sel, null));
        }
        rateValue = NEGATIVE_RATE;
    }
}
