package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class SalesPersonCode(
    val SalesEmployeeCode: String,
    val SalesEmployeeName: String,
    val id: Int
) : Parcelable