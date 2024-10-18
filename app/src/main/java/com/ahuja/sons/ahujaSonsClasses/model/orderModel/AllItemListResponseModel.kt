package com.ahuja.sons.ahujaSonsClasses.model.orderModel

data class AllItemListResponseModel(
    val `data`: ArrayList<Data>,
    val message: String = "",
    val status: Int = 0
){
    data class Data(
        val CostingCode2: String = "",
        val DiscountPercent: Double = 0.0,
        val EndDate: String = "",
        val FreeText: String = "",
        val Frequency: String = "",
        val IsService: String = "",
        val ItemCode: String = "",
        val ItemDescription: String = "",
        val ItemSerialNo: String = "",
        val LineNum: Int = 0,
        val MainSystem: String = "",
        val MeasureUnit: String = "",
        val OrderRequestID: Int = 0,
        val PriceAfterVAT: Double = 0.0,
        val ProjectCode: String = "",
        val Quantity: Int = 0,
        val ReferenceItem: String = "",
        val ReferenceSerial: String = "",
        val SapOrderID: String = "",
        val ShipToCode: String = "",
        val ShipToState: String = "",
        val StartDate: String = "",
        val System: String = "",
        val TaxCode: String = "",
        val TaxRate: String = "",
        val U_FGITEM: String = "",
        val U_REPTYP: String = "",
        val UnitPrice: Double = 0.0,
        val U_Size: String = "",
        val id: Int = 0
    )
}