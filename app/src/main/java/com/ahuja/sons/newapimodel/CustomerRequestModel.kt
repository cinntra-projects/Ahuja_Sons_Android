package com.ahuja.sons.newapimodel

data class CustomerRequestModel(
    val PageNo: Int,
    val SalesPersonCode: String,
    val SearchText: String,
    val `field`: Field,
    val maxItem: Int



){
    data class Field(
        val FinalStatus: String
    )
}