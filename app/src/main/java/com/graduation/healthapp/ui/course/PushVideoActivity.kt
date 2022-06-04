package com.graduation.healthapp.ui.course

import android.annotation.SuppressLint
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
import com.graduation.healthapp.ui.find.GlideEngine
import com.graduation.healthapp.util.TempStoreUtil
import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import com.luck.picture.lib.utils.ToastUtils
import org.json.JSONObject
import android.provider.MediaStore
import android.widget.Toast

import android.graphics.Bitmap

import android.media.MediaMetadataRetriever
import cn.bmob.v3.exception.BmobException

import cn.bmob.v3.listener.UploadFileListener
import com.luck.picture.lib.engine.UriToFileTransformEngine
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.utils.SandboxTransformUtils


class PushVideoActivity : BaseActivity() {
    private lateinit var binding: ActivityPushBinding

    private lateinit var adapter: BaseRecyAdapter<String>
    private var data: MutableList<String> = mutableListOf()
    private var urls: MutableList<String> = mutableListOf()
    private val userinfo = TempStoreUtil.get(TempStoreUtil.USERNAME)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPushBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.gosmalclass.visibility = View.GONE
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
            //添加视频
            PictureSelector.create(this)
                .openGallery(SelectMimeType.ofVideo())
                .setImageEngine(GlideEngine.createGlideEngine())
                .isPreviewVideo(true)
                .setMaxVideoSelectNum(1)
                .setSandboxFileEngine(object: UriToFileTransformEngine {
                    override fun onUriToFileAsyncTransform(
                        context: Context?,
                        srcPath: String?,
                        mineType: String?,
                        call: OnKeyValueResultCallbackListener?
                    ) {
                        if (call != null) {
                            val sandboxPath = SandboxTransformUtils.copyPathToSandbox(context, srcPath, mineType)
                            call.onCallback(srcPath,sandboxPath)
                        }
                    }
                })
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
                ToastUtils.showToast(this@PushVideoActivity, "内容不能为空")
                return@setOnClickListener
            }
            if (data == null || data.size == 0) {
                //必须添加视频文件
                ToastUtils.showToast(this@PushVideoActivity, "必须添加视频文件")
                return@setOnClickListener
            } else {
                uploadMultiFile()
            }
        }

    }

    //图片上传到笔墨拿到链接
    fun uploadMultiFile() {
        BmobFile.uploadBatch(data.toTypedArray(), object : UploadBatchListener {
            override fun onError(code: Int, error: String?) {
                //ToastUtils.showToast(this@PushVideoActivity, "上传出错：$code - $error")
            }

            override fun onProgress(curIndex: Int, curPercent: Int, total: Int, totalPercent: Int) {
                ToastUtils.showToast(this@PushVideoActivity, "上传进度：$curIndex")
            }

            override fun onSuccess(
                bmobFiles: MutableList<BmobFile>?,
                images: MutableList<String>?
            ) {
                if (images != null) {
                    if (images.size == data.size) {
                        ToastUtils.showToast(this@PushVideoActivity, "全部上传成功")
                        //替换文件
                        urls = images!!
                        addTopic()
                    }
                }
            }
        })
    }

    //把文字和视频链接上传到服务器
    fun addTopic() {
        if (urls == null){
            ToastUtils.showToast(this@PushVideoActivity, "上传失败")
            return
        }
        val req = JSONObject()
        req.put("urls", urls.toString())
        req.put("introduce", binding.comment.text.toString())
        req.put("user", userinfo.getString("phone"))
        req.put("type", "1") //表示视频类型
        NetRequest.Instance.post(MyUrl.addTopic, req) {
            if (it is Result.Success) {
                if (it.data.getInt("code") == 200) {
                    ToastUtils.showToast(this@PushVideoActivity, "发表成功")
                    finish()
                } else {
                    ToastUtils.showToast(this@PushVideoActivity, "发表失败")
                }
            } else if (it is Result.Error) {
                ToastUtils.showToast(this@PushVideoActivity, "发表失败")
            }
        }
    }

}