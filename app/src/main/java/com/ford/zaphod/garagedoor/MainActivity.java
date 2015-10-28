package com.ford.zaphod.garagedoor;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.net.*;
import android.graphics.*;
import java.net.*;

public class MainActivity extends Activity implements View.OnClickListener {

    private final static String MESSAGE = "31337 activate ";
    private final static String IPADDRESS = "172.17.10.25";
    private final static int PORT = 6736;
    private Button mButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String status;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button)findViewById(R.id.garagebutton);
        //mButton.setBackgroundColor(Color.RED);
        mButton.setOnClickListener(this);
        status = getWifiName(this);
    }

    protected void onResume(Bundle savedInstanceState) {
        String status;
        setContentView(R.layout.activity_main);
        mButton = (Button)findViewById(R.id.garagebutton);
        //mButton.setBackgroundColor(Color.RED);
        mButton.setOnClickListener(this);
        status = getWifiName(this);
    }

    public String getWifiName(Context context) {
        android.net.wifi.WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ssid = " ";
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                //if (state == NetworkInfo.DetailedState.CONNECTED) {
                ssid = wifiInfo.getSSID();
                ssid = ssid.substring(1, ssid.length() - 1);
                if (ssid.equalsIgnoreCase("solarsphere")) {
                    mButton.setBackgroundColor(Color.GREEN);
                    mButton.setText("Actuate Door\nConnected to Solarsphere");
                } else {
                    mButton.setBackgroundColor(Color.RED);
                    mButton.setText("Door Not Available");
                }
                //mButton.setText(ssid);  //Troubleshooting line
                return ssid;
                //}
            }
        } else {
            mButton.setBackgroundColor(Color.GRAY);
            mButton.setText("No WIFI\nDoor Not Available");
        }
        return ssid;
    }

    @Override
    public void onClick (View v) {
        //if (getWifiName(this).compareTo("solarsphere") == 0) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        InetAddress serverAddr = InetAddress.getByName(IPADDRESS);
                        DatagramSocket clientSocket = new DatagramSocket();
                        clientSocket.setReuseAddress(true);
                        byte[] sendData = new byte[1024];
                        sendData = MESSAGE.getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr, PORT);
                        clientSocket.send(sendPacket);
                        clientSocket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        //} else {
            //code to stop the service
        //}

    }
}
