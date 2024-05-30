package com.ahuja.sons.newapimodel

data class SolutionListResponseModel(
    val `data`: ArrayList<DataXXX>,
    val message: String,
    val status: Int
){
    data class DataXXX(
        val CreatedBy: String,
        val CreatedByName: String,
        val CreatedDate: String,
        val CreatedTime: String,
        val IssueCategory: String,
        val Title: String,
        val id: Int
    )
}