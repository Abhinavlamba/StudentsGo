package com.travis.android.studentsgo.Activity;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.gson.JsonObject;
import com.travis.android.studentsgo.adapter.WallAdapter;
import com.travis.android.studentsgo.model.Wall;
import com.travis.android.studentsgo.R;
import com.travis.android.studentsgo.model.Wall;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.Response;

public class WallActivity extends AppCompatActivity implements View.OnClickListener{
    String curBranch , curYear , curImage;
    FloatingActionButton upload_image;
    RecyclerView wallView;
    List<Wall> display;
    WallAdapter wallAdapter;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);
//        AndroidNetworking.initialize(getApplicationContext());
        Intent intent = getIntent();

        display = new ArrayList<>();
        curBranch = intent.getExtras().getString("Branch");
        curYear = intent.getExtras().getString("Year");
        curImage = intent.getExtras().getString("Image");
        upload_image = findViewById(R.id.upload_image);
        upload_image.setOnClickListener(this);
        wallView = findViewById(R.id.wallView);
        display.add(new Wall("Abhinav" , "https://res.cloudinary.com/dpxfdn3d8/image/upload/v1548546710/file_gu3s0d.jpg"));
        display.add(new Wall("Abhinav" , "https://res.cloudinary.com/dpxfdn3d8/image/upload/v1548546710/file_gu3s0d.jpg"));
        display.add(new Wall("Abhinav" , "https://res.cloudinary.com/dpxfdn3d8/image/upload/v1548546710/file_gu3s0d.jpg"));
        display.add(new Wall("Abhinav" , "https://res.cloudinary.com/dpxfdn3d8/image/upload/v1548546710/file_gu3s0d.jpg"));
        display.add(new Wall("Abhinav" , "https://res.cloudinary.com/dpxfdn3d8/image/upload/v1548546710/file_gu3s0d.jpg"));
        getData();
        wallAdapter = new WallAdapter(display, this);
        wallView.setLayoutManager(new LinearLayoutManager(this));
        wallView.setAdapter(wallAdapter);
        Log.v("display", String.valueOf(display));
    }

public void getData() {
//    String temp = getString(R.string.base_url)+"/hotel/menu/";
    final RequestQueue requestQueue = Volley.newRequestQueue(this);
    StringRequest request = new StringRequest(Request.Method.GET,"https://polar-dusk-85200.herokuapp.com/ninja" ,
            new com.android.volley.Response.Listener<String>() {
                @Override
                public void onResponse(String jsonArray) {
                    try {
                        JSONArray array = new JSONArray(jsonArray);
                        for (int i = 0; i < array.length(); i++) {
                        try{
                            JSONObject jsonObject = array.getJSONObject(i);
                            String identity = curBranch + curYear;
                            String name = jsonObject.getString("name");
                            String image = jsonObject.getString("imageurl");
                            if (identity.equals(jsonObject.getString("class"))) {
                            Log.v("Fuck you", jsonObject.getString("class"));
                            display.add(new Wall(name, image));
                            Log.v("display",display.get(0).getUploader()+display.get(0).getUploadedImage());
                            }
                        }
                        catch(JSONException e) {
                        }
                        }
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new com.android.volley.Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                }
            });
    requestQueue.add(request);
//    final RequestQueue requestQueue = Volley.newRequestQueue(this);
//    JsonArrayRequest request = new JsonArrayRequest("https://polar-dusk-85200.herokuapp.com/ninja",
//            new com.android.volley.Response.Listener<JSONArray>() {
//                @Override
//                public void onResponse(JSONArray jsonArray) {
//
//                }
//            },
//            new com.android.volley.Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError volleyError) {
//                }
//            });
//    requestQueue.add(request);
}

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.upload_image:
                Intent intent2 = new Intent(WallActivity.this,ClassActivity.class);
                intent2.putExtra("Branch",curBranch);
                intent2.putExtra("Image",curImage);
                intent2.putExtra("Year",curYear);
//                        Log.v("Maa","90");
                startActivity(intent2);
        }
    }
}
