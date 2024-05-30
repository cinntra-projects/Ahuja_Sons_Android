package com.ahuja.sons.newapimodel

data class EscallationResponseModel(
    val `data`: ArrayList<DataXXX>,
    val message: String,
    val status: Int
){
    data class DataXXX(
        val CreateDate: String,
        val CreateTime: String,
        val Email: String,
        val Message: String,
        val Name: String,
        val Phone: String,
        val SalesEmployeeCode: String,
        val TicketId: String,
        val Title: String,
        val id: Int
    )
}