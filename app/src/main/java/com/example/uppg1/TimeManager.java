package com.example.uppg1;

import android.widget.TextView;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Date;

public class TimeManager {
    private NTPUDPClient client = null;
    private InetAddress inetAddress = null;
    TimeInfo NTPTime = null;
    ClockManager clockManager = null;

    /* https://www.pool.ntp.org/zone/se
    List of NTP Servers in Sweden.
    I decided on "0.se.pool.ntp.org" after comparing the response time of the available servers.

    Ping statistics for 0.se.pool.ntp.org:
        Packets: Sent = 4, Received = 4, Lost = 0 (0% loss),
    Approximate round trip times in milli-seconds:
        Minimum = 7ms, Maximum = 7ms, Average = 7ms
     */
    private final static String NTPServer = "0.se.pool.ntp.org";

    public TimeManager(ClockManager cm) {
        this.clockManager = cm;
    }

    // Method for getting the network time from the NTP server
    public void getTime() {
        if (client == null) {
            client = new NTPUDPClient();
            System.out.println("Creating NTP Client");
        }
        try {
            inetAddress = InetAddress.getByName(NTPServer);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error getting IP address.");
        }
        while (true) {
            try {
                client.open();
                client.setDefaultTimeout(5000);
                NTPTime =  client.getTime(inetAddress);
                break;
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: Add specific error message depending on exception (SocketException and IOExcept)
                System.out.println("Error occurred opening datagram socket at the local host.");
                System.out.println("Alternatively, error getting the time.");
            }  //Update system clock update here? In finally { block }
            System.out.println(NTPTime.toString());
            clockManager.updateClock("Time");
        }
    }
}
