package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TicketDataModel(
    var id: Int = 0,
    var AMCDueDate: String = "",
    var AssignTo: String= "",
    var BpCardCode: String= "",
    var CMCDueDate: String= "",
    var ClosedDate: String= "",
    var ContactAddress: String= "",
    var ContactEmail: String= "",
    var ContactName: String= "",
    var ContactPhone: String= "",
    var CreateDate: String= "",
    var DueDate: String= "",
    var CreatedBy: String= "",
    var DeliveryID: String = "",
    var Description: String= "",
    var DurationOfService: String= "",
    var Priority: String= "",
    var AlternatePhone: String= "",
    var ProductCategory: String= "",
    var ProductModelNo: String= "",
    var ProductName: String= "",
    var ProductSerialNo: String= "",
    var SignatureStatus: String= "",
    var Status: String= "",
    var Title: String= "",
    var Type: String= "",
    var WarrantyDueDate: String= "",
    var WarrantyStartDate: String= "",
    var ExtWarrantyStartDate: String= "",
    var ExtWarrantyDueDate: String= "",
    var AMCStartDate: String= "",
    var CMCStartDate: String= "",
    var Datetime: String= "",
    var TicketEndDate: String?= "",
    var TicketStartDate: String?= "",
    var ManufacturingDate: String= "",
    var CustomerPIR: String?= "",
    var ExpiryDate: String= "",
    var Zone: String= "",
    var TicketStatus: String= "",
    var ProductCategoryName : String = "",
    var BusinessPartner : BusinessPartner = BusinessPartner(),
    var AssignToDetails : AssignToDetails = AssignToDetails(),
    var CreatedByDetails : CreatedByDetails = CreatedByDetails()


) : Parcelable