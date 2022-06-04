package com.graduation.healthapp.ui.find

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.healthapp.R
import com.graduation.healthapp.data.adapter.BaseRecyAdapter
import com.graduation.healthapp.databinding.ActivityTopicDetailsBinding
import com.graduation.healthapp.ui.BaseActivity
import com.graduation.healthapp.ui.login.ViewModelFactory
import com.graduation.healthapp.util.TempStoreUtil
import com.luck.picture.lib.utils.ToastUtils
import org.json.JSONArray
import org.json.JSONObject

class TopicDetailsActivity : BaseActivity() {
    private lateinit var binding: ActivityTopicDetailsBinding
    private lateinit var topicDetailsViewModel: TopicDetailsViewModel
    private val userinfo = TempStoreUtil.get(TempStoreUtil.USERNAME)!!
    private lateinit var aim: String
    private lateinit var replyphone: String
    private lateinit var adapter: BaseRecyAdapter<JSONArray>
    private var datas = mutableListOf<JSONArray>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTopicDetailsBinding.inflate(layoutInflater)
        topicDetailsViewModel =
            ViewModelProvider(this, ViewModelFactory()).get(TopicDetailsViewModel::class.java)
        setContentView(binding.root)

        binding.bar.setLeftOnClick {
            finish()
        }
        var data = JSONObject(intent.getStringExtra("data"))
        //默认回复贴主
        aim = data.getInt("id").toString()
        replyphone = data.getString("user")
        Glide.with(this).load(data.getString("portrait")).into(binding.portrait)
        binding.username.text = data.getString("username")
        binding.time.text = data.getString("createdtime")
        binding.content.text = data.getString("introduce")
        binding.ht.text = "# ${data.getString("name")}"
        //加载图片
        val str = data.getString("urls")
        if (str != "[]") {
            val urls = str.substring(1, str.length - 1).split(",")
            val list = mutableListOf<String>()
            for (element in urls) {
                list.add(element.trim())
            }
            val adapter2 = BaseRecyAdapter<String>(R.layout.push_image_item, list)
            adapter2.setBind { itemview, position ->
                val imageview = itemview.findViewById<ImageView>(R.id.push_image_item)
                val delte = itemview.findViewById<ImageView>(R.id.delete)
                imageview.setOnClickListener {
                    PhotoviewDialog(this,list[position]).show()
                }
                Glide.with(imageview.context).load(list[position]).centerCrop().into(imageview)
                delte.visibility = View.GONE
            }
            binding.images.adapter = adapter2
            binding.images.layoutManager = GridLayoutManager(this, 3)
        }

        topicDetailsViewModel.commentStatus.observe(this) {
            if (it) {
                binding.commentInput.text!!.clear()
                binding.commentInput.clearFocus()
                ReshView(data)
                ToastUtils.showToast(this, "成功")
            } else {
                ToastUtils.showToast(this, "失败")
            }
        }

        adapter = BaseRecyAdapter(R.layout.topicdetails_item,datas)
        binding.comment.layoutManager = LinearLayoutManager(this)
        binding.comment.adapter = adapter
        topicDetailsViewModel.commentResult.observe(this) {
            if (it.status) {
                datas = it.list!!
                adapter.setBind { itemview, position ->
                    val port = itemview.findViewById<ImageView>(R.id.portrait)
                    val name = itemview.findViewById<TextView>(R.id.name)
                    val time = itemview.findViewById<TextView>(R.id.time)
                    val content = itemview.findViewById<TextView>(R.id.content)
                    val reply = itemview.findViewById<RecyclerView>(R.id.reply)
                    val replyback = itemview.findViewById<CardView>(R.id.reply_back)
                    //默认加载第一个
                    Glide.with(this).load(datas[position].getJSONObject(0).getString("portrait")).into(port)
                    name.text = datas[position].getJSONObject(0).getString("username")
                    time.text = datas[position].getJSONObject(0).getString("createdtime")
                    content.text = datas[position].getJSONObject(0).getString("comment")
                    content.setOnClickListener {
                        if (aim.equals(datas[position].getJSONObject(0).getString("id"))){
                            binding.commentInput.hint = "请输入"
                            aim = data.getInt("id").toString()
                            replyphone = data.getString("user")
                            return@setOnClickListener
                        }
                        binding.commentInput.hint = "回复:${datas[position].getJSONObject(0).getString("username")}"
                        aim = datas[position].getJSONObject(0).getString("id")
                        replyphone = datas[position].getJSONObject(0).getString("phone")
                    }
                    //刷新回复适配器
                    val replylist = mutableListOf<JSONObject>()
                    for (i in 0 until datas[position].length()){
                        replylist.add(datas[position].getJSONObject(i))
                    }
                    replylist.removeAt(0)
                    if (replylist.size == 0) replyback.visibility = View.GONE
                    val adapter2 = BaseRecyAdapter<JSONObject>(R.layout.topicdetails_item2,replylist)
                    adapter2.setBind { itemview, position ->
                        val phone = itemview.findViewById<TextView>(R.id.phone)
                        val answerphone = itemview.findViewById<TextView>(R.id.replyphone)
                        val replytext = itemview.findViewById<TextView>(R.id.reply_content)
                        phone.text = replylist.get(position).getString("phone")
                        answerphone.text = "@${replylist.get(position).getString("replyphone")}"
                        replytext.text = replylist.get(position).getString("comment")
                        replytext.setOnClickListener{
                            if (aim.equals(replylist[position].getString("id"))){
                                binding.commentInput.hint = "请输入"
                                aim = data.getInt("id").toString()
                                replyphone = data.getString("user")
                                return@setOnClickListener
                            }
                            binding.commentInput.hint = "回复:${replylist[position].getString("username")}"
                            aim = replylist[position].getString("id")
                            replyphone = replylist[position].getString("phone")
                        }
                    }
                    runOnUiThread {
                        reply.layoutManager = LinearLayoutManager(this)
                        reply.adapter = adapter2
                    }
                }
                runOnUiThread {
                    adapter.Updata(datas)
                }
            } else {
                ToastUtils.showToast(this, "加载失败")
            }
        }
        binding.commentSend.setOnClickListener {
            if (TextUtils.isEmpty(binding.commentInput.text)){
                ToastUtils.showToast(this, "评论不能为空")
                return@setOnClickListener
            }
            topicDetailsViewModel.addComment(
                binding.commentInput.text.toString(),
                data.getInt("id").toString(),
                userinfo.getString("phone"),
                aim,
                replyphone
            )
        }
        ReshView(data)
    }

    fun ReshView(data:JSONObject){
        topicDetailsViewModel.getComments(data.getInt("id").toString(), data.getInt("id").toString())
    }
}