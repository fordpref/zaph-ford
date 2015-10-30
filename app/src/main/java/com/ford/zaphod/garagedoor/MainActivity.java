package com.ford.zaphod.garagedoor;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.net.*;
import android.graphics.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity implements View.OnClickListener {

    private String message = null;
    private final static String IPADDRESS = "172.17.10.25";
    private final static int PORT = 6736;
    private boolean wifistate = false;
    private boolean doorstate = false;
    private boolean beaglestate = false;
    private Button mButton;
    public static String response = "test";
    private IntentFilter wifiStateFilter = new IntentFilter();
    private final BroadcastReceiver wifiStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkStates();
        }
    };

    public Runnable garagePacketSend = new Runnable() {
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(IPADDRESS);
                DatagramSocket clientSocket = new DatagramSocket();
                clientSocket.setReuseAddress(true);
                byte[] sendData = new byte[1024];
                byte[] recvData = new byte[1024];
                sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr, PORT);
                DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
                clientSocket.send(sendPacket);
                clientSocket.receive(recvPacket);
                response = new String(recvPacket.getData(), 0, recvPacket.getLength(), StandardCharsets.UTF_8);
                response = response.substring(0,response.length() - 1);
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button)findViewById(R.id.garagebutton);
        mButton.setOnClickListener(this);
        wifiStateFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiStateReceiver, wifiStateFilter);
        checkStates();
    }

    protected void onStart(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mButton = (Button)findViewById(R.id.garagebutton);
        mButton.setOnClickListener(this);
        registerReceiver(wifiStateReceiver, wifiStateFilter);
        checkStates();
    }

    protected void onResume(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mButton = (Button)findViewById(R.id.garagebutton);
        mButton.setOnClickListener(this);
        registerReceiver(wifiStateReceiver, wifiStateFilter);
        checkStates();
    }

    protected void onRestart(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mButton = (Button)findViewById(R.id.garagebutton);
        mButton.setOnClickListener(this);
        registerReceiver(wifiStateReceiver, wifiStateFilter);
        checkStates();
    }

    protected void onPause(Bundle savedInstanceState) {
        unregisterReceiver(wifiStateReceiver);
    }

    protected void onStop(Bundle savedInstanceState) {
        unregisterReceiver(wifiStateReceiver);
    }

    protected void onDestroy(Bundle savedInstanceState) {
        unregisterReceiver(wifiStateReceiver);
    }

    public void checkWifiState() {
        wifistate = getWifiState(this);
        checkStates();
    }

    public boolean getBeagleState(Context context) {
        Log.d("getBeagleState", "called");
        boolean bstate = false;
        if (wifistate) {
            message = "31337 status\n";
            Thread send = new Thread(garagePacketSend);
            send.start();
            Log.d("beagleState", "send packet: " + message);
            try {
                send.join(500);
            } catch (Exception e) {
                //
            }
            Log.d("beagleState", "response: " + response);
            if (response.equals("READY")) {
                bstate = true;
            } else {
                bstate = false;
            }
        } return bstate;
    }

    public boolean getWifiState(Context context) {
        android.net.wifi.WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ssid = " ";
        boolean dstate;
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                ssid = wifiInfo.getSSID();
                ssid = ssid.substring(1, ssid.length() - 1);
                if (ssid.equalsIgnoreCase("solarsphere")) {
                    dstate = true;
                } else {
                    dstate = false;
                }
            } else {
                dstate = false;
            }
        } else {
            dstate = false;
        }
        return dstate;
    }

    private void checkStates() {
        Log.d("checkStates", "called, doorstate: " + doorstate + "  beaglestate: " + beaglestate);
        wifistate = getWifiState(this);
        if (wifistate) {
            beaglestate = getBeagleState(this);
            Log.d("checkStates", "beaglestate: " + beaglestate);
        }
        if ((wifistate) && (beaglestate)) {
            doorstate = true;
            mButton.setBackgroundColor(Color.GREEN);
            mButton.setText("Actuate Door");
        } else if ((wifistate) && (beaglestate == false)){
            mButton.setBackgroundColor(Color.GRAY);
            mButton.setText("WIFI CONNECTED\nActuator not READY");
            doorstate = false;
        } else if (wifistate == false) {
            doorstate = false;
            mButton.setBackgroundColor(Color.GRAY);
            mButton.setText("WIFI UNAVAILABLE\nAcutator not READY");
        } else {
            doorstate = false;
            mButton.setBackgroundColor(Color.GRAY);
            mButton.setText("Something really wrong\nGet Beer or Vodka");
        }

    }

    @Override
    public void onClick (View v) {
        if (doorstate) {
            message = "31337 activate\n";
            Thread send = new Thread(garagePacketSend);
            send.start();
            try {
                send.join(500);
            } catch (Exception e) {
                //
            }
        } else {
            checkStates();
        }
    }
}
