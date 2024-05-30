package com.ahuja.sons.newapimodel

data class OrderListResponseModel(
    val `data`: List<DataXXX>,
    val message: String,
    val meta: MetaXXX,
    val status: Int
){
    data class DataXXX(
        val Attach: List<Any>,
        val CancelStatus: String,
        val CardCode: String,
        val CardName: String,
        val DocDate: String,
        val DocDueDate: String,
        val DocEntry: String,
        val DocumentStatus: String,
        val FinalStatus: String,
        val NetTotal: String,
        val ReadLevel1: String,
        val ReadLevel2: String,
        val ReadLevel3: String,
        val SalesPersonCode: String,
        val TaxDate: String,
        val amendment_action: String,
        val amendment_status: String,
        val approval_id: String,
        val approval_name: String,
        val id: Int
    )
}