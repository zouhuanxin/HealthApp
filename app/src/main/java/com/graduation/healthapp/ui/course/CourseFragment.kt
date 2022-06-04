package com.graduation.healthapp.ui.course

import android.content.Intent
import android.graphics.Rect
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.dueeeke.videoplayer.util.L
import com.graduation.healthapp.R
import com.graduation.healthapp.data.Result
import com.graduation.healthapp.ui.course.VideoRecyclerViewAdapter.VideoHolder
import com.graduation.healthapp.ui.find.FindViewModel
import com.graduation.healthapp.ui.find.TopicDetailsActivity
import com.graduation.healthapp.util.TempStoreUtil
import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import com.luck.picture.lib.utils.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CourseFragment : RecyclerViewFragment() {
    private val userinfo = TempStoreUtil.get(TempStoreUtil.USERNAME)!!

    override fun initView() {
        super.initView()
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { //滚动停止
                    autoPlayVideo(recyclerView)
                }
            }

            private fun autoPlayVideo(view: RecyclerView?) {
                if (view == null) return
                //遍历RecyclerView子控件,如果mPlayerContainer完全可见就开始播放
                val count = view.childCount
                for (i in 0 until count) {
                    val itemView = view.getChildAt(i) ?: continue
                    val holder = itemView.tag as VideoHolder
                    val rect = Rect()
                    holder.mPlayerContainer.getLocalVisibleRect(rect)
                    val height = holder.mPlayerContainer.height
                    if (rect.top == 0 && rect.bottom == height) {
                        startPlay(holder.mPosition)
                        break
                    }
                }
            }
        })

        push.setOnClickListener { startActivity(Intent(activity, PushVideoActivity::class.java)) }

        //监听
        mAdapter.setOnItemScAndDzClickListener(object : VideoRecyclerViewAdapter.OnItemScAndDzClickListener{
            override fun sc(holder: VideoHolder, position: Int) {
                NetRequest.Instance.get(MyUrl.collect + "?id=${mVideos.get(position).getString("id")}&user=${userinfo.getString("phone")}") {
                    if (it is Result.Success) {
                        if (it.data.getString("message").equals("取消收藏成功")) {
                            holder.sc.setImageResource(R.mipmap.sc1)
                        }
                        if (it.data.getString("message").equals("收藏成功")) {
                            holder.sc.setImageResource(R.mipmap.sc2)
                        }
                    } else {
                        ToastUtils.showToast(context,"操作失败")
                    }
                }
            }

            override fun dz(holder: VideoHolder, position: Int) {
                NetRequest.Instance.get(MyUrl.favour + "?id=${mVideos.get(position).getString("id")}&user=${userinfo.getString("phone")}") {
                    if (it is Result.Success) {
                        if (it.data.getString("message").equals("取消点赞成功")) {
                            holder.dz.setImageResource(R.mipmap.dz1)
                        }
                        if (it.data.getString("message").equals("点赞成功")) {
                            holder.dz.setImageResource(R.mipmap.dz2)
                        }
                    } else {
                        ToastUtils.showToast(context,"操作失败")
                    }
                }
            }

            override fun click(holder: VideoHolder, position: Int) {
                val intent = Intent(context, VideoDetailsActivity::class.java)
                intent.putExtra("data",mVideos.get(position).toString())
                startActivity(intent)
            }

        })
    }

    override fun initData() {
        super.initData()
        getData()
    }

    /**
     * 获取视频数据
     */
    fun getData() {
        NetRequest.Instance.get(MyUrl.getPageData+"?page=1&limit=1000&user=${userinfo.getString("phone")}&type=1") {
            if (it is Result.Success) {
                val arr = JSON.parseArray(it.data.getJSONArray("data").toString())
                for (i in 0 until arr.size) {
                    mVideos.add(arr.getJSONObject(i))
                }
                CoroutineScope(Dispatchers.Main).launch {
                    mAdapter.notifyDataSetChanged()
                    mRecyclerView.post {
                        //自动播放第一个
                        startPlay(0)
                    }
                }
            } else {
                ToastUtils.showToast(context,"获取视频数据失败")
            }
        }
    }
}