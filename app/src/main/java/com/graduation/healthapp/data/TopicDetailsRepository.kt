package com.graduation.healthapp.data

class TopicDetailsRepository(private val dataSource: TopicDetailsDataSource) {

    fun addComment(
        msg: String,
        topicid: String,
        phone: String,
        aim: String,
        replyphone: String,
        block: (Result) -> Unit
    ) {
        dataSource.addComment(msg, topicid, phone, aim,replyphone) {
            block.invoke(it)
        }
    }

    fun getComments(
        topicid: String,
        aim: String,
        block: (Result) -> Unit
    ) {
        dataSource.getComments(topicid, aim) {
            block.invoke(it)
        }
    }

}