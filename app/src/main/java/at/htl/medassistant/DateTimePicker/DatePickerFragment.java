package at.htl.medassistant.DateTimePicker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import at.htl.medassistant.R;

/**
 * Created by Sabrina on 16.06.2016.
 */

public class DatePickerFragment extends DialogFragment implements
        DatePickerDialog.OnDateSetListener{

    String parentButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        parentButton = getArguments().getString("ParentButton");

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date(year - 1900, monthOfYear, dayOfMonth);

        if (parentButton.equals("startdate")) {
            ((EditText) getActivity().findViewById(R.id.editTextStartDate)).setText(df.format(date));
        } else if (parentButton.equals("enddate")) {
            ((EditText) getActivity().findViewById(R.id.editTextEndDate)).setText(df.format(date));
        }
    }
}
