package com.ford.zaphod.garagedoor;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.net.*;

public class MainActivity extends Activity implements View.OnClickListener {

    private final static String MESSAGE = "31337 activate ";
    private final static String IPADDRESS = "172.17.10.25";
    private final static int PORT = 6736;
    private Button mButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button)findViewById(R.id.garagebutton);
        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick (View v) {
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
    }
}
