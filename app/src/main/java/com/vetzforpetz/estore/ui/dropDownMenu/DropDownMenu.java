package com.vetzforpetz.estore.ui.dropDownMenu;

import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

public class DropDownMenu extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    public  DropDownMenu() {
        super();
    }

    public DropDownMenu(int spinnerLayout) {

        super();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
