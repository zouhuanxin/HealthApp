package com.graduation.healthapp.util;

import android.content.Context;

import com.dueeeke.videoplayer.player.VideoView;

public class VideoViewUtil {

    /**
     * kotlin泛型不会写，不知道怎么初始化VideoView，所以直接通过Java创建返回了
     */
    public static VideoView getVideoView(Context context){
        return new VideoView(context);
    }
}
