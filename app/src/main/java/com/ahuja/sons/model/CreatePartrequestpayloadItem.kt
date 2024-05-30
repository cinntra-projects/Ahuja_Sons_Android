package com.ahuja.sons.model

data class CreatePartrequestpayloadItem(

    val Comments: String,
    val ItemCode: String,
    val UnitPrice: String,
    val ProjectCode: String,
    val ItemQty: Int,
    val id: Int,
    val PartRequestType: String,
    val ContractType: String,
    val ItemSrialNo: String

)