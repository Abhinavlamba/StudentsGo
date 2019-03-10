package com.travis.android.studentsgo.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.travis.android.studentsgo.BuildConfig;
import com.travis.android.studentsgo.R;
import com.travis.android.studentsgo.adapter.HistoryAdapter;
import com.travis.android.studentsgo.model.History;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class DownloadedFragment extends Fragment {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    List<History> itemList;
    Gson gson;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;
    Activity activity;
    @SuppressLint("ValidFragment")
    public DownloadedFragment(){}
    public DownloadedFragment(Activity activity) {
        this.activity = activity;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_downloaded, container, false);
        Intent intent = getActivity().getIntent();
                if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.v("ABC","ABC");
        }
        recyclerView = view.findViewById(R.id.recyclerview);
        sharedPreferences = getContext().getSharedPreferences("Downloaded", Context.MODE_PRIVATE);
        itemList = new ArrayList<>();
        editor = sharedPreferences.edit();
        gson = new Gson();
        String response = sharedPreferences.getString("key", "null");
        if (!response.equals("null")) {
            itemList = gson.fromJson(response, new TypeToken<List<History>>() {
            }.getType());
        }
        historyAdapter = new HistoryAdapter(itemList,getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(historyAdapter);
        historyAdapter.notifyDataSetChanged();
        historyAdapter.setOnItemClick(new HistoryAdapter.OnItemClick() {
            @Override
            public void getPosition(int pos) {
//                File path = Environment.getExternalStorageDirectory();
//                File file = new File(path,itemList.get(pos).getName());
//                Log.v("FIleNAMe",itemList.get(pos).getName());
//                MimeTypeMap map = MimeTypeMap.getSingleton();
//                String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
//                String type = map.getMimeTypeFromExtension(ext);
//                if (type == null)
//                    type = "*/*";
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                Log.v("FIleNAMe", String.valueOf(file));
////                Uri data = Uri.fromFile(file);
//
//                Log.v("FILENAME", String.valueOf(file));
//                intent.setDataAndType(data, type);
//                startActivity(intent);
                File file = new File(Environment.getExternalStorageDirectory(),
                        itemList.get(pos).getName());
                Uri path = Uri.fromFile(file);
//                Uri path = FileProvider.getUriForFile(activity,BuildConfig.APPLICATION_ID + ".provider",file);
                Log.v("Hello1234",itemList.get(pos).getName());
                Intent pdfOpenintent = new Intent(Intent.ACTION_VIEW);
                pdfOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                pdfOpenintent.setDataAndType(path, "*/*");
                try {
                    startActivity(pdfOpenintent);
                }
                catch (ActivityNotFoundException e) {

                }
            }
        });
        String json = gson.toJson(itemList);
        editor.putString("key", json);
        editor.apply();
        return view;

    }

}
