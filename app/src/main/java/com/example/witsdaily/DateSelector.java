package com.example.witsdaily;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class DateSelector extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    public int year, day, month;
    TextView edit;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        System.err.println(year);
        System.err.println(month);
        System.err.println(day);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        this.year = year;
        this.month = month+1;
        this.day = day;

        edit = getActivity().findViewById(R.id.cancelDate);
        edit.setText(returnDate());
    }

    public String returnDate(){
        String year = Integer.toString(this.year), month=Integer.toString(this.month), day = Integer.toString(this.day);
        if(month.length() < 2){
            month = "0" + month;
        }
        if(day.length() < 2){
            day = "0" + day;
        }
        return year + "-" + month + "-" + day;
    }
}

