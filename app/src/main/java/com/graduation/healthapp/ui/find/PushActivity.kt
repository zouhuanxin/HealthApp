package com.graduation.healthapp.ui.find

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.graduation.healthapp.databinding.ActivityPushBinding
import com.graduation.healthapp.ui.BaseActivity
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.engine.CompressEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnCallbackListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.utils.SdkVersionUtils
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.listener.UploadBatchListener
import com.bumptech.glide.Glide
import com.graduation.healthapp.R
import com.graduation.healthapp.data.Result
import com.graduation.healthapp.data.adapter.BaseRecyAdapter
import com.graduation.healthapp.util.TempStoreUtil
import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import com.luck.picture.lib.utils.ToastUtils
import org.json.JSONObject

class PushActivity : BaseActivity() {
    private lateinit var binding: ActivityPushBinding
    private val compresseng = object : CompressEngine {
        override fun onStartCompress(
            context: Context?,
            list: java.util.ArrayList<LocalMedia>?,
            listener: OnCallbackListener<java.util.ArrayList<LocalMedia>>?
        ) {
            val compress: MutableList<Uri> = ArrayList()
            for (i in 0 until list!!.size) {
                val media = list!![i]
                val availablePath = media.availablePath
                val uri: Uri =
                    if (PictureMimeType.isContent(availablePath) || PictureMimeType.isHasHttp(
                            availablePath
                        )
                    ) Uri.parse(availablePath) else Uri.fromFile(
                        File(availablePath)
                    )
                compress.add(uri)
            }
            if (compress.size == 0) {
                listener!!.onCall(list)
                return
            }
            Luban.with(context)
                .load(compress)
                .ignoreBy(100)
                .setCompressListener(object : OnCompressListener {
                    override fun onStart() {

                    }

                    override fun onSuccess(index: Int, compressFile: File?) {
                        val media = list[index]
                        if (compressFile!!.exists() && !TextUtils.isEmpty(compressFile!!.absolutePath)) {
                            media.isCompressed = true
                            media.compressPath = compressFile!!.absolutePath
                            media.sandboxPath =
                                if (SdkVersionUtils.isQ()) media.compressPath else null
                        }
                        // 因为是多图压缩，所以判断压缩到最后一张时返回结果
                        if (index === list.size - 1) {
                            listener!!.onCall(list)
                        }
                    }

                    override fun onError(index: Int, e: Throwable?) {
                        // 压缩失败
                        if (index !== -1) {
                            val media = list[index]
                            media.isCompressed = false
                            media.compressPath = null
                            media.sandboxPath = null
                            if (index === list.size - 1) {
                                listener!!.onCall(list)
                            }
                        }
                    }
                }).launch()
        }
    }
    private lateinit var adapter: BaseRecyAdapter<String>
    private var data: MutableList<String> = mutableListOf()
    private var urls: MutableList<String> = mutableListOf()
    private val userinfo = TempStoreUtil.get(TempStoreUtil.USERNAME)!!
    private var participationTopic: JSONObject? = null
    val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                participationTopic = JSONObject(intent!!.getStringExtra("data"))
                binding.cyht.text = "# " + participationTopic!!.getString("name")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPushBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = BaseRecyAdapter(R.layout.push_image_item, data)
        adapter.setBind { itemview: View, position: Int ->
            val imageview = itemview.findViewById<ImageView>(R.id.push_image_item)
            val delte = itemview.findViewById<ImageView>(R.id.delete)
            Glide.with(imageview.context).load(data.get(position)).centerCrop().into(imageview)
            delte.setOnClickListener {
                data.removeAt(position)
                adapter.notifyDataSetChanged()
            }
        }

        binding.images.adapter = adapter
        binding.images.layoutManager = GridLayoutManager(this, 3)

        binding.addimage.setOnClickListener {
            //添加照片
            PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setCompressEngine(compresseng)
                .setImageEngine(GlideEngine.createGlideEngine())
                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: ArrayList<LocalMedia?>?) {
                        for (item in result!!) {
                            println(item!!.availablePath)
                            data.add(item!!.availablePath)
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onCancel() {}
                })
        }
        binding.push.setOnClickListener {
            if (TextUtils.isEmpty(binding.comment.text)) {
                ToastUtils.showToast(this@PushActivity, "内容不能为空")
                return@setOnClickListener
            }
            if (data == null || data.size == 0) {
                addTopic()
            } else {
                uploadMultiFile()
            }
        }

        binding.gosmalclass.setOnClickListener {
            startForResult.launch(Intent(this, SmalclassActivity::class.java))
        }
    }

    //图片上传到笔墨拿到链接
    fun uploadMultiFile() {
        BmobFile.uploadBatch(data.toTypedArray(), object : UploadBatchListener {
            override fun onError(code: Int, error: String?) {
                ToastUtils.showToast(this@PushActivity, "上传出错：$error")
            }

            override fun onProgress(curIndex: Int, curPercent: Int, total: Int, totalPercent: Int) {
                ToastUtils.showToast(this@PushActivity, "上传进度：$curIndex")
            }

            override fun onSuccess(
                bmobFiles: MutableList<BmobFile>?,
                images: MutableList<String>?
            ) {
                if (images != null) {
                    if (images.size == data.size) {
                        ToastUtils.showToast(this@PushActivity, "全部上传成功")
                        //替换文件
                        urls = images!!
                        addTopic()
                    }
                }
            }
        })
    }

    //把文字和图片链接上传到服务器
    fun addTopic() {
        val req = JSONObject().apply {
            if (participationTopic != null && participationTopic!!.getInt("id") != null)
                put("smalclass", participationTopic!!.getInt("id"))
            if (urls != null)
                put("urls", urls.toString())
            put("introduce", binding.comment.text.toString())
            put("user", userinfo.getString("phone"))
        }
        NetRequest.Instance.post(MyUrl.addTopic, req) {
            if (it is Result.Success) {
                if (it.data.getInt("code") == 200) {
                    ToastUtils.showToast(this@PushActivity, "发表成功")
                    finish()
                } else {
                    ToastUtils.showToast(this@PushActivity, "发表失败")
                }
            } else if (it is Result.Error) {
                ToastUtils.showToast(this@PushActivity, "发表失败")
            }
        }
    }

}