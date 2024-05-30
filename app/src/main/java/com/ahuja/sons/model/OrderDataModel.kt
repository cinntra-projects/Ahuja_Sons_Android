package com.ahuja.sons.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class OrderDataModel(
    val AddressExtension: AddressExtension,
    val CardCode: String,
    val CardName: String,
    val Comments: String,
    val ContactPersonCode: List<ContactPersonCode>,
    val CreateDate: String,
    val CreateTime: String,
    val CreationDate: String,
    val DiscountPercent: String,
    val DocCurrency: String,
    val DocDate: String,
    val DocDueDate: String,
    val DocEntry: String,
    val DocTotal: String,
    val DocumentLines: List<DocumentLine>,
    val DocumentStatus: String,
    val InvoiceID: String,
    val SalesPersonCode: List<SalesPersonCodeX>,
    val TaxDate: String,
    val UpdateDate: String,
    val UpdateTime: String,
    val VatSum: String,
    val id: Int
):Parcelable