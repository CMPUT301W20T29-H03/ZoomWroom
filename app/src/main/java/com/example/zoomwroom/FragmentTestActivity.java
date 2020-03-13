package com.example.zoomwroom;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.zoomwroom.Entities.Driver;

public class FragmentTestActivity extends AppCompatActivity
        implements RateDriverFragment.OnFragmentInteractionListener {

    private String driverName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_test_activity);

        Button button1 = findViewById(R.id.fragment_test_button1);
        button1.setOnClickListener(v -> {
            new RateDriverFragment(new Driver("Test Driver", driverName, "uid"))
                .show(getSupportFragmentManager(), "RATE_DRIVER");
        });
    }

    @Override
    public void onConfirmPressed(int rateValue) {
        TextView textView = findViewById(R.id.fragment_test_text_view);
        if (rateValue == RateDriverFragment.POSITIVE_RATE) {
            textView.setText("THUMBS UP");
        } else if (rateValue == RateDriverFragment.NEGATIVE_RATE) {
            textView.setText("THUMBS DOWN");
        } else {
            textView.setText("NO RATE");
        }
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverName() {
        return driverName;
    }
}
