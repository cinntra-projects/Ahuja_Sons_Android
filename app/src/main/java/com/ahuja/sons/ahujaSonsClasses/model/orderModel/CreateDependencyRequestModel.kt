package com.ahuja.sons.ahujaSonsClasses.model.orderModel

data class CreateDependencyRequestModel(
    val CreatedBy: String,
    val OrderDependency: List<Int>,
    val OrderRequestID: String
)