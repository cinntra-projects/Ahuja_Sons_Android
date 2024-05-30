package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ContactPersonCode(
    val E_Mail: String,
    val FirstName: String,
    val id: Int
):Parcelable