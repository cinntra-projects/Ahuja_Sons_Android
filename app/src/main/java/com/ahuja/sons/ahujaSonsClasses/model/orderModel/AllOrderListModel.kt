package com.ahuja.sons.ahujaSonsClasses.model.orderModel

data class AllOrderListModel(
    val `data`: ArrayList<Data>,
    val message: String,
    val meta: Meta,
    val status: Int
){


    data class Meta(
        val count: Int
    )

    data class Data(
        val CancelStatus: String = "",
        val CardCode: String = "",
        val CardName: String = "",
        val CreateDate: String = "",
        val CreateTime: String = "",
        val Doctor: List<Doctor> = ArrayList(),
        val DocumentLines: List<List<DocumentLine>> = ArrayList(),
        val Employee: List<Employee> = ArrayList(),
        val NoOfCSRRequired: String = "",
        val OrderInformation: String = "",
        val Remarks: String = "",
        val SapOrderId: String = "",
        val Stages: List<List<Stage>> = ArrayList(),
        val Status: String = "",
        val SurgeryDate: String = "",
        val SurgeryName: String = "",
        val SurgeryTime: String = "",
        val UpdateDate: String = "",
        val UpdateTime: String = "",
        val id: Int = 0
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
}