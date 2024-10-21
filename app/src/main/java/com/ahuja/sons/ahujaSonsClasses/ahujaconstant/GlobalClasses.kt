package com.ahuja.sons.ahujaSonsClasses.ahujaconstant

import com.ahuja.sons.ahujaSonsClasses.demo.ParentItemModel
import com.ahuja.sons.ahujaSonsClasses.model.local.DeliveryIDOrderModel
import com.ahuja.sons.ahujaSonsClasses.model.local.LocalSelectedOrder
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel

object GlobalClasses {

    val cartListForOrderRequest = hashMapOf<String,LocalSelectedOrder>()

    val cartListForDeliveryCoordinatorCheck = hashMapOf<String,LocalSelectedOrder>()

    val allOrderIDCoordinatorCheck = ArrayList<LocalSelectedOrder>()

    val deliveryIDsList = ArrayList<AllWorkQueueResponseModel.InspectedDelivery>()

    val demoForOrderRequest: MutableList<ParentItemModel.ChildItem> = mutableListOf()

    var isAllShouldSelected=false

}