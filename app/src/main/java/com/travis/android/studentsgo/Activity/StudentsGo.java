package com.travis.android.studentsgo.Activity;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cloudinary.android.LogLevel;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.policy.GlobalUploadPolicy;
import com.cloudinary.android.policy.UploadPolicy;
import com.travis.android.studentsgo.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LENOVO on 28-01-2019.
 */

public class StudentsGo extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();
                Map config = new HashMap();
        config.put("cloud_name", "dpxfdn3d8");
        config.put("api_key", "172568498646598");
        config.put("api_secret", "NNa_bFKyVxW0AB30wL8HVoFxeSs");
        MediaManager.init(this, config);
//        Fresco.initialize(this);
        Log.d("*******:", "onCreate");
    }}
