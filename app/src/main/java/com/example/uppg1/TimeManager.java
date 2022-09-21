package com.example.uppg1;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

public class TimeManager {
    private static final String TAG = "TimeManager";
    
    private NTPUDPClient client = null;
    private InetAddress inetAddress = null;
    private TimeInfo NTPTime = null;
    private Handler timeHandler = null;
    private Runnable timeRunnable;
    String timeString = "";
    private long timeReturned, offset;
    //Used by MainActivity to set the connection status. Defaults to false (no connection)
    public boolean isConnected = false;


    /* https://www.pool.ntp.org/zone/se
    List of NTP Servers in Sweden.
    I decided on "0.se.pool.ntp.org" after comparing the response time of the available servers.

    Ping statistics for 0.se.pool.ntp.org:
        Packets: Sent = 4, Received = 4, Lost = 0 (0% loss),
    Approximate round trip times in milli-seconds:
        Minimum = 7ms, Maximum = 7ms, Average = 7ms
     */
    private final static String NTPServer = "0.se.pool.ntp.org";

    public TimeManager(TextView clockText) {
        if (timeHandler == null) {
            timeHandler = new Handler();
        }
        timeRunnable = new Runnable() {
            /* Having a Runnable inside a while(true) loop was the only way I could
            find to access an element from a java class that belongs to the MainActivity. */
            @Override
            public void run() {
                // Set the TextView in MainActivity to timeString.
                clockText.setText(timeString);
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                while(true) {
                    // Posts or performs the timeRunnable's run function inside the infinite loop.
                    timeHandler.post(timeRunnable);
                    SystemClock.sleep(1000);
                    Log.d(TAG, "run: TimeManager is updating the time");
                    timeString = getTime().toString();
                }
            }
        };
        thread.start();
    }

    public long handleAttempts(int attempts) {
        if (attempts <= 6) {
            return System.currentTimeMillis();
        }
        return -1;
    }

    // Method for getting the network time from the NTP server
    public Date getTime() {
        // Permits the thread network access.
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll()
                .build();
        StrictMode.setThreadPolicy(policy);

        if (client == null) {
            client = new NTPUDPClient();
        }

        try {
            inetAddress = InetAddress.getByName(NTPServer);
        } catch (UnknownHostException e) {
            Log.d(TAG, "getTime: Error reaching the NTP server for time synchronization.");
            e.printStackTrace();
        }

        int loopCounter = 0;
        while(loopCounter < 6) {
            loopCounter++;
            // If there is no internet connection, return System time.
            if(!isConnected) {
                long timeNow = System.currentTimeMillis();
                timeReturned = timeNow + offset;
                //Log.d(TAG, "getTime: Returning system time.");
                return new Date(timeReturned);
            }
            try {
                client.open();
                client.setSoTimeout(3000);
                // Get time from NTP server. TimeInfo object returned.
                NTPTime = client.getTime(inetAddress);
                timeReturned  = NTPTime.getMessage().getTransmitTimeStamp().getTime();
                long timeNow = System.currentTimeMillis();
                offset = timeReturned - timeNow;
                timeReturned = timeNow + offset;
                break;
            } catch (SocketException e) {
                Log.d(TAG, "getTime: Error: Socket Exception");
                timeReturned = handleAttempts(loopCounter);
                e.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "getTime: Error: IOException");
                timeReturned = handleAttempts(loopCounter);
                e.printStackTrace();
            }
        }
        return new Date(timeReturned);
    }
}


