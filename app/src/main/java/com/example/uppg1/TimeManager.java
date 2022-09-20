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
    TimeInfo NTPTime = null;
    private Handler timeHandler = null;
    private Runnable timeRunnable;
    String timeString = "00:00";
    private long offset = 0;
    long ntpTime = 0;


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
            @Override
            public void run() {
                long timeNow = System.currentTimeMillis();
                long adjustedTime = timeNow + offset;
                timeString = new Date(adjustedTime).toString();
                clockText.setText(timeString);
            }
        };

        Thread thread = new Thread() {
            @Override
            public void run() {
                while(true) {
                    timeHandler.post(timeRunnable);
                    SystemClock.sleep(3000);
                    Log.d(TAG, "run: TimeManager is updating the time");

                    timeString = getTime().toString();

                }
            }
        };
        thread.start();
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
            Log.d(TAG, "getTime: Error reaching the NTP server for time syncronization.");
            e.printStackTrace();
        }
        while(true) {
            try {
                client.open();
                client.setSoTimeout(3000);
                NTPTime = client.getTime(inetAddress);
                break;
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        ntpTime  = NTPTime.getMessage().getTransmitTimeStamp().getTime();

        //WIP: Figuring out offset

        long timeNow = System.currentTimeMillis();
        Log.d(TAG, "getTime: SysTime = " + timeNow);
        Log.d(TAG, "getTime: NTPTime = " + ntpTime);
        offset = ntpTime - timeNow;
        Log.d(TAG, "getTime: offset = " + offset);

        long returnedTime = timeNow + offset;


        return new Date(returnedTime);
    }


}


