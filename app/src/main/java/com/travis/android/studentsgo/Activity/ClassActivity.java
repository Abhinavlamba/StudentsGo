package com.travis.android.studentsgo.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.cloudinary.android.policy.TimeWindow;
import com.google.gson.JsonObject;
import com.isseiaoki.simplecropview.CropImageView;
import com.isseiaoki.simplecropview.callback.CropCallback;
import com.isseiaoki.simplecropview.callback.LoadCallback;
import com.isseiaoki.simplecropview.callback.SaveCallback;
import com.travis.android.studentsgo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ClassActivity extends AppCompatActivity implements View.OnClickListener {
    Intent intent1;
    String curBranch,curYear,curImage;
    Button ratio16,ratio4,ratiof,ratios;
    ImageButton button,save,rotateC,rotateA,camera;
    ImageView uploadimage;
    Uri saveUri,selectedImage;
    Bitmap bitmap;
    Context context;
    CropImageView mCropView;
    LoadCallback mLoadCallback;
    SaveCallback saveCallback;
    File photoFile;
    String path;
    ProgressBar pb;
    byte [] byteArray;
    ImageButton cropimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);
        AndroidNetworking.initialize(getApplicationContext());
//        AndroidNetworking.setParserFactory(new JacksonParserFactory());

//        AndroidNetworking.setParserFactory(new JacksonParserFactory());
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            //do your check here
        }

        intent1 = getIntent();
        curBranch = intent1.getStringExtra("Branch");
        curYear = intent1.getStringExtra("Year");
        curImage = intent1.getStringExtra("Image");
        button = findViewById(R.id.gallery);
        mCropView =  findViewById(R.id.cropImageView);
        camera = findViewById(R.id.camera);
//        cropimage = findViewById(R.id.cropimage);
//        pb = findViewById(R.id.pb);
//        uploadimage = findViewById(R.id.uploadImage);
        save = findViewById(R.id.save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), 1);
            }
        });
        rotateA = findViewById(R.id.rotate_anticlockwise);
        rotateC = findViewById(R.id.rotate_clockwise);
        ratio16 = findViewById(R.id.ratio16);
        ratio4 = findViewById(R.id.ratio4);
        ratiof = findViewById(R.id.ratiof);
        ratios = findViewById(R.id.ratios);
        ratio4.setOnClickListener(this);
        ratio16.setOnClickListener(this);
        ratiof.setOnClickListener(this);
        ratios.setOnClickListener(this);
        rotateA.setOnClickListener(this);
        rotateC.setOnClickListener(this);
        camera.setOnClickListener(this);
//        pb.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode == Activity.RESULT_OK) {
            selectedImage = data.getData();
            path = getRealPathFromURI(this,selectedImage);
            Log.v("PAATH",path);
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                mCropView.load(selectedImage).execute(mLoadCallback);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      crop();
                    }
                });
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (requestCode == 1024 && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
//            mCropView.setImageBitmap(photo);
            selectedImage = getImageUri(this,photo);
            Log.v("URI123", String.valueOf(selectedImage));
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mCropView.load(selectedImage).execute(mLoadCallback);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    crop();
                }
            });

        }

    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public void crop()
    {
        mCropView.crop(selectedImage)
                .execute(new CropCallback() {
                    @Override public void onSuccess(final Bitmap cropped) {
                        mCropView.save(cropped)
                                .execute(saveUri, saveCallback);
                        selectedImage = saveUri;
                        mCropView.setImageBitmap(cropped);
                        mCropView.setCompressFormat(Bitmap.CompressFormat.JPEG);
                        mCropView.setCompressQuality(100);
                        mCropView.setOutputMaxSize(300, 300);
                        mCropView.setAnimationEnabled(true);
                        mCropView.setAnimationDuration(200);
//                                        Bitmap bmp = intent.getExtras().get("data");
                        Bitmap cp = cropped;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        cp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                        byteArray = stream.toByteArray();
//                                        Time.sle ep(10000);
//                                        String requestId = MediaManager.get().upload(path)
//                                                .unsigned("xf7gsy1r")
//                                                .option("public_id", "test")
//
//                                                .dispatch();
//                        Log.v("paaath",path);
                        String requestId = MediaManager.get().upload(byteArray).constrain(TimeWindow.immediate())
                                .unsigned("xf7gsy1r")
                                .option("connect_timeout", 10000)
                                .option("read_timeout", 10000)
                                .callback(new UploadCallback() {
                                    @Override
                                    public void onStart(String requestId) {

                                    }
                                    @Override
                                    public void onProgress(String requestId, long bytes, long totalBytes) {
                                    }

                                    @Override
                                    public void onSuccess(String requestId, Map resultData) {
//                                      System.out.println("URLLL  "+resultData.get("url"));
                                        String asd = String.valueOf(resultData.get("url"));
                                        String phone = "8384099318";
                                        JSONObject jsonObject;
                                        jsonObject = new JSONObject();
                                        try {
                                            jsonObject.put("name", "Abhinav");
                                            jsonObject.put("class", curBranch+curYear);
                                            jsonObject.put("phoneNo",phone);
                                            jsonObject.put("imageurl",asd);
                                            jsonObject.put("docurl","abc");
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
//                                                        curYear = "2018";
//                                                        curBranch = "CSED";
                                        AndroidNetworking.post("https://polar-dusk-85200.herokuapp.com/ninja/")
                                                .addJSONObjectBody(jsonObject)
                                                .setTag("test")
                                                .setPriority(Priority.MEDIUM)
                                                .build()
                                                .getAsJSONObject(new JSONObjectRequestListener() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        Log.v("ABCCC","YOURS");
                                                        // do anything with response
                                                    }
                                                    @Override
                                                    public void onError(ANError error) {
                                                        // handle error
                                                    }
                                                });
                                        Toast.makeText(ClassActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                    @Override
                                    public void onError(String requestId, ErrorInfo error) {
                                        Log.v("HELLO","JIJIJ");
                                        finish();
                                        Toast.makeText(ClassActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                                        // your code here
                                    }

                                    @Override
                                    public void onReschedule(String requestId, ErrorInfo error) {
                                        // your code here
                                    }
                                })
                                .dispatch(ClassActivity.this);
                    }
                    @Override public void onError(Throwable e) {
                        Log.v("ERROR","JIJIJ");
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.ratio4:
                mCropView.setCropMode(CropImageView.CropMode.RATIO_4_3);
                break;
            case R.id.ratio16:
                mCropView.setCropMode(CropImageView.CropMode.RATIO_16_9);
                break;
            case R.id.ratiof:
                mCropView.setCropMode(CropImageView.CropMode.CIRCLE_SQUARE);
                break;
            case R.id.ratios:
                mCropView.setCropMode(CropImageView.CropMode.SQUARE);
                break;
            case R.id.rotate_clockwise:
                mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D); // rotate counter-clockwise by 90 degrees
                break;
            case R.id.rotate_anticlockwise:
                mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D); // rotate clockwise by 90 degrees
                break;
            case R.id.camera:
                Log.v("Hello","Check");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                   {
                    requestPermissions(new String[] {Manifest.permission.CAMERA},1020);
                   }
                   else
                    {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent,1024);
                    }
                }
                break;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1020) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new
                        Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1024);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }

        }
    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
