package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class DeliveryID(
    val CardCode: String,
    val CardName: String,
    val Comments: String,
    val ContactPersonCode: String,
    val CreateDate: String,
    val CreateTime: String,
    val CreationDate: String,
    val DiscountPercent: String,
    val DocCurrency: String,
    val DocDate: String,
    val DocDueDate: String,
    val DocEntry: String,
    val DocTotal: String,
    val DocumentStatus: String,
    val InvoiceID: String,
    val SalesPersonCode: String,
    val TaxDate: String,
    val UpdateDate: String,
    val UpdateTime: String,
    val VatSum: String,
    val id: Int
) : Parcelable {
    constructor() : this("","","","","","","","","","","","","","","","","","","","",0)
}



