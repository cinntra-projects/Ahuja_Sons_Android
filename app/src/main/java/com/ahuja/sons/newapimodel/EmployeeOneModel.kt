package com.ahuja.sons.newapimodel

data class EmployeeOneModel(
    val `data`: List<DataXXX> = ArrayList(),
    val message: String = "",
    val status: Int = 0
)
{
    data class DataXXX(
        val Active: String  = "",
        val CountryCode: String = "",
        val Email: String = "",
        val EmployeeID: String = "",
        val FCM: String = "",
        val Mobile: String = "",
        val SalesEmployeeCode: String = "",
        val SalesEmployeeName: String = "",
        val branch: String = "",
        val companyID: String = "",
        val dep: String = "",
        val div: List<Div> = ArrayList(),
        val firstName: String = "",
        val id: Int = 0,
        val lastLoginOn: String = "",
        val lastName: String = "",
        val level: Int = 0,
        val logedIn: String = "",
        val middleName: String = "",
        val password: String = "",
        val passwordUpdatedOn: String = "",
        val position: String = "",
        val reportingTo: String = "",
        val role: String = "",
        val salesUnit: String = "",
        val subdep: Int = 0,
        val timestamp: String = "",
        val userName: String = "",
        val zone: String = ""
    )

    data class Div(
        val GroupName: String = "",
        val Number: Int = 0,
        val id: Int = 0
    )
}