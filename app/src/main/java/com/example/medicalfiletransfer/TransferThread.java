package com.example.medicalfiletransfer;

import android.bluetooth.BluetoothSocket;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * TransferThread by Michael Viscardi
 * This thread handles transferring and receiving a confirmation message (confirmation currently not working).
 */
public class TransferThread extends Thread {
    private final BluetoothSocket transferSocket;   //Connected socket.
    private final InputStream readStream;           //Stream of data coming in from Doctor.
    private final OutputStream writeStream;         //Stream of data outputting to Doctor
    private byte[] receivedBytes;                   //A Byte Array of received data.

    //Constructor.
    public TransferThread(BluetoothSocket inputSocket) {
        this.transferSocket = inputSocket;

        InputStream tempIn;     //Temps used due to recommendation from android developers.
        OutputStream tempOut;

        try {
            tempIn = transferSocket.getInputStream();
        } catch (IOException ioException) {
            System.out.println("Bummer at socket InputStream initialization");
            sendUpdateMessage(2);
            tempIn = null;
        }
        try {
            tempOut = transferSocket.getOutputStream();
        } catch (IOException ioException) {
            System.out.println("Bummer at socket OutputStream initialization");
            sendUpdateMessage(3);
            tempOut = null;
        }
        readStream = tempIn;
        writeStream = tempOut;
    }

    //Run is the reading aspect of TransferThread. I unfortunately could not get it to work without stalling the UIThread because I am bad at threading.
    public void run() {
        receivedBytes = new byte[1024];
        String received;
        while (true) {
            try {
                readStream.read(receivedBytes, 0, receivedBytes.length);
            } catch (IOException ioException) {
                System.out.println("Bummer at read!");
            }
            received = new String(receivedBytes);
            System.out.println("BYTE RECIEVED: " + received);
        }
        //TODO: Fix confirmation message.
    }

    //Write sends the medicalProfiel to the doctor.
    public void write(byte[] sendMe) {
        try {
            writeStream.write(sendMe);
            //sendUpdateMessage(1); for use with broken confirmation method.
            sendUpdateMessage(1337);
        } catch (IOException ioException) {
            System.out.println("Bummer at write(). Bummer!");
            sendUpdateMessage(-4);
        }
    }

    //The abort method. Stops everything.
    public void cancel() {
        try {
            transferSocket.close();
        } catch (IOException ioException) {
            System.out.println("Impossible Bummer at transferSocket.close()");
        }
    }

    //Helper method to send ui-updating messages to TransferActivity. Negative inputs are error messages while Positive inputs are progress messages.
    private void sendUpdateMessage(int messageCode) {
        Message failure = Message.obtain();
        failure.arg1 = messageCode;
        failure.setTarget(TransferActivity.uiHandler);
        failure.sendToTarget();
    }




}
