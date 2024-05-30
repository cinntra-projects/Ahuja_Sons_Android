package com.ahuja.sons.newapimodel

data class IssueListResponseModel(
    val `data`: List<DataXXX>,
    val message: String,
    val status: Int
){
    data class DataXXX(
        val CreatedBy: String,
        val CreatedDate: String,
        val CreatedTime: String,
        val Description: String,
        val IssueType: String,
        val SerialNo: String,
        val Solution: String,
        val TicketId: String,
        val id: Int
    )
}