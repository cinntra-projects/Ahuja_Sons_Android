package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class PayTermsGrpCode(
    val GroupNumber: String,
    val PaymentTermsGroupName: String,
    val id: Int
) : Parcelable