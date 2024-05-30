package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class AddressExtension(
    val BillToBuilding: String,
    val BillToCity: String,
    val BillToCountry: String,
    val BillToState: String,
    val BillToStreet: String,
    val BillToZipCode: String,
    //val DeliveryID: String,
    val ShipToBuilding: String,
    val ShipToCity: String,
    val ShipToCountry: String,
    val ShipToState: String,
    val ShipToStreet: String,
    val ShipToZipCode: String,
    val U_BCOUNTRY: String,
    val U_BSTATE: String,
    val U_SCOUNTRY: String,
    val U_SHPTYPB: String,
    val U_SHPTYPS: String,
    val U_SSTATE: String,
    val id: Int
):Parcelable