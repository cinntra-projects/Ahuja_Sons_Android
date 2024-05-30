package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Attachments(
    val Attachment: String,
    val Datetime: String,
    val PRID: Int,
    val id: Int
) : Parcelable