package com.ahuja.sons.newapimodel

data class DataAllPartRequest(
    val ApprovedDate: String,
    val ApproverId: String,
    val BillToAddress: String,
    val BusinessPartnerDetails: BusinessPartnerDetails,
    val Datetime: String,
    val EmployeeDetails: EmployeeDetails,
    val EstimateAmt: String,
    val OwnerId: String,
    val OwnerType: String,
    val RequestedDate: String,
    val Status: String,
    val TicketId: Int,
    val WarrantyDate: String,
    val WarrantyStatus: String,
    val id: Int
)