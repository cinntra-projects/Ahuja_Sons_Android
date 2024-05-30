package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    val CategoryName: String,
    val Comments: String,
    val Datetime: String,
    val Discount: String,
    val ItemCode: String,
    val ItemName: String,
    val ItemQty: String,
    val PRID: Int,
    val Status: String,
    val UnitPrice: String,
    val id: Int
) : Parcelable