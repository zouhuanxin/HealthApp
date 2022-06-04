package com.graduation.healthapp.ui.my

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnChildAttachStateChangeListener
import com.alibaba.fastjson.JSON
import com.bumptech.glide.Glide
import com.dueeeke.videocontroller.StandardVideoController
import com.dueeeke.videocontroller.component.*
import com.dueeeke.videoplayer.player.VideoView
import com.dueeeke.videoplayer.player.VideoView.SimpleOnStateChangeListener
import com.dueeeke.videoplayer.player.VideoViewManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.graduation.healthapp.R
import com.graduation.healthapp.data.Result
import com.graduation.healthapp.data.adapter.BaseRecyAdapter
import com.graduation.healthapp.databinding.ActivityMyTopicAndVideoBinding
import com.graduation.healthapp.ui.BaseActivity
import com.graduation.healthapp.ui.course.OnItemChildClickListener
import com.graduation.healthapp.ui.course.RecyclerViewFragment
import com.graduation.healthapp.ui.course.VideoRecyclerViewAdapter
import com.graduation.healthapp.ui.course.VideoRecyclerViewAdapter.VideoHolder
import com.graduation.healthapp.ui.find.TopicDetailsActivity
import com.graduation.healthapp.util.TempStoreUtil
import com.graduation.healthapp.util.VideoViewUtil
import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import com.luck.picture.lib.utils.ToastUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.ArrayList

class MyTopicAndVideoActivity : BaseActivity(), OnItemChildClickListener {
    private lateinit var binding:ActivityMyTopicAndVideoBinding
    private val userinfo = TempStoreUtil.get(TempStoreUtil.USERNAME)!!
    private var mVideos: ArrayList<com.alibaba.fastjson.JSONObject> = ArrayList()
    private var mAdapter: VideoRecyclerViewAdapter? = null
    private lateinit var mRecyclerView: RecyclerView

    private var mLinearLayoutManager: LinearLayoutManager? = null

    private lateinit var mVideoView: VideoView<*>
    private var mController: StandardVideoController? = null
    private var mErrorView: ErrorView? = null
    private var mCompleteView: CompleteView? = null
    private var mTitleView: TitleView? = null
    private var push: FloatingActionButton? = null

    /**
     * 当前播放的位置
     */
    protected var mCurPos = -1

    /**
     * 上次播放的位置，用于页面切回来之后恢复播放
     */
    protected var mLastPos = mCurPos


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyTopicAndVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    fun initView(){
        mRecyclerView = binding.recy
        binding.titlebar.setLeftOnClick {
            finish()
        }
        binding.titlebar.setText(intent.getStringExtra("title")!!)
        if (intent.getIntExtra("type",0) == 0){
            //话题
            getTopicData()
        }else{
            //视频
            initVideoView()
            getVideoData()
        }
    }

    /**
     * 获取话题数据
     */
    fun getTopicData() {
        NetRequest.Instance.get(MyUrl.getPageData + "?page=1&limit=10000&user=${userinfo.getString("phone")}&type=0") {
            if (it is Result.Success) {
                val json = it.data
                val array = json.getJSONArray("data")
                val list = mutableListOf<JSONObject>()
                for (i in 0 until array.length()) {
                    if(array.getJSONObject(i).getString("user").equals(userinfo.getString("phone"))){
                        list.add(array.getJSONObject(i))
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    initTopicAdapter(list)
                }
            } else {
                ToastUtils.showToast(this,"获取话题数据失败")
            }
        }
    }

    fun initTopicAdapter(data:MutableList<JSONObject>){
        val adapter = BaseRecyAdapter(R.layout.find_content_item, data)
        adapter.setBind { itemview, position ->
            val portrait = itemview.findViewById<ImageView>(R.id.portrait)
            val username = itemview.findViewById<TextView>(R.id.name)
            val ht = itemview.findViewById<TextView>(R.id.ht)
            val sc = itemview.findViewById<ImageView>(R.id.sc)
            val dz = itemview.findViewById<ImageView>(R.id.dz)
            val content = itemview.findViewById<TextView>(R.id.content)
            val imaegs = itemview.findViewById<RecyclerView>(R.id.imaegs)
            Glide.with(this).load(data.get(position).getString("portrait")).into(portrait)
            username.text = data.get(position).getString("username")
            content.text = data.get(position).getString("introduce")
            ht.text = "# ${data.get(position).getString("name")}"
            content.setOnClickListener {
                val intent = Intent(this, TopicDetailsActivity::class.java)
                intent.putExtra("data",data.get(position).toString())
                startActivity(intent)
            }
            dz.setImageResource(
                if (data[position].getBoolean("dz")) R.mipmap.dz2 else R.mipmap.dz1
            )
            sc.setImageResource(
                if (data[position].getBoolean("sc")) R.mipmap.sc2 else R.mipmap.sc1
            )

            //加载图片
            val str = data.get(position).getString("urls")
            if (str == "[]") return@setBind
            val urls = str.substring(1, str.length - 1).split(",")
            val list = mutableListOf<String>()
            for (element in urls) {
                list.add(element.trim())
            }
            val adapter2 = BaseRecyAdapter<String>(R.layout.push_image_item, list)
            adapter2.setBind { itemview, position ->
                val imageview = itemview.findViewById<ImageView>(R.id.push_image_item)
                val delte = itemview.findViewById<ImageView>(R.id.delete)
                Glide.with(imageview.context).load(list[position]).centerCrop().into(imageview)
                delte.visibility = View.GONE
            }
            imaegs.adapter = adapter2
            imaegs.layoutManager = GridLayoutManager(this, 3)
        }
        mRecyclerView.adapter = adapter
        mRecyclerView.layoutManager = LinearLayoutManager(this)
    }


    /**
     * 获取视频数据
     */
    fun getVideoData() {
        NetRequest.Instance.get(MyUrl.getPageData+"?page=1&limit=1000&user=${userinfo.getString("phone")}&type=1") {
            if (it is Result.Success) {
                val arr = JSON.parseArray(it.data.getJSONArray("data").toString())
                for (i in 0 until arr.size) {
                    if(arr.getJSONObject(i).getString("user").equals(userinfo.getString("phone"))){
                        mVideos.add(arr.getJSONObject(i))
                    }
                }
                CoroutineScope(Dispatchers.Main).launch {
                    mAdapter!!.notifyDataSetChanged()
                    mRecyclerView.post {
                        //自动播放第一个
                        startPlay(0)
                    }
                }
            } else {
                ToastUtils.showToast(this,"获取视频数据失败")
            }
        }
    }

    fun initVideoView(){
        mVideoView = VideoViewUtil.getVideoView(this)
        mVideoView.setOnStateChangeListener(object : SimpleOnStateChangeListener() {
            override fun onPlayStateChanged(playState: Int) {
                //监听VideoViewManager释放，重置状态
                if (playState == VideoView.STATE_IDLE) {
                    RecyclerViewFragment.removeViewFormParent(mVideoView)
                    mLastPos = mCurPos
                    mCurPos = -1
                }
            }
        })
        mController = StandardVideoController(this)
        mErrorView = ErrorView(this)
        mController!!.addControlComponent(mErrorView)
        mCompleteView = CompleteView(this)
        mController!!.addControlComponent(mCompleteView)
        mTitleView = TitleView(this)
        mController!!.addControlComponent(mTitleView)
        mController!!.addControlComponent(VodControlView(this))
        mController!!.addControlComponent(GestureView(this))
        mController!!.setEnableOrientation(true)
        mVideoView.setVideoController(mController)

        mLinearLayoutManager = LinearLayoutManager(this)
        mRecyclerView.layoutManager = mLinearLayoutManager
        mAdapter = VideoRecyclerViewAdapter(mVideos)
        mAdapter!!.setOnItemChildClickListener(this)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.addOnChildAttachStateChangeListener(object :
            OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {}
            override fun onChildViewDetachedFromWindow(view: View) {
                val playerContainer = view.findViewById<FrameLayout>(R.id.player_container)
                val v = playerContainer.getChildAt(0)
                if (v != null && v === mVideoView && !mVideoView!!.isFullScreen) {
                    releaseVideoView()
                }
            }
        })
    }

    /**
     * 开始播放
     * @param position 列表位置
     */
    protected fun startPlay(position: Int) {
        if (mCurPos == position) return
        if (mCurPos != -1) {
            releaseVideoView()
        }
        val videoBean = mVideos[position].getString("urls").replace("[", "").replace("]", "")
        mVideoView!!.setUrl(videoBean)
        mTitleView!!.setTitle("播放:$position")
        val itemView = mLinearLayoutManager!!.findViewByPosition(position) ?: return
        val viewHolder = itemView.tag as VideoHolder
        //把列表中预置的PrepareView添加到控制器中，注意isDissociate此处只能为true, 请点进去看isDissociate的解释
        mController!!.addControlComponent(viewHolder.mPrepareView, true)
        RecyclerViewFragment.removeViewFormParent(mVideoView)
        viewHolder.mPlayerContainer.addView(mVideoView, 0)
        //播放之前将VideoView添加到VideoViewManager以便在别的页面也能操作它
        VideoViewManager.instance().add(mVideoView, "list")
        mVideoView!!.start()
        mCurPos = position
    }

    private fun releaseVideoView() {
        mVideoView!!.release()
        if (mVideoView!!.isFullScreen) {
            mVideoView!!.stopFullScreen()
        }
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        }
        mCurPos = -1
    }
    override fun onItemChildClick(position: Int) {
        startPlay(position)
    }

}