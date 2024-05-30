package com.ahuja.sons.newapimodel

data class ServiceContractListRequest(
    var BranchId: String,
    var CardCode: String,
    var PageNo: Int,
    var SearchText: String,
    var `field`: Field,
    var maxItem: Int

){
    data class Field(
        
        val WS_FromDate: String,
        
        val WS_ToDate: String,
        
        val WE_FromDate: String,
        
        val WE_ToDate: String,
        val finalstatus: String,
        val searchAssignTo: String,
        val searchpriority: String,
    )
}