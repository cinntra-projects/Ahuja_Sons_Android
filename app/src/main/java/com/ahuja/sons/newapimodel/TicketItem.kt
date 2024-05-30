package com.ahuja.sons.newapimodel

data class TicketItem(
    val BranchId: String = "",
    val ItemCode: String = "",
    val ItemName: String = "",
    val ItemsGroupCode: String = "",
    val ItemsGroupName: String = "",
    val ModelNo: String = "",
    val Quantity: String = "",
    val SerialNo: String = "",
    val UnitPrice: String = "",
    val WarrantyEndDate: String = "",
    val WarrantyStartDate: String = "",
    val ExtendedWarrantyEndDate: String = "",
    val ExtendedWarrantyStartDate: String = "",
    val ToDate: String = "",
    val id : Int = 0
)