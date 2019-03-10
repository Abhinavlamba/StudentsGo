package com.travis.android.studentsgo.adapter;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.artjimlop.altex.AltexImageDownloader;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.travis.android.studentsgo.Activity.CurrentImage;
import com.travis.android.studentsgo.Activity.GlideApp;
import com.travis.android.studentsgo.model.Wall;
import com.travis.android.studentsgo.R;
import com.travis.android.studentsgo.model.Wall;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Random;

import jp.wasabeef.glide.transformations.BlurTransformation;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class WallAdapter extends RecyclerView.Adapter<WallAdapter.MyViewHolder> {

    private List<Wall> itemList;
    private Activity context;
    Bitmap img;
    public WallAdapter(List<Wall> itemList,Activity context)
    {
        this.itemList = itemList;
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AndroidNetworking.initialize(context);
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_wall_adapter,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final String uploadedBy , Image;
        uploadedBy = itemList.get(position).getUploader();
        Image = itemList.get(position).getUploadedImage();
        holder.uploadedBy.setText(uploadedBy);
        GlideApp.with(context)
                .load(Image)
                .apply(bitmapTransform(new BlurTransformation(4, 4)))
                .into(holder.image);
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Picasso.get().load(Image).into(holder.image);
                Intent intent = new Intent(context, CurrentImage.class);
                intent.putExtra("Image",itemList.get(position).getUploadedImage());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView uploadedBy;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);
            uploadedBy = itemView.findViewById(R.id.uploadedby);
            image = itemView.findViewById(R.id.image);
        }
    }
}
