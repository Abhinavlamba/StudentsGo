package com.travis.android.studentsgo.adapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.travis.android.studentsgo.Activity.Downloaded;
import com.travis.android.studentsgo.R;
import com.travis.android.studentsgo.model.DownloadedFile;

import java.util.List;
public class AdapterDownloaded extends RecyclerView.Adapter<AdapterDownloaded.MyViewHolder> {
    List<DownloadedFile> files;
    Activity context;
    OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    public interface OnItemClick {
        void getPosition(int pos);
    }

    public AdapterDownloaded (List<DownloadedFile> itemList , Activity context)
    {
        this.files = itemList;
        this.context = context;
    }
    @NonNull
    @Override
    public AdapterDownloaded.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_adapter_downloaded,parent,false);

        return new MyViewHolder(itemView);
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull AdapterDownloaded.MyViewHolder holder, final int position) {
        holder.fileName.setText(files.get(position).getName());
        holder.uploadedBy.setText(files.get(position).getUploadedBy());
        if (position % 5 == 0)
            holder.document.setCardBackgroundColor(Color.parseColor("#CC1E88E5")) ;
        else if (position % 5 == 1)
            holder.document.setCardBackgroundColor(Color.parseColor("#CCFF8F00")) ;
        else if (position % 5 == 2)
            holder.document.setCardBackgroundColor(Color.parseColor("#CCE53935"));
        else if (position % 5 == 3)
            holder.document.setCardBackgroundColor(Color.parseColor("#CCAA00FF"));
        else if (position % 5 == 4)
            holder.document.setCardBackgroundColor(Color.parseColor("#CC69F0AE"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClick.getPosition(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView uploadedBy,fileName;
        CardView document;
        public MyViewHolder(View itemView)
        {
            super(itemView);
//            String path;
            uploadedBy = itemView.findViewById(R.id.uploadedby);
            fileName = itemView.findViewById(R.id.name);
            document = itemView.findViewById(R.id.document);
        }

    }
}
