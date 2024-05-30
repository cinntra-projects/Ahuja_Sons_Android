package com.ahuja.sons.model

data class TicketHistoryData(
    var Datetime: String = "",
    var Remarks: String= "",
    var OwnerId: String= "",
    var Message: String= "",
    var OwnerName: String= "",
    var OwnerType: String= "",
    var TicketId: Int= 0,
    var Type: String= "",
    var id: Int= 0,
    var viewType : Int = 0
)