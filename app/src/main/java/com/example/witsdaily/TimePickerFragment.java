package com.example.witsdaily;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public final class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    int hour, minute;
    TextView edit;
    View tView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        hour = hourOfDay;
        this.minute = minute;
        edit = tView.findViewById(R.id.time);
        edit.setText(returnTime());

    }

    public String returnTime(){
        String hour = Integer.toString(this.hour), minute=Integer.toString(this.minute);
        if(hour.length() < 2){
            hour = "0" + hour;
        }
        if(minute.length() < 2){
            minute = "0" + minute;
        }
        return hour + ":" + minute + ":00";
    }

    public void setView(View v) {
        this.tView = v;
    }

}
