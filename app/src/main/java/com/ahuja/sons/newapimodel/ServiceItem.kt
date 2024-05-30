package com.ahuja.sons.newapimodel

data class ServiceItem(
    val BranchId: String,
    val ItemCode: String,
    val ItemName: String,
    val ItemsGroupCode: String,
    val ItemsGroupName: String,
    val ModelNo: String,
    val Quantity: Int,
    val SerialNo: String,
    val ServiceContractID: String,
    val UnitPrice: String,
    val WarrantyEndDate: String,
    val WarrantyStartDate: String,
    val id: Int
)