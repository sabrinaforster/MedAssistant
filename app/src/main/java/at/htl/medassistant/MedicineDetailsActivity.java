package at.htl.medassistant;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.sql.SQLDataException;
import java.util.Calendar;
import java.util.Date;

import at.htl.medassistant.DateTimePicker.DatePickerFragment;
import at.htl.medassistant.DateTimePicker.TimePickerFragment;
import at.htl.medassistant.entity.MedType;
import at.htl.medassistant.entity.Medicine;
import at.htl.medassistant.entity.Treatment;
import at.htl.medassistant.entity.User;
import at.htl.medassistant.model.MedicineDatabaseHelper;

public class MedicineDetailsActivity extends AppCompatActivity {

    private static final String TAG = MedicineDetailsActivity.class.getName();

    private EditText name;
    private EditText activeSubstance;
    private EditText startDate;
    private EditText endDate;
    private EditText timeOfTaking;
    private EditText periodicityInDays;
    private Button saveButton;

    private Button buttonStartDate;
    private Button buttonEndDate;
    private Button buttonTime;
    private Button buttonOptions;
    private boolean buttonOptionsOnClick = false;

    private View startDateTextInputLayout;
    private View endDateTextInputLayout;

    private RadioButton rbPharmaceutical;
    private RadioButton rbVaccine;

    private long medicineIdEdit = -1;
    boolean canSave = true;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_details);

        name = (EditText) findViewById(R.id.editTextName);
        activeSubstance = (EditText) findViewById(R.id.editTextActiveSubstance);
        startDate = (EditText) findViewById(R.id.editTextStartDate);
        endDate = (EditText) findViewById(R.id.editTextEndDate);
        timeOfTaking = (EditText) findViewById(R.id.editTextTimeOfTaking);
        periodicityInDays = (EditText) findViewById(R.id.editTextPeriodicityInDays);

        rbPharmaceutical = (RadioButton) findViewById(R.id.radioButtonPharmaceutical);
        rbPharmaceutical.setChecked(true);
        rbVaccine = (RadioButton) findViewById(R.id.radioButtonVaccine);

        rbPharmaceutical.setOnCheckedChangeListener(onCheckedChangeListener);
        rbVaccine.setOnCheckedChangeListener(onCheckedChangeListener);

        startDateTextInputLayout = findViewById(R.id.startDateTextInputLayout);
        endDateTextInputLayout = findViewById(R.id.endDateTextInputLayout);

        periodicityInDays.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (startDate.getText().toString().equals("") && endDate.getText().toString().equals("")) {
                    try {
                        Date now = new Date();
                        Date later = new Date();
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, Integer.parseInt(periodicityInDays.getText().toString()));
                        later.setYear(calendar.get(Calendar.YEAR) - 1900);
                        later.setMonth(calendar.get(Calendar.MONTH));
                        later.setDate(calendar.get(Calendar.DATE));
                        Treatment treatment = new Treatment(null, null, now, later, null);

                        startDate.setText(treatment.getStartDateToString());
                        endDate.setText(treatment.getEndDateToString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        buttonOptions = (Button) findViewById(R.id.details_options);
        buttonOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonOptionsOnClick == false) {
                    startDateTextInputLayout.setVisibility(View.VISIBLE);
                    endDateTextInputLayout.setVisibility(View.VISIBLE);

                    buttonOptionsOnClick = true;

                } else {
                    startDateTextInputLayout.setVisibility(View.GONE);
                    endDateTextInputLayout.setVisibility(View.GONE);
                    buttonOptionsOnClick = false;

                }
            }
        });

        saveButton = (Button) findViewById(R.id.buttonSave);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            changeDate();
                canSave = true;
            if (rbPharmaceutical.isChecked()) {
                checkLabelName();
                checkLabelPeriodicityInDays();
                checkLabelTimeOfTaking();
            } else {
                checkLabelName();
                checkLabelEnddate();
                checkLabelTimeOfTaking();
                Treatment t = new Treatment();
                t.setStartDate(new Date());
                startDate.setText(t.getStartDateToString());
                changeDate();
            }

            if (canSave) {
                saveTreatment(v);
            }
            }
        });

        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            name.setText(intent.getStringExtra("Medicinename"));
            periodicityInDays.setText(intent.getStringExtra("periodicityInDays"));
            activeSubstance.setText(intent.getStringExtra("ActiveSubstance"));
            startDate.setText(intent.getStringExtra("Startdate"));
            endDate.setText(intent.getStringExtra("Enddate"));
            timeOfTaking.setText(intent.getStringExtra("TimeOfTaking"));
            String medtype = intent.getStringExtra("MedicineType");
            if (medtype.equals("PHARMACEUTICAL")) {
                rbPharmaceutical.setChecked(true);
            } else if (medtype.equals("VACCINE")) {
                rbVaccine.setChecked(true);
            }

            MedicineDatabaseHelper db = MedicineDatabaseHelper.getInstance(this);

            medicineIdEdit = db.findMedicineByName(name.getText().toString()).getId();
        }

        buttonStartDate = (Button) findViewById(R.id.buttonStartDate);
        buttonStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("ParentButton", "startdate");
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.setArguments(args);
                dateFragment.show(getFragmentManager(), "datePicker");

            }
        });

        buttonEndDate = (Button) findViewById(R.id.buttonEndDate);
        buttonEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("ParentButton", "enddate");
                DialogFragment dateFragment = new DatePickerFragment();
                dateFragment.setArguments(args);
                dateFragment.show(getFragmentManager(), "datePicker");

            }
        });

        buttonTime = (Button) findViewById(R.id.buttonTime);
        buttonTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogFragment timeFragment = new TimePickerFragment();
                timeFragment.show(getFragmentManager(), "timePicker");
            }
        });

        startDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changeDate();
            }
        });

        endDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                changeDate();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void checkLabelEnddate() {
        if (endDate.getText().toString().length() == 0) {
            endDate.setError(getString(R.string.endDateError));
            canSave = false;
        }
        else {
            endDate.setError(null);
        }
    }

    private void checkLabelName() {
        if (name.getText().toString().length() == 0) {
            name.setError(getString(R.string.nameError));
            canSave = false;
        }
        else{
            name.setError(null);
        }
    }
    private void checkLabelPeriodicityInDays() {

        if (periodicityInDays.getText().toString().length() == 0) {
            periodicityInDays.setError(getString(R.string.periodicityInDaysError));
            canSave = false;
        }
        else {
            periodicityInDays.setError(null);
        }
    }

    private void checkLabelTimeOfTaking(){
        if (timeOfTaking.getText().toString().length() == 0) {
            timeOfTaking.setError(getString(R.string.timeOfTakingError));
            canSave = false;
        }
        else {
            timeOfTaking.setError(null);
        }
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (rbPharmaceutical.isChecked()) {
                buttonOptions.setVisibility(View.VISIBLE);
                startDateTextInputLayout.setVisibility(View.GONE);
                endDateTextInputLayout.setVisibility(View.GONE);
                periodicityInDays.setVisibility(View.VISIBLE);

                /*
                endDate.setHint(null);
                endDate.setHint(R.string.details_end_date);

                timeOfTaking.setHint(null);
                timeOfTaking.setHint(R.string.details_time_of_taking);*/

            } else if (rbVaccine.isChecked()) {
                buttonOptions.setVisibility(View.GONE);
                startDateTextInputLayout.setVisibility(View.GONE);

                endDateTextInputLayout.setVisibility(View.VISIBLE);
                periodicityInDays.setVisibility(View.GONE);

                /*
                endDate.setHint(null);
                endDate.setHint(R.string.details_reminderDate);

                timeOfTaking.setHint(null);
                timeOfTaking.setHint(R.string.details_reminderTime);*/
            }
            changeDate();
        }
    };

    private void changeDate() {
        try {
            String startDateNow = startDate.getText().toString();
            String endDateNow = endDate.getText().toString();

            if (startDateNow.equals("") || endDateNow.equals("")) {
                return;
            }
            Treatment treatment = null;
            try {
                treatment = new Treatment(null, null, startDateNow, endDateNow, "08:00");
            } catch (Exception e) {
                e.printStackTrace();
            }

            Calendar startCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            startCalendar.set(treatment.getStartDate().getYear(), treatment.getStartDate().getMonth(), treatment.getStartDate().getDate());
            endCalendar.set(treatment.getEndDate().getYear(), treatment.getEndDate().getMonth(), treatment.getEndDate().getDate());

            long diff = endCalendar.getTimeInMillis() - startCalendar.getTimeInMillis();

            long days = diff / (24 * 60 * 60 * 1000);

            periodicityInDays.setText(days + "");
            return;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTreatment(View v) {
        changeDate();
        MedicineDatabaseHelper db = MedicineDatabaseHelper.getInstance(this);

        User user = db.findUserById(db.findCurrentUserId());
        Medicine medicine = db.findMedicineByName(name.getText().toString());

        Intent intent = getIntent();

        if (intent.getExtras() != null && medicineIdEdit != -1) {
            medicine = new Medicine(name.getText().toString(), activeSubstance.getText().toString(), onMedTypeRadioButtonClick(), Integer.parseInt(periodicityInDays.getText().toString()));
            medicine.setId(medicineIdEdit);
        } else if (medicine == null) {
            medicine = new Medicine(name.getText().toString(), activeSubstance.getText().toString(), onMedTypeRadioButtonClick(), Integer.parseInt(periodicityInDays.getText().toString()));
            try {
                db.insertMedicine(medicine);
                long medicineId = db.findMedicineByName(medicine.getName()).getId();
                medicine.setId(medicineId);
            } catch (Exception e) {
                Log.d(TAG, "saveTreatment: Hinzufügen der Medicine nicht mögich");
            }
        }

        Treatment treatment = new Treatment(user, medicine, startDate.getText().toString(), endDate.getText().toString(), timeOfTaking.getText().toString());

        try {
            if (getIntent().getExtras() == null) {
                db.insertTreatment(treatment);
            } else {
                db.updateTreatment(treatment);
            }
        } catch (SQLDataException e) {
            Log.d(TAG, "saveTreatment: Hinzufügen des Treatment nicht möglich");
            e.printStackTrace();
        }
        finish();
    }

    private MedType onMedTypeRadioButtonClick() {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.radioButtonPharmaceutical:
                if (rbPharmaceutical.isChecked())
                    return MedType.PHARMACEUTICAL;
                break;
            case R.id.radioButtonVaccine:
                if (rbVaccine.isChecked())
                    return MedType.VACCINE;
                break;
        }
        return MedType.PHARMACEUTICAL;
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("MedicineDetails Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}

