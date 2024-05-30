package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class TicketDetailsModel(
    val `data`: List<TicketDetailsData>,
    val message: String,
    val status: Int
) : Parcelable