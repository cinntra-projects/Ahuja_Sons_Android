package com.ahuja.sons.newapimodel

data class BranchAllListResponseModel(
    val `data`: ArrayList<DataXXX> ,
    val message: String,
    val status: Int
){
    data class DataXXX(
        val AddressName: String,
        val AddressName2: String,
        val AddressName3: String,
        val AddressType: String,
        val BPCode: String,
        val zone: String,
        val BPID: String,
        val Block: String,
        val BranchName: String,
        val BranchType: String,
        val BuildingFloorRoom: String,
        val City: String,
        val Country: String,
        val CountryCode: String,
        val County: String,
        val CreateDate: String,
        val CreateTime: String,
        val CreditLimit: String,
        val CurrentBalance: String,
        val Default: Int,
        val Email: String,
        val Fax: String,
        val GSTIN: String,
        val GstType: String,
        val LandLine: String,
        val Lat: String,
        val Long: String,
        val PaymentTerm: String,
        val Phone: String,
        val RowNum: String,
        val ShipToRemark: String,
        val ShippingType: String,
        val State: String,
        val Status: Int,
        val Street: String,
        val TaxOffice: String,
        val U_COUNTRY: String,
        val U_SHPTYP: String,
        val U_STATE: String,
        val UpdateDate: String,
        val UpdateTime: String,
        val ZipCode: String,
        val id: String
    )
}