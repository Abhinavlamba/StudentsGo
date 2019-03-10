package com.travis.android.studentsgo.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.travis.android.studentsgo.R;
import com.travis.android.studentsgo.model.Chat;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    List<Chat> chatList;
    Activity activity;
    Chat chat;
//    FirebaseAuth auth;

    public ChatAdapter(List<Chat> chatList, Activity activity) {
        this.chatList = chatList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        auth = FirebaseAuth.getInstance();
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatbox,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {
        chat = chatList.get(position);

//        if(chat.getUid().equals(auth.getCurrentUser().getUid())){
//    }
//        else{
//            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        }
        holder.textView.setText(chat.getPhone());
        holder.textView1.setText(chat.getBody());
        holder.textView2.setText(chat.getName());

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView,textView1,textView2;
        CardView cardView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.phone);
            textView1 = itemView.findViewById(R.id.body);
            textView2 = itemView.findViewById(R.id.name);
            cardView = itemView.findViewById(R.id.cardView);


        }
    }
}
