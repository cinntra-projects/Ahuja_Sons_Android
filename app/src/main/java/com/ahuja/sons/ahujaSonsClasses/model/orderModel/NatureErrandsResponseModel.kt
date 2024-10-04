package com.ahuja.sons.ahujaSonsClasses.model.orderModel

data class NatureErrandsResponseModel(
    val `data`: ArrayList<Data>,
    val message: String,
    val status: Int
){
    data class Data(
        val CreatedAt: String,
        val Description: String,
        val Name: String,
        val UpdatedAt: String,
        val id: Int
    )
}