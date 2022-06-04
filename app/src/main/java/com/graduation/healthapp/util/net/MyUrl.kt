package com.graduation.healthapp.util.net

class MyUrl {

    companion object{
        val ip = "http://192.168.3.94:9092"
        val verify = "$ip/health/register/verify"
        val register = "$ip/health/register/register"
        val login = "$ip/health/register/login"
        val sendsms = "$ip/health/register/sendsms"
        val getEffectiveAd = "$ip/health/advertisement/getEffectiveAd"
        val addTopic = "$ip/health/TopicController/addTopic"
        val getAllSmalClass = "$ip/health/SmalClassController/getAllSmalClass"
        val getPageData = "$ip/health/TopicController/getPageData"
        val getPageData2 = "$ip/health/TopicController/getPageData2"
        val favour = "$ip/health/FavourController/favour"
        val collect = "$ip/health/CollectController/collect"
        val addTopicReply = "$ip/health/TopicReplyController/addTopicReply"
        val getCommentData1 = "$ip/health/TopicReplyController/getCommentData1"
        val dish = "$ip/health/RecognitionController/dish"
        val dishText = "$ip/health/RecognitionController/dishText"
        val recommendActivity = "$ip/health/RecommendController/recommendActivity"
        val recommendFood = "$ip/health/RecommendController/recommendFood"
        val recommendGoods = "$ip/health/RecommendController/recommendGoods"
        val addCalorie = "$ip/health/RecordController/addCalorie"
        val byUserAndTimeData = "$ip/health/RecordController/byUserAndTimeData"
        val updateUserinfo = "$ip/health/register/updateUserinfo"
        val addPunchCard = "$ip/health/PunchCardController/addPunchCard"
        val getPunchCard = "$ip/health/PunchCardController/getPunchCard"
        val getPunchCardRank = "$ip/health/PunchCardController/getPunchCardRank"
        val getFoodListData = "$ip/health/FoodListController/getFoodListData"
        val byUserFoodlist = "$ip/health/FoodListController/byUserFoodlist"
        val addFoodlistFavour = "$ip/health/FoodListController/addFoodlistFavour"
        val analysis = "$ip/health/RecognitionController/analysis"
    }

}