package com.ahuja.sons.newapimodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ResponseAddTicket(
    val message: String,
    var status: Int

):Parcelable
