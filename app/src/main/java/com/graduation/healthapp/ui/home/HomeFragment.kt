package com.graduation.healthapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.listener.UploadBatchListener
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.bumptech.glide.Glide
import com.graduation.healthapp.data.Result
import com.graduation.healthapp.data.adapter.BaseAdapter
import com.graduation.healthapp.data.adapter.BaseHolder
import com.graduation.healthapp.databinding.FragmentHomeBinding
import com.graduation.healthapp.databinding.HomeQueryItemBinding
import com.graduation.healthapp.databinding.HomeRecommendItemBinding
import com.graduation.healthapp.ui.find.GlideEngine
import com.graduation.healthapp.ui.web.X5WebViewActivity
import com.graduation.healthapp.util.TempStoreUtil
import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.engine.UriToFileTransformEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.utils.SandboxTransformUtils
import com.luck.picture.lib.utils.ToastUtils
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class HomeFragment : Fragment(), CoroutineScope {
    private lateinit var binding: FragmentHomeBinding
    private var lasttime = System.currentTimeMillis()
    private val job = Job()
    private val userinfo = TempStoreUtil.get(TempStoreUtil.USERNAME)!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        initView()
        getRecommendData(MyUrl.recommendActivity, binding.recommendactivity)
        getRecommendData(MyUrl.recommendFood, binding.recommendfood)
        getRecommendData(MyUrl.recommendGoods, binding.recommendgoods)
        return binding.root
    }

    fun initView() {
        initAudio()
        binding.homeHomeheadComponent.PunchCardClick {
            startActivity(Intent(activity, PunchCardActivity::class.java))
        }
        binding.homeHomeheadComponent.InFormClick {
            startActivity(Intent(activity, PunchCardActivity::class.java))
        }
        binding.homeHomeheadComponent.SearchClick {
            startActivity(Intent(activity, SearchActivity::class.java))
        }
        binding.cqpp.setOnClickListener {
            openImage()
        }
        binding.wzss.setOnClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
        }
        //红黑榜
        binding.hhb.setOnClickListener {
            val myIntent = Intent(activity, X5WebViewActivity::class.java)
            myIntent.putExtra("title", "红黑榜")
            myIntent.putExtra(
                "url",
                "https://pixiu.boohee.com/food-list-guide?theme=204,183,233&app_device=Android&os_version=10&app_version=8.0.9&version_code=248&channel=xiaomi&app_key=one&token=AsRpZuPXrKy6xFoxkUR5NkhRFxCJXmMM"
            )
            startActivity(myIntent)
        }
        //食物称重
        binding.swcz.setOnClickListener {
            val myIntent = Intent(activity, X5WebViewActivity::class.java)
            myIntent.putExtra("title", "食物称重")
            myIntent.putExtra(
                "url",
                "https://food.boohee.com/food_weight?app_device=Android&os_version=10&app_version=8.0.9&version_code=248&channel=xiaomi&app_key=one&token=AsRpZuPXrKy6xFoxkUR5NkhRFxCJXmMM"
            )
            startActivity(myIntent)
        }
        //查奶茶
        binding.cnc.setOnClickListener {
            val myIntent = Intent(activity, X5WebViewActivity::class.java)
            myIntent.putExtra("title", "查奶茶")
            myIntent.putExtra(
                "url",
                "https://leaf.boohee.com/drinks-match?app_device=Android&os_version=10&app_version=8.0.9&version_code=248&channel=xiaomi&app_key=one&token=AsRpZuPXrKy6xFoxkUR5NkhRFxCJXmMM"
            )
            startActivity(myIntent)
        }
        //辅食推荐
        binding.fstj.setOnClickListener {
            val myIntent = Intent(activity, X5WebViewActivity::class.java)
            myIntent.putExtra("title", "辅食推荐")
            myIntent.putExtra(
                "url",
                "https://foodie.boohee.com/complementary-search?app_device=Android&os_version=10&app_version=8.0.9&version_code=248&channel=xiaomi&app_key=one&token=AsRpZuPXrKy6xFoxkUR5NkhRFxCJXmMM"
            )
            startActivity(myIntent)
        }
        binding.smartscrollview.scrollY
        binding.smartscrollview.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lasttime = System.currentTimeMillis()
                    binding.homeHomeheadComponent.close()
                }
            }
            false
        }
        launch(Dispatchers.IO) {
            while (true) {
                delay(1000)
                if (System.currentTimeMillis() - lasttime > 3000) {
                    launch(Dispatchers.Main) {
                        binding.homeHomeheadComponent.open()
                    }
                }
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    /**
     * 打开相册准备图片识别
     */
    fun openImage() {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setMaxSelectNum(1)
            .setImageEngine(GlideEngine.createGlideEngine())
            .setSandboxFileEngine(object : UriToFileTransformEngine {
                override fun onUriToFileAsyncTransform(
                    context: Context?,
                    srcPath: String?,
                    mineType: String?,
                    call: OnKeyValueResultCallbackListener?
                ) {
                    if (call != null) {
                        val sandboxPath =
                            SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType)
                        call.onCallback(srcPath, sandboxPath)
                    }
                }
            })
            .forResult(object : OnResultCallbackListener<LocalMedia?> {
                override fun onResult(result: ArrayList<LocalMedia?>?) {
                    if (result!!.size == 1) {
                        openProgressBar()
                        uploadMultiFile(result[0]!!.availablePath)
                    } else {
                        closeProgressBar()
                        ToastUtils.showToast(activity, "打开失败")
                    }
                }

                override fun onCancel() {}
            })
    }

    //图片上传到笔墨拿到链接
    fun uploadMultiFile(url: String) {
        val data = ArrayList<String>()
        data.add(url)
        BmobFile.uploadBatch(data.toTypedArray(), object : UploadBatchListener {
            override fun onError(code: Int, error: String?) {
                closeProgressBar()
                ToastUtils.showToast(activity, "上传出错：$error")
            }

            override fun onProgress(curIndex: Int, curPercent: Int, total: Int, totalPercent: Int) {
                ToastUtils.showToast(activity, "上传进度：$curIndex")
            }

            override fun onSuccess(
                bmobFiles: MutableList<BmobFile>?,
                images: MutableList<String>?
            ) {
                if (images != null) {
                    if (images.size == data.size) {
                        ToastUtils.showToast(activity, "全部上传成功")
                        Analysis(images[0])
                    }
                }
            }
        })
    }

    //图片菜品识别
    fun Analysis(url: String) {
        NetRequest.Instance.get(MyUrl.dish + "?url=" + url) {
            if (it is Result.Success) {
                //识别成功
                val data = ArrayList<JSONObject>()
                val arr = JSON.parseArray(it.data.getJSONArray("data").toString())
                for (i in 0 until arr.size) {
                    data.add(arr.getJSONObject(i))
                }
                CoroutineScope(Dispatchers.Main).launch {
                    initAdapter(data)
                }
            } else {
                ToastUtils.showToast(activity, "识别失败")
            }
            closeProgressBar()
        }
    }

    //加载查询菜品结果适配器
    fun initAdapter(data: ArrayList<JSONObject>) {
        binding.querylist.visibility = View.VISIBLE
        binding.queryres.layoutManager = LinearLayoutManager(activity)
        binding.queryres.adapter = object : BaseAdapter<JSONObject, HomeQueryItemBinding>(data) {
            override fun onBindingData(
                holder: BaseHolder<HomeQueryItemBinding>?,
                t: JSONObject?,
                position: Int
            ) {
                val bind = holder!!.viewbinding
                Glide.with(context!!).load(t!!.getString("image")).into(bind.image)
                bind.text.text = t!!.getString("name") + "\n" + t!!.getString("introduce")
                bind.bt1.setOnClickListener {
                    addRecord(t)
                    binding.querylist.visibility = View.GONE
                }
            }

            override fun onBindingView(viewGroup: ViewGroup?): HomeQueryItemBinding {
                return HomeQueryItemBinding.inflate(
                    LayoutInflater.from(viewGroup!!.context),
                    viewGroup, false
                )
            }

        }
    }

    //开启加载等待条
    fun openProgressBar() {
        CoroutineScope(Dispatchers.Main).launch {
            binding.progress.visibility = View.VISIBLE
        }
    }

    //开启加载等待条
    fun closeProgressBar() {
        CoroutineScope(Dispatchers.Main).launch {
            binding.progress.visibility = View.GONE
        }
    }

    /**
     * 加载推荐列表
     */
    fun getRecommendData(url: String, recycler: RecyclerView) {
        NetRequest.Instance.get(url) {
            if (it is Result.Success) {
                val data = ArrayList<JSONObject>()
                val arr = JSON.parseArray(it.data.getJSONArray("data").toString())
                for (i in 0 until arr.size) {
                    data.add(arr.getJSONObject(i))
                }
                CoroutineScope(Dispatchers.Main).launch {
                    loadRecommendList(recycler, data)
                }
            } else {
                ToastUtils.showToast(activity, "识别失败")
            }
        }
    }

    /**
     * 加载推荐视图
     */
    fun loadRecommendList(recycler: RecyclerView, data: ArrayList<JSONObject>) {
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter =
            object : BaseAdapter<JSONObject, HomeRecommendItemBinding>(data) {
                override fun onBindingData(
                    holder: BaseHolder<HomeRecommendItemBinding>?,
                    t: JSONObject?,
                    position: Int
                ) {
                    val bind = holder!!.viewbinding
                    Glide.with(context!!).load(t!!.getString("image")).into(bind.image)
                    bind.text.text = t!!.getString("name") + "\n" + t!!.getString("introduce")
                    bind.text.setOnClickListener {
                        val myIntent = Intent(activity, X5WebViewActivity::class.java)
                        myIntent.putExtra("title",t!!.getString("name"))
                        myIntent.putExtra(
                            "url",
                            t!!.getString("url")
                        )
                        startActivity(myIntent)
                    }
                }

                override fun onBindingView(viewGroup: ViewGroup?): HomeRecommendItemBinding {
                    return HomeRecommendItemBinding.inflate(
                        LayoutInflater.from(viewGroup!!.context),
                        viewGroup, false
                    )
                }

            }
    }

    /**
     * 添加一条饮食记录
     */
    fun addRecord(ob: JSONObject) {
        val req = org.json.JSONObject().apply {
            //切割名字
            put("name", ob.getString("name").split("，")[0].split("(")[0])
            //拼接内容
            put("introduce", ob.getString("name") + " " + ob.getString("introduce"))
            //切割卡路里信息
            put("calorie", ob.getString("introduce").replace(" 大卡", "").replace("热量：", "").split("(")[0])
            //hrf
            put("href", ob.getString("href"))
            //菜品图片
            put("url", ob.getString("image"))
            //用户手机号
            put("user", userinfo.getString("phone"))
        }
        NetRequest.Instance.post(MyUrl.addCalorie, req) {
            if (it is Result.Success) {
                ToastUtils.showToast(activity, "添加成功")
            } else {
                ToastUtils.showToast(activity, "添加失败")
            }
        }
    }

    /**
     * 初始化语音识别服务
     */
    fun initAudio() {

    }
}