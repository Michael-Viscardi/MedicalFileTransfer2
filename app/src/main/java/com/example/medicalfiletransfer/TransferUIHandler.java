package com.example.medicalfiletransfer;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * TransferUIHandler by Michael Viscardi
 * TransferUIHandler is a handler that updates the UI of TransferActivity in a thread-safe manner.
 */
public class TransferUIHandler extends Handler {
    private final WeakReference<Activity> activity; //For WeakReference to TransferActivity for thread saftey.
    private final ProgressBar transferProgress;     //Progress bar on TransferActivity
    private final TextView statusTextView;          //Status Text on TransferActivity.

    //Constructor.
    public TransferUIHandler(Activity inputActivity, ProgressBar inputProgressBar, TextView inputTextView) {
        this.activity = new WeakReference<Activity>(inputActivity);
        this.transferProgress = inputProgressBar;
        this.statusTextView = inputTextView;
    }


    //HandleMessage changes the UI based on what "checkpoint" the app reaches.
    //The following lines are the values of arg1
    //ERRORS
        //-1    Connection failed
        //-2    Socket InputStream initialization failed.
        //-3    Socket OutputStream initialization failed.
        //-4    [TransferThread] writeStream.write() failed.
    //SUCCESSES
        //0     Connection Successful
        //1     [TransferThread] writeStream.write() successful.

        //1337  FULL SUCCESS
    @Override
    public void handleMessage(Message message) {    //HandleMessage handles messages sent from the threads.
        if (message.arg1 == -1) {
            //TODO: Error Message
            statusTextView.setText("Error code: -1");
        }
        if (message.arg1 == 0) {
            transferProgress.setProgress(25);
            statusTextView.setText("Connection successful. Beginning transfer");
        }
        if (message.arg1 == 1) {
            //TODO: Optional: Finalizing re-transfer
            transferProgress.setProgress(75);
            statusTextView.setText("Transfer completed, finalizing");
        }
        if (message.arg1 == 1337) {
            transferProgress.setProgress(100);
            statusTextView.setText("Transfer Completed.");
            TransferActivity.finishTransfer();
        }
        if (message.arg1 == -2) {
            //TODO: Error Message
            statusTextView.setText("Error code: -2");
        }
        if (message.arg1 == -3) {
            //TODO: Error Message
            statusTextView.setText("Error code: -3");
        }
        if (message.arg1 == -4) {
            //TODO: Error Message
            statusTextView.setText("Error code: -4");
        }
    }
}
