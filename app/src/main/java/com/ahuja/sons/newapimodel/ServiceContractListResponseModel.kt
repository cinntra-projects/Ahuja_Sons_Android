package com.ahuja.sons.newapimodel

data class ServiceContractListResponseModel(
    val `data`: List<DataXXX>,
    val message: String,
    val meta: Meta,
    val status: Int
){
    data class DataXXX(
        val AddressName: String,
        val AssignedToId: String,
        val AssignedToName: String,
        val BillingFrequency: String,
        val BranchId: String,
        val CardCode: String,
        val CardName: String,
        val ContractType: String,
        val CreatedById: String,
        val CreatedByName: String,
        val Frequency: String,
        val FromDate: String,
        val PaymentTerm: String,
        val Remarks: String,
        val ServiceItemList: List<ServiceItem>,
        val Status: Int,
        val ToDate: String,
        val TotalAmount: String,
        val id: String
    )
}