package com.travis.android.studentsgo.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.solver.Cache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.travis.android.studentsgo.R;
import com.travis.android.studentsgo.model.History;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder>{
    List <History> itemList;
    Activity context;
    OnItemClick onItemClick;
    public void setOnItemClick(OnItemClick onItemClick ){
            this.onItemClick = onItemClick;
    }
    public interface OnItemClick {
        void getPosition(int pos);
    }
    public HistoryAdapter(List<History> itemList , Activity context)
    {
        this.itemList = itemList;
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_history,parent,false);
        return new MyViewHolder(itemView);
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.uploadedBy.setText(itemList.get(position).getUploadedBy());
//        #FF8F00</color>
//    <color name="redpink">#E53935</color>
//    <color name="purple">#AA00FF</color>
//    <color name="green">#69F0AE</
        holder.name.setText(itemList.get(position).getName());
        if (position % 5 == 0)
            holder.cardView.setCardBackgroundColor(Color.parseColor("#1E88E5")) ;
        else if (position % 5 == 1)
            holder.cardView.setCardBackgroundColor(Color.parseColor("#FF8F00")) ;
        else if (position % 5 == 2)
            holder.cardView.setCardBackgroundColor(Color.parseColor("#E53935"));
        else if (position % 5 == 3)
            holder.cardView.setCardBackgroundColor(Color.parseColor("#AA00FF"));
        else if (position % 5 == 4)
            holder.cardView.setCardBackgroundColor(Color.parseColor("#69F0AE"));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.getPosition(position);
            }
        });
    }
    @Override
    public int getItemCount() {
        return itemList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView uploadedBy,name;
        CardView cardView;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            uploadedBy = itemView.findViewById(R.id.uploadedby);
            cardView  = itemView.findViewById(R.id.info);
        }
    }
}
