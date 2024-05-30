package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllPartRequestItemData(
    val ApprovedDate: String,
    val ApproverId: String,
    val BillToAddress: String,
    val BusinessPartnerDetails: BusinessPartnerDetails,
    val Datetime: String,
    val EmployeeDetails: EmployeeDetails,
    val EstimateAmt: String,
    val Items: List<Item>,
    val OwnerId: String,
    val OwnerType: String,
    val RequestedDate: String,
    val Status: String,
    val TicketId: Int,
    val WarrantyStatus: String,
    val id: Int,
    val PRAttachments: List<Attachments>,
) : Parcelable