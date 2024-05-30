package com.ahuja.sons.newapimodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactPersonCode(
    val FirstName: String,
    val InternalCode: String
):Parcelable