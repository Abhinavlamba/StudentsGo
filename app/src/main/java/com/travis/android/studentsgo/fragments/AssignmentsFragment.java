package com.travis.android.studentsgo.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.travis.android.studentsgo.Activity.ClassActivity;
import com.travis.android.studentsgo.Activity.Downloaded;
import com.travis.android.studentsgo.Activity.MainActivity;
import com.travis.android.studentsgo.Activity.PdfPicker;
import com.travis.android.studentsgo.R;
import com.travis.android.studentsgo.adapter.AdapterDownloaded;
import com.travis.android.studentsgo.model.DownloadedFile;
import com.travis.android.studentsgo.model.History;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AssignmentsFragment extends Fragment implements View.OnClickListener {
    Button button;
    String docFilePath;
    private StorageReference mStorageRef;
    String downloadUrl;
//    TextView textView;
    List<DownloadedFile> files;
    List <History> downloadedList;
    Uri fileuri;
    int posi;
    String requireUrl;
    Context context;
    ProgressDialog progressDialog;
    AdapterDownloaded documentAdapter;
    RecyclerView recyclerView;
    SharedPreferences downloaded;
    SharedPreferences.Editor editor;
    Gson gson;
    String ext;
    String fileName;
    String name;
    Button downloadHistory;
    String filepath;
    Activity activity;
    @SuppressLint("ValidFragment")
    public AssignmentsFragment(Activity activity)
    {
        this.activity = activity;
    }
    public AssignmentsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


//        else {
//            Log.e("DB", "PERMISSION GRANTED");
//        }
        if (Build.VERSION.SDK_INT >= 23) {
//            if ((Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            }
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
//                Manifest.permission.WRITE_EXTERNAL_STORAGE = String.valueOf(PackageManager.PERMISSION_GRANTED);
        }
//        if (ActivityCompat.checkSelfPermission(getContext(),
//                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            Log.v("ABC","ABC");
//        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_assignments, container, false);
        AndroidNetworking.initialize(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Downloading File");
        progressDialog.setIndeterminate(true);

        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        button = view.findViewById(R.id.pdf);
//        textView = view.findViewById(R.id.url);
        downloadHistory = view.findViewById(R.id.downloadedTask);
        downloadHistory.setOnClickListener(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDocument();
            }
        });

        files = new ArrayList<>();
//        files.add(new DownloadedFile("https://firebasestorage.googleapis.com/v0/b/studentsgo-1eed0.appspot.com/o/doc%2F1533745376693_Tutorial-2-1.pdf?alt=media&token=24628ea7-1c7d-4c2f-b89a-6321b1744323"
//         , "Abhinav",""));
        final RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest request = new StringRequest(Request.Method.GET,"https://polar-dusk-85200.herokuapp.com/ninja" ,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonArray) {
                        try {
                            JSONArray array = new JSONArray(jsonArray);
                            for (int i = 0; i < array.length(); i++) {
                                try{
                                    JSONObject jsonObject = array.getJSONObject(i);
                                    String url2 = jsonObject.getString("docurl");
                                    if (url2.equals("abc"))
                                        continue;
                                    Log.v("docUrl",jsonObject.getString("docurl"));
                                    String p = jsonObject.getString("docurl");
                                    String n = jsonObject.getString("name");
                                    files.add(new DownloadedFile(p,n,""));
                                }
                                catch(JSONException e) {
                                }
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        documentAdapter.notifyDataSetChanged();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });

        requestQueue.add(request);
        Log.v("displayji", String.valueOf(files));
        recyclerView = view.findViewById(R.id.recyclerview);
        documentAdapter = new AdapterDownloaded(files , getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(documentAdapter);
        documentAdapter.notifyDataSetChanged();
        documentAdapter.setOnItemClick(new AdapterDownloaded.OnItemClick() {
            @Override
            public void getPosition(int pos) {

                posi = pos;
                downloaded = getActivity().getSharedPreferences("Downloaded",MODE_PRIVATE);
                editor = downloaded.edit();
                downloadedList = new ArrayList<>();
                String json = downloaded.getString("key","null");
                gson = new Gson();
                if (!json.equals("null"))
                {
                    downloadedList = gson.fromJson(json,new TypeToken<List<History>>(){}.getType());
                }
                Log.v("downloaded", String.valueOf(downloadedList));
                ext = MimeTypeMap.getFileExtensionFromUrl(files.get(posi).getPath());
                name = files.get(posi).getUploadedBy();
                filepath = files.get(posi).getPath();
                final DownloadTask downloadTask = new DownloadTask(getActivity());
                downloadTask.execute(files.get(posi).getPath());
//                Log.v("Paath hello",files.get)
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        downloadTask.cancel(true);
                    }
                });

                return;
            }
        });

        return view;
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case 3:
//            }
//
//            // other 'case' lines to check for other
//            // permissions this app might request
//        }
//    }
    @Override
public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

    switch(permsRequestCode)
    {
        case 3:
            boolean locationAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
            Log.v("Hello123","hello1234");
            boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;
            break;

    }

}
    private void getDocument()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/msword,application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
        startActivityForResult(intent, 101);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.downloadedTask:
                startActivity(new Intent(getActivity(),ClassActivity.class)) ;
                break;
        }
    }

    private class DownloadTask extends AsyncTask<String,Integer,String> {
        private Context context;
        private PowerManager.WakeLock wakeLock;
        public DownloadTask(Context context){
            this.context = context;
        }
        @Override
        protected String doInBackground(String... strings) {
            File file;
            Log.v("PERMISSION23","ACCESS");
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            int fileLength = connection.getContentLength();
            try {
//                String root = Environment.getExternalStorageDirectory().toString();
//                File path = Environment.getExternalStorageDirectory();
               File path =  Environment.getExternalStorageDirectory();
                String b = URLUtil.guessFileName(filepath, null, null);
                file = new File(path, b);
//                fileName = "StudentsFile."+ext;
                downloadedList.add(new History(name,b));
//                Log.v("fine",fileName);
                input = connection.getInputStream();
                output = new BufferedOutputStream(new FileOutputStream(file),4096);
                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data))!=-1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0) {
                        publishProgress((int) (total * 100 / fileLength));
                        output.write(data,0,count);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (output != null)
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                if (input != null)
                {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPreExecute(){

            super.onPreExecute();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK , getClass().getName());
            wakeLock.acquire();
            progressDialog.show();
        }
        @Override
        protected void onProgressUpdate (Integer... progress)
        {
            super.onProgressUpdate(progress);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(100);
            progressDialog.setProgress(progress[0]);
            progressDialog.setMessage("Downloading File " + String.valueOf(progress[0]) + "%");
        }
        @Override
        protected void onPostExecute(String result)
        {
            wakeLock.release();
            progressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download Error", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "File Downloaded", Toast.LENGTH_SHORT).show();
            MimeTypeMap map = MimeTypeMap.getSingleton();
//                Log.v("filenamm",fileName);
//            Log.v("ABC",files.get(pos).getPath());
            String save = gson.toJson(downloadedList);
            editor.putString("key",save);
            editor.apply();
        }
    }
    @Override
    public void onActivityResult(int req, int result, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(req, result, data);
        if (result == RESULT_OK) {
            fileuri = data.getData();
            docFilePath = getFileNameByUri(getActivity(), fileuri);
        }
//        File file = new File(docFilePath);
//        Intent target = new Intent(Intent.ACTION_VIEW);
//        target.setDataAndType(Uri.fromFile(file),"application/pdf");
//        target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//
//        Intent intent = Intent.createChooser(target, "Open File");
//        try {
//            startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            // Instruct the user to install a PDF reader here, or something
//        }
//        Toast.makeText(PdfPicker.this,docFilePath,Toast.LENGTH_SHORT).show();
//        Uri file = Uri.fromFile(new File(docFilePath));
        //        StorageReference riversRef = mStorageRef.child("doc/"+file.getLastPathSegment());
//
//
//        riversRef.putFile(file)
//                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        // Get a URL to the uploaded content
//                        Toast.makeText(PdfPicker.this,"Your file has successfully uploaded",Toast.LENGTH_SHORT).show();
//                        downloadUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
//                        textView.setText(downloadUrl);
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        Toast.makeText(PdfPicker.this,exception.toString(),Toast.LENGTH_LONG).show();
//
//                    }
//                });
        if (fileuri != null) {
            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReferenceProfilePic = firebaseStorage.getReference();
            final StorageReference imageRef = storageReferenceProfilePic.child("doc/" + fileuri.getLastPathSegment());

            imageRef.putFile(fileuri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
//                                    final DownloadTask downloadTask = new DownloadTask(PdfPicker.this);
//                                    downloadTask.execute(uri.toString());
                                    requireUrl = uri.toString();
                                    String phone = "8384099318";
                                    JSONObject jsonObject;
                                    jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("name", "Sharry");
                                        jsonObject.put("class", "CSE DUAL");
                                        jsonObject.put("phoneNo",phone);
                                        jsonObject.put("imageurl","");
                                        jsonObject.put("docurl",requireUrl);
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

//                                    AndroidNetworking.download(uri.toString(),Environment.DIRECTORY_DOWNLOADS,"ABC")
//                                            .setTag("downloadTest")
//                                            .setPriority(Priority.MEDIUM)
//                                            .build();
                                    Log.v("PUCK",uri.toString());
                                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_LONG).show();
//                                    textView.setText(uri.toString());
//                                    Log.v("CHECKDOC",uri.toString());
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            //if the upload is not successful
                            //hiding the progress dialog

                            //and displaying error message
                            Toast.makeText(getActivity(), exception.getCause().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //calculating progress percentage
//                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
//                        //displaying percentage in progress dialog
//                        progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
    }

// get file path

    private String getFileNameByUri(Context context, Uri uri)
    {
        String filepath = "";//default fileName
        //Uri filePathUri = uri;
        File file;
        if (uri.getScheme().toString().compareTo("content") == 0)
        {
            Cursor cursor = context.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.ORIENTATION }, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            cursor.moveToFirst();

            String mImagePath = cursor.getString(column_index);
            cursor.close();
            filepath = mImagePath;
        }
        else
        if (uri.getScheme().compareTo("file") == 0)
        {
            try
            {
                file = new File(new URI(uri.toString()));
                if (file.exists())
                    filepath = file.getAbsolutePath();

            }
            catch (URISyntaxException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        else
        {
            filepath = uri.getPath();
        }
        return filepath;
    }

}
