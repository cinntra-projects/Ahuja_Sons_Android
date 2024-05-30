package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class TicketDataResponse(
    val `data`: List<TicketDataModel> ,
    val message: String,
    val status: Int
) : Parcelable