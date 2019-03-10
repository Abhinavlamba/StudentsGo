package com.travis.android.studentsgo.Activity;

import android.app.Activity;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.travis.android.studentsgo.adapter.BranchAdapter;
import com.travis.android.studentsgo.model.Branch;
import com.travis.android.studentsgo.R;
import com.travis.android.studentsgo.adapter.BranchAdapter;

import java.util.ArrayList;
import java.util.List;

public class BranchActivity extends AppCompatActivity {
    ArrayList<Branch> list = new ArrayList<>();
    Activity activity;
    RecyclerView rv;
    EditText search;
    BranchAdapter branchAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch);
//        setStatusBarTranslucent(true);
        list.add(new Branch("CSE DUAL","1st Year","https://res.cloudinary.com/dpxfdn3d8/image/upload/v1548383637/Class-of-2020.jpg"));
        list.add(new Branch("CSE","1st Year","https://res.cloudinary.com/dpxfdn3d8/image/upload/v1548383637/Class-of-2020.jpg"));
        list.add(new Branch("CSE DUAL","2nd Year","https://res.cloudinary.com/dpxfdn3d8/image/upload/v1548383637/Class-of-2020.jpg"));
        list.add(new Branch("CSE","2nd Year","https://res.cloudinary.com/dpxfdn3d8/image/upload/v1548383637/Class-of-2020.jpg"));
        list.add(new Branch("IIIT CSE","1st Year","https://res.cloudinary.com/dpxfdn3d8/image/upload/v1548383637/Class-of-2020.jpg"));
        list.add(new Branch("IIIT CSE","2nd Year","https://res.cloudinary.com/dpxfdn3d8/image/upload/v1548383637/Class-of-2020.jpg"));
        branchAdapter = new BranchAdapter(list,this);
        rv = findViewById(R.id.branch_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(branchAdapter);
        branchAdapter.notifyDataSetChanged();
        search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // TODO Auto-generated method stub
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });
    }
    public void filter(String text){
        List<Branch> temp = new ArrayList();
        for(Branch d: list){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getBranch().equals(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        if (temp.size() != 0)
        branchAdapter.updateList(temp);
        else
            branchAdapter.updateList(list);
    }
//    protected void setStatusBarTranslucent(boolean makeTranslucent) {
//        View v = findViewById(R.id.bellow);
//        if (v != null) {
//            int paddingTop = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? getStatusBarHeight() : 0;
//            TypedValue tv = new TypedValue();
//            getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, tv, true);
//            paddingTop += TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
//            v.setPadding(0, makeTranslucent ? paddingTop : 0, 0, 0);
//        }
//
//        if (makeTranslucent) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        } else {
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }
//    }
//    public int getStatusBarHeight() {
//        int result = 0;
//        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
//        if (resourceId > 0) {
//            result = getResources().getDimensionPixelSize(resourceId);
//        }
//        return result;
//    }
}
