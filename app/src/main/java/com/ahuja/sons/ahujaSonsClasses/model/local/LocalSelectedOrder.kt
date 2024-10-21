package com.ahuja.sons.ahujaSonsClasses.model.local

import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel

data class LocalSelectedOrder(
    var orderId: String = "",
    var orderName: String = "",
    var id : String = "",
    var errandId : String = "",
    var isErrand : Boolean = false,
    var isReturn : Boolean = false,
    var InspectedDeliverys: ArrayList<AllWorkQueueResponseModel.InspectedDelivery> = arrayListOf(),
)
