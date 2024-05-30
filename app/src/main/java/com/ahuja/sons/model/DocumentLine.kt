package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DocumentLine(
    var AMCDueDate: String = "",
    var CMCDueDate: String = "",
   // var DeliveryID: String = "",
    var DiscountPercent: Double = 0.0,
    var ItemCode: String = "",
    var ItemName: String = "",
    var ItemCategory: String = "",
    var ItemCategoryName: String = "",
    var ItemDescription: String = "",
    var LineNum: Int = 0,
    var Quantity: Int=0,
    var SerialNo: String = "",
    var TaxCode: String = "",
    var UnitPrice: Double = 0.0,
    var WarrantyDueDate: String = "",
    var ExtWarrantyStartDate: String = "",
    var ExtWarrantyDueDate: String = "",
    var AMCStartDate: String = "",
    var CMCStartDate: String = "",
    var ExpiryDate: String = "",
    var ManufacturingDate: String = "",
    var id: Int = 0,
    var PageNo: Int = 0,
    var InStock: String = "",
    var ItemsGroupCode: String = "",
    var itemquantity : Int = 0,
    var Remarks : String = "",
    var PartRequestType : String = "",
    var ProjectCode : String = "",
    var ItemSrialNo : String = "",
    var ContractType : String = ""


) : Parcelable