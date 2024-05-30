package com.ahuja.sons.newapimodel

data class SpareItemListApiModel(
    val `data`: List<DataXXX> = ArrayList(),
    val message: String = "",
    val status: Int = 0
){
    data class DataXXX(
        val ItemDescription: String = "",
        val ItemGroup: String = "",
        val ItemNo: String = "",
        val Remark: String = "",
        val SerialNo: String = "",
        val SparePartPrice: String = "",
        val id: Int = 0
    )
}