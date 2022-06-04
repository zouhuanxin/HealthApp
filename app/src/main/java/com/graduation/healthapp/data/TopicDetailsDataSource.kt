package com.graduation.healthapp.data

import com.graduation.healthapp.util.net.MyUrl
import com.graduation.healthapp.util.net.NetRequest
import org.json.JSONObject

class TopicDetailsDataSource {

    //添加评论
    fun addComment(
        msg: String,
        topicid: String,
        phone: String,
        aim: String,
        replyphone: String,
        block: (Result) -> Unit
    ) {
        val req = JSONObject().apply {
            put("comment", msg)
            put("phone", phone)
            put("aim", aim)
            put("replyphone", replyphone)
            put("topicid", topicid)
        }
        NetRequest.Instance.post(MyUrl.addTopicReply, req) {
            NetRequest.Instance.IsResult(block, it)
        }
    }


    //获取所有评论信息
    fun getComments(
        topicid: String,
        aim: String,
        block: (Result) -> Unit
    ) {
        NetRequest.Instance.get(MyUrl.getCommentData1 + "?topicid=${topicid}&aim=${aim}") {
            NetRequest.Instance.IsResult(block, it)
        }
    }
}