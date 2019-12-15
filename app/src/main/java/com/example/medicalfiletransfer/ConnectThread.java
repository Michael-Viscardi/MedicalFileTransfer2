package com.example.medicalfiletransfer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Message;

import java.io.IOException;
import java.util.UUID;

/**
 * ConnectThread by Michael Viscardi
 * This thread handles Bluetooth Connection. Used in TransferActivity.
 */
public class ConnectThread extends Thread {
    private final BluetoothDevice doctorDevice;     //Doctor device
    private final BluetoothSocket doctorSocket;     //Socket to connect on.
    private final UUID mTFUUID;                     //App UUID to connect over. Shared by Doctor Program.

    //Constructor.
    public ConnectThread(BluetoothDevice inputDoctorDevice) {
        this.mTFUUID = UUID.fromString("ee53bd91-1af0-4807-a6f9-fe3e748fc1bb");
        this.doctorDevice = inputDoctorDevice;

        BluetoothSocket testSocket = null;  //Test socket exists purely to sidestep the IOException that has to be try-catched
        try {
            testSocket = doctorDevice.createRfcommSocketToServiceRecord(mTFUUID);
        } catch (IOException ioException) {
            System.out.println("Bummer at socket creation");
        }
        doctorSocket = testSocket;
    }

    //Actually connects when the thread is run.
    public void run() {
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

        try {
            System.out.println("Attempting Connection");
            doctorSocket.connect();

            TransferActivity.connectionSocket = doctorSocket;   //TODO: Probably bad Threading practice to just edit this. Possibly declare connectionSocket as final.

            Message connected = Message.obtain();
            connected.arg1 = 0;
            connected.setTarget(TransferActivity.uiHandler);
            connected.sendToTarget();

        } catch (IOException ioException) {
            System.out.println("Bummer at socket connection attempt");
        }
    }

    public void cancel() {
        try {
            doctorSocket.close();
        } catch (IOException ioException) {
            System.out.println("Bummer at socket closing!");
        }
    }
}

//TODO: Fix crash if the doctor fails to enable listening on the UUID.