package com.ahuja.sons.model

data class CreatePartrequestpayload(
    val PRItems: List<CreatePartrequestpayloadItem> = emptyList(),
    val TicketId: Int,
    val OwnerId: Int,
    val BillToAddress: String
    )