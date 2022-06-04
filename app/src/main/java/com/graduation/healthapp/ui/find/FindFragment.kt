package com.graduation.healthapp.ui.find

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.graduation.healthapp.databinding.FragmentFindBinding
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.stx.xhb.androidx.XBanner.XBannerAdapter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

import com.bumptech.glide.request.RequestOptions
import com.graduation.healthapp.R
import com.graduation.healthapp.data.adapter.BaseRecyAdapter
import com.graduation.healthapp.ui.login.ViewModelFactory
import com.luck.picture.lib.utils.ToastUtils
import com.stx.xhb.androidx.XBanner
import org.json.JSONObject


class FindFragment : Fragment() {
    private lateinit var binding: FragmentFindBinding
    private lateinit var viewModel: FindViewModel
    private lateinit var adapter: BaseRecyAdapter<JSONObject>
    private var data = mutableListOf<JSONObject>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, ViewModelFactory()).get(FindViewModel::class.java)

        viewModel.urls.observe(this) {
            binding.xbanner.setBannerData(it)
            binding.xbanner.loadImage(XBannerAdapter { banner, model, view, position ->
                Glide.with(this)
                    .load((model as FindViewModel.BannerInfo).xBannerUrl)
                    .apply(RequestOptions().centerCrop().transform(RoundedCorners(50)))
                    .into(view as ImageView)
            })
        }
        viewModel.smalclass.observe(this) {
            for (i in it.indices step 2) {
                val json1 = it[i]
                val json2 = it[i + 1]
                val params1 = LinearLayout.LayoutParams(
                    resources.getDimension(R.dimen.dp_150).toInt(),
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                params1.leftMargin = resources.getDimension(R.dimen.dp_5).toInt()
                val linelayout1 = LinearLayout(context)
                linelayout1.orientation = LinearLayout.VERTICAL
                //第一个
                val params2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0)
                params2.weight = 1f
                val linelayout2 = LinearLayout(context)
                linelayout2.orientation = LinearLayout.HORIZONTAL
                linelayout2.setOnClickListener {
                    val intent = Intent(activity,SmalclassDetailsActivity::class.java)
                    intent.putExtra("data",json1.toString())
                    startActivity(intent)
                }
                //第二个
                val linelayout3 = LinearLayout(context)
                linelayout3.orientation = LinearLayout.HORIZONTAL
                linelayout3.setOnClickListener {
                    val intent = Intent(activity,SmalclassDetailsActivity::class.java)
                    intent.putExtra("data",json2.toString())
                    startActivity(intent)
                }
                val params3 = LinearLayout.LayoutParams(
                    resources.getDimension(R.dimen.dp_40).toInt(),
                    resources.getDimension(R.dimen.dp_40).toInt()
                )
                params3.topMargin = resources.getDimension(R.dimen.dp_17).toInt()
                val imageview1 = ImageView(context)
                Glide.with(this).load(json1.getString("url"))
                    .apply(RequestOptions().centerCrop().transform(RoundedCorners(50)))
                    .into(imageview1)
                val imageview2 = ImageView(context)
                Glide.with(this).load(json2.getString("url"))
                    .apply(RequestOptions().centerCrop().transform(RoundedCorners(50)))
                    .into(imageview2)
                val params4 = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
                params4.weight = 1f
                val textview1 = TextView(context)
                val textview2 = TextView(context)
                textview1.setPadding(resources.getDimension(R.dimen.dp_5).toInt())
                textview2.setPadding(resources.getDimension(R.dimen.dp_5).toInt())
                textview1.gravity = 0x11 or 0x03
                textview2.gravity = 0x11 or 0x03
                textview1.text = json1.getString("name")
                textview2.text = json2.getString("name")
                linelayout2.addView(imageview1, params3)
                linelayout2.addView(textview1, params4)
                linelayout3.addView(imageview2, params3)
                linelayout3.addView(textview2, params4)
                linelayout1.addView(linelayout2, params2)
                linelayout1.addView(linelayout3, params2)
                binding.horsco.addView(linelayout1, params1)
            }
        }

        viewModel.contents.observe(this) {
            data = it as MutableList<JSONObject>
            adapter = BaseRecyAdapter(R.layout.find_content_item, data)
            adapter.setBind { itemview, position ->
                val portrait = itemview.findViewById<ImageView>(R.id.portrait)
                val username = itemview.findViewById<TextView>(R.id.name)
                val ht = itemview.findViewById<TextView>(R.id.ht)
                val sc = itemview.findViewById<ImageView>(R.id.sc)
                val dz = itemview.findViewById<ImageView>(R.id.dz)
                val content = itemview.findViewById<TextView>(R.id.content)
                val imaegs = itemview.findViewById<RecyclerView>(R.id.imaegs)
                Glide.with(context!!).load(data.get(position).getString("portrait")).into(portrait)
                username.text = data.get(position).getString("username")
                content.text = data.get(position).getString("introduce")
                ht.text = "# ${data.get(position).getString("name")}"
                content.setOnClickListener {
                    val intent = Intent(context,TopicDetailsActivity::class.java)
                    intent.putExtra("data",data.get(position).toString())
                    startActivity(intent)
                }
                dz.setOnClickListener {
                    viewModel.favouropter(
                        data[position].getInt("id").toString(),
                        position
                    )
                }
                sc.setOnClickListener {
                    viewModel.collectopter(
                        data[position].getInt("id").toString(),
                        position
                    )
                }
                dz.setImageResource(
                    if (data[position].getBoolean("dz")) R.mipmap.dz2 else R.mipmap.dz1
                )
                sc.setImageResource(
                    if (data[position].getBoolean("sc")) R.mipmap.sc2 else R.mipmap.sc1
                )
                viewModel.favourResult.observe(this) {
                    if (it.position != position){
                        return@observe
                    }
                    if (it.status == null){
                        ToastUtils.showToast(context,"操作失败")
                        return@observe
                    }
                    if (it.status == true) {
                        dz.setImageResource(R.mipmap.dz2)
                    } else {
                        dz.setImageResource(R.mipmap.dz1)
                    }
                }
                viewModel.collectResult.observe(this) {
                    if (it.position != position){
                        return@observe
                    }
                    if (it.status == null){
                        ToastUtils.showToast(context,"操作失败")
                        return@observe
                    }
                    if (it.status == true) {
                        sc.setImageResource(R.mipmap.sc2)
                    } else {
                        sc.setImageResource(R.mipmap.sc1)
                    }
                }
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
                    imageview.setOnClickListener {
                        PhotoviewDialog(activity,list[position]).show()
                    }
                    Glide.with(imageview.context).load(list[position]).centerCrop().into(imageview)
                    delte.visibility = View.GONE
                }
                imaegs.adapter = adapter2
                imaegs.layoutManager = GridLayoutManager(context!!, 3)
            }
            binding.recy.adapter = adapter
            binding.recy.layoutManager = LinearLayoutManager(context)
        }
        binding.recy.isNestedScrollingEnabled = false

        binding.push.setOnClickListener {
            startActivity(Intent(context, PushActivity::class.java))
        }

        viewModel.getAllSmalClass()
        viewModel.getPageData(1, 10000)

        return binding.root
    }


}