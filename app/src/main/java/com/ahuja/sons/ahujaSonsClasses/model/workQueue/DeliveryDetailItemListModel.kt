package com.ahuja.sons.ahujaSonsClasses.model.workQueue

data class DeliveryDetailItemListModel(
    var `data`: List<Data> = listOf(),
    var message: String = "",
    var status: Int = 200
) {
    data class Data(
        var BaseEntry: String = "",
        var DeliveryNoteID: String = "",
        var DiscountPercent: Double = 0.0,
        var ItemCode: String = "",
        var ItemDescription: String = "",
        var LineNum: Int = 0,
        var MainSystem: String = "",
        var Quantity: Int = 0,
        var SerialNumbers: List<Any> = listOf(),
        var ShipToCode: String = "",
        var System: String = "",
        var TaxCode: String = "",
        var TaxRate: String = "",
        var U_REPTYP: String = "",
        var UnitPrice: Double = 0.0,
        var UomNo: String = "",
        var U_Size: String = "",
        var id: Int = 0
    )
}