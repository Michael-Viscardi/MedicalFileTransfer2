package com.example.medicalfiletransfer;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

/**
 * TransferActivity by Michael Viscardi
 * This Activity shows the user a Progress bar with a Status Text underneath it. It starts threads that handle connecting the devices and transferring the medical profile over them.
 */
public class TransferActivity extends AppCompatActivity {
public String medicalProfile;           //Profile to be transferred
public BluetoothDevice doctorDevice;    //Object representing doctor device that was found in DiscoveryActivity
public static BluetoothSocket connectionSocket;     //The socket the bluetooth connection is made over.
public static TransferUIHandler uiHandler;          //Updates UI in a thread-safe manner.



    //Initializes Activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        doctorDevice = this.getIntent().getExtras().getParcelable("doctorDevice");
        medicalProfile = this.getIntent().getStringExtra("medicalProfile");

        ProgressBar progressBar = findViewById(R.id.TransferProgress);
        TextView textView = findViewById(R.id.StatusTextView);
        uiHandler = new TransferUIHandler(this, progressBar, textView);

        ConnectThread connectThread = new ConnectThread(doctorDevice);
        connectThread.start();
        //connectionSocket is initialized from ConnectThread
        while(connectThread.isAlive()) {
            //Wait till connection is done.
        }
        TransferThread transferThread = new TransferThread(connectionSocket);
        medicalProfile = "[MEDICALFILETRANSFER][END]" + medicalProfile + "[END]";
        transferThread.write(medicalProfile.getBytes());
    }

    //Called when transfer is completed by TransferUIHandler.
    public static void finishTransfer() {
        try {
            connectionSocket.close();
        } catch (IOException ioException) {
            System.out.println("Bummer at finish");
        }
    }

    //Called when "Done" button is pressed.
    public void doneOnClick(View view) {
        this.finish();
    }





    //TODO: Close threads on failure.
    //TODO: Discovering animation
    //TODO: Idempotency?
    //TODO: Optional at this point: read.
}
