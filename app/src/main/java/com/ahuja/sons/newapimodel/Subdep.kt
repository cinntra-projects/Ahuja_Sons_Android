package com.ahuja.sons.newapimodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Subdep(
    val Department: String,
    val Name: String,
    val id: String
):Parcelable