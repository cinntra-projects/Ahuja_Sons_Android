package com.ahuja.sons.ahujaSonsClasses.model.workQueue

data class WorkQueueRequestModel(
    val PageNo: Int,
    val SalesPersonCode: String,
    val SearchText: String,
    val `field`: Field,
    val maxItem: Int,
    val role_id: String
){
    data class Field(
        val CardCode: String,
        val CardName: String,
        val FinalStatus: String,
        val FromDate: String,
        val ToDate: String
    )
}