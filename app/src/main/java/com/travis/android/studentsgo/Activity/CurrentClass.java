package com.travis.android.studentsgo.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.travis.android.studentsgo.R;
import com.travis.android.studentsgo.adapter.SimplePagerAdapter;
import com.travis.android.studentsgo.fragments.DownloadedFragment;
public class CurrentClass extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    DownloadedFragment fragment;
    ViewPager viewPager;
    TabLayout tabLayout;
//    private int [] tabIcons = {
//            R.drawable.announcements24,R.drawable.assignments24,R.drawable.test24,R.drawable.syllabus24,R.drawable.timetable24
//            , R.drawable.downloaded
//    };
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_class);
        viewPager = findViewById(R.id.viewpager);
        if (Build.VERSION.SDK_INT >= 23)
        {
            ActivityCompat.requestPermissions(CurrentClass.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
//                Manifest.permission.WRITE_EXTERNAL_STORAGE = String.valueOf(PackageManager.PERMISSION_GRANTED);
        }
        final SimplePagerAdapter adapter = new SimplePagerAdapter(getSupportFragmentManager(),CurrentClass.this);
        viewPager.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        tabLayout =  findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0;i< tabLayout.getTabCount() ;i++)
        {
            TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_text, null);
//            tabOne.setText("Announcements");
//            tabOne.setCompoundDrawablesWithIntrinsicBounds(tabIcons[i], 0, 0, 0);
            tabLayout.getTabAt(i).setCustomView(tabOne);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        bottomNavigationView = findViewById(R.id.nav_bar);
//        bottomNavigationView.setOnNavigationItemSelectedListener(naviagtionItemReselectedListner);
//        bottomNavigationView.setItemBackgroundResource(R.drawable.backgroundgen);
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new DownloadedFragment(CurrentClass.this)).commit();
    }
//    private BottomNavigationView.OnNavigationItemSelectedListener naviagtionItemReselectedListner = new BottomNavigationView.OnNavigationItemSelectedListener(){
//        @Override
//        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//            fragment = null;
//            switch (item.getItemId())
//            {
//                case R.id.downloaded:
//                    fragment = new DownloadedFragment(CurrentClass.this);
//                    break;
//            }
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
//            return true;
//        }
//    };

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode)
        {
            case 3:
                Log.v("ABC","123456");
                if(grantResults.length>=1)
                {boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.v("Hello123","hello1234");
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;}
                break;

        }

    }

}
