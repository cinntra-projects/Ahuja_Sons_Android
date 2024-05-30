package com.ahuja.sons.newapimodel

data class CreateServiceContractRequestModel(
    val AssignedToId: String,
    val BillingFrequency: String,
    val BranchId: String,
    val CardCode: String,
    val ContractType: String,
    val CreatedById: String,
    val FromDate: String,
    val PaymentTerm: String,
    val Remarks: String,
    val ServiceItemList: List<ServiceItemX>,
    val ToDate: String,
    val id: Any
){

    data class ServiceItemX(
        val BranchId: String,
        val ItemCode: String,
        val ItemName: String,
        val ItemsGroupCode: String,
        val ItemsGroupName: String,
        val ModelNo: String,
        val Quantity: String,
        val SerialNo: String,
        val UnitPrice: String,
        val WarrantyEndDate: String,
        val WarrantyStartDate: String
    )
}