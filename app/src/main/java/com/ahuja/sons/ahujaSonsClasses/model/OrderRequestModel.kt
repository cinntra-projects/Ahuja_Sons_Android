package com.ahuja.sons.ahujaSonsClasses.model

class OrderRequestModel(
    
    val SalesPersonCode: String,
    
    val PageNo: Int,
    val maxItem: String,
    
    val SearchText: String,
    val field: Field,
){
    data class Field(
        
        val FromDate: String,
        
        val ToDate: String,
        
        val FinalStatus: String,
        
        val CardCode: String,
        
        val CardName: String,
        
        val ShipToCode: String,
        
        val FromAmount: String,
        
        val ToAmount: String,
        
        val U_MR_NO: String,
    )
}