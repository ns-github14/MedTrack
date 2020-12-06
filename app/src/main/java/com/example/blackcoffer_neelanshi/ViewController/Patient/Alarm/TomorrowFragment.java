package com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.example.blackcoffer_neelanshi.Model.Alarm;
import com.example.blackcoffer_neelanshi.Model.PillBox;
import com.example.blackcoffer_neelanshi.R;

/**
 * This fragment is based on the code at
 * http://www.feelzdroid.com/2014/10/android-action-bar-tabs-swipe-views.html
 *
 * This fragment handles the view and controller of the tomorrow tab on home screen
 */

public class TomorrowFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_tomorrow, container, false);

        TableLayout stk = (TableLayout) rootView.findViewById(R.id.table_tomorrow);

        //Typeface lightFont = Typeface.createFromAsset(container.getContext().getAssets(), "fonts/Roboto-Light.ttf");

        PillBox pillBox = new PillBox();

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) + 1;
        if(day == 8)
            day = 1;

        List<Alarm> alarms = Collections.emptyList();

        try {
            alarms = pillBox.getAlarms(getActivity(), day);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if(alarms.size() != 0) {
            for(Alarm alarm: alarms) {
                TableRow tbrow = new TableRow(getActivity());

                TextView t1v = new TextView(getActivity());
                t1v.setText(alarm.getPillName());
                //t1v.setTextColor(Color.WHITE);
                t1v.setGravity(Gravity.CENTER);
                t1v.setPadding(30, 30, 30, 30);
                t1v.setTextSize(25);
                //t1v.setTypeface(lightFont);
                tbrow.addView(t1v);

                TextView t2v = new TextView(getActivity());

                String time = alarm.getStringTime();

                t2v.setText(time);
                //t2v.setTextColor(Color.WHITE);
                t2v.setGravity(Gravity.CENTER);
                t2v.setPadding(30, 30, 30, 30);
                t2v.setTextSize(25);
                //t2v.setTypeface(lightFont);
                tbrow.addView(t2v);

                stk.addView(tbrow);
            }
        } else {
            TableRow tbrow = new TableRow(getActivity());

            TextView t1v = new TextView(getActivity());
            t1v.setText("You don't have any alarms for Tomorrow!");
            //t1v.setTextColor(Color.WHITE);
            t1v.setGravity(Gravity.CENTER);
            t1v.setPadding(30, 30, 30, 30);
            t1v.setTextSize(25);
            //t1v.setTypeface(lightFont);
            t1v.setMaxEms(10);
            tbrow.addView(t1v);

            stk.addView(tbrow);
        }
        return rootView;
    }
}

