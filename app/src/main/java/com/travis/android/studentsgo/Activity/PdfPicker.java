package com.travis.android.studentsgo.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.travis.android.studentsgo.R;
import com.travis.android.studentsgo.adapter.AdapterDownloaded;
import com.travis.android.studentsgo.model.DownloadedFile;
import com.travis.android.studentsgo.model.History;
import com.travis.android.studentsgo.model.Wall;

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

public class PdfPicker extends AppCompatActivity implements View.OnClickListener {
    Button button;
    String docFilePath;
    private StorageReference mStorageRef;
    String downloadUrl;
    TextView textView;
    List<DownloadedFile> files;
    List <History> downloadedList;
    Uri fileuri;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidNetworking.initialize(this);
        setContentView(R.layout.activity_pdf_picker);
        progressDialog = new ProgressDialog(PdfPicker.this);
        progressDialog.setMessage("Downloading File");
        progressDialog.setIndeterminate(true);
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE
                    ,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.WAKE_LOCK}, 3);
            Log.v("PERMISSION","ACCESS");
        }
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(true);
        button = findViewById(R.id.pdf);
        textView = findViewById(R.id.url);
        downloadHistory = findViewById(R.id.downloadedTask);
        downloadHistory.setOnClickListener(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                //                intent.addCategory(Intent.CATEGORY_OPENABLE);
                //                intent.setType("*/*");
                //                startActivityForResult(intent, 1);
                getDocument();
            }
        });

        files = new ArrayList<>();
//        files.add(new DownloadedFile("https://firebasestorage.googleapis.com/v0/b/studentsgo-1eed0.appspot.com/o/doc%2F1533745376693_Tutorial-2-1.pdf?alt=media&token=24628ea7-1c7d-4c2f-b89a-6321b1744323"
//         , "Abhinav",""));
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
        recyclerView = findViewById(R.id.recyclerview);
        documentAdapter = new AdapterDownloaded(files , this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(documentAdapter);
        documentAdapter.notifyDataSetChanged();
        documentAdapter.setOnItemClick(new AdapterDownloaded.OnItemClick() {
            @Override
            public void getPosition(int pos) {
                downloaded = getSharedPreferences("Downloaded",MODE_PRIVATE);
                editor = downloaded.edit();
                downloadedList = new ArrayList<>();
                String json = downloaded.getString("key","null");
                gson = new Gson();
                if (!json.equals("null"))
                {
                    downloadedList = gson.fromJson(json,new TypeToken<List<History>>(){}.getType());
                }
                Log.v("downloaded", String.valueOf(downloadedList));
                ext = MimeTypeMap.getFileExtensionFromUrl(files.get(pos).getPath());
                name = files.get(pos).getUploadedBy();
                filepath = files.get(pos).getPath();
                final DownloadTask downloadTask = new DownloadTask(PdfPicker.this);
                downloadTask.execute(files.get(pos).getPath());
                progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        downloadTask.cancel(true);
                    }
                });
            }
        });
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
                startActivity(new Intent(this,Downloaded.class));
                break;
        }
    }

    private class DownloadTask extends AsyncTask<String,Integer,String>{
        private Context context;
        private PowerManager.WakeLock wakeLock;
        public DownloadTask(Context context){
            this.context = context;
        }
        @Override
        protected String doInBackground(String... strings) {
            File file;
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
                File  path = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS);
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
    protected void onActivityResult(int req, int result, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(req, result, data);
        if (result == RESULT_OK) {
            fileuri = data.getData();
            docFilePath = getFileNameByUri(this, fileuri);
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
                                    Toast.makeText(PdfPicker.this, "Success", Toast.LENGTH_LONG).show();
                                    textView.setText(uri.toString());
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
                            Toast.makeText(PdfPicker.this, exception.getCause().getLocalizedMessage(), Toast.LENGTH_LONG).show();
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

