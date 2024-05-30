package com.ahuja.sons.newapimodel

data class AddProductRequestModel(
    val BranchId: Int,
    val CardCode: String,
    val CommissioningDate: String,
    val ContractorName: String,
    val DeInstallationDate: String,
    val InstallationDate: String,
    val ItemCode: String,
    val ItemName: String,
    val ItemType: String,
    val ItemsGroupCode: Int,
    val ItemsGroupName: String,
    val ModelNo: String,
    val Quantity: String,
    val ReInstallationDate: String,
    val Remarks: String,
    val SerialNo: String,
    val SiteSurvey: String,
    val UnitPrice: String,
    val WarrantyEndDate: String,
    val WarrantyStartDate: String,
    val WarrantyType: String,
    val id: String
)