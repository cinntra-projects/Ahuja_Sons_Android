package com.ahuja.sons.newapimodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Dep(
    val Name: String,
    val id: String
):Parcelable