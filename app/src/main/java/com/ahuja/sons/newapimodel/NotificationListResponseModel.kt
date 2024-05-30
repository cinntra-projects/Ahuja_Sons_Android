package com.ahuja.sons.newapimodel

data class NotificationListResponseModel(
    val `data`: ArrayList<DataXXX>,
    val message: String,
    val status: Int
){
    data class DataXXX(
        val notification: Notification
    )


    data class Notification(
        val CreatedDate: String,
        val CreatedTime: String,
        val Description: String,
        val Emp: String,
        val Push: Int,
        val Read: String,
        val SourceID: String,
        val SourceTime: String,
        val SourceType: String,
        val Title: String,
        val Type: String,
        val id: Int
    )
}