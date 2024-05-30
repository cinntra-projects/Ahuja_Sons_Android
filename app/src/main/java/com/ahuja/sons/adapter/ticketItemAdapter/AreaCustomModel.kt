package com.ahuja.sons.adapter.ticketItemAdapter

data class AreaCustomModel(
    val area: List<Area>,
){
    data class Area(
        var Location: String = "",
        var Length: String = "",
        var Width: String = "",
        var Height: String = "",
    )
}



