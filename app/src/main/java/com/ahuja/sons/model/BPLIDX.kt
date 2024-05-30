package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class BPLIDX(
    val Address: String,
    val BPLId: String,
    val BPLName: String,
    val Building: String,
    val City: String,
    val Country: String,
    val DflWhs: String,
    val Disabled: String,
    val MainBPL: String,
    val State: String,
    val StreetNo: String,
    val TaxIdNum: String,
    val UpdateDate: String,
    val UserSign2: String,
    val ZipCode: String,
    val id: Int
) : Parcelable