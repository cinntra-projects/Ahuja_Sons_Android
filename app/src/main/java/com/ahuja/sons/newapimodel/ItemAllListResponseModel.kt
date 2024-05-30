package com.ahuja.sons.newapimodel

data class ItemAllListResponseModel(
    val `data`: ArrayList<DataXXX> = ArrayList(),
    val message: String = "",
    val status: Int
){
    data class DataXXX(
        val BPBranch: List<BPBranch> = ArrayList(),
        val BranchId: String = "",
        val CardCode: String = "",
        val CardName: String = "",
        val CommissioningDate: String = "",
        val ContractorName: String = "",
        val DeInstallationDate: String = "",
        val MachineLocationFloar: String = "",
        val MachineLocationArea: String = "",
        val DateOfInstallation: String = "",
        val TDSInput: String = "",
        val TDSOutput: String = "",
        val InstallationDate: String = "",
        val ItemCode: String = "",
        val ItemName: String = "",
        val ItemType: String = "",
        val ItemsGroupCode: String = "",
        val ItemsGroupName: String = "",
        val ModelNo: String = "",
        val Quantity: Int = 0,
        val ReInstallationDate: String = "",
        val Remarks: String = "",
        val SerialNo: String = "",
        val SiteSurvey: String = "",
        val UnitPrice: String = "",
        val TicketId: String = "",
        val WarrantyEndDate: String = "",
        val WarrantyStartDate: String = "",
        val ExtendedWarrantyApproved: String = "",
        val ExtendedWarrantyApprovedBy: String = "",
        val ExtendedWarrantyEndDate: String = "",
        val ExtendedWarrantyStartDate: String = "",
        val ToDate: String = "",
        val WarrantyType: String = "",
        val id: Int = 0,
        val zone: String = "",
        val is_Reported: Boolean = false
    ): java.io.Serializable
}