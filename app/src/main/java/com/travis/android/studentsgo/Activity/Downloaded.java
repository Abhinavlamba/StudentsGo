package com.travis.android.studentsgo.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.travis.android.studentsgo.R;
import com.travis.android.studentsgo.adapter.HistoryAdapter;
import com.travis.android.studentsgo.model.History;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Downloaded extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    List<History> itemList;
    Gson gson;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded);
        Intent intent = getIntent();
        recyclerView = findViewById(R.id.recyclerview);
        sharedPreferences = getSharedPreferences("Downloaded", Context.MODE_PRIVATE);
        itemList = new ArrayList<>();
        editor = sharedPreferences.edit();
        gson = new Gson();
        String response = sharedPreferences.getString("key", null);
        if (!response.equals(null)) {
            itemList = gson.fromJson(response, new TypeToken<List<History>>() {
            }.getType());
        }
        historyAdapter = new HistoryAdapter(itemList,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();
        historyAdapter.setOnItemClick(new HistoryAdapter.OnItemClick() {
            @Override
            public void getPosition(int pos) {
                File  path = Environment.getExternalStorageDirectory();
                File file = new File(path,itemList.get(pos).getName());
                Log.v("FIleNAMe",itemList.get(pos).getName());
                MimeTypeMap map = MimeTypeMap.getSingleton();
                String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
                String type = map.getMimeTypeFromExtension(ext);
                if (type == null)
                    type = "*/*";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Log.v("FIleNAMe", String.valueOf(file));
                Uri data = Uri.fromFile(file);
                intent.setDataAndType(data, type);
                startActivity(intent);
            }
        });
        String json = gson.toJson(itemList);
        editor.putString("key", json);
        editor.apply();
    }
}
