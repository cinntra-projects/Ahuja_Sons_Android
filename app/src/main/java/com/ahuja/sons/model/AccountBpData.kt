package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AccountBpData(
    var AttachmentEntry: String = "",
    var BPAddresses: List<BPAddresse> = emptyList(),
    var CardCode: String = "",
    var CardName: String = "",
    var CardType: String = "",
    var CommissionPercent: String = "",
    var ContactEmployees: List<ContactEmployee> = emptyList(),
    var ContactPerson: String = "",
    var CreateDate: String = "",
    var CreateTime: String = "",
    var CreditLimit: String = "",
    var Currency: String = "",
    var DiscountPercent: String = "",
    var EmailAddress: String = "",
    var Industry: String = "",
    var IntrestRatePercent: String = "",
    var Notes: String = "",
    var PayTermsGrpCode: String = "",
    var SalesPersonCode: String = "",
    val PayTermsGrpDetails: ArrayList<PayTermsGrpCodes> = ArrayList(),
    var Phone1: String = "",
    val SalesPersonDetails: ArrayList<SalesPersonCodes> = ArrayList(),
    var U_ACCNT: String = "",
    var U_ANLRVN: String = "",
    var U_BPGRP: String = "",
    var U_CONTOWNR: String = "",
    var U_CURBAL: String = "",
    var U_INVNO: String = "",
    var U_LAT: String = "",
    var U_LONG: String = "",
    var U_PARENTACC: String = "",
    var U_RATING: String = "",
    var U_TYPE: String = "",
    var U_Landline: String = "",
    var UpdateDate: String = "",
    var UpdateTime: String = "",
    var Website: String = "",
    var zone: String = "",
    var id: Int = 0
) : Parcelable {
    @Parcelize
    data class PayTermsGrpCodes(
        val id: Int,
        val GroupNumber: String,
        val PaymentTermsGroupName: String,
    ) : Parcelable

    @Parcelize
    data class SalesPersonCodes(
        val id: Int,

        val companyID: String,

        val SalesEmployeeCode: String,

        val SalesEmployeeName: String,

        val EmployeeID: String,
        val userName: String,
        val password: String,
        val firstName: String,
        val middleName: String,
        val lastName: String,

        val Email: String,

        val Mobile: String,

        val CountryCode: String,
        val role: String,
        val position: String,
        val branch: String,

        val Active: String,
        val salesUnit: String,
        val passwordUpdatedOn: String,
        val lastLoginOn: String,
        val logedIn: String,
        val reportingTo: String,

        val FCM: String,
        val div: String,
        val timestamp: String,
        val level: Long,
        val zone: String,
        val dep: Long,
        val subdep: Long,
    ) : Parcelable
}