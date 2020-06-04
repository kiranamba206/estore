package com.vetzforpetz.estore.ui.datePicker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.vetzforpetz.estore.ConfirmFinalOrderActivity;
import com.vetzforpetz.estore.Prevalent.Prevalent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public  class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    Prevalent mPrevalent = Prevalent.getInstance();
    Date initDate;
    TextView viewToUpdateAfterUserSetsDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year;
        int month;
        int day;

        if (initDate != null) {
            c.setTime(initDate);
        }
         year = c.get(Calendar.YEAR);
         month = c.get(Calendar.MONTH);
         day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog =new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        //TODO : disable dates more than a week from current date in the DatePicker
        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user
        Calendar selectedDate  = Calendar.getInstance();
        selectedDate.set(year,month,day);
        /*if (selectedDate ) {
            Toast.makeText(this.getContext(), "Please select today's date or a future date",
                    Toast.LENGTH_SHORT).show();
        }

         */
        mPrevalent.getOrderHeader().setRequestedPickupDate(selectedDate.getTime());
        if(this.viewToUpdateAfterUserSetsDate != null) {
            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            this.viewToUpdateAfterUserSetsDate.setText(currentDate.format(selectedDate.getTime()));
        }
    }

    public void showDatePickerFragmentForOrderPage(FragmentManager fragmentManager, String tag,
                                                   TextView viewToUpdateDateAfterSetting,
                                                   Date dateToInitialize){
        this.viewToUpdateAfterUserSetsDate= viewToUpdateDateAfterSetting;
        this.initDate = dateToInitialize;

        this.show(fragmentManager, tag);
    }


}