package com.travis.android.studentsgo.model;

/**
 * Created by LENOVO on 24-01-2019.
 */

public class Branch {
    String branch;
    String year;
    String image;
    public Branch (String branch ,String year ,String image)
    {
        this.branch = branch;
        this.year = year;
        this.image = image;
    }
    public String getBranch()
    {
        return branch;
    }
    public String getYear()
    {
        return year;
    }
    public String getImage()
    {
        return image;
    }
}

