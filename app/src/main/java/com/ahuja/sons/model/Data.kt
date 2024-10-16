package com.ahuja.sons.model

data class Data(
    val AddressExtension: AddressExtensionX,
    val Attach: List<Any>,
    val BPEmail: String,
    val BPLID: String,
    val CancelStatus: String,
    val Caption: String,
    val CardCode: String,
    val CardName: String,
    val Comments: String,
    val ContactPersonCode: List<ContactPersonCodeX>,
    val CreateDate: String,
    val CreateTime: String,
    val CreatedBy: String,
    val CreationDate: String,
    val DelStatus: String,
    val DepName: String,
    val DiscountPercent: Double,
    val DocCurrency: String,
    val DocDate: String,
    val DocDueDate: String,
    val DocEntry: String,
    val DocTotal: String,
    val DocumentLines: List<DocumentLineX>,
    val DocumentStatus: String,
    val FinalStatus: String,
    val OrdLevel1: String,
    val OrdLevel1Status: String,
    val OrdLevel2: Any,
    val OrdLevel2Status: String,
    val OrdLevel3: Any,
    val OrdLevel3Status: String,
    val PRID: String,
    val PaymentGroupCode: String,
    val PoAmt: String,
    val PoDate: String,
    val PoNo: String,
    val SalesPersonCode: List<SalesPersonCodeXX>,
    val ShippingAndHandling: String,
    val TaxDate: String,
    val TermsAndConditions: String,
    val U_LEADID: Int,
    val U_LEADNM: String,
    val U_OPPID: String,
    val U_OPPRNM: String,
    val U_Pay1: Double,
    val U_Pay2: Double,
    val U_Pay3: Double,
    val U_Pay4: Double,
    val U_Pay5: Double,
    val U_QUOTID: Int,
    val U_QUOTNM: String,
    val U_TermDueDate: String,
    val U_TermInterestRate: Double,
    val U_TermPaymentTerm: String,
    val U_Term_Condition: String,
    val UpdateDate: String,
    val UpdateTime: String,
    val VatSum: String,
    val id: Int
)