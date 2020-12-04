package com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatCheckBox;

import com.example.blackcoffer_neelanshi.R;

/**
 * Utilized the link below as a reference guide:
 * http://stackoverflow.com/questions/20015463/defining-custom-checkbox-in-android
 *
 * This is a customized check box used in AddActivity to allow the user to select
 * days of the week. The background gets blue when the checkbox is selected.
 */
public class CustomCheckBox extends AppCompatCheckBox {

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean t){
        if(t) {
            // checkbox_background is blue
            this.setBackgroundResource(R.drawable.checkbox_background);
            this.setTextColor(Color.WHITE);
        } else {
            this.setBackgroundColor(Color.TRANSPARENT);
            this.setTextColor(Color.BLACK);
        }
        super.setChecked(t);
    }
}