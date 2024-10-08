package com.ahuja.sons.ahujaSonsClasses.model.local

import com.ahuja.sons.ahujaSonsClasses.model.AllOrderListResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllOrderListModel

data class LocalRouteData(
    var id: String = "",

    var orderName: String = "",

    var orderList: MutableList<AllOrderListModel.Data> = mutableListOf()


)