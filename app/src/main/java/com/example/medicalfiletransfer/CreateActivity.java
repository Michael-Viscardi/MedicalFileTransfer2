package com.example.medicalfiletransfer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * CreateActivity by Michael Viscardi
 * This activity allows a user to create and save a medicalProfile
 */
public class CreateActivity extends AppCompatActivity {

    //Initializes Activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        if (this.getIntent().getBooleanExtra("fileExists", false)) {
            loadProfile(this.getIntent().getStringExtra("filePath"));
        }
    }

    //The following 4 methods are used for dynamically adding views. The reason they call a "real" method is so that the
    //addition methods can be used in loadProfile.
    public void addPastMedicalHistory (View view) {
        realAddPastMedicalHistory();
    }

    public void addCurrentMedication (View view) {
        realAddCurrentMedication();
    }

    public void addAllergiesToMedication(View view) {
        realAddAllergiesToMedication();
    }

    public void addPastSurgicalHistory(View view) {
        realAddPastSurgicalHistory();
    }

    //These next 4 methods are the aforementioned "real" methods.
    //Adds PastMedicalHistory Fields.
    public void realAddPastMedicalHistory() {
        LinearLayout addToMe = findViewById(R.id.PastMedicalHistoryLayout);
        EditText addition = new EditText(this);
        addition.setHint("Past/Current Condition(s)");
        if (addToMe.getChildCount() == 1) {             //If no other elements are added, this logic adds a remove button for the first PastMedicalHistory.
            createRemoveButton(addToMe);
        }
        addToMe.addView(addition);
        createRemoveButton(addToMe);
    }

    //Adds CurrentMedication Fields.
    public void realAddCurrentMedication () {      //Adds new CurrentMedications to CurrentMedicationLayout
        LinearLayout addToMe = findViewById(R.id.CurrentMedicationLayout);

        LinearLayout addition = new LinearLayout(this);         //Creates addition LinearLayout
        addition.setOrientation(LinearLayout.HORIZONTAL);

        EditText additionMedication = new EditText(this);
        EditText additionFrequency = new EditText(this);        //Creates EditTexts for addition
        EditText additionDosage = new EditText(this);

        additionMedication.setHint("Medication Name");
        additionFrequency.setHint("Frequency");                         //Sets Hints for EditTexts
        additionDosage.setHint("Dosage");

        //Sets Layout Parameters and Weight values for EditTexts.
        additionMedication.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 21));
        additionFrequency.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 28));
        additionDosage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 30));

        addition.addView(additionMedication);
        addition.addView(additionFrequency);                            //Adds EditTexts to addition
        addition.addView(additionDosage);

        if (addToMe.getChildCount() == 1) {
            createRemoveButton(addToMe);
        }
        addToMe.addView(addition);                                      //Adds addition
        createRemoveButton(addToMe);                                    //Adds "Remove" button.
    }

    //Adds AllergiesToMedications Fields.
    public void realAddAllergiesToMedication () {      //Adds new AllergiesToMedications to AllergiesToMedicationsLayout
        LinearLayout addToMe = findViewById(R.id.AllergiesToMedicationsLayout);

        LinearLayout addition = new LinearLayout(this);           //Creates addition LinearLayout
        addition.setOrientation(LinearLayout.HORIZONTAL);

        EditText additionAllergy = new EditText(this);           //Creates EditTexts for addition
        EditText additionReaction = new EditText(this);

        additionAllergy.setHint("Medication");                           //Sets hints for addition
        additionReaction.setHint("Reaction");

        //Sets Layout Parameters and Weights for EditTexts.
        additionAllergy.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        additionReaction.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        addition.addView(additionAllergy);                              //Adds EditTexts to addition.
        addition.addView(additionReaction);

        if (addToMe.getChildCount() == 1) {
            createRemoveButton(addToMe);
        }
        addToMe.addView(addition);                                      //Adds addition to AllergiesToMedicationsLayout.
        createRemoveButton(addToMe);
    }

    //Adds PastSurgicalHistory Fields
    public void realAddPastSurgicalHistory () {      //Adds new AllergiesToMedicationss to AllergiesToMedicationsLayout
        LinearLayout addToMe = findViewById(R.id.PastSurgicalHistoryLayout);

        LinearLayout addition = new LinearLayout(this);           //Creates addition LinearLayout
        addition.setOrientation(LinearLayout.HORIZONTAL);

        EditText additionProcedure = new EditText(this);           //Creates EditTexts for addition
        EditText additionYear = new EditText(this);

        additionProcedure.setHint("Procedure");                           //Sets hints for addition
        additionYear.setHint("Year");

        //Sets Layout Parameters and Weights for EditTexts.
        additionProcedure.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        additionYear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        additionYear.setInputType(InputType.TYPE_CLASS_NUMBER);           //Sets Keyboard type for year.
        additionYear.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});

        addition.addView(additionProcedure);                              //Adds EditTexts to addition.
        addition.addView(additionYear);

        if (addToMe.getChildCount() == 1) {
            createRemoveButton(addToMe);
        }
        addToMe.addView(addition);                                      //Adds addition to AllergiesToMedicationsLayout.
        createRemoveButton(addToMe);

    }

    //Helper method. Adds a "Remove" button with parameters that match the rest of the app.
    //Remove buttons allow removal of ALL fields. The app compensates for this too.
    private void createRemoveButton (LinearLayout addToMe) {
        final Button removeButton = new Button(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.gravity = Gravity.CENTER;          //Sets Layout Parameters for Button
        removeButton.setLayoutParams(buttonParams);
        removeButton.setText("Remove");
        addToMe.addView(removeButton);                  //Adds to the LinearLayout.
        removeButton.setOnClickListener(new View.OnClickListener()
        {                                                                    //Nested class for removeButtons.
            @Override
            public void onClick(View view) {
                LinearLayout parentLayout = (LinearLayout)removeButton.getParent();     //Gets the removeButton's parent Layout
                View currentRemove = null;
                for (int i = 0; i < parentLayout.getChildCount(); i++) {        //Loop to find the correct removeButton and its corresponding textView.
                    currentRemove = parentLayout.getChildAt(i);
                    if (currentRemove.equals(removeButton)) {
                        parentLayout.removeView(parentLayout.getChildAt(i));
                        parentLayout.removeView(parentLayout.getChildAt(i-1));  //Removes both  the button and the textView.
                    }
                }
            }
        });
    }

    //Ensures that the User filled out all required fields (like name or address). These fields are marked with a star.
    public ArrayList<String> findIllegalInputs() {       //Makes sure User Input is valid.
        ArrayList<String> illegalInputs = new ArrayList<String>();  //Strings representing fields the user failed to fill out.

        ArrayList<View> requiredInputs = new ArrayList<View>();     //Views of the fields that the user failed to fill.
        EditText FirstName = findViewById(R.id.FirstName);
        EditText LastName = findViewById(R.id.LastName);
        RadioGroup Gender = findViewById(R.id.GenderLayout);
        Spinner DOBMonth = findViewById(R.id.DOBMonth);
        Spinner DOBDay = findViewById(R.id.DOBDay);
        EditText DOBYear = findViewById(R.id.DOBYear);
        EditText StreetAddress = findViewById(R.id.StreetAddress);
        EditText City = findViewById(R.id.City);
        Spinner State = findViewById(R.id.State);
        EditText PostalCode = findViewById(R.id.PostalCode);
        EditText InsuranceProvider = findViewById(R.id.InsuranceProvider);
        EditText InsuranceIDNumber = findViewById(R.id.InsuranceIDNumber);
        EditText PrimaryDoctor = findViewById(R.id.PrimaryDoctor);
        RadioGroup Alcohol = findViewById(R.id.Alcohol);
        RadioGroup Marijuana = findViewById(R.id.Marijuana);
        RadioGroup IllicitDrugs = findViewById(R.id.IllicitDrugs);

        requiredInputs.add(FirstName);
        requiredInputs.add(LastName);
        requiredInputs.add(Gender);
        requiredInputs.add(DOBMonth);
        requiredInputs.add(DOBDay);
        requiredInputs.add(DOBYear);
        requiredInputs.add(StreetAddress);
        requiredInputs.add(City);
        requiredInputs.add(State);
        requiredInputs.add(PostalCode);
        requiredInputs.add(InsuranceProvider);
        requiredInputs.add(InsuranceIDNumber);
        requiredInputs.add(PrimaryDoctor);
        requiredInputs.add(Alcohol);
        requiredInputs.add(Marijuana);
        requiredInputs.add(IllicitDrugs);

        EditText currentEditText;
        Spinner currentSpinner;
        RadioGroup currentRadioGroup;
        String illegalEditTextHint;

        for (View current : requiredInputs) {
            if (current instanceof EditText) {
                currentEditText = (EditText) current;
                if (currentEditText.getText().toString().trim().equals("") || currentEditText.getText().toString().trim() == null) {
                    illegalEditTextHint = currentEditText.getHint().toString();
                    Scanner starParse = new Scanner(illegalEditTextHint).useDelimiter(Pattern.quote("*"));
                    illegalInputs.add(starParse.next().trim());

                }
            }
            if (current instanceof Spinner) {
                currentSpinner = (Spinner) current;
                if (currentSpinner.getSelectedItem().equals("MONTH") || currentSpinner.getSelectedItem().equals("DAY")) {

                    illegalInputs.add(currentSpinner.getSelectedItem().toString());
                }
                if (currentSpinner.getSelectedItem().equals("* STATE")) {
                    illegalInputs.add("STATE");
                }
            }
            if (current instanceof RadioGroup) {
                currentRadioGroup = (RadioGroup) current;
                if (currentRadioGroup.getCheckedRadioButtonId() == -1) {
                    illegalInputs.add(findViewById(currentRadioGroup.getId()).getResources().getResourceName(currentRadioGroup.getId()).substring(35));
                }
            }
        }

        return illegalInputs;
    }


    //Saves the medicalProfile to the getFilesDir() directory.
    public void save (View view) {     //Saves user input to a file.

        ArrayList<String> illegalInputs = findIllegalInputs();
        if (illegalInputs.size() == 0) {

            ArrayList<View> views = new ArrayList<View>();
            LinearLayout userInput = findViewById(R.id.UserInput);
            for (int inputParser = 0; inputParser < userInput.getChildCount(); inputParser++) {
                views.add(userInput.getChildAt(inputParser));
            }

            SaveManager saveManager = new SaveManager(this, views);     //SaveManager is a class I made to handle the saving of a medicalProfile.
            String medicalProfile = saveManager.getMedicalProfile();
            String fullName;
            //Specifies the name of the file.
            if (((EditText) findViewById(R.id.MiddleName)).getText().toString().trim().equals("") || ((EditText) findViewById(R.id.MiddleName)).getText().toString().trim() == null) {
                fullName = "" + ((EditText) findViewById(R.id.FirstName)).getText().toString().trim() + "_" + ((EditText) findViewById(R.id.LastName)).getText().toString().trim();
            } else {
                fullName = "" + ((EditText) findViewById(R.id.FirstName)).getText().toString().trim() + "_" + ((EditText) findViewById(R.id.MiddleName)).getText().toString().trim() + "_" + ((EditText) findViewById(R.id.LastName)).getText().toString().trim();
            }

            if (this.getIntent().getBooleanExtra("fileExists", false)) {
                File deleteMe = new File (this.getIntent().getStringExtra("filePath"));
                deleteMe.delete();
            }

            //Writes the medicalProfile to the file.
            try {
                FileOutputStream medicalProfileFile = openFileOutput(fullName + ".txt", MODE_PRIVATE);
                OutputStreamWriter profileOutput = new OutputStreamWriter(medicalProfileFile);
                profileOutput.write(medicalProfile);
                profileOutput.flush();
                profileOutput.close();
            } catch (IOException e) {
                System.out.println("Bummer");
            }

            Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
            this.finish();

        } else {    //If illegal inputs are found, a message will show the user which inputs they must fix.
            System.out.println("ILLEGAL INPUTS: " + illegalInputs.toString());
            String formattedIllegals = illegalInputs.toString().substring(1, illegalInputs.toString().length()-1);
            Toast.makeText(this, "You must fill out the following fields: " + formattedIllegals, Toast.LENGTH_LONG).show();
        }
    }

    //LoadProfile recreates (if dynamic) and repopulates the views that allow the user to edit the medicalProfile.
    public void loadProfile(String filePath) {
        EditText FirstName = findViewById(R.id.FirstName);
        EditText MiddleName = findViewById(R.id.MiddleName);
        EditText LastName = findViewById(R.id.LastName);
        RadioGroup Gender = findViewById(R.id.GenderLayout);
        Spinner DOBMonth = findViewById(R.id.DOBMonth);
        Spinner DOBDay = findViewById(R.id.DOBDay);
        EditText DOBYear = findViewById(R.id.DOBYear);
        EditText StreetAddress = findViewById(R.id.StreetAddress);
        EditText ApartmentNumber = findViewById(R.id.ApartmentNumber);
        EditText City = findViewById(R.id.City);
        Spinner State = findViewById(R.id.State);
        EditText PostalCode = findViewById(R.id.PostalCode);
        EditText InsuranceProvider = findViewById(R.id.InsuranceProvider);
        EditText InsuranceIDNumber = findViewById(R.id.InsuranceIDNumber);
        EditText PrimaryDoctor = findViewById(R.id.PrimaryDoctor);
        LinearLayout PastMedicalHistoryLayout = findViewById(R.id.PastMedicalHistoryLayout);
        EditText PastMedicalHistory1 = findViewById(R.id.PastMedicalHistory1);
        LinearLayout CurrentMedicationLayout = findViewById(R.id.CurrentMedicationLayout);
        EditText CurrentMedication = findViewById(R.id.CurrentMedication);
        EditText MedicationFrequency = findViewById(R.id.MedicationFrequency);
        EditText MedicationDosage = findViewById(R.id.MedicationDosage);
        LinearLayout AllergiesToMedicationsLayout = findViewById(R.id.AllergiesToMedicationsLayout);
        EditText Allergy1 = findViewById(R.id.Allergy1);
        EditText Reaction1 = findViewById(R.id.Reaction1);
        LinearLayout PastSurgicalHistoryLayout = findViewById(R.id.PastSurgicalHistoryLayout);
        EditText Procedure1 = findViewById(R.id.Procedure1);
        EditText Year1 = findViewById(R.id.Year1);
        LinearLayout SocialHistoryLayout = findViewById(R.id.SocialHistoryLayout);
        CheckBox SmokeCheckBox = findViewById(R.id.SmokeCheckBox);
        EditText SmokeFrequency = findViewById(R.id.SmokeFrequency);
        CheckBox VapeCheckBox = findViewById(R.id.VapeCheckBox);
        EditText VapeFrequency = findViewById(R.id.VapeFrequency);
        CheckBox ChewingCheckBox = findViewById(R.id.ChewingCheckBox);
        EditText ChewingFrequency = findViewById(R.id.ChewingFrequency);
        EditText FatherHistory = findViewById(R.id.FatherHistory);
        EditText MotherHistory = findViewById(R.id.MotherHistory);
        EditText SiblingConditions = findViewById(R.id.SiblingConditionsField2);

        File savedFile = new File (filePath);
            String loadedProfile;
        try {
            Scanner getProfile = new Scanner (savedFile);
            loadedProfile = getProfile.nextLine();

            Scanner dynamicParse = new Scanner(loadedProfile).useDelimiter(Pattern.quote("[DYNAMIC]")); //Parses dynamics from statics.
            String first14 = dynamicParse.next();
            String dynamics = dynamicParse.next();
            String SocialHistory = dynamicParse.next();
            String FamilyHistory = dynamicParse.next();

           Scanner parseFirst14 = new Scanner (first14).useDelimiter(Pattern.quote("[INPUT]"));    //Fills name
           FirstName.setText(parseFirst14.next());
           String middleName = parseFirst14.next();
           if (!(middleName.equals("[EMPTY]"))) {
               MiddleName.setText(middleName);
            }
           LastName.setText(parseFirst14.next());

           if (parseFirst14.next().equals("Male")) {                //Fills Gender
               Gender.check(Gender.getChildAt(0).getId());
           } else {
               Gender.check(Gender.getChildAt(1).getId());
           }

           DOBMonth.setSelection(getSelectionIndex(parseFirst14.next(), getResources().getStringArray(R.array.months)));    //Fills Date of Birth.
           DOBDay.setSelection(getSelectionIndex(parseFirst14.next(), getResources().getStringArray(R.array.days)));
           DOBYear.setText(parseFirst14.next());

           StreetAddress.setText(parseFirst14.next());          //Fills address.
           String apartmentNumber = parseFirst14.next();
           if (!(apartmentNumber).equals("[EMPTY]")) {
               ApartmentNumber.setText(apartmentNumber);
           }
           City.setText(parseFirst14.next());

           State.setSelection(getSelectionIndex(parseFirst14.next(), getResources().getStringArray(R.array.states)));

           PostalCode.setText(parseFirst14.next());
           InsuranceProvider.setText(parseFirst14.next());
           InsuranceIDNumber.setText(parseFirst14.next());
           PrimaryDoctor.setText(parseFirst14.next());

           parseFirst14.close();

           Scanner parseDynamics = new Scanner(dynamics).useDelimiter(Pattern.quote("[FIELD]"));        //Begins parsing of dynamic fields.
           String pastMedicalHistory = parseDynamics.next();
           String currentMedication = parseDynamics.next();
           String allergiesToMedications = parseDynamics.next();
           String pastSurgicalHistory = parseDynamics.next();
           parseDynamics.close();

            //Fills PastMedicalHistory
           Scanner parsePastMedicalHistory = new Scanner(pastMedicalHistory).useDelimiter(Pattern.quote("[SET]"));      //Parses Past Medical History
           if (!(pastMedicalHistory.equals("[EMPTY-FIELD]"))) {
               String pastMedicalAddition = parsePastMedicalHistory.next();
               if (!(pastMedicalAddition.equals("[EMPTY]"))) {
                   PastMedicalHistory1.setText(pastMedicalAddition);
               }
               loadRemainingDynamics(0, parsePastMedicalHistory, PastMedicalHistoryLayout);
           }
           parsePastMedicalHistory.close();

           Scanner parseCurrentMedication = new Scanner(currentMedication).useDelimiter(Pattern.quote("[SET]"));       //Parses Current Medications
           if (!(currentMedication.equals("[EMPTY-FIELD]"))) {
               Scanner parseFencePost = new Scanner(parseCurrentMedication.next()).useDelimiter(Pattern.quote("[INPUT]"));
               String currentMedicationAddition = parseFencePost.next();
               String currentFrequencyAddition = parseFencePost.next();
               String currentDosageAddition = parseFencePost.next();
               if (!(currentMedicationAddition.equals("[EMPTY]"))) {
                   CurrentMedication.setText(currentMedicationAddition);
               }
               if (!(currentFrequencyAddition.equals("[EMPTY]"))) {
                   MedicationFrequency.setText(currentFrequencyAddition);
               }
               if (!(currentDosageAddition.equals("[EMPTY]"))) {
                   MedicationDosage.setText(currentDosageAddition);
               }
               loadRemainingDynamics(1, parseCurrentMedication, CurrentMedicationLayout);
           }
           parseCurrentMedication.close();

           Scanner parseAllergiesToMedication = new Scanner (allergiesToMedications).useDelimiter(Pattern.quote("[SET]"));      //Parses Allergies to Medications.
           if (!(allergiesToMedications.equals("[EMPTY-FIELD]"))) {
               Scanner parseFencePost = new Scanner(parseAllergiesToMedication.next()).useDelimiter(Pattern.quote("[INPUT]"));
               String currentAllergyAddition = parseFencePost.next();
               String currentReactionAddition = parseFencePost.next();
               if (!(currentAllergyAddition.equals("[EMPTY]"))) {
                   Allergy1.setText(currentAllergyAddition);
               }
               if (!(currentReactionAddition).equals("[EMPTY]")) {
                   Reaction1.setText(currentReactionAddition);
               }
               loadRemainingDynamics(2, parseAllergiesToMedication, AllergiesToMedicationsLayout);
           }
           parseAllergiesToMedication.close();

            Scanner parsePastSurgicalHistory = new Scanner (pastSurgicalHistory).useDelimiter(Pattern.quote("[SET]"));          //Parses Past Surgeries.
            if (!(pastSurgicalHistory.equals("[EMPTY-FIELD]"))) {
                Scanner parseFencePost = new Scanner(parsePastSurgicalHistory.next()).useDelimiter(Pattern.quote("[INPUT]"));
                String currentSurgeryAddition = parseFencePost.next();
                String currentYearAddition = parseFencePost.next();
                if (!(currentSurgeryAddition.equals("[EMPTY]"))) {
                    Procedure1.setText(currentSurgeryAddition);
                }
                if (!(currentYearAddition).equals("[EMPTY]")) {
                    Year1.setText(currentYearAddition);
                }
                loadRemainingDynamics(3, parsePastSurgicalHistory, PastSurgicalHistoryLayout);
            }
            parsePastSurgicalHistory.close();


            Scanner parseSocialHistory = new Scanner(SocialHistory).useDelimiter(Pattern.quote("[SET]"));       //Parses SocialHistory.
            String unparsedTobacco = parseSocialHistory.next();
            if (!(unparsedTobacco.equals("[EMPTY-FIELD]"))) {
                Scanner parseTobacco = new Scanner(unparsedTobacco).useDelimiter(Pattern.quote("[INPUT]"));     //Parses Tobacco Product.
                String usesProduct;
                String frequency;
                for (int tobaccoProduct = 0; tobaccoProduct < 3; tobaccoProduct++) {
                    usesProduct = parseTobacco.next();
                    frequency = parseTobacco.next();
                    if (tobaccoProduct == 0) {
                        if (usesProduct.equals("Yes")) {
                            SmokeCheckBox.setChecked(true);
                        }
                        if (!(frequency.equals("[EMPTY]"))) {
                            SmokeFrequency.setText(frequency);
                        }
                    }
                    if (tobaccoProduct == 1) {
                        if (usesProduct.equals("Yes")) {
                            VapeCheckBox.setChecked(true);
                        }
                        if (!(frequency.equals("[EMPTY]"))) {
                            VapeFrequency.setText(frequency);
                        }
                    }
                    if (tobaccoProduct == 2) {
                        if (usesProduct.equals("Yes")) {
                            ChewingCheckBox.setChecked(true);
                        }
                        if (!(frequency.equals("[EMPTY]"))) {
                            ChewingFrequency.setText(frequency);
                        }
                    }
                }
                parseTobacco.close();
            }

            Scanner parseSHInput;
            String unparsedInputString;
            String currentSelection;
            String currentExtra;
            RadioGroup currentSocialHistoryGroup;
            RadioButton checkMe;
            EditText additionalInfo;
            for (int socialHistoryGroup = 7; socialHistoryGroup < SocialHistoryLayout.getChildCount(); socialHistoryGroup+=3) {     //Parses the rest of the Social History (the rest is similar).
                unparsedInputString = parseSocialHistory.next();
                parseSHInput = new Scanner(unparsedInputString).useDelimiter(Pattern.quote("[INPUT]"));
                currentSelection = parseSHInput.next();
                currentExtra = parseSHInput.next();
                parseSHInput.close();
                currentSocialHistoryGroup = (RadioGroup) SocialHistoryLayout.getChildAt(socialHistoryGroup);
                additionalInfo = (EditText) SocialHistoryLayout.getChildAt(socialHistoryGroup + 1);

                    if (currentSelection.equals("No")) {
                        checkMe = (RadioButton) currentSocialHistoryGroup.getChildAt(0);
                        currentSocialHistoryGroup.check(checkMe.getId());
                    } else {
                        checkMe = (RadioButton) currentSocialHistoryGroup.getChildAt(1);
                        currentSocialHistoryGroup.check(checkMe.getId());
                        if (!(currentExtra.equals("[EMPTY]"))) {
                            additionalInfo.setText(currentExtra);
                        }
                    }
                }
            parseSocialHistory.close();

            Scanner parseFamilyHistory = new Scanner (FamilyHistory).useDelimiter(Pattern.quote("[INPUT]"));        //Parses FamilyHistory.
            String fatherHistory = parseFamilyHistory.next();
            String motherHistory = parseFamilyHistory.next();
            String siblingConditions = parseFamilyHistory.next();
            if (!(fatherHistory.equals("[EMPTY]"))) {
                FatherHistory.setText(fatherHistory);
            }
            if (!(motherHistory.equals("[EMPTY]"))) {
                MotherHistory.setText(motherHistory);
            }
            if (!(siblingConditions.equals("[EMPTY]"))) {
                SiblingConditions.setText(siblingConditions);
            }

        } catch (FileNotFoundException fileNotFound) {
            System.out.println("Bummer with the file at loading");
        }
    }

    //Private helper. Determines the index of the value the user selected.
    private int getSelectionIndex(String selection, String[] choices) {
        int selectionIndex = -1;
        int currentValue = 0;
        for (String value : choices) {
            if (value.equals(selection)) {
                selectionIndex = currentValue;
            }
            currentValue++;
        }
        return selectionIndex;
    }

    //Loads remaining dynamics after initial fields are filled.
    //dynamic corresponds to a specific dynamic treatment. 0 = Past Medical History; 1 = Current Medication; 2 = Allergies to Medication; 3 = Surgical History
    private void loadRemainingDynamics(int dynamic, Scanner parseDynamic, LinearLayout parentLayout) {
        int currentConstruction;
        String addition;
        String addition2;
        String addition3;
        String inputs;
        LinearLayout subLayout;
        EditText first;
        EditText second;
        EditText third;
        if (dynamic == 0) {
            currentConstruction = 2;
            while (parseDynamic.hasNext()) {
                addition = parseDynamic.next();
                if (!(addition.equals("[EMPTY]"))) {
                    realAddPastMedicalHistory();
                    if (parentLayout.getChildAt(currentConstruction) instanceof EditText) {
                        first = ((EditText) parentLayout.getChildAt(currentConstruction));
                        first.setText(addition);
                    }
                }
                currentConstruction+=2;
            }
        } else if (dynamic == 1) {
            currentConstruction = 2;
            while (parseDynamic.hasNext()) {
                inputs = parseDynamic.next();
                Scanner parseInputs = new Scanner(inputs).useDelimiter(Pattern.quote("[INPUT]"));
                realAddCurrentMedication();
                if (parentLayout.getChildAt(currentConstruction) instanceof LinearLayout) {
                    subLayout = (LinearLayout) parentLayout.getChildAt(currentConstruction);
                    first = ((EditText) subLayout.getChildAt(0));
                    second = ((EditText) subLayout.getChildAt(1));
                    third = ((EditText) subLayout.getChildAt(2));
                    addition = parseInputs.next();
                    addition2 = parseInputs.next();
                    addition3 = parseInputs.next();
                    if (!(addition.equals("[EMPTY]"))) {
                        first.setText(addition);
                    }
                    if (!(addition2.equals("[EMPTY]"))) {
                        second.setText(addition2);
                    }
                    if (!(addition3.equals("[EMPTY]"))) {
                        third.setText(addition3);
                    }
                }
                currentConstruction+=2;
                parseInputs.close();
            }
        } else if (dynamic == 2) {
            currentConstruction = 2;
            while (parseDynamic.hasNext()) {
                inputs = parseDynamic.next();
                Scanner parseInputs = new Scanner(inputs).useDelimiter(Pattern.quote("[INPUT]"));
                realAddAllergiesToMedication();
                if (parentLayout.getChildAt(currentConstruction) instanceof LinearLayout) {
                    subLayout = (LinearLayout) parentLayout.getChildAt(currentConstruction);
                    first = ((EditText) subLayout.getChildAt(0));
                    second = ((EditText) subLayout.getChildAt(1));
                    addition = parseInputs.next();
                    addition2 = parseInputs.next();
                    if (!(addition.equals("[EMPTY]"))) {
                        first.setText(addition);
                    }
                    if (!(addition2.equals("[EMPTY]"))) {
                        second.setText(addition2);
                    }
                }
                currentConstruction+=2;
                parseInputs.close();
            }
        } else if (dynamic == 3) {
            currentConstruction = 2;
            while (parseDynamic.hasNext()) {
                inputs = parseDynamic.next();
                Scanner parseInputs = new Scanner(inputs).useDelimiter(Pattern.quote("[INPUT]"));
                realAddPastSurgicalHistory();
                if (parentLayout.getChildAt(currentConstruction) instanceof LinearLayout) {
                    subLayout = (LinearLayout) parentLayout.getChildAt(currentConstruction);
                    first = ((EditText) subLayout.getChildAt(0));
                    second = ((EditText) subLayout.getChildAt(1));
                    addition = parseInputs.next();
                    addition2 = parseInputs.next();
                    if (!(addition.equals("[EMPTY]"))) {
                        first.setText(addition);
                    }
                    if (!(addition2.equals("[EMPTY]"))) {
                        second.setText(addition2);
                    }
                }
                currentConstruction+=2;
                parseInputs.close();
            }
        }
        parseDynamic.close();
    }


}

//TODO: Ensure Date is Correct (Don't allow illegal dates like February 31).
//TODO: Optional: Reduce Redundancy (if check in add).
