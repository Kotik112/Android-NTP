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

import java.util.Date;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView txtClock;
    private Button connectionStatus;
    private TimeManager timeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing UI elements
        txtClock = findViewById(R.id.txt_data);
        connectionStatus = findViewById(R.id.status_button);

        // Running and creating a timeManager object.
        timeManager = new TimeManager(txtClock);

        /*
        Checks if the device has an internet connection and sets timeManager's connection
        status to match the device status.
        This variable is in turn used to adjust the timeManager's behaviour accordingly.
        */
        if (isConnectedToInternet()) { //
            Toast.makeText(this, "Online mode", Toast.LENGTH_SHORT).show();
            timeManager.isConnected = true;
        } else {

            Toast.makeText(this, "Offline mode", Toast.LENGTH_SHORT).show();
            timeManager.isConnected = false;

        }
        
    }

    // Checks for an internet connection when pressed. Used to check the network status.
    public void onPress(View view) {
        Log.d(TAG, "onPress: checking connection!");
        if (isConnectedToInternet()) {
            Log.d(TAG, "onPress: Running online mode.");
            connectionStatus.setText("Online");
        } else {
            Log.d(TAG, "onCreate: Running offline mode.");
            connectionStatus.setText("Offline");
        }
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        Log.v(this.getClass().getName(), info[i].toString());
                        return true;
                    }
        }
        return false;
    }
}