package com.ahuja.sons.model

data class ComplainDetailResponseModel(
    val `data`: ArrayList<DataX>,
    val message: String,
    val status: Int
){
    data class DataX(
        val CreatedAt: String,
        val Name: String,
        val Remark: String,
        val UpdateAt: String,
        val id: Int
    )
}