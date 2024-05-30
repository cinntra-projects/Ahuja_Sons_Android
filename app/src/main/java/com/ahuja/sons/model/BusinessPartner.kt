package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BusinessPartner(
    var BPAddresses: List<BPAddresse> = emptyList(),
    var AttachmentEntry: String="",
    var CardCode: String="",
    var CardName: String="",
    var CardType: String="",
    var CommissionPercent: String="",
    var ContactPerson: String="",
    var CreateDate: String="",
    var CreateTime: String="",
    var CreditLimit: String="",
    var Currency: String="",
    var DiscountPercent: String="",
    var EmailAddress: String="",
    var Industry: String="",
    var IntrestRatePercent: String="",
    var Notes: String="",
    var PayTermsGrpCode: String="",
    var Phone1: String="",
    var SalesPersonCode: String="",
    var U_ACCNT: String="",
    var U_ANLRVN: String="",
    var U_BPGRP: String="",
    var U_CONTOWNR: String="",
    var U_CURBAL: String="",
    var U_INVNO: String="",
    var U_LAT: String="",
    var U_LONG: String="",
    var U_PARENTACC: String="",
    var U_RATING: String="",
    var U_TYPE: String="",
    var UpdateDate: String="",
    var UpdateTime: String="",
    var Website: String="",
    var id: Int=0,
    var BPEmployee : List<BPEmployee>  = emptyList()
) : Parcelable {
}