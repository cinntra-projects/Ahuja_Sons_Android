package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BusinessPartnerDetails(
    val AttachmentEntry: String,
    val BPAddresses: List<BPAddresse>,
    val BPLID: List<BPLIDX>,
    val CardCode: String,
    val CardName: String,
    val CardType: String,
    val CommissionPercent: String,
    val ContactPerson: String,
    val CreateDate: String,
    val CreateTime: String,
    val CreditLimit: String,
    val Currency: String,
    val DiscountPercent: String,
    val EmailAddress: String,
    val Industry: String,
    val IntrestRatePercent: String,
    val Notes: String,
    val PayTermsGrpCode: String,
    val Phone1: String,
    val SalesPersonCode: String,
    val U_ACCNT: String,
    val U_ANLRVN: String,
    val U_BPGRP: String,
    val U_CONTOWNR: String,
    val U_CURBAL: String,
    val U_INVNO: String,
    val U_LAT: String,
    val U_LEADID: Int,
    val U_LEADNM: String,
    val U_LONG: String,
    val U_PARENTACC: String,
    val U_RATING: String,
    val U_TYPE: String,
    val UpdateDate: String,
    val UpdateTime: String,
    val Website: String,
    val Zone: String,
    val id: Int
) : Parcelable