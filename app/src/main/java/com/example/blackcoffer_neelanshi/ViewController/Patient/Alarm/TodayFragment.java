package com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm;

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

public class TodayFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_today, container, false);

        TableLayout stk = (TableLayout) rootView.findViewById(R.id.table_today);

        PillBox pillBox = new PillBox();

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        List<Alarm> alarms = Collections.emptyList();

        try {
            alarms = pillBox.getAlarms(getActivity(), day);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if(alarms.size() != 0) {
            for(Alarm alarm: alarms) {
                TableRow tbrow = new TableRow(getActivity());

                TextView t1v = new TextView(getActivity());
                t1v.setText(alarm.getPillName());
                t1v.setGravity(Gravity.CENTER);
                t1v.setPadding(30, 30, 30, 30);
                t1v.setTextSize(20);
                t1v.setTextColor(getResources().getColor(android.R.color.black));
                t1v.setMaxEms(6);

                tbrow.addView(t1v);

                TextView t2v = new TextView(getActivity());

                String time = alarm.getStringTime();
                t2v.setText(time);
                t2v.setGravity(Gravity.CENTER);
                t2v.setPadding(30, 30, 30, 30);
                t2v.setTextSize(20);
                t2v.setTextColor(getResources().getColor(android.R.color.black));
                tbrow.addView(t2v);

                stk.addView(tbrow);
            }
        }
        else {
            TableRow tbrow = new TableRow(getActivity());

            TextView t1v = new TextView(getActivity());
            t1v.setText("You don't have any alarms for Today!");
            t1v.setGravity(Gravity.CENTER);
            t1v.setPadding(30, 30, 30, 30);
            t1v.setTextSize(20);
            t1v.setMaxEms(10);
            tbrow.addView(t1v);

            stk.addView(tbrow);
        }
        return rootView;
    }
}
