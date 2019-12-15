/**
 * Medical File Transfer v2.0
 * @author Michael Viscardi
 * This is a revised version of my Medical File Transfer Project from Advanced Projects. In this version, I emphasized functionality and less clunky design.
 * As of November 28, 2019, this app will hold ONE (1) User's medical information.
 */
package com.example.medicalfiletransfer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * MedicalFileTransfer2 Patient app by Michael Viscardi.
 * This Android Application complies at SDK Version 29 and requires a RFCOMM Bluetooth-compatible device.
 * It will allow a patient to create and save a medical profile that they can then transfer to the doctor Windows Forms program.
 *
 * MainActivity by Michael Viscardi.
 * This activity simply acts as the start point of the app. It bridges the Creating and Transferring.
 */
public class MainActivity extends AppCompatActivity {
public static boolean fileExists = false;   //If file creation has already happened
public static String filePath;              //path of saved file.
public static String medicalProfile;        //a string representing the saved medical profile. Prevents having to read from the file every time I want to access it.
public static int REQUEST_ACCESS_FINE_LOCATION = 0;     //For use in requesting location permissions for bluetooth, which gathers location data.

    @Override
    protected void onCreate(Bundle savedInstanceState) {    //Creates first activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();    //Checks if Bluetooth is compatible. Restricts transfer functionality if not
        if (adapter == null) {
            Toast.makeText(this, "Your device does not support Bluetooth, but you can still create a Medical Profile.", Toast.LENGTH_LONG).show();
            Button transferButton = findViewById(R.id.TransferButton);
            transferButton.setEnabled(false);
        }
        checkPermissions();
        checkFile();

    }

    //When the user returns to MainActivity, this is run.
    @Override
    protected void onRestart() {
        super.onRestart();
        checkFile();
    }

    //CheckFile checks if a medicalProfile exists. updates fileExists accordingly.
    private void checkFile() {
        File savedFilesFolder = getFilesDir();
        File[] savedFile = savedFilesFolder.listFiles(); //Can be changed to hold more profiles.
        if (savedFilesFolder.isDirectory()) {
            if (savedFilesFolder.list().length == 0) {
                System.out.println("Empty!");
                Button transferButton = findViewById(R.id.TransferButton);
                transferButton.setEnabled(false);
            } else {
                System.out.println("File Present!");
                TextView CurrentUser = findViewById(R.id.CurrentUser);
                Button LoadButton = findViewById(R.id.CreateButton);
                LoadButton.setText("EDIT");


                if (savedFile[0].exists()) {
                    try {
                        fileExists = true;
                        filePath = savedFile[0].getAbsolutePath();
                        File medicalFile = new File(filePath);
                        Scanner getFile = new Scanner(medicalFile);
                        medicalProfile = getFile.nextLine();
                        Scanner parseName = new Scanner(medicalProfile).useDelimiter(Pattern.quote("[INPUT]"));
                        String displayName;
                        String firstName = parseName.next();
                        String middleName = parseName.next();
                        String lastName = parseName.next();
                        if (middleName.equals("[EMPTY]")) {
                            displayName = firstName + " " + lastName;
                        } else {
                            displayName = firstName + " " + middleName + " " + lastName;
                        }

                        CurrentUser.setText("CURRENT USER: " + displayName);
                        CurrentUser.setVisibility(View.VISIBLE);
                        Button transferButton = findViewById(R.id.TransferButton);
                        transferButton.setEnabled(true);
                    } catch (FileNotFoundException fileNotFound) {
                        System.out.println("Impossible Bummer at File");
                    }
                }
            }
        }
    }

    //Starts CreateActivity
    public void startCreateActivity (View view) {            //Starts second activity when "Create" button is pressed.

        Intent createActivityIntent = new Intent(this, CreateActivity.class);
        createActivityIntent.putExtra("fileExists", fileExists);
        createActivityIntent.putExtra("filePath", filePath);
        startActivity(createActivityIntent);
    }

    //Starts TransferActivity
    public void startTransferActivity (View view) {
        if (fileExists) {
            Intent discoveryActivityIntent = new Intent (this, DiscoveryActivity.class);
            discoveryActivityIntent.putExtra("medicalProfile", medicalProfile);
            startActivity(discoveryActivityIntent);
        }
    }

    //Checks Permissions given to MedicalFileTransfer.
    //If they are granted, transfer functionality is enabled.
    //If they are denied, transfer functionality is disabled. Shows a button for enabling permissions.
    public void checkPermissions() {
         if (!(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "You must allow location because Bluetooth gathers location data.", Toast.LENGTH_LONG).show();
            Button transferButton = findViewById(R.id.TransferButton);
            transferButton.setEnabled(false);
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    //onClick for "Enable Permissions" button. Re-opens the dialogue to grant location permissions.
    public void enablePermissionsOnClick(View view) {
        requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FINE_LOCATION);
    }

    //Listener for the result of the dialogue asking for location permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION) { //0 is permission manager's request code.
            Button enablePermissionsButton = findViewById(R.id.EnablePermissionsButton);
            Button transferButton = findViewById(R.id.TransferButton);
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                transferButton.setEnabled(false);
                enablePermissionsButton.setVisibility(View.VISIBLE);
                enablePermissionsButton.setEnabled(true);
                Toast.makeText(this, "You must allow location because Bluetooth gathers location data.", Toast.LENGTH_LONG).show();
            } else if (fileExists) {
                enablePermissionsButton.setEnabled(false);
                enablePermissionsButton.setVisibility(View.GONE);
                transferButton.setEnabled(true);
            } else {
                enablePermissionsButton.setEnabled(false);
                enablePermissionsButton.setVisibility(View.GONE);
            }
        }
    }
}

//TODO: Handle "DO NOT ASK AGAIN" people.
//TODO: OPTIONAL: Add app logo
//TODO: OPTIONAL: Animations.
//TODO: OPTIONAL: Fix fonts on createActivity

//TODO: Maybe add this. to classes.


//com.example.medicalfiletransfer I/System.out: