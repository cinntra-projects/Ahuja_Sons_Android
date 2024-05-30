package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EmployeeDetails(
    val Active: String,
    val Email: String,
    val EmployeeID: String,
    val FCM: String,
    val Mobile: String,
    val SalesEmployeeCode: String,
    val SalesEmployeeName: String,
    val branch: String,
    val companyID: String,
    val div: String,
    val firstName: String,
    val id: Int,
    val lastLoginOn: String,
    val lastName: String,
    val logedIn: String,
    val middleName: String,
    val password: String,
    val passwordUpdatedOn: String,
    val position: String,
    val reportingTo: String,
    val role: String,
    val salesUnit: String,
    val timestamp: String,
    val userName: String,
    val zone: String
) : Parcelable