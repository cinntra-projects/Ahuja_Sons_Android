package com.ahuja.sons.apibody

data class BodySparePart(
    val spareParts: List<SparePart>
)  {
    data class SparePart(
        var SellType: String = "",
        var SparePartId: String = "",
        var SparePartName: String = "",
        var PartQty: String = "",
        var SpareSerialNo: String = "",
        var SparePartPrice: String = ""
    )
}
