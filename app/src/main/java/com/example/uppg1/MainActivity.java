package com.example.uppg1;

import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView txtClock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtClock = findViewById(R.id.txt_data);
        Button connectionStatus = findViewById(R.id.status_button);

        new TimeManager(txtClock);

        Log.d(TAG, "onCreate: test!");

    }



    public boolean getInternetConnection() {
        boolean isConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkList = connectivityManager.getAllNetworkInfo();
        for(NetworkInfo network : networkList){
            if (network.getTypeName().equalsIgnoreCase("WIFI"))
                if (network.isConnected())
                    isConnected = true;
            if (network.getTypeName().equalsIgnoreCase("MOBILE DATA"))
                if (network.isConnected())
                    isConnected = true;
        }
        if (isConnected) {
            Log.d(TAG, "getInternetConnection: Device online");
            return true;
        } else {
            Log.d(TAG, "getInternetConnection: Device offline.");
            return false;
        }
    }


}