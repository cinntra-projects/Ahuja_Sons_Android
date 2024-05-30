package com.ahuja.sons.newapimodel

data class AllTicketRequestModel(
    val BranchId: String,
    val CardCode: String,
    val PageNo: Int,
    val SalesPersonCode: String,
    val SearchText: String,
    val `field`: Field,
    val maxItem: Int,
    val ServiceContractId: String,
){
    data class Field(
        val finalstatus: String,
        val fromdate: String,
        val todate: String,
        val searchAssignTo: String,
        val searchpriority: String,
        val scopeWork: String
    )
}