package com.ahuja.sons.ahujaSonsClasses.model

data class AllOrderListResponseModel(
    val `data`: List<Data>,
    val message: String,
    val meta: Meta,
    val status: Int
){
    data class Data(
        val AddressExtension: AddressExtension,
        val Attach: List<Any>,
        val BPEmail: String,
        val BPLID: String,
        val BPLName: String,
        val BuyBack: String,
        val CancelStatus: String,
        val Caption: String,
        val CardCode: String,
        val CardName: String,
        val Comments: String,
        val ContactPersonCode: List<ContactPersonCode>,
        val CreateDate: String,
        val CreateTime: String,
        val CreatedBy: String,
        val CreationDate: String,
        val DelStatus: String,
        val DepName: String,
        val DiscountPercent: Int,
        val DocCurrency: String,
        val DocDate: String,
        val DocDueDate: String,
        val DocEntry: String,
        val DocNum: String,
        val DocTotal: Double,
        val DocumentLines: List<DocumentLine>,
        val DocumentStatus: String,
        val FinalStatus: String,
        val OrdLevel1: Any,
        val OrdLevel1Status: String,
        val OrdLevel2: Any,
        val OrdLevel2Status: String,
        val OrdLevel3: Any,
        val OrdLevel3Status: String,
        val PRID: String,
        val PayToCode: String,
        val PaymentGroupCode: String,
        val PoAmt: Int,
        val PoDate: String,
        val PoNo: String,
        val SalesPersonCode: List<SalesPersonCode>,
        val ShipToCode: String,
        val ShippingAndHandling: String,
        val ShippingAndHandlingTax: String,
        val SystemName: String,
        val TaxDate: String,
        val TaxPercentage: String,
        val TermsAndConditions: String,
        val U_COMISSIOND: String,
        val U_INSTALL_DATE: String,
        val U_LEADID: Int,
        val U_LEADNM: String,
        val U_MR_NO: String,
        val U_OPPID: String,
        val U_OPPRNM: String,
        val U_ORDRID: Int,
        val U_ORDRNM: String,
        val U_Pay1: Int,
        val U_Pay2: Int,
        val U_Pay3: Int,
        val U_Pay4: Int,
        val U_Pay5: Int,
        val U_QUOTID: Int,
        val U_QUOTNM: String,
        val U_TermDueDate: String,
        val U_TermInterestRate: Int,
        val U_TermPaymentTerm: String,
        val U_Term_Condition: String,
        val U_WARNTY_MONTH: Int,
        val UpdateDate: String,
        val UpdateTime: String,
        val VatSum: String,
        val id: Int
    )


    data class AddressExtension(
        val BillToBlock: String,
        val BillToBuilding: String,
        val BillToBuildingFloorRoom: String,
        val BillToCity: String,
        val BillToContactEmail: String,
        val BillToContactNo: String,
        val BillToContactPersonCode: String,
        val BillToCountry: String,
        val BillToGst: String,
        val BillToState: String,
        val BillToStreet: String,
        val BillToZipCode: String,
        val OrderID: String,
        val ShipToBlock: String,
        val ShipToBuilding: String,
        val ShipToBuildingFloorRoom: String,
        val ShipToCity: String,
        val ShipToCode: String,
        val ShipToContactEmail: String,
        val ShipToContactNo: String,
        val ShipToContactPersonCode: String,
        val ShipToCountry: String,
        val ShipToGst: String,
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
    )


    data class ContactPersonCode(
        val FirstName: String,
        val InternalCode: String
    )

    data class DocumentLine(
        val CategoryName: String,
        val CostingCode2: String,
        val DiscountPercent: Int,
        val EndDate: String,
        val FreeText: String,
        val Frequency: String,
        val IsService: String,
        val ItemCode: String,
        val ItemDescription: String,
        val ItemSerialNo: String,
        val LineNum: Int,
        val MainSystem: String,
        val MeasureUnit: String,
        val OrderID: String,
        val PriceAfterVAT: Double,
        val ProjectCode: String,
        val Quantity: Int,
        val ReferenceItem: String,
        val ReferenceSerial: String,
        val ShipToCode: String,
        val ShipToState: String,
        val StartDate: String,
        val System: String,
        val TaxCode: String,
        val TaxRate: String,
        val U_FGITEM: String,
        val U_REPTYP: String,
        val UnitPrice: Double,
        val id: Int
    )


    data class SalesPersonCode(
        val Active: String,
        val CountryCode: String,
        val Email: String,
        val EmployeeCode: String,
        val EmployeeID: String,
        val FCM: String,
        val Mobile: String,
        val SalesEmployeeCode: String,
        val SalesEmployeeName: String,
        val branch: String,
        val companyID: String,
        val dep: Dep,
        val div: String,
        val firstName: String,
        val id: Int,
        val lastLoginOn: String,
        val lastName: String,
        val level: Int,
        val logedIn: String,
        val middleName: String,
        val password: String,
        val passwordUpdatedOn: String,
        val position: String,
        val reportingTo: String,
        val role: Role,
        val salesUnit: String,
        val subdep: Subdep,
        val timestamp: String,
        val userName: String,
        val zone: String
    )

    data class Dep(
        val Name: String,
        val id: Int
    )

    data class Role(
        val DiscountPercentage: String,
        val Level: Int,
        val Name: String,
        val Subdepartment: Int,
        val id: Int
    )

    data class Subdep(
        val Code: String,
        val Department: String,
        val Name: String,
        val id: Int
    )


    data class Meta(
        val count: Int
    )
}