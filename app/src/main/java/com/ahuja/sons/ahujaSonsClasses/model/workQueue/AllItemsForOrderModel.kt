package com.ahuja.sons.ahujaSonsClasses.model.workQueue

data class AllItemsForOrderModel(
    val `data`: ArrayList<Data>,
    val message: String,
    val status: Int
){

    data class Data(
        val all_item: ArrayList<AllItem>,
        val delivery_item: ArrayList<DeliveryItem>,
        val pending_item: ArrayList<PendingItem>
    )


    data class AllItem(
        val CostingCode2: String,
        val DiscountPercent: Double,
        val EndDate: String,
        val FreeText: String,
        val Frequency: String,
        val IsService: String,
        val ItemCode: String,
        val ItemDescription: String,
        val ItemSerialNo: String,
        val LineNum: Int,
        val MainSystem: String,
        val MeasureUnit: String,
        val OrderRequestID: Int,
        val PriceAfterVAT: Double,
        val ProjectCode: String,
        val Quantity: Double,
        val ReferenceItem: String,
        val ReferenceSerial: String,
        val SapOrderID: String,
        val ShipToCode: String,
        val ShipToState: String,
        val StartDate: String,
        val System: String,
        val TaxCode: String,
        val TaxRate: String,
        val U_FGITEM: String,
        val U_REPTYP: String,
        val UnitPrice: Double,
        val id: Int
    )


    data class DeliveryItem(
        val BaseEntry: String,
        val DeliveryNoteID: String,
        val DiscountPercent: Double,
        val ItemCode: String,
        val ItemDescription: String,
        val LineNum: Int,
        val MainSystem: String,
        val Quantity: Double,
        val SerialNumbers: List<Any>,
        val ShipToCode: String,
        val System: String,
        val TaxCode: String,
        val TaxRate: String,
        val U_REPTYP: String,
        val UnitPrice: Double,
        val UomNo: String,
        val id: Int
    )


    data class PendingItem(
        val CostingCode2: String,
        val DiscountPercent: Double,
        val EndDate: String,
        val FreeText: String,
        val Frequency: String,
        val IsService: String,
        val ItemCode: String,
        val ItemDescription: String,
        val ItemSerialNo: String,
        val LineNum: Int,
        val MainSystem: String,
        val MeasureUnit: String,
        val OrderRequestID: Int,
        val PriceAfterVAT: Double,
        val ProjectCode: String,
        val Quantity: Double,
        val ReferenceItem: String,
        val ReferenceSerial: String,
        val SapOrderID: String,
        val ShipToCode: String,
        val ShipToState: String,
        val StartDate: String,
        val System: String,
        val TaxCode: String,
        val TaxRate: String,
        val U_FGITEM: String,
        val U_REPTYP: String,
        val UnitPrice: Double,
        val id: Int
    )


}