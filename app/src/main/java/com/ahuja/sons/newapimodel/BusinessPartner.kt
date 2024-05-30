package com.ahuja.sons.newapimodel


data class BusinessPartner(
    val AttachmentEntry: String,
    val BPAddresses: MutableList<BPAddresse>,
    val BPEmployee: List<BPEmployee>,
   // val BPLID: List<Any>,
//    val BPLID: List<Bplid>,
    val CardCode: String,
    val CardName: String,
    val CardType: String,
    val CommissionPercent: String,
    val ContactPerson: String,
    val CountryCode: String,
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
    val U_EMIRATESID: String,
    val U_INVNO: String,
    val U_LAT: String,
    val U_LEADID: String,
    val U_LEADNM: String,
    val U_LONG: String,
    val U_PARENTACC: String,
    val U_RATING: String,
    val U_SOURCE: String,
    val U_TYPE: String,
    val U_VATNUMBER: String,
    val UpdateDate: String,
    val UpdateTime: String,
    val Website: String,
    val Zone: String,
    val EscalationTAT: String,
    val id: String
): java.io.Serializable/*{
    data class Bplid(
        val id: Int,
        @JsonProperty("")
        val BPLId: String,
        @JsonProperty("")
        val BPLName: String,
        @JsonProperty("")
        val Address: String,
        @JsonProperty("")
        val MainBPL: String,
        @JsonProperty("")
        val Disabled: String,
        @JsonProperty("")
        val UserSign2: String,
        @JsonProperty("")
        val UpdateDate: String,
        @JsonProperty("")
        val DflWhs: String,
        @JsonProperty("")
        val TaxIdNum: String,
        @JsonProperty("")
        val StreetNo: String,
        @JsonProperty("")
        val Building: String,
        @JsonProperty("")
        val ZipCode: String,
        @JsonProperty("City")
        val city: String,
        @JsonProperty("State")
        val state: String,
        @JsonProperty("Country")
        val country: String,
    ):Serializable
}*/