package com.travis.android.studentsgo.model;

/**
 * Created by LENOVO on 30-01-2019.
 */

public class DownloadedFile {
    String path;
    String uploadedBy;
    String name;
    public DownloadedFile (String path , String uploadedBy , String name)
    {
        this.path = path;
        this.uploadedBy = uploadedBy;
        this.name = name;
    }
    public String getPath()
    {
        return path;
    }
    public String getName()
    {
        return name;
    }
    public String getUploadedBy()
    {
        return uploadedBy;
    }
}
