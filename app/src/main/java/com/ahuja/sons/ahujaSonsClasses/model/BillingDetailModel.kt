package com.ahuja.sons.ahujaSonsClasses.model

data class BillingDetailModel(
    val message: String,
    val status: Int,
    val data: Data,
    val errors: String,
)

data class Data(

    val UpdateDate: String,

    val UpdateTime: String,

    val CompletedBy: String,
)
