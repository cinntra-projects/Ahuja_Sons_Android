package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BPAddresse(
    val AddressName: String = "",
    val AddressName2: String= "",
    val AddressName3: String= "",
    val AddressType: String= "",
    val BPCode: String= "",
    val BPID: String= "",
    val Block: String= "",
    val BranchName: String= "",
    val BuildingFloorRoom: String= "",
    val City: String= "",
    val Country: String= "",
    val County: String= "",
    val CreateDate: String= "",
    val CreateTime: String= "",
    val CreditLimit: String= "",
    val CurrentBalance: String= "",
    val Default: Int= 0,
    val Email: String= "",
    val Fax: String= "",
    val GSTIN: String= "",
    val GstType: String= "",
    val Lat: String= "",
    val Long: String= "",
    val PaymentTerm: String= "",
    val Phone: String= "",
    val RowNum: String= "",
    val ShippingType: String= "",
    val State: String= "",
    val Status: Int =0 ,
    val Street: String= "",
    val TaxOffice: String= "",
    val U_COUNTRY: String= "",
    val U_SHPTYP: String= "",
    val U_STATE: String= "",
    val UpdateDate: String= "",
    val UpdateTime: String= "",
    val ZipCode: String= "",
    val id: Int= 0
) : Parcelable