package com.ahuja.sons.newapimodel

data class EmployeeRoleResponseModel(
    val `data`: List<DataXXX>,
    val message: String,
    val status: Int
){
    data class DataXXX(
        val Level: Int,
        val Name: String,
        val Subdepartment: Subdepartment,
        val id: Int
    )
}