package com.travis.android.studentsgo.adapter;

import android.app.Activity;
import android.support.v4.app.FragmentPagerAdapter;

import com.travis.android.studentsgo.fragments.BotFragment;
import com.travis.android.studentsgo.fragments.AssignmentsFragment;
import com.travis.android.studentsgo.fragments.ChatFragment;
import com.travis.android.studentsgo.fragments.DownloadedFragment;
import com.travis.android.studentsgo.fragments.SyllabusFragment;
import com.travis.android.studentsgo.fragments.TimeTableFragment;

/**
 * Created by LENOVO on 02-02-2019.
 */

public class SimplePagerAdapter extends FragmentPagerAdapter {
    Activity activity;
    private String[] tab_titles = new String[]{"Announcements","Assignments","Syllabus","TimeTable","Downloaded","NITHBot"};
    public SimplePagerAdapter(android.support.v4.app.FragmentManager fm, Activity activity) {
        super(fm);
        this.activity = activity;
    }
    @Override
    public int getCount() {
        return tab_titles.length;
    }
    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        if (position == 0)
            return new ChatFragment(activity);
        else if(position == 1)
            return new AssignmentsFragment(activity);
        else if(position == 2)
            return new SyllabusFragment(activity);
        else if(position == 3)
            return new TimeTableFragment(activity);
        else if(position == 4)
            return new DownloadedFragment(activity);
        else if(position==5)
            return new BotFragment(activity);
        return new DownloadedFragment(activity);
    }
    @Override
    public CharSequence getPageTitle(int position)
    {
        return tab_titles[position];
    }
}
