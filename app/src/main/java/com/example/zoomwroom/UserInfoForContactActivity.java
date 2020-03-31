package com.example.zoomwroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zoomwroom.Entities.Driver;
import com.example.zoomwroom.Entities.Rider;
import com.example.zoomwroom.Entities.User;
import com.example.zoomwroom.database.MyDataBase;

public class UserInfoForContactActivity extends AppCompatActivity {
    private User user;
    private TextView nameTextView;
    private TextView usernameTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private View ratingView;
    private TextView numThumbsUpTextView;
    private TextView numThumbsDownTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_for_contact_activity);

        findViews();
        phoneTextView.setOnClickListener( v -> {
            makeCall();
        });

        emailTextView.setOnClickListener( v -> {
            sendEmail();
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        //retrieve userID from intent
        Intent intent = getIntent();
        String userID = intent.getStringExtra("USER_ID");

        // retrieve the user
        user = MyDataBase.getDriver(userID);
        if (user == null) {
            user = MyDataBase.getRider(userID);
            if (user == null) {
                Toast.makeText(UserInfoForContactActivity.this,
                        "Something went wrong.", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        
        nameTextView.setText(user.getName());
        usernameTextView.setText(user.getUserName());
        phoneTextView.setText(user.getContactDetails().getPhoneNumber());
        emailTextView.setText(user.getContactDetails().getEmail());

        if (user instanceof Rider) {
            // hide rating view group
            ratingView.setVisibility(View.INVISIBLE);
            return;
        }

        // set num of thumbs up and down
        numThumbsUpTextView.setText(String.valueOf(((Driver) user).getRating().getThumbsUp()));
        numThumbsDownTextView.setText(String.valueOf(((Driver) user).getRating().getThumbsDown()));
    }

    public void makeCall(){
        String phone = user.getContactDetails().getPhoneNumber();
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }

    public void sendEmail(){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{user.getContactDetails().getEmail()});

        try {
            startActivity(i);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(UserInfoForContactActivity.this,
                    "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }


    private void findViews() {
        nameTextView = findViewById(R.id.user_info_name);
        usernameTextView = findViewById(R.id.user_info_username);
        phoneTextView = findViewById(R.id.user_info_phone);
        emailTextView = findViewById(R.id.user_info_email);
        ratingView = findViewById(R.id.user_info_rating);
        numThumbsUpTextView = findViewById(R.id.user_info_num_thumbs_up);
        numThumbsDownTextView = findViewById(R.id.user_info_num_thumbs_down);
    }

}
