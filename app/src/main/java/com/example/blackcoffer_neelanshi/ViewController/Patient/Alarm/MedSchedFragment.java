package com.example.blackcoffer_neelanshi.ViewController.Patient.Alarm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.blackcoffer_neelanshi.R;
import com.example.blackcoffer_neelanshi.ViewController.Doctor.PendingFragment;
import com.example.blackcoffer_neelanshi.ViewController.Doctor.RequestFragment;
import com.example.blackcoffer_neelanshi.ViewController.Doctor.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedSchedFragment extends Fragment {

    ViewPager2 viewPager;

    public MedSchedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_appointment, container, false);

        View view = inflater.inflate(R.layout.fragment_med_sched, container, false);
        viewPager = view.findViewById(R.id.viewPager);
        List<Map<String, Object>> mapList = new ArrayList<>();

        Map<String, Object> tab1 = setTabTitleAndFragment("History", new RequestFragment());
        Map<String, Object> tab2 = setTabTitleAndFragment("Today", new PendingFragment());
        Map<String, Object> tab3 = setTabTitleAndFragment("Tomorrow", new PendingFragment());

        mapList.add(tab1);
        mapList.add(tab2);
        mapList.add(tab3);

        final ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity(), mapList);
        viewPager.setAdapter(viewPagerAdapter);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);

        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(viewPagerAdapter.getTitle(position));
            }
        }).attach();

        return view;
    }

    Map<String, Object> setTabTitleAndFragment(String title, Fragment fragment) {

        Map<String, Object> fragmentwithTitleMap = new HashMap<>();
        fragmentwithTitleMap.put("fragmentTitle", title);
        fragmentwithTitleMap.put("fragment", fragment);
        return fragmentwithTitleMap;
    }
}