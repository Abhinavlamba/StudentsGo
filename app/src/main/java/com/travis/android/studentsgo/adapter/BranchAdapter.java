//package com.travis.android.adapter;
package com.travis.android.studentsgo.adapter;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;
import com.travis.android.studentsgo.model.Branch;
import com.travis.android.studentsgo.Activity.BranchActivity;
import com.travis.android.studentsgo.Activity.ClassActivity;
import com.travis.android.studentsgo.Activity.WallActivity;
import com.travis.android.studentsgo.R;
import com.travis.android.studentsgo.model.Branch;

import java.util.List;

/**
 * Created by LENOVO on 23-01-2019.
 */

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.MyViewHolder>{

    private List<Branch> itemList;
        private Activity context;
        public Context context2;
       Branch branch;
    public BranchAdapter(List<Branch> itemList, Activity context)
    {
        this.itemList = itemList;
        this.context = context;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_branch,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        branch = itemList.get(position);
        holder.textView.setText(branch.getBranch());
        holder.textView1.setText(branch.getYear());
        Picasso.get().load(branch.getImage()).into(holder.imageView);
//        holder.imageView.setImageURI(Uri.parse(""));
        holder.branchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator objectAnimator1 =ObjectAnimator.ofFloat(holder.branchCard,"scaleX",0.95f);
                ObjectAnimator objectAnimator2 =ObjectAnimator.ofFloat(holder.branchCard,"scaleX",1f);
                ObjectAnimator objectAnimator3 =ObjectAnimator.ofFloat(holder.branchCard,"scaleY",0.95f);
                ObjectAnimator objectAnimator4 =ObjectAnimator.ofFloat(holder.branchCard,"scaleY",1f);
                objectAnimator1.setDuration(200);
                objectAnimator2.setDuration(100);
                objectAnimator3.setDuration(200);
                objectAnimator4.setDuration(100);
                objectAnimator2.setStartDelay(200);
                objectAnimator4.setStartDelay(200);
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(objectAnimator1,objectAnimator2,objectAnimator3,objectAnimator4);
                animatorSet.start();
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        Intent intent = new Intent(context2,WallActivity.class);
                        intent.putExtra("Branch",itemList.get(position).getBranch());
                        intent.putExtra("Image",itemList.get(position).getImage());
                        intent.putExtra("Year",itemList.get(position).getYear());
                        Log.v("Branch123",itemList.get(position).getBranch());
                        Log.v("Year", itemList.get(position).getYear());
//                        Log.v("Maa","90");
                        context2.startActivity(intent);
                    }
                });
            }
        });
    }

    public void updateList (List<Branch> list) {
        itemList = list;
        notifyDataSetChanged();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView,textView1;
        CardView branchCard;
        SelectableRoundedImageView imageView;
        public MyViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            textView1 = itemView.findViewById(R.id.textView1);
            imageView = itemView.findViewById(R.id.branchImage);
            branchCard = itemView.findViewById(R.id.branch_card);
            context2 = itemView.getContext();
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
//
}
