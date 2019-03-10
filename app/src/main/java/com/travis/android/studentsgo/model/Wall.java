package com.travis.android.studentsgo.model;

/**
 * Created by LENOVO on 27-01-2019.
 */
public class Wall {
    String uploadedBy , Image;
    public Wall(String uploadedBy , String Image)
    {
        this.uploadedBy = uploadedBy;
        this.Image = Image;
    }
    public String getUploader()
    {
        return uploadedBy;
    }
    public String getUploadedImage()
    {
     return Image;
    }
}
