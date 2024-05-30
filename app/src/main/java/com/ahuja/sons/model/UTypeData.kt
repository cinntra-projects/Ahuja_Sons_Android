package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UTypeData (
    var id : Int = 0,
    var type : String =""
        ) : Parcelable {

}
