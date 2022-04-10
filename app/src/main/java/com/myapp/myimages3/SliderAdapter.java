package com.myapp.myimages3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.database.DatabaseReference;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder>   {

    private List<Upload> mUploads;
    private ViewPager2 viewPager2;
    private Context context;
    private int pos, currentPosition;

    public SliderAdapter(List<Upload> mUploads, ViewPager2 viewPager2, Context context, int pos) {
        this.mUploads = mUploads;
        this.viewPager2 = viewPager2;
        this.context = context;
        this.pos = pos;
    }
    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item_container,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder,int position) {

            if (pos == mUploads.size()-1){
                currentPosition = pos;
                pos = -1;
            }
            else {
                position = position + pos;
                currentPosition = position;
            }


        holder.setImage(currentPosition);

        if (position == mUploads.size() -2){
            viewPager2.post(runnable);
        }
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder  {
        public RoundedImageView imageView;
        DatabaseReference databaseReference;

    SliderViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageSlide);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View v) {

                    Intent intent1 = new Intent(context,displayActivity.class);
                    intent1.putExtra("url", mUploads.get(pos+getAdapterPosition()).getmImageUrl());
                    context.startActivity(intent1);
                }
            });

        }

        void setImage(int item){

            Picasso.with(context)
                    .load(mUploads.get(item).getmImageUrl())
                    .placeholder(R.drawable.holder)
                    .fit()
                    .centerCrop()
                    .into(imageView);
        }

    }

    private  Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mUploads.addAll(mUploads);
            notifyDataSetChanged();

        }
    };

}