package com.graduation.healthapp.ui.course;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.dueeeke.videocontroller.component.PrepareView;
import com.graduation.healthapp.R;

import java.util.List;

public class VideoRecyclerViewAdapter extends RecyclerView.Adapter<VideoRecyclerViewAdapter.VideoHolder> {

    private List<JSONObject> videos;

    private OnItemChildClickListener mOnItemChildClickListener;

    private OnItemScAndDzClickListener onItemScAndDzClickListener;

    public interface OnItemScAndDzClickListener{
        void sc(@NonNull VideoHolder holder, @SuppressLint("RecyclerView") int position);
        void dz(@NonNull VideoHolder holder, @SuppressLint("RecyclerView") int position);
        void click(@NonNull VideoHolder holder, @SuppressLint("RecyclerView") int position);
    }

    public void setOnItemScAndDzClickListener(OnItemScAndDzClickListener onItemScAndDzClickListener) {
        this.onItemScAndDzClickListener = onItemScAndDzClickListener;
    }

    public VideoRecyclerViewAdapter(List<JSONObject> videos) {
        this.videos = videos;
    }

    @Override
    @NonNull
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_content_item, parent, false);
        return new VideoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(videos.get(position).getString("username"));
        Glide.with(holder.portrait.getContext()).load(videos.get(position).getString("portrait")).into(holder.portrait);
        holder.content.setText(videos.get(position).getString("introduce"));
        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemScAndDzClickListener != null){
                    onItemScAndDzClickListener.click(holder,position);
                }
            }
        });
        holder.dz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemScAndDzClickListener != null){
                    onItemScAndDzClickListener.dz(holder,position);
                }
            }
        });
        holder.sc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemScAndDzClickListener != null){
                    onItemScAndDzClickListener.sc(holder,position);
                }
            }
        });
        if (videos.get(position).getBoolean("dz")) {
            holder.dz.setImageResource(R.mipmap.dz2);
        } else {
            holder.dz.setImageResource(R.mipmap.dz1);
        }
        if (videos.get(position).getBoolean("sc")) {
            holder.sc.setImageResource(R.mipmap.sc2);
        } else {
            holder.sc.setImageResource(R.mipmap.sc1);
        }
        holder.mPosition = position;
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }


    public class VideoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public int mPosition;
        public FrameLayout mPlayerContainer;
        public TextView name, content;
        public ImageView portrait, dz, sc;
        public PrepareView mPrepareView;

        VideoHolder(View itemView) {
            super(itemView);
            mPlayerContainer = itemView.findViewById(R.id.player_container);
            name = itemView.findViewById(R.id.name);
            content = itemView.findViewById(R.id.content);
            portrait = itemView.findViewById(R.id.portrait);
            dz = itemView.findViewById(R.id.dz);
            sc = itemView.findViewById(R.id.sc);
            mPrepareView = itemView.findViewById(R.id.prepare_view);
            if (mOnItemChildClickListener != null) {
                mPlayerContainer.setOnClickListener(this);
            }

            //通过tag将ViewHolder和itemView绑定
            itemView.setTag(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.player_container) {
                if (mOnItemChildClickListener != null) {
                    mOnItemChildClickListener.onItemChildClick(mPosition);
                }
            }
        }
    }


    public void setOnItemChildClickListener(OnItemChildClickListener onItemChildClickListener) {
        mOnItemChildClickListener = onItemChildClickListener;
    }

}