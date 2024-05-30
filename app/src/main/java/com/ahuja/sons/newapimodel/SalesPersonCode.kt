package com.ahuja.sons.newapimodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SalesPersonCode(
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
    val dep: Dep,
    val div: String,
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
    val reportingTo: String,
    val role: Role,
    val salesUnit: String,
    val subdep: Subdep,
    val timestamp: String,
    val userName: String,
    val zone: String
):Parcelable