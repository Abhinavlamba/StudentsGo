package com.travis.android.studentsgo.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.travis.android.studentsgo.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimeTableFragment extends Fragment {

    Activity activity;
    @SuppressLint("ValidFragment")
    public TimeTableFragment(Activity activity)
    {
        this.activity = activity;
    }
    public TimeTableFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time_table, container, false);
    }

}
