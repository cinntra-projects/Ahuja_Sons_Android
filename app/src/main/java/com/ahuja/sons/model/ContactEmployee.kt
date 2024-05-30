package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactEmployee(
    var CardCode: String = "",
    var E_Mail: String = "",
    var FirstName: String = "",
    var InternalCode: String = "",
    var MobilePhone: String = "",
    var Name: String = "",
    var Position: String = "",
    var id: Int = 0
) : Parcelable