package com.example.medicalfiletransfer;

import android.app.Activity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.ArrayList;


/**
 * SaveManager by Michael Viscardi
 * Handles the "dirty work of saving, assuming input is valid."
 * Accepts an ArrayList of Views of all the Views of CreateActivity and creates a medicalProfile
 */
public class SaveManager {
    public String medicalProfile = "";   //The point of SaveManager is to get a medicalProifle.
    public Activity CreateActivity;      //Connects this to the CreateActivity.

    public SaveManager (Activity inputActivity, ArrayList<View> views) {
        CreateActivity = inputActivity;
        LinearLayout UserInput = CreateActivity.findViewById(R.id.UserInput);
        int endOfDynamics = UserInput.getChildCount() - 3;
        //FIRST 14 STATIC CHILDREN LOOP. PUTS [INPUT] DELIMITER BETWEEN INPUTS FROM NAME TO PRIMARY CARE DOCTOR. PUTS [EMPTY] FOR ANY NON-REQUIRED FIELDS (Middle name AND Apt#)
        LinearLayout fencePostName = (LinearLayout) UserInput.getChildAt(2);
        String currentName = ((EditText)fencePostName.getChildAt(0)).getText().toString().trim();
        medicalProfile += currentName;
        for (int nameCount = 1; nameCount < fencePostName.getChildCount(); nameCount++) {
            medicalProfile += "[INPUT]";
            currentName =  ((EditText)fencePostName.getChildAt(nameCount)).getText().toString().trim();
            if (currentName.equals("") || currentName == null) {
                medicalProfile += "[EMPTY]";
            } else {
                medicalProfile += currentName;
            }
        }

        for (int child = 4; child < 15; child++) {     //Loop to go through userInput

            if (UserInput.getChildAt(child) instanceof LinearLayout) {  //Only fires for LinearLayouts of input, not the instruction TextViews.
                LinearLayout subLayout = (LinearLayout) UserInput.getChildAt(child);    //  For iteration through every input
                if (subLayout instanceof RadioGroup) {
                    RadioGroup Gender = (RadioGroup) subLayout;
                    RadioButton selectedGender = CreateActivity.findViewById(Gender.getCheckedRadioButtonId());
                    medicalProfile += "[INPUT]" + selectedGender.getText().toString();
                } else {
                    for (int subChild = 0; subChild < subLayout.getChildCount(); subChild++) {  //Loop to save every input.

                        if (subLayout.getChildAt(subChild) instanceof EditText) {       //Fires if children are EditTexts.
                            if (((EditText) subLayout.getChildAt(subChild)).getText().toString().length() == 0) { //Only exists to prevent "empty" errors in MiddleName and ApartmentNumber.
                                medicalProfile += "[INPUT][EMPTY]";
                            } else {
                                medicalProfile += "[INPUT]" + ((EditText) subLayout.getChildAt(subChild)).getText().toString();
                            }
                        } else if (subLayout.getChildAt(subChild) instanceof Spinner) {    //Else only fires if child is a spinner.
                            Spinner subChildSpinner = (Spinner) subLayout.getChildAt(subChild);
                            medicalProfile += "[INPUT]" + subChildSpinner.getSelectedItem();
                        }
                    }
                }
            } else if (UserInput.getChildAt(child) instanceof EditText) {
                medicalProfile += "[INPUT]" + ((EditText) UserInput.getChildAt(child)).getText().toString();
            }
        }

        //DYNAMIC CHILDREN LOOPS. SAVES PAST MEDICAL HISTORY, CURRENT MEDICATION(S), ALLERGIES TO MEDICATION(S), AND SURGICAL HISTORY.
            //Places [DYNAMIC] between the static first14 and Social History.
            //Places [FIELD] between dynamic fields. (e.g. between Past Medical History and Current Medications)
                //Places [EMPTY-FIELD] if the field is empty or all views within it have been removed.
            //Places [Set] between dynamic additions within the fields (e.g. between two current medications)
            //Places [INPUT] between inputs of each set. (e.g. between medication frequency and medication dosage in CurrentMedication).
                //Places [EMPTY] if a set has an input, but another input is not filled out (e.g. if surgical procedure is filled out, but not year in Surgical History).
        medicalProfile += "[DYNAMIC]";
        boolean lastSetWasEmpty = false;    //Allows for Fenceposting to work. [SET] delimiter is not added if the set before it is empty.
        boolean emptyField = true;          //Simplifies loading. If everything is empty, it adds the [EMPTY-FIELD] delimiter.
        LinearLayout currentField;          //Current Field in UserInput
        LinearLayout currentSet;            //Current Set in Field.
        EditText currentEditText1;
        EditText currentEditText2;          //For use in getting input. Prevents need for nested^2.
        EditText currentEditText3;
        String currentAddition;


        currentField = (LinearLayout) UserInput.getChildAt(15); //PASTMEDICALHISTORY
        if (currentField.getChildCount() == 0) {    //If no sets exist, emptyField is true.
            emptyField = true;
        } else {
            currentEditText1 = (EditText) currentField.getChildAt(0);
            if (currentEditText1.getText().toString().trim().equals("")) {  //If the first in the fencepost is empty.
                lastSetWasEmpty = true;
            } else {
                medicalProfile += currentEditText1.getText().toString().trim(); //If the first in the fencepost is filled.
                emptyField = false;
            }
            for (int remainingFencepost = 2; remainingFencepost < currentField.getChildCount(); remainingFencepost += 2) {
                currentEditText1 = (EditText) currentField.getChildAt(remainingFencepost);
                if (currentEditText1.getText().toString().trim().equals("")) {
                    lastSetWasEmpty = true;
                } else {
                    if (lastSetWasEmpty) {
                        medicalProfile += currentEditText1.getText().toString().trim();
                    } else {
                        medicalProfile += "[SET]" + currentEditText1.getText().toString().trim();
                    }
                    emptyField = false;
                }
            }
        }
        if (emptyField) {
            medicalProfile += "[EMPTY-FIELD]";
        }

        //CURRENTMEDICATION
            medicalProfile += "[FIELD]";
            lastSetWasEmpty = false;
            emptyField = true;
            currentField = (LinearLayout) UserInput.getChildAt(18); //CURRENTMEDICATION
            if (currentField.getChildCount() == 0) {
                emptyField = true;
            } else {
                currentSet = (LinearLayout) currentField.getChildAt(0);
                currentAddition = "";
                currentEditText1 = (EditText) currentSet.getChildAt(0);
                currentEditText2 = (EditText) currentSet.getChildAt(1);
                currentEditText3 = (EditText) currentSet.getChildAt(2);
                if (currentEditText1.getText().toString().trim().equals("")) {
                    currentAddition += "[EMPTY][INPUT]";
                } else {
                    currentAddition += currentEditText1.getText().toString().trim() + "[INPUT]";
                }
                if (currentEditText2.getText().toString().trim().equals("")) {
                    currentAddition += "[EMPTY][INPUT]";
                } else {
                    currentAddition += currentEditText2.getText().toString().trim() + "[INPUT]";
                }
                if (currentEditText3.getText().toString().trim().equals("")) {
                    currentAddition += "[EMPTY]";
                } else {
                    currentAddition += currentEditText3.getText().toString().trim();
                }
                if (currentAddition.equals("[EMPTY][INPUT][EMPTY][INPUT][EMPTY]")) {
                    lastSetWasEmpty = true;
                } else {
                    medicalProfile += currentAddition;
                    emptyField = false;
                }
                for (int currentSetIndex = 2; currentSetIndex < currentField.getChildCount(); currentSetIndex += 2) {
                        currentSet = (LinearLayout) currentField.getChildAt(currentSetIndex);
                        currentAddition = "";
                        currentEditText1 = (EditText) currentSet.getChildAt(0);
                        currentEditText2 = (EditText) currentSet.getChildAt(1);
                        currentEditText3 = (EditText) currentSet.getChildAt(2);
                        if (currentEditText1.getText().toString().trim().equals("")) {
                            currentAddition += "[EMPTY][INPUT]";
                        } else {
                            currentAddition += currentEditText1.getText().toString().trim() + "[INPUT]";
                        }
                        if (currentEditText2.getText().toString().trim().equals("")) {
                            currentAddition += "[EMPTY][INPUT]";
                        } else {
                            currentAddition += currentEditText2.getText().toString().trim() + "[INPUT]";
                        }
                        if (currentEditText3.getText().toString().trim().equals("")) {
                            currentAddition += "[EMPTY]";
                        } else {
                            currentAddition += currentEditText3.getText().toString().trim();
                        }
                        if (currentAddition.equals("[EMPTY][INPUT][EMPTY][INPUT][EMPTY]")) {
                            lastSetWasEmpty = true;
                        } else {
                            if (lastSetWasEmpty) {
                                medicalProfile += currentAddition;
                            } else {
                                medicalProfile += "[SET]" + currentAddition;
                            }
                            emptyField = false;
                        }
                }
            }
            if (emptyField) {
                medicalProfile += "[EMPTY-FIELD]";
            }

            //ALLERGIESTOMEDICATIONS
                    emptyField = true;
                    lastSetWasEmpty = false;
                        medicalProfile += "[FIELD]";
                        currentField = (LinearLayout) UserInput.getChildAt(21);
                        if (currentField.getChildCount() == 0) {
                            emptyField = true;
                        } else {
                            currentSet = (LinearLayout) currentField.getChildAt(0);
                            currentAddition = "";
                            currentEditText1 = (EditText) currentSet.getChildAt(0);
                            currentEditText2 = (EditText) currentSet.getChildAt(1);
                            if (currentEditText1.getText().toString().trim().equals("")) {
                                currentAddition += "[EMPTY][INPUT]";
                            } else {
                                currentAddition += currentEditText1.getText().toString().trim() + "[INPUT]";
                            }
                            if (currentEditText2.getText().toString().trim().equals("")) {
                                currentAddition += "[EMPTY]";
                            } else {
                                currentAddition += currentEditText2.getText().toString().trim();
                            }
                            if (currentAddition.equals("[EMPTY][INPUT][EMPTY]")) {
                                lastSetWasEmpty = true;
                            } else {
                                medicalProfile += currentAddition;
                                emptyField = false;
                            }
                            for (int currentSetIndex = 2; currentSetIndex < currentField.getChildCount(); currentSetIndex+=2) {
                                currentSet = (LinearLayout) currentField.getChildAt(currentSetIndex);
                                currentAddition = "";
                                currentEditText1 = (EditText) currentSet.getChildAt(0);
                                currentEditText2 = (EditText) currentSet.getChildAt(1);
                                if (currentEditText1.getText().toString().trim().equals("")) {
                                    currentAddition += "[EMPTY][INPUT]";
                                } else {
                                    currentAddition += currentEditText1.getText().toString().trim() + "[INPUT]";
                                }
                                if (currentEditText2.getText().toString().trim().equals("")) {
                                    currentAddition += "[EMPTY]";
                                } else {
                                    currentAddition += currentEditText2.getText().toString().trim();
                                }
                                if (currentAddition.equals("[EMPTY][INPUT][EMPTY]")) {
                                    lastSetWasEmpty = true;
                                } else {
                                    if (lastSetWasEmpty) {
                                        medicalProfile += currentAddition;
                                    } else {
                                        medicalProfile += "[SET]" + currentAddition;
                                    }
                                    emptyField = false;
                                }
                            }
                        }
                        if (emptyField) {
                            medicalProfile += "[EMPTY-FIELD]";
                        }

        //PAST SURGERIES.
        emptyField = true;
        lastSetWasEmpty = false;
        medicalProfile += "[FIELD]";
        currentField = (LinearLayout) UserInput.getChildAt(24);
        if (currentField.getChildCount() == 0) {
            emptyField = true;
        } else {
            currentSet = (LinearLayout) currentField.getChildAt(0);
            currentAddition = "";
            currentEditText1 = (EditText) currentSet.getChildAt(0);
            currentEditText2 = (EditText) currentSet.getChildAt(1);
            if (currentEditText1.getText().toString().trim().equals("")) {
                currentAddition += "[EMPTY][INPUT]";
            } else {
                currentAddition += currentEditText1.getText().toString().trim() + "[INPUT]";
            }
            if (currentEditText2.getText().toString().trim().equals("")) {
                currentAddition += "[EMPTY]";
            } else {
                currentAddition += currentEditText2.getText().toString().trim();
            }
            if (currentAddition.equals("[EMPTY][INPUT][EMPTY]")) {
                lastSetWasEmpty = true;
            } else {
                medicalProfile += currentAddition;
                emptyField = false;
            }
            for (int currentSetIndex = 2; currentSetIndex < currentField.getChildCount(); currentSetIndex+=2) {
                currentSet = (LinearLayout) currentField.getChildAt(currentSetIndex);
                currentAddition = "";
                currentEditText1 = (EditText) currentSet.getChildAt(0);
                currentEditText2 = (EditText) currentSet.getChildAt(1);
                if (currentEditText1.getText().toString().trim().equals("")) {
                    currentAddition += "[EMPTY][INPUT]";
                } else {
                    currentAddition += currentEditText1.getText().toString().trim() + "[INPUT]";
                }
                if (currentEditText2.getText().toString().trim().equals("")) {
                    currentAddition += "[EMPTY]";
                } else {
                    currentAddition += currentEditText2.getText().toString().trim();
                }
                if (currentAddition.equals("[EMPTY][INPUT][EMPTY]")) {
                    lastSetWasEmpty = true;
                } else {
                    if (lastSetWasEmpty) {
                        medicalProfile += currentAddition;
                    } else {
                        medicalProfile += "[SET]" + currentAddition;
                    }
                    emptyField = false;
                }
            }
        }
        if (emptyField) {
            medicalProfile += "[EMPTY-FIELD]";
        }



        medicalProfile += "[DYNAMIC]";
        //SOCIAL HISTORY
        //Places [EMPTY-FIELD] for Tobacco if no tobacco is used.
        //For every drug input (Smoke, Vape, Chewing Tobacco, Alcohol, Marijuana, and Illicits):
            //Places [SET] between the full drug inputs (e.g. between alcohol/alcoholfrequency and marijuana/marijuanafrequency)
            //Places [INPUT] between each drug input (e.g. between smokes and smokefrequency)
            //Places [EMPTY] for unspecified drug-use frequencies.
        LinearLayout socialHistory = (LinearLayout) UserInput.getChildAt(endOfDynamics);

        LinearLayout tobaccoProductLayout = (LinearLayout) socialHistory.getChildAt(2);
        CheckBox smokes = (CheckBox) tobaccoProductLayout.getChildAt(0);
        CheckBox vapes = (CheckBox) tobaccoProductLayout.getChildAt(1);
        CheckBox chewsTobacco = (CheckBox) tobaccoProductLayout.getChildAt(2);

        if (!(smokes.isChecked() || vapes.isChecked() || chewsTobacco.isChecked())) {
            medicalProfile += "[EMPTY-FIELD]";
        } else {
            EditText currentEditText; //Records frequency for each Tobacco Product.
            if (smokes.isChecked()) {
                currentEditText = (EditText) socialHistory.getChildAt(3);
                if (!(currentEditText.getText().toString().trim().equals("") || currentEditText.getText()==null)) {
                    medicalProfile += "Yes[INPUT]" + currentEditText.getText().toString().trim();
                } else {
                    medicalProfile += "Yes[INPUT][EMPTY]";
                }
            } else {
                medicalProfile += "No[INPUT][EMPTY]";
            }
            if (vapes.isChecked()) {
                currentEditText = (EditText) socialHistory.getChildAt(4);
                if (!(currentEditText.getText().toString().trim().equals("") || currentEditText.getText()==null)) {
                    medicalProfile += "[INPUT]Yes[INPUT]" + currentEditText.getText().toString().trim();
                } else {
                    medicalProfile += "[INPUT]Yes[INPUT][EMPTY]";
                }
            } else {
                medicalProfile += "[INPUT]No[INPUT][EMPTY]";
            }
            if (chewsTobacco.isChecked()) {
                currentEditText = (EditText) socialHistory.getChildAt(5);
                if (!(currentEditText.getText().toString().trim().equals("") || currentEditText.getText()==null)) {
                    medicalProfile += "[INPUT]Yes[INPUT]" + currentEditText.getText().toString().trim();
                } else {
                    medicalProfile += "[INPUT]Yes[INPUT][EMPTY]";
                }
            } else {
                medicalProfile += "[INPUT]No[INPUT][EMPTY]";
            }
        }

        //Tobacco acts as a FencePost, so I can immediately start adding "[SET]"
        RadioGroup currentRadioGroup;
        RadioButton currentNo;
        RadioButton currentSelection;
        String currentFreq;
        for (int sHGroup = 7; sHGroup < socialHistory.getChildCount(); sHGroup+=3) {
            medicalProfile += "[SET]";
            currentRadioGroup = (RadioGroup) socialHistory.getChildAt(sHGroup);
            currentNo = (RadioButton) currentRadioGroup.getChildAt(0);
            currentFreq = ((EditText)socialHistory.getChildAt(sHGroup + 1)).getText().toString().trim();
            if (currentNo.isChecked()) {
                medicalProfile += "No[INPUT][EMPTY]";
            } else {
                currentSelection = CreateActivity.findViewById(currentRadioGroup.getCheckedRadioButtonId());
                if (currentFreq.equals("") || currentFreq == null) {
                    medicalProfile += currentSelection.getText().toString() + "[INPUT][EMPTY]";
                } else {
                    medicalProfile += currentSelection.getText().toString() + "[INPUT]" + currentFreq;
                }
            }


        }

        //FAMILY HISTORY
        //Places [INPUT] between inputs (e.g. between fatherConditions and motherConditions).
        //Places [EMPTY] if the input is not filled.
        medicalProfile += "[DYNAMIC]";
        LinearLayout FamilyHistory = (LinearLayout) UserInput.getChildAt(UserInput.getChildCount()-1);
        String currentFamHist = ((EditText)FamilyHistory.getChildAt(0)).getText().toString().trim();
        if (currentFamHist.equals("") || currentFamHist == null) {
            medicalProfile += "[EMPTY]";
        } else {
            medicalProfile += currentFamHist;
        }
        for (int famHistCount = 1; famHistCount < FamilyHistory.getChildCount(); famHistCount++) {
            medicalProfile += "[INPUT]";
            currentFamHist = ((EditText)FamilyHistory.getChildAt(famHistCount)).getText().toString().trim();
            if (currentFamHist.equals("") || currentFamHist == null) {
                medicalProfile += "[EMPTY]";
            } else {
                medicalProfile += currentFamHist;
            }
        }

    }

    public String getMedicalProfile () {    //Returns the MedicalProfile.
        System.out.println(medicalProfile);
        return this.medicalProfile;
    }
}


//TODO: Increase readability through introducing variables instead of calling .getChildAt, .toString(), .trim(), and repeated casts.

