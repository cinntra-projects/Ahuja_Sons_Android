package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TicketDetailsData(
    var AMCDueDate: String= "",
    var AMCStartDate: String= "",
    var BusinessPartner: BusinessPartner = BusinessPartner(),
    var CMCDueDate: String= "",
    var CMCStartDate: String= "",
    var DeliveryId: DeliveryID=DeliveryID(),
    var DiscountPercent: Double= 0.0,
    var ExpiryDate: String= "",
    var ExtWarrantyDueDate: String= "",
    var ExtWarrantyStartDate: String= "",
    var ItemCategory: String= "",
    var ItemCategoryName: String= "",
    var ItemCode: String= "",
    var ItemDescription: String= "",
    var LineNum: Int=0,
    var ManufacturingDate: String= "",
    var Quantity: Int= 0,
    var SerialNo: String= "",
    var Status: String= "",
    var TaxCode: String= "",
    var UnitPrice: Double=0.0,
    var WarrantyDueDate: String= "",
    var WarrantyStartDate: String= "",
    var id: Int=0
) : Parcelable