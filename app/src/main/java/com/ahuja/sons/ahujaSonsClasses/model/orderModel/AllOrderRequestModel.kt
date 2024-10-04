package com.ahuja.sons.ahujaSonsClasses.model.orderModel

data class AllOrderRequestModel(
    val PageNo: Int,
    val SalesPersonCode: String,
    val SearchText: String,
    val `field`: Field,
    val maxItem: Int
){
    data class Field(
        val CardCode: String,
        val FromAmount: String,
        val FromDate: String,
        val MrNo: String,
        val PoDateFrom: String,
        val PoDateTo: String,
        val ShipToCode: String,
        val ToAmount: String,
        val ToDate: String
    )


}