package com.ahuja.sons.ahujaSonsClasses.model.workQueue

data class AllWorkQueueResponseModel(
    val `data`: ArrayList<Data> = ArrayList(),
    val message: String = "",
    val errors: String = "",
    val is_return: Boolean = false,
    val meta: Meta? = null,
    val status: Int = 0
){
    data class Meta(
        val count: Int
    )

    data class Data(
        val AssignedToRole: AssignedToRole? = null,
        val CompletedBy: String = "",
        val is_return: Boolean = false,
        val is_return_to_office: Boolean = false,
        val CreateDate: String = "",
        val CreateTime: String = "",
        val DeliveryId: String = "",
        val OrderRequest: OrderRequest? = null,
        val TaskStatus: String  = "",
        val UpdateDate: String = "",
        val UpdateTime: String = "",
        val id: String = "",
        val DeliveryNote: ArrayList<DeliveryNote> = ArrayList(),
        var BuyBack: String = "",
        var CancelStatus: String = "",
        var CardCode: String = "",
        var CardName: String = "",
        var Comments: String = "",
        var ContactPersonCode: String = "",
        var CreationDate: String = "",
        var DeliveryStatus: String = "",
        var isInspectionProofUpload: Boolean = false,
        var isReturnInspectionProofUpload: Boolean = false,
        var DiscountPercent: String = "",
        var DocCurrency: String = "",
        var DocDate: String = "",
        var DocDueDate: String = "",
        var DocEntry: String = "",
        var DocNum: String = "",
        var DocTotal: Double = 0.0,
        var DocumentStatus: String = "",
        var OrderID: String = "",
        var PayToCode: String = "",
        var Printed: String = "",
        var SalesPersonCode: String = "",
        var ShipToCode: String = "",
        var ShippingAndHandling: String = "",
        var ShippingAndHandlingTax: String = "",
        var TaxDate: String = "",
        var TransporterID: String = "",
        var TransporterName: String = "",
        var U_COURIERNAME: String = "",
        var U_DISPDATE: String = "",
        var U_DOCDT: String = "",
        var U_DOCKETNO: String = "",
        var U_MATDISPDT: String = "",
        var U_MR_NO: String = "",
        var U_RCPTDATE: String = "",
        var U_TMODE: String = "",
        var U_VNO: String = "",
        var U_VTYPE: String = "",
        var U_VehicleNo: String = "",
        var VatSum: String = "",
        var DeliveryAssigned: String = "",
        var InspectedDeliverys: ArrayList<InspectedDelivery> = arrayListOf(),
    )

    data class InspectedDelivery(
        var BuyBack: String = "",
        var CancelStatus: String = "",
        var CardCode: String = "",
        var CardName: String = "",
        var Comments: String = "",
        var ContactPersonCode: String = "",
        var CreateDate: String = "",
        var CreateTime: String = "",
        var CreationDate: String = "",
        var DeliveryStatus: String = "",
        var DiscountPercent: String = "",
        var DocCurrency: String = "",
        var DocDate: String = "",
        var DocDueDate: String = "",
        var DocEntry: String = "",
        var DocNum: String = "",
        var DocTotal: Double = 0.0,
        var DocumentStatus: String = "",
        var OrderID: String = "",
        var PayToCode: String = "",
        var Printed: String = "",
        var SalesPersonCode: String = "",
        var ShipToCode: String = "",
        var ShippingAndHandling: String = "",
        var ShippingAndHandlingTax: String = "",
        var TaxDate: String = "",
        var TransporterID: String = "",
        var TransporterName: String = "",
        var U_COURIERNAME: String = "",
        var U_DISPDATE: String = "",
        var U_DOCDT: String = "",
        var U_DOCKETNO: String = "",
        var U_MATDISPDT: String = "",
        var U_MR_NO: String = "",
        var U_RCPTDATE: String = "",
        var U_TMODE: String = "",
        var U_VNO: String = "",
        var U_VTYPE: String = "",
        var U_VehicleNo: String = "",
        var UpdateDate: String = "",
        var UpdateTime: String = "",
        var VatSum: String = "",
        var id: Int = 0,
        var isSelected: Boolean,
    )

    data class DeliveryNote(
        val id: Int,
       
        val TaxDate: String,
       
        val DocDueDate: String,
       
        val DocNum: String,
       
        val ContactPersonCode: String,
       
        val DiscountPercent: String,
       
        val DocDate: String,
       
        val CardCode: String,
       
        val Comments: String,
       
        val SalesPersonCode: String,
       
        val DocumentStatus: String,
       
        val CancelStatus: String,
       
        val Printed: String,
       
        val DocCurrency: String,
       
        val DocTotal: Double,
       
        val CardName: String,
       
        val VatSum: String,
       
        val CreationDate: String,
       
        val DocEntry: String,
       
        val OrderID: String,
       
        val U_DISPDATE: String,
       
        val U_MR_NO: String,
       
        val U_VehicleNo: String,
       
        val DeliveryStatus: String,
       
        val CreateDate: String,
       
        val CreateTime: String,
       
        val UpdateDate: String,
       
        val UpdateTime: String,
       
        val PayToCode: String,
       
        val ShipToCode: String
    )
    
    
    data class AssignedToRole(
        val DiscountPercentage: String,
        val Level: Int,
        val Name: String,
        val Subdepartment: Subdepartment,
        val id: Int,
        val permissions: List<Int>
    )

    data class Subdepartment(
        val Code: String,
        val Department: String,
        val Name: String,
        val id: Int
    )


    data class OrderRequest(

        var isTripStarted: Boolean = false,
        var isTripEnd: Boolean = false,
        var isDepProofUp: Boolean = false,

        var isReturnTripStarted: Boolean = false,
        var isReturnTripEnd : Boolean = false,
        var isReturnDepProofUp: Boolean = false,

        val CancelStatus: String,
        val CardCode: String,
        val CardName: String,
        val CreateDate: String,
        val CreateTime: String,
        val Doctor: List<Doctor>,
        val DocumentLines: ArrayList<ArrayList<DocumentLine>> = ArrayList(),
        val Employee: List<Employee>,
        val NoOfCSRRequired: String,
        val OrderInformation: String,
        val Remarks: String,
        val SapOrderId: String,
        val Stages: List<List<Stage>>,
        val Status: String,
        val SurgeryDate: String,
        val SurgeryName: String,
        val PreparedBy: String,
        val InspectedBy: String,
        val SurgeryTime: String,
        val UpdateDate: String,
        val UpdateTime: String,
        val id: Int,
        val isOrderPrepared: Boolean,

    )

    data class Doctor(
        val C_Block: String,
        val C_BuildingFloorRoom: String,
        val C_Country: String,
        val C_State: String,
        val C_StreetPOBox: String,
        val C_Zipcode: String,
        val CreateDate: String,
        val CreateTime: String,
        val DoctorCode: String,
        val DoctorFirstName: String,
        val DoctorLastName: String,
        val DoctorName: String,
        val EmailID: String,
        val H_Block: String,
        val H_BuildingFloorRoom: String,
        val H_Country: String,
        val H_State: String,
        val H_StreetPOBox: String,
        val H_Zipcode: String,
        val MobileNo1: String,
        val MobileNo2: String,
        val UpdateDate: String,
        val UpdateTime: String,
        val id: Int,
        val isActive: Boolean
    )


    data class DocumentLine(
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
        val OrderRequestID: Int,
        val PriceAfterVAT: Double,
        val ProjectCode: String,
        val Quantity: Int,
        val ReferenceItem: String,
        val ReferenceSerial: String,
        val SapOrderID: String,
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

    data class Employee(
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


    data class Role(
        val DiscountPercentage: String,
        val Level: Int,
        val Name: String,
        val Subdepartment: Int,
        val id: Int,
        val permissions: List<Int>
    )

    data class Stage(
        val Comment: String,
        val CreateDate: String,
        val CreateTime: String,
        val File: String,
        val Name: String,
        val OrderId: String,
        val PaymentPercentage: Int,
        val SequenceNo: String,
        val Stageno: Int,
        val Status: Int,
        val UpdateDate: String,
        val UpdateTime: String,
        val id: Int
    )


    data class Dep(
        val Name: String,
        val id: Int
    )


    data class Subdep(
        val Code: String,
        val Department: String,
        val Name: String,
        val id: Int
    )

}