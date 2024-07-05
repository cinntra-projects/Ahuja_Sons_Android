package com.ahuja.sons.ahujaSonsClasses.model.local

import com.ahuja.sons.ahujaSonsClasses.model.AllOrderListResponseModel

data class LocalRouteData(
    var id: String = "",

    var orderName: String = "",

    var orderList: MutableList<AllOrderListResponseModel.Data> = mutableListOf()


)