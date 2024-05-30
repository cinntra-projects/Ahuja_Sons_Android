package com.ahuja.sons.newapimodel

import kotlinx.serialization.Serializable


@Serializable
data class EmployeeAtLoginData(
    val Active: String,
    val CountryCode: String,
    val Email: String,
    val EmployeeID: String="",
    val FCM: String,
    val Mobile: String,
    val SalesEmployeeCode: String,
    val SalesEmployeeName: String,
    val branch: String,
    val companyID: String,
    val dep: Dep,
    val discount: String,
    val div: List<Any>,
    val firstName: String,
    val id: Int,
    val lastLoginOn: String,
    val lastName: String,
    val level: Int,
    val logedIn: String,
    val middleName: String,
    val password: String,
    val passwordUpdatedOn: String,
    val position: String,
    val reportingName: String,
    val reportingTo: String,
    val role: Role ,//Role
    val salesUnit: String,
    val subdep: Subdep , //SubDepartMentOfEmployeeAtLoginData
    val timestamp: String,
    val userName: String,
    val zone: String
){
    data class Role(
        val DiscountPercentage: String,
        val Level: Int,
        val Name: String,
        val Subdepartment: Int,
        val id: Int
    )


    data class Subdep(
        val Code: String,
        val Department: String,
        val Name: String,
        val id: Int
    )

    data class Dep(
        val Name: String,
        val id: Int
    )


}