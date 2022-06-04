package com.graduation.healthapp.ui.find

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.graduation.healthapp.data.Result
import com.graduation.healthapp.data.TopicDetailsRepository
import org.json.JSONArray

class TopicDetailsViewModel(private val repository: TopicDetailsRepository) : ViewModel() {
    val commentStatus = MutableLiveData<Boolean>()
    val commentResult = MutableLiveData<CommentData>()

    fun addComment(
        msg: String,
        topicid: String,
        phone: String,
        aim: String,
        replyphone: String,
    ) {
        repository.addComment(msg, topicid, phone, aim,replyphone) {
            if (it is Result.Success) {
                commentStatus.postValue(true)
            } else {
                commentStatus.postValue(false)
            }
        }
    }

    fun getComments(
        topicid: String,
        aim: String
    ) {
        repository.getComments(topicid, aim) {
            if (it is Result.Success) {
                val array = it.data.getJSONArray("data")
                val list = mutableListOf<JSONArray>()
                for (i in 0 until array.length()){
                    list.add(array.getJSONArray(i))
                }
                commentResult.postValue(CommentData(true,list))
            } else {
                commentResult.postValue(CommentData(false,null))
            }
        }
    }

    data class CommentData(var status:Boolean,var list:MutableList<JSONArray>?)
}