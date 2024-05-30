package com.ahuja.sons.newapimodel

data class EmployeeSubDepResponseModel(
    val `data`: List<DataXXX>,
    val message: String,
    val status: Int
){
    data class DataXXX(
        val Department: String,
        val Name: String,
        val id: String
    )
}