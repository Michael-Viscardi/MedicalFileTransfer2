package com.example.medicalfiletransfer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * DiscoveryActivity by Michael Viscardi
 * This activity asks the user to verify their information and begins device discovery for a doctor device.
 */
public class DiscoveryActivity extends AppCompatActivity {
    public static String medicalProfile;        //medicalProfile to transfer
    public static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //The phone's bluetooth adapter, There's only one.
    public ArrayList<BluetoothDevice> discoveredDevices = new ArrayList<BluetoothDevice>(); //Discovered Devices
    private int savedScreenOrientation;     //Saves screen orientation for tasks that require "stillness." (e.g. discovery).

    public static final int REQUEST_ENABLE_BT = 12; //Request to enable bluetooth.

    /**
     * discoveryReceiver by Michael Viscardi
     * This Nested Class BroadcastReceiver receives broadcasts that designate if Bluetooth has been turned on or off, if a device has been discovered, and if discovery is finished.
     */
    private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {  //Found device with discovery.
                BluetoothDevice foundDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!(foundDevice == null)) {
                    discoveredDevices.add(foundDevice);     //Adds discovered device to an array that is displayed to the User to select the doctor device.
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {     //discovery finished.
                discoveryFinished();
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {         //Bluetooth turned on or off.
                System.out.println("Action state changed");
                if (bluetoothAdapter.getState() == BluetoothAdapter.STATE_TURNING_OFF || bluetoothAdapter.getState() == BluetoothAdapter.STATE_OFF) {
                    bluetoothAdapter.cancelDiscovery();
                }
            }
        }
    };

    //Initializes Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovery);

        medicalProfile = this.getIntent().getStringExtra("medicalProfile");

        //For use in verifying user information.
        TextView Name = findViewById(R.id.Name);
        TextView Gender = findViewById(R.id.Gender);
        TextView DOB = findViewById(R.id.DOB);
        TextView Address = findViewById(R.id.Address);
        TextView InsuranceProvider = findViewById(R.id.InsuranceProvider);
        TextView InsuranceID = findViewById(R.id.InsuranceID);
        TextView Smokes = findViewById(R.id.Smokes);
        TextView SmokeFrequency = findViewById(R.id.SmokeFrequency);
        TextView Vapes = findViewById(R.id.Vapes);
        TextView VapeFrequency = findViewById(R.id.VapeFrequency);
        TextView Chews = findViewById(R.id.Chews);
        TextView ChewFrequency = findViewById(R.id.ChewFrequency);
        TextView AlcoholFrequency = findViewById(R.id.AlcoholFrequency);
        TextView MarijuanaFrequency = findViewById(R.id.MarijuanaFrequency);
        TextView OtherIllicitDrugs = findViewById(R.id.OtherIllicitDrugs);
        TextView FatherHistory = findViewById(R.id.FatherHistory);
        TextView MotherHistory = findViewById(R.id.MotherHistory);
        TextView SiblingConditions = findViewById(R.id.SiblingConditions);

        //Loads information
        Scanner dynamicParser = new Scanner (medicalProfile).useDelimiter(Pattern.quote("[DYNAMIC]"));
        String first14 = dynamicParser.next();
        String dynamics = dynamicParser.next();
        String socialHistory = dynamicParser.next();
        String familyHistory = dynamicParser.next();
        dynamicParser.close();

        Scanner parseFirst14 = new Scanner(first14).useDelimiter(Pattern.quote("[INPUT]"));
        String name = parseFirst14.next() + " ";
        String middleName = parseFirst14.next();
        if (!(middleName.equals("[EMPTY]"))) {
            name += middleName + " ";
        }
        name += parseFirst14.next();
        Name.setText(name);

        Gender.setText(parseFirst14.next());

        DOB.setText(parseFirst14.next() + " " + parseFirst14.next() + ", " + parseFirst14.next());

        String address = "";
        address += parseFirst14.next();
        String apartmentNumber = parseFirst14.next();
        if (!(apartmentNumber.equals("[EMPTY]"))) {
            address += " " + apartmentNumber;
        }
        address += " " + parseFirst14.next() + " " + parseFirst14.next() + " " + parseFirst14.next();
        Address.setText(address);

        InsuranceProvider.setText(parseFirst14.next());

        InsuranceID.setText(parseFirst14.next());

        parseFirst14.close();

        Scanner parseDynamics = new Scanner(dynamics).useDelimiter(Pattern.quote("[FIELD]"));
        String pastMedicalHistory = parseDynamics.next();
        String currentMedications = parseDynamics.next();
        String allergiesToMedications = parseDynamics.next();
        String pastSurgicalHistory = parseDynamics.next();
        parseDynamics.close();

        LinearLayout PastMedicalHistoryLayout = findViewById(R.id.PastMedicalHistoryLayout);
        Scanner parsePastMedicalHistory = new Scanner (pastMedicalHistory).useDelimiter(Pattern.quote("[SET]"));
        String currentPMH;
        while (parsePastMedicalHistory.hasNext()) {
            currentPMH = parsePastMedicalHistory.next();
            if (!(currentPMH.equals("[EMPTY-FIELD]"))) {
                TextView addition = new TextView(this);
                addition.setText(currentPMH);
                PastMedicalHistoryLayout.addView(addition);
            } else {
                TextView noneAddition = new TextView(this);
                noneAddition.setText("NONE");
                PastMedicalHistoryLayout.addView(noneAddition);
            }
        }
        parsePastMedicalHistory.close();

        LinearLayout CurrentMedicationsLayout = findViewById(R.id.CurrentMedicationsLayout);
        Scanner parseCurrentMedications = new Scanner(currentMedications).useDelimiter(Pattern.quote("[SET]"));
        String currentCM;
        while (parseCurrentMedications.hasNext()) {
            currentCM = parseCurrentMedications.next();
            if (!(currentCM.equals("[EMPTY-FIELD]"))) {
                Scanner parseCurrentInput = new Scanner(currentCM).useDelimiter(Pattern.quote("[INPUT]"));

                LinearLayout addition = new LinearLayout(this);         //Creates addition LinearLayout
                addition.setOrientation(LinearLayout.HORIZONTAL);

                TextView Medication = new TextView(this);
                Medication.setText(parseCurrentInput.next());
                TextView Frequency = new TextView(this);
                Frequency.setText(parseCurrentInput.next());
                TextView Dosage = new TextView(this);
                Dosage.setText(parseCurrentInput.next());

                addition.addView(Medication);
                addition.addView(Frequency);
                addition.addView(Dosage);

                Medication.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 21));
                Frequency.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 28));
                Dosage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 30));

                CurrentMedicationsLayout.addView(addition);
                parseCurrentInput.close();
            } else {
                TextView noneAddition = new TextView(this);
                noneAddition.setText("NONE");
                CurrentMedicationsLayout.addView(noneAddition);
            }
        }

        LinearLayout AllergiesToMedicationsLayout = findViewById(R.id.AllergiesToMedicationsLayout);
        Scanner parseAllergiesToMedications = new Scanner(allergiesToMedications).useDelimiter(Pattern.quote("[SET]"));
        String currentPATM;
        while (parseAllergiesToMedications.hasNext()) {
            currentPATM = parseAllergiesToMedications.next();
            if (!(currentPATM.equals("[EMPTY-FIELD]"))) {
                Scanner parseCurrentInput = new Scanner(currentPATM).useDelimiter(Pattern.quote("[INPUT]"));

                LinearLayout addition = new LinearLayout(this);
                addition.setOrientation(LinearLayout.HORIZONTAL);

                TextView Allergy = new TextView(this);
                Allergy.setText(parseCurrentInput.next());
                TextView Reaction = new TextView(this);
                Reaction.setText(parseCurrentInput.next());

                Allergy.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                Reaction.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                addition.addView(Allergy);
                addition.addView(Reaction);

                AllergiesToMedicationsLayout.addView(addition);
                parseCurrentInput.close();
            } else {
                TextView noneAddition = new TextView(this);
                noneAddition.setText("NONE");
                AllergiesToMedicationsLayout.addView(noneAddition);
            }
        }
        parseAllergiesToMedications.close();

        LinearLayout PastSurgeriesLayout = findViewById(R.id.PastSurgeriesLayout);
        Scanner parsePastSurgeries = new Scanner(pastSurgicalHistory).useDelimiter(Pattern.quote("[SET]"));
        String currentPS;
        while (parsePastSurgeries.hasNext()) {
            currentPS = parsePastSurgeries.next();
            if (!(currentPS.equals("[EMPTY-FIELD]"))) {
                Scanner parseCurrentInput = new Scanner(currentPS).useDelimiter(Pattern.quote("[INPUT]"));

                LinearLayout addition = new LinearLayout(this);
                addition.setOrientation(LinearLayout.HORIZONTAL);

                TextView Procedure = new TextView(this);
                Procedure.setText(parseCurrentInput.next());
                TextView Year = new TextView(this);
                Year.setText(parseCurrentInput.next());

                Procedure.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
                Year.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                addition.addView(Procedure);
                addition.addView(Year);

                PastSurgeriesLayout.addView(addition);
                parseCurrentInput.close();
            } else {
                TextView noneAddition = new TextView(this);
                noneAddition.setText("NONE");
                PastSurgeriesLayout.addView(noneAddition);
            }
        }
        parsePastSurgeries.close();

        Scanner parseSocialHistory = new Scanner(socialHistory).useDelimiter(Pattern.quote("[SET]"));
        String unParsedTobacco = parseSocialHistory.next();
        if (unParsedTobacco.equals("[EMPTY-FIELD]")) {
            Smokes.setText("Tobacco:");
            SmokeFrequency.setText("No tobacco use");
            SmokeFrequency.setVisibility(View.VISIBLE);
            Vapes.setVisibility(View.GONE);
            VapeFrequency.setVisibility(View.GONE);
            Chews.setVisibility(View.GONE);
            ChewFrequency.setVisibility(View.GONE);
        } else {
            Scanner parseTobacco = new Scanner(unParsedTobacco).useDelimiter(Pattern.quote("[INPUT]"));
            String smokes = parseTobacco.next();
            String smokeFrequency = parseTobacco.next();
            String vapes = parseTobacco.next();
            String vapeFrequency = parseTobacco.next();
            String chews = parseTobacco.next();
            String chewFrequency = parseTobacco.next();
            parseTobacco.close();
            if (smokes.equals("Yes")) {
                if (!(smokeFrequency).equals("[EMPTY]")) {
                    SmokeFrequency.setText(smokeFrequency);
                } else {
                    SmokeFrequency.setText("Yes - No frequency specified");
                }
            } else {
                SmokeFrequency.setText("No");
            }
            if (vapes.equals("Yes")) {
                if (!(vapeFrequency).equals("[EMPTY]")) {
                    VapeFrequency.setText(vapeFrequency);
                } else {
                    VapeFrequency.setText("Yes - No frequency specified");
                }
            } else {
                VapeFrequency.setText("No");
            }
            if (chews.equals("Yes")) {
                if (!(chewFrequency).equals("[EMPTY]")) {
                    ChewFrequency.setText(chewFrequency);
                } else {
                    ChewFrequency.setText("Yes - No frequency specified.");
                }
            } else {
                ChewFrequency.setText("None");
            }
        }

        Scanner parseAlcohol = new Scanner(parseSocialHistory.next()).useDelimiter(Pattern.quote("[INPUT]"));
        String alcohol = parseAlcohol.next();
        String alcoholFrequency = parseAlcohol.next();
        parseAlcohol.close();

        Scanner parseMarijuana = new Scanner(parseSocialHistory.next()).useDelimiter(Pattern.quote("[INPUT]"));
        String marijuana = parseMarijuana.next();
        String marijuanaFrequency = parseMarijuana.next();
        parseMarijuana.close();

        Scanner parseIllicitDrugs = new Scanner(parseSocialHistory.next()).useDelimiter(Pattern.quote("[INPUT]"));
        String illicitDrugs = parseIllicitDrugs.next();
        String otherIllicitDrugs = parseIllicitDrugs.next();
        parseIllicitDrugs.close();

        parseSocialHistory.close();

        if (!(alcohol.equals("No"))) {
            AlcoholFrequency.setText(alcoholFrequency);
        } else {
            AlcoholFrequency.setText("No alcohol use");
        }
        if (!(marijuana.equals("No"))) {
            MarijuanaFrequency.setText(marijuanaFrequency);
        } else {
            MarijuanaFrequency.setText("No marijuana use");
        }
        if (!(illicitDrugs.equals("No"))) {
            OtherIllicitDrugs.setText(otherIllicitDrugs);
        } else {
            OtherIllicitDrugs.setText("No illicit drug use");
        }


        Scanner parseFamilyHistory = new Scanner(familyHistory).useDelimiter(Pattern.quote("[INPUT]"));
        String fatherHistory = parseFamilyHistory.next();
        String motherHistory = parseFamilyHistory.next();
        String siblingConditions = parseFamilyHistory.next();
        if (!(fatherHistory.equals("[EMPTY]"))){
            FatherHistory.setText(fatherHistory);
        } else {
            FatherHistory.setText("None");
        }
        if (!(motherHistory.equals("[EMPTY]"))){
            MotherHistory.setText(motherHistory);
        } else {
            MotherHistory.setText("None");
        }
        if (!(siblingConditions.equals("[EMPTY]"))){
            SiblingConditions.setText(siblingConditions);
        } else {
            SiblingConditions.setText("None");
        }


        //REGISTERING OF BroadcastReceiver. This turns on discoveryReciever.

        IntentFilter discoveryIntentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        discoveryIntentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        discoveryIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(discoveryReceiver, discoveryIntentFilter);

    }

    //onClick for "Yes my information is correct" button.
    //Hides verification page and shows discovery page.
    public void yesButtonOnClick (View view) {                      //BEGINS TRANSFER. Starts with discovery.
        ScrollView scrollView = findViewById(R.id.ScrollView);
        scrollView.removeAllViews();
        TextView Instruction = findViewById(R.id.Instruction);
        Instruction.setText("Discovering Devices, Please Wait");
        if (bluetoothAdapter.isEnabled()) {
            discoverDevices();
            Button cancelButton = findViewById(R.id.NoButton);
            cancelButton.setText("Cancel");
            Button yesButton = findViewById(R.id.YesButton);
            yesButton.setText("");
            yesButton.setEnabled(false);
        } else {
            Intent enableBluetooth = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);       //If Bluetooth is off, requests that it is turned on.
            startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
        }
    }

    //onClick for "No my information is wrong" button.
    //Cancels discovery and returns user to MainActivity.
    public void noButtonOnClick (View view) {
        bluetoothAdapter.cancelDiscovery();
        this.finish();
    }

    //Begins device discovery.
    private void discoverDevices() {
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
        savedScreenOrientation = getRequestedOrientation();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);  //Locks screen rotation to prevent data loss.
        bluetoothAdapter.startDiscovery();
    }

    //Method that displays discoveredDevices.
    public void discoveryFinished() {
        setRequestedOrientation(savedScreenOrientation);
        System.out.println(discoveredDevices.toString());
        ScrollView scrollView = findViewById(R.id.ScrollView);
        scrollView.removeAllViews();
        final RadioGroup deviceSelection = new RadioGroup(this);
        deviceSelection.setTag("DeviceSelection");

        int deviceCount = 0;
        for (BluetoothDevice currentDevice : discoveredDevices) {
            RadioButton currentRadioButton = new RadioButton(this);
            if (!(currentDevice.getName() == null)) {
                currentRadioButton.setText(currentDevice.getName());
                currentRadioButton.setId(deviceCount);
                deviceSelection.addView(currentRadioButton);
            }
            deviceCount++; //used to assign Id's for every RadioButton that corresponds to an index of discoveredDevices.
        }
        if (deviceSelection.getChildCount() == 0) {
            scrollView.removeAllViews();
            TextView noDevicesFound = new TextView(this);
            noDevicesFound.setText("No devices were found!");
            noDevicesFound.setGravity(Gravity.CENTER);
            scrollView.addView(noDevicesFound);
            Button yesButton = findViewById(R.id.YesButton);
            yesButton.setEnabled(true);
            yesButton.setText("Retry");
        } else {

            TextView Instruction = findViewById(R.id.Instruction);
            Instruction.setText("Please select the doctor's device.");
            scrollView.addView(deviceSelection);
            final Button transferButton = findViewById(R.id.YesButton);
            transferButton.setText("Transfer");
            transferButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    BluetoothDevice doctorDevice = discoveredDevices.get(deviceSelection.getCheckedRadioButtonId());
                    startTransferActivity(doctorDevice);
                }
            });
            transferButton.setEnabled(false);

            deviceSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {     //Ensures selection of a doctor device before "transfer" button can be pressed.
                    if (checkedId != -1) {
                        transferButton.setEnabled(true);
                    } else {
                        transferButton.setEnabled(false);
                    }
                }
            });

        }
    }

    //Starts TransferActivity
    public void startTransferActivity(BluetoothDevice doctorDevice) {
        Intent transferActivityIntent = new Intent(this, TransferActivity.class);
        transferActivityIntent.putExtra("medicalProfile", medicalProfile);
        transferActivityIntent.putExtra("doctorDevice", doctorDevice);
        startActivity(transferActivityIntent);
    }

    //Listens if Bluetooth was turned on or left off in the yesButtonOnClick method.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth successfully enabled.", Toast.LENGTH_LONG).show();
                discoverDevices();
            } else {
                Toast.makeText(this, "Bluetooth must be turned on for the transfer to work.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(discoveryReceiver);
        bluetoothAdapter.cancelDiscovery();
        this.finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.finish();
    }



}



//TODO - OPTIONAL: Respond to permission changes
//TODO: OPTIONAL - Make the ArrayList not global.
//TODO: Double-Discovery on "No" press.

