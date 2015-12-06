package com.ipfw.myezshopper;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DatePickerFragment extends DialogFragment {

    private DatePicker mDatePicker;
    public static final String EXTRA_DATE = "com.ipfw.myezshopper.date";
    public static final String EXTRA_LONG_DATE = "com.ipfw.myezshopper.longdate";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int year = Calendar.getInstance().get(Calendar.YEAR);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_date_picker);
        mDatePicker.init(year, month, day, null);
        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.date_picker_title).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth()+1;
                int day = mDatePicker.getDayOfMonth();
                String date = month + "-" + day + "-" + year;

                String selectedDate = year + "-" + month + "-" + day;
                TimeZone tz = TimeZone.getTimeZone("UTC");
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                formatter.setTimeZone(tz);
                String time = "";
                try {
                    Date selDate = formatter.parse(selectedDate);
                    time = selDate.getTime() + "";
                } catch (ParseException e) {

                }

                sendResult(Activity.RESULT_OK, date, time);
            }
        }).create();
    }

    public void sendResult(int resultCode, String strDate, String longDate){
        if(getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, strDate);
        intent.putExtra(EXTRA_LONG_DATE, longDate);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
