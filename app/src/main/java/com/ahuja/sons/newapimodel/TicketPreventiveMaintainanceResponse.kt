package com.ahuja.sons.newapimodel

import com.ahuja.sons.adapter.ticketItemAdapter.SparePart

data class TicketPreventiveMaintainanceResponse(
    var `data`: List<DataXXX>,
    var message: String = "",
    var status: Int = 0
){
    data class DataXXX(
        var ClientComplainDetail: String = "",
        var ColdWater: String = "",
        var CustomerName: String = "",
        var CustomerNumber: String = "",
        var CustomerRemark: String = "",
        var DefectFound: String = "",
        var DefectReason: String = "",
        var EngineerName: String = "",
        var EngineerNumber: String = "",
        var EngineerRemark: String = "",
        var Files: List<File> = ArrayList(),
        var HotWater: String = "",
        var ItemCode: String = "",
        var ItemSerialNo: String = "",
        var Membrane: String = "",
        var ROPump: String = "",
        var Raw: String = "",
        var Rejected: String = "",
        var RemedialAction: String = "",
        var SparePart: List<SparePart> = ArrayList(),
        var TicketId: String = "",
        var Treated: String = "",
        var OtherClientComplain: String = "",
        var OtherDefectFound: String = "",
        var OtherDefectReason: String = "",
        var OtherRemedialAction: String = "",
        var id: Int = 0,
        var is_DrainCheck: Boolean = false,
        var is_FilterWash: Boolean = false,
        var is_HarnessCheck: Boolean = false,
        var is_HygieneCleansing: Boolean = false,
        var is_LeakageCheck: Boolean = false
    )

 /*   data class SparePart(
        var SellType: String = "",
        var SparePartId: String = "",
        var SparePartName: String = "",
        var PartQty: String = "",
        var ServiceReportId: String = "",
        var SpareSerialNo: String = "",
        var ItemCode: String = "",
        var ItemSerialNo: String = "",
        var TicketId: String = "",
        var id: String = ""
    )*/

}