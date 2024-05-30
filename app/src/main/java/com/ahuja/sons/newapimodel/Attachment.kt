package com.ahuja.sons.newapimodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Attachment(


    @SerializedName("id"         ) var id         : Int?    = null,
    @SerializedName("File"       ) var File       : String? = null,
    @SerializedName("LinkType"   ) var LinkType   : String? = null,
    @SerializedName("Caption"    ) var Caption    : String? = null,
    @SerializedName("LinkID"     ) var LinkID     : Int?    = null,
    @SerializedName("CreateDate" ) var CreateDate : String? = null,
    @SerializedName("CreateTime" ) var CreateTime : String? = null,
    @SerializedName("UpdateDate" ) var UpdateDate : String? = null,
    @SerializedName("UpdateTime" ) var UpdateTime : String? = null
):Parcelable
