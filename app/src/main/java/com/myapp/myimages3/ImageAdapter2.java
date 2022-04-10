package com.myapp.myimages3;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter2 extends RecyclerView.Adapter<ImageAdapter2.ImageViewHolder> {
    private Context mContext;
//    private List<Upload> mUploads;
    private List<fvrtClass> fvrtlist;
    private OnItemClickListener mListener;

    public ImageAdapter2(Context mContext,List<fvrtClass> fvrtlist) {
        this.mContext = mContext;
        this.fvrtlist = fvrtlist;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Picasso.with(mContext)
                .load(fvrtlist.get(position).getImageUrl())
                .placeholder(R.drawable.holder)
                .fit()
                .centerCrop()
                .into(holder.imageView);

    }

    //    @Override
//    public void onBindViewHolder(ImageViewHolder holder, int position) {
////        Upload uploadCurrent = mUploads.get(position);
////        String imgUrl = mUploads.get(position).getmImageUrl();
////        int fvrtUrl = Integer.parseInt(fvrtList.get(position).getFm());
//
////        if (imgUrl != null && fvrtUrl != null){
//
////        }
//
//
//
//    }
    @Override
    public int getItemCount() {
        return fvrtlist.size();
    }


    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        public ImageView imageView;
        public ImageViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view_upload);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem doWhatever = menu.add(Menu.NONE, 1, 1, "set profile picture");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            mListener.onWhatEverClick(position);
//                            Toast.makeText(mContext, "whatever", Toast.LENGTH_SHORT).show();
                            return true;
                        case 2:
                            mListener.onDeleteClick(position);
//                            Toast.makeText(mContext, "delete", Toast.LENGTH_SHORT).show();
                            return true;
                    }
                }
            }
            return false;
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onWhatEverClick(int position);
        void onDeleteClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
}
