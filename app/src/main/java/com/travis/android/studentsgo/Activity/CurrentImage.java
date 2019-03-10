package com.travis.android.studentsgo.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.artjimlop.altex.AltexImageDownloader;
import com.squareup.picasso.Picasso;
import com.travis.android.studentsgo.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class CurrentImage extends AppCompatActivity {
    ImageView imageView;
    Button btn;
    Activity context;
    String url,url1;
    String ext;
    ProgressDialog mProgressDialog;

// instantiate it within the onCreate method

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_image);
        Intent i = getIntent();
        if (Build.VERSION.SDK_INT >= 23) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE
            ,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.WAKE_LOCK}, 3);
            //do your check here
            Log.v("PERMISSION","ACCESS");
        }
        imageView = findViewById(R.id.image);
        mProgressDialog = new ProgressDialog(CurrentImage.this);
        mProgressDialog.setMessage("Downloading Image");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        url = i.getStringExtra("Image");
        url1 = url;
        Picasso.get().load(url).into(imageView);
        btn = findViewById(R.id.download_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DownloadTask downloadTask = new DownloadTask(CurrentImage.this);
                downloadTask.execute(url);
//                AltexImageDownloader.writeToDisk(CurrentImage.this, url, Environment.getDataDirectory()+"/new");

            }
        });

    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                File path = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS);
                ext = MimeTypeMap.getFileExtensionFromUrl(url);
               String p = URLUtil.guessFileName(url, null, null);
               Log.v("papa" , p);
                File file = new File(path, p);
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();
                Log.v("fileLength", String.valueOf(fileLength));
                // download the file

                input = connection.getInputStream();
                Log.v("PAATH",Environment.getExternalStorageDirectory().getAbsolutePath());
//                output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath());
                output = new BufferedOutputStream(new FileOutputStream(file), 8192);
                Log.v("file123", String.valueOf(file));
                byte data[] = new byte[8192];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length1 is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
            mProgressDialog.setMessage("Downloading Image " + String.valueOf(progress[0]) + "%");
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null)
            Log.v("resulting",result);
            mWakeLock.release();
            mProgressDialog.dismiss();
//            Log.v("DEMO",result);
            if (result != null)
                Toast.makeText(context,"Download error: "+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();
        }


    }
}
