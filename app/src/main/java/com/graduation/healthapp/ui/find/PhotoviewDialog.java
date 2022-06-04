package com.graduation.healthapp.ui.find;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.graduation.healthapp.R;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoviewDialog extends Dialog {
    private PhotoView photoviewImg;
    private Activity activity;
    private String url;
    private int w,h;

    public PhotoviewDialog(Activity activity, String url) {
        super(activity,R.style.Theme_AppCompat_Dialog);
        this.activity = activity;
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.photoview);
        w = activity.getWindowManager().getDefaultDisplay().getWidth();
        h = activity.getWindowManager().getDefaultDisplay().getHeight();
        initView();
    }

    private void initView() {
        photoviewImg = (PhotoView) findViewById(R.id.photoview_img);
        ViewGroup.LayoutParams params = photoviewImg.getLayoutParams();
        params.height = h;
        params.width = w-10;
        photoviewImg.setLayoutParams(params);
        photoviewImg.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                cancel();
            }
        });
        Glide.with(activity).load(url).into(photoviewImg);
    }
}