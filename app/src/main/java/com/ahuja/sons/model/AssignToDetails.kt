package com.ahuja.sons.model

import android.os.Parcelable
import com.ahuja.sons.newapimodel.Role
import kotlinx.parcelize.Parcelize


@Parcelize
data class AssignToDetails(
    var Active: String = "",
    var Email: String= "",
    var EmployeeID: String= "",
    var FCM: String= "",
    var Mobile: String= "",
    var SalesEmployeeCode: String= "",
    var SalesEmployeeName: String= "",
    var branch: String= "",
    var companyID: String= "",
    var div: String= "",
    var firstName: String= "",
    var id: Int= 0,
    var lastLoginOn: String= "",
    var lastName: String= "",
    var logedIn: String= "",
    var middleName: String= "",
    var password: String= "",
    var passwordUpdatedOn: String= "",
    var position: String= "",
    var reportingTo: String= "",
    var role: Role=Role("","","",""),
    var salesUnit: String= "",
    var timestamp: String= "",
    var userName: String= ""
) : Parcelable