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

import com.example.blackcoffer_neelanshi.Model.History;
import com.example.blackcoffer_neelanshi.Model.PillBox;
import com.example.blackcoffer_neelanshi.R;

/**
 * This fragment is based on the code at
 * http://www.feelzdroid.com/2014/10/android-action-bar-tabs-swipe-views.html
 *
 * This fragment handles the view and controller of the history tab on home screen
 */

public class HistoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_history, container, false);

        TableLayout stk = (TableLayout) rootView.findViewById(R.id.table_history);

        TableRow tbrow0 = new TableRow(getActivity());

        TextView tt1 = new TextView(getActivity());
        tt1.setText("Pill Name");
        tt1.setTextSize(20);
        tt1.setGravity(Gravity.CENTER);
        tt1.setTypeface(null, Typeface.BOLD);
        tbrow0.addView(tt1);

        TextView tt2 = new TextView(getActivity());
        tt2.setText("Date Taken");
        tt2.setTextSize(20);
        tt2.setGravity(Gravity.CENTER);
        tt2.setTypeface(null, Typeface.BOLD);
        tbrow0.addView(tt2);

        TextView tt3 = new TextView(getActivity());
        tt3.setText("Time Taken");
        tt3.setTextSize(20);
        tt3.setGravity(Gravity.CENTER);
        tt3.setTypeface(null, Typeface.BOLD);
        tbrow0.addView(tt3);

        stk.addView(tbrow0);

        PillBox pillBox = new PillBox();

        for (History history: pillBox.getHistory(getActivity())){
            TableRow tbrow = new TableRow(getActivity());

            TextView t1v = new TextView(getActivity());
            t1v.setText(history.getPillName());
            t1v.setTextSize(20);
            t1v.setGravity(Gravity.CENTER);
            t1v.setMaxEms(4);
            tbrow.addView(t1v);

            TextView t2v = new TextView(getActivity());
            String date = history.getDateString();
            t2v.setText(date);
            t2v.setTextSize(20);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);

            TextView t3v = new TextView(getActivity());

            int nonMilitaryHour = history.getHourTaken() % 12;
            if (nonMilitaryHour == 0)
                nonMilitaryHour = 12;

            String minute;
            if (history.getMinuteTaken() < 10)
                minute = "0" + history.getMinuteTaken();
            else
                minute = "" + history.getMinuteTaken();

            String time = nonMilitaryHour + ":" + minute + " " + history.getAm_pmTaken();
            t3v.setText(time);
            t3v.setTextSize(20);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);

            stk.addView(tbrow);
         }
        return rootView;
    }
}