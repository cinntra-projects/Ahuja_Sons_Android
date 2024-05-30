package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class SalesPersonCodeX(
    val SalesEmployeeCode: String,
    val SalesEmployeeName: String,
    val id: Int
):Parcelable