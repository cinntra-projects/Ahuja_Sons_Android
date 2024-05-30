package com.ahuja.sons.newapimodel

data class ProductResponseModel(
    val `data`: List<DataXXX> = ArrayList(),
    val message: String = "",
    val meta: MetaX? = null,
    val status: Int = 0
){
    data class DataXXX(
        val BPBranch: List<BPBranchX> = ArrayList(),
        val BranchId: String = "",
        val CardCode: String = "",
        val CardName: String = "",
        val CommissioningDate: String = "",
        val ContractorName: String = "",
        val DeInstallationDate: String = "",
        val InstallationDate: String = "",
        val ItemCode: String = "",
        val ItemName: String = "",
        val ItemType: String = "",
        val ItemsGroupCode: String = "",
        val ItemsGroupName: String = "",
        val ModelNo: String = "",
        val Quantity: String = "",
        val ReInstallationDate: String = "",
        val Remarks: String = "",
        val SerialNo: String = "",
        val SiteSurvey: String = "",
        val UnitPrice: String = "",
        val WarrantyEndDate: String = "",
        val WarrantyStartDate: String = "",
        val WarrantyType: String = "",
        val id: String = "",
        val zone: String = ""
    )
}