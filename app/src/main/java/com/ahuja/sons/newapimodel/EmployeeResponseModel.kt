package com.ahuja.sons.newapimodel

data class EmployeeResponseModel(
    val `data`: List<DataXXX>,
    val message: String,
    val meta: MetaXX,
    val status: Int
){
    data class DataXXX(
        val Active: String,
        val CountryCode: String,
        val Email: String,
        val EmployeeID: String,
        val FCM: String,
        val Mobile: String,
        val SalesEmployeeCode: String,
        val SalesEmployeeName: String,
        val branch: String,
        val companyID: String,
        val dep: Int,
        val div: String,
        val firstName: String,
        val id: String,
        val lastLoginOn: String,
        val lastName: String,
        val level: Int,
        val logedIn: String,
        val middleName: String,
        val password: String,
        val passwordUpdatedOn: String,
        val position: String,
        val reportingTo: String,
        val role: String,
        val salesUnit: String,
        val subdep: Int,
        val timestamp: String,
        val userName: String,
        val zone: String
    )
}