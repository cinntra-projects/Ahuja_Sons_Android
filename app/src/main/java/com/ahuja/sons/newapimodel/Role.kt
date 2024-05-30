package com.ahuja.sons.newapimodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Role(
    val Level: String,
    val Name: String,
    val Subdepartment: String,
    val id: String
):Parcelable