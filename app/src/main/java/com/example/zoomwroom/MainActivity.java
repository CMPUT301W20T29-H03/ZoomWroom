package com.example.zoomwroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Map;

import com.example.zoomwroom.Entities.DriveRequest;
import com.example.zoomwroom.Entities.Driver;
import com.example.zoomwroom.Entities.Rider;
import com.example.zoomwroom.database.MyDataBase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.android.volley.Response;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    final private String serverKey = "key=" + "AIzaSyCzF5PRcdDADRymHeRF2piC4TOUDIflvZM";
    final private String contentType = "application/json";
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //////////////////////
        // Driver Button
        Button driverBT = findViewById(R.id.DriverBT);
        driverBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenActivityDriverMode();
            }
        });
        //Rider Button
        Button riderBT = findViewById(R.id.RiderBT);
        riderBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenActivityRiderMode();
            }
        });
    }

    ////////////////////////////////////////////////

    public void OpenActivityRiderMode(){
        Intent intent = new Intent(this,RiderModeActivity.class);
        startActivity(intent);
    }

    //OpenActivityDriverMode
    public void OpenActivityDriverMode() {

        Intent intent = new Intent(this,DriverModeActivity.class);
        startActivity(intent);

    }
    ///////////////////////////////////////////////

    // call new Notify().execute() to run
    public class Notify extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... voids) {


            try {
                Log.d("getToken", token);
                URL url = new URL("https://fcm.googleapis.com/fcm/send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", serverKey);
                conn.setRequestProperty("Content-Type", contentType);

                JSONObject json = new JSONObject();

                json.put("to", token);

                JSONObject info = new JSONObject();
                info.put("title", "Hey");   // Notification title
                info.put("body", "Hello"); // Notification body

                json.put("notification", info);

                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(json.toString());
                wr.flush();
                conn.getInputStream();

            }
            catch (Exception e)
            {
                Log.d("Error",""+e);
            }

            return null;
        }
    }
}
