package com.graduation.healthapp.ui.my

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.listener.UploadBatchListener
import com.bumptech.glide.Glide
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.github.gzuliyujiang.wheelpicker.OptionPicker
import com.github.gzuliyujiang.wheelpicker.contract.OnDatePickedListener
import com.github.gzuliyujiang.wheelpicker.contract.OnOptionPickedListener
import com.graduation.healthapp.R
import com.graduation.healthapp.data.Result
import com.graduation.healthapp.databinding.ActivityUpdateUserInfoBinding
import com.graduation.healthapp.ui.BaseActivity
import com.graduation.healthapp.ui.find.GlideEngine
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
import org.json.JSONObject

/**
 * 修改个人信息
 */
class UpdateUserInfoActivity : BaseActivity() {
    private lateinit var binding: ActivityUpdateUserInfoBinding
    private val userinfo = TempStoreUtil.get(TempStoreUtil.USERNAME)!!

    //获取到的更新头像数据的地址
    private lateinit var poUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    fun initView() {
        binding.titlebar.setLeftOnClick {
            finish()
        }
        binding.titlebar.setRightOnClick {
            //更新数据,更新服务器，更新本地
            update(
                poUrl,
                binding.username.text.toString().replace("用户名 ", "").trim(),
                binding.address.text.toString().replace("地址 ", "").trim(),
                binding.brithday.hint.toString().replace("出生日期 ", "").trim(),
                binding.sex.hint.toString().replace("性别 ", "").trim()
            )
        }

        binding.portrait.setOnClickListener {
            openPortrait()
        }

        binding.brithday.setOnClickListener {
            openDataSelect()
        }

        binding.sex.setOnClickListener {
            openSexSelect()
        }

        initData()
    }

    fun initData() {
        //加载固定前缀
        binding.phone.setEdtHint("手机号 ", userinfo.getString("phone"))
        //默认头像等于当前头像地址
        poUrl = userinfo.getString("portrait")
        Glide.with(this).load(poUrl).into(binding.portrait)
        if (TextUtils.isEmpty(userinfo.getString("username"))) {
            binding.username.setEdtHint("用户名 ", "请输入")
        } else {
            binding.username.setEdtHint("用户名 ", userinfo.getString("username"))
        }
        binding.username.setFixText("用户名 ", R.color.purple_200)
        if (userinfo.toString().indexOf("brithday") == -1) {
            binding.brithday.setEdtHint("出生日期 ", "请输入")
        } else {
            binding.brithday.setEdtHint("出生日期 ", userinfo.getString("brithday"))
        }
        binding.brithday.setFixText("出生日期 ", R.color.purple_200)
        if (userinfo.toString().indexOf("address") == -1) {
            binding.address.setEdtHint("地址 ", "请输入")
        } else {
            binding.address.setEdtHint("地址 ", userinfo.getString("address"))
        }
        binding.address.setFixText("地址 ", R.color.purple_200)
        if (userinfo.toString().indexOf("sex") == -1) {
            binding.sex.setEdtHint("性别 ", "请输入")
        } else {
            binding.sex.setEdtHint("性别 ", userinfo.getString("sex"))
        }
        binding.sex.setFixText("性别 ", R.color.purple_200)
    }

    /**
     * 更新数据
     */
    fun update(portrait: String, username: String, address: String, brithday: String, sex: String) {
        if (TextUtils.isEmpty(portrait)) {
            ToastUtils.showToast(this, "头像不能为空，请选择")
            return
        }
        val req = JSONObject().apply {
            put("phone", userinfo.getString("phone"))
            put("portrait", portrait)
            put("username", username)
            put("address", address)
            put("brithday", brithday)
            put("sex", sex)
        }
        NetRequest.Instance.post(MyUrl.updateUserinfo, req) {
            if (it is Result.Success) {
                //更新成功，开始更新本地数据
                userinfo.apply {
                    put("portrait", portrait)
                    put("username", username)
                    put("address", address)
                    put("brithday", brithday)
                    put("sex", sex)
                }
                TempStoreUtil.save(TempStoreUtil.USERNAME, userinfo)
                ToastUtils.showToast(this, "更新成功")
                finish()
            } else {
                ToastUtils.showToast(this, "更新失败")
            }
        }
    }

    /**
     * 打开相册选择头像
     */
    fun openPortrait() {
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
                        //openProgressBar()
                        uploadMultiFile(result[0]!!.availablePath)
                    } else {
                        //closeProgressBar()
                        ToastUtils.showToast(this@UpdateUserInfoActivity, "打开失败")
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
                //closeProgressBar()
                ToastUtils.showToast(this@UpdateUserInfoActivity, "上传出错：$error")
            }

            override fun onProgress(curIndex: Int, curPercent: Int, total: Int, totalPercent: Int) {
                ToastUtils.showToast(this@UpdateUserInfoActivity, "上传进度：$curIndex")
            }

            override fun onSuccess(
                bmobFiles: MutableList<BmobFile>?,
                images: MutableList<String>?
            ) {
                if (images != null) {
                    if (images.size == data.size) {
                        ToastUtils.showToast(this@UpdateUserInfoActivity, "全部上传成功")
                        poUrl = images[0]
                        Glide.with(this@UpdateUserInfoActivity).load(poUrl).into(binding.portrait)
                    }
                }
            }
        })
    }

    /**
     * 打开日期选择器
     */
    fun openDataSelect() {
        val picker = DatePicker(this)
        picker.setOnDatePickedListener(object : OnDatePickedListener {
            override fun onDatePicked(year: Int, month: Int, day: Int) {
                binding.brithday.setEdtHint("出生日期 ", year.toString() + "-" + month + "-" + day)
            }
        })
        picker.show()
    }

    /**
     * 打开性别选择器
     */
    fun openSexSelect() {
        val data: MutableList<String> = ArrayList()
        data.add("男")
        data.add("女")
        val picker = OptionPicker(this)
        picker.setTitle("性别选择")
        picker.setBodyWidth(140)
        picker.setData(data)
        picker.setDefaultPosition(2)
        picker.setOnOptionPickedListener(object : OnOptionPickedListener {
            override fun onOptionPicked(position: Int, item: Any?) {
                binding.sex.setEdtHint("性别 ", data[position])
            }
        })
        picker.show()
    }

}
