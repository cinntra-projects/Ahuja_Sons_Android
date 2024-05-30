package com.ahuja.sons.adapter.ticketItemAdapter

data class AvailabilityCustomModel(
    val availability: List<Availability>,

){
    data class Availability(
        var ItemName: String = "",
        var ItemQty: String = "",
        var ItemDistance: String = ""
    )
}
