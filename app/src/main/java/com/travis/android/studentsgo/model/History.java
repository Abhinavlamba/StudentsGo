package com.travis.android.studentsgo.model;

import android.support.v7.widget.RecyclerView;

/**
 * Created by LENOVO on 31-01-2019.
 */

public class History {
    String uploadedBy;
    String name;

    public History(String uploadedBy, String name) {
        this.uploadedBy = uploadedBy;
        this.name = name;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public String getName() {
        return name;
    }

}
