package com.ahuja.sons.ahujaSonsClasses.model

data class RouteListModel(
    val `data`: ArrayList<Data> = arrayListOf(),
    val errors: String = "",
    val message: String = "",
    val status: Int = 0
) {

    data class Data(
        val CreateDate: String = "",
        val CreateTime: String = "",
        val CreatedBy: CreatedBys = CreatedBys(),
        val DeliveryID: ArrayList<DeliveryIDs> = arrayListOf(),
        val DeliveryNote: String = """1""",
        val DeliveryPerson1: String = "",
        val DeliveryPerson2: String = """SANJAY KUMAR""",
        val DeliveryPerson3: String = """SANJAY KUMAR""",
        val DeliveryPerson1_detail: String = """SANJAY KUMAR""",
        val DeliveryPerson2_detail: String = """SANJAY KUMAR""",
        val DeliveryPerson3_detail: String = """SANJAY KUMAR""",
        val OrderID: ArrayList<OrderIDs> = arrayListOf(),
        val UpdateDate: String = """2024-09-11""",
        val UpdateTime: String = """16:23:13""",
        val VechicleNo: String = """HR16AB2134""",
        val id: Int = 1
    ) {
        data class CreatedBys(
            val Active: String = """tYES""",
            val CountryCode: String = """+91""",
            val Email: String = """deepsharmadelhi@gmail.com""",
            val EmployeeCode: String = """285""",
            val EmployeeID: String = """285""",
            val FCM: String = """edwh7r6DTweslOQWYXGkRV:APA91bGA_P7GH3WUSFn0ZZJPaugwhpdBV7qzXr65HcDFr7LtjF4aS2bgo8gcakZaOunnQev6c_FJQ2c3e_Lgkyo7CvP2tvTCXOUOJkTPvVbeSWp7wBzIDkoMKBdYqFQAs1io8sZAukDI""",
            val Mobile: String = """9213759395""",
            val SalesEmployeeCode: String = """10""",
            val SalesEmployeeName: String = """DEEPAK SHARMA""",
            val branch: String = """1""",
            val companyID: String = "",
            val dep: Dep = Dep(),
            val div: String = """108,109""",
            val firstName: String = """DEEPAK""",
            val id: Int = 10,
            val lastLoginOn: String = "",
            val lastName: String = """SHARMA""",
            val level: Int = 1,
            val logedIn: String = "",
            val middleName: String = """None""",
            val password: String = """123""",
            val passwordUpdatedOn: String = "",
            val position: String = """Key Account Manager""",
            val reportingTo: String = """2""",
            val role: Role = Role(),
            val salesUnit: String = """APD,LED""",
            val subdep: Subdep = Subdep(),
            val timestamp: String = """2022-04-11 19:45:15""",
            val userName: String = """DEEPAK""",
            val zone: String = """North"""
        ) {
            data class Dep(
                val Name: String = """Management""",
                val id: Int = 1
            )

            data class Role(
                val DiscountPercentage: String = """10""",
                val Level: Int = 1,
                val Name: String = """Delivery Coordinator""",
                val Subdepartment: Int = 1,
                val id: Int = 6,
                val permissions: List<Int> = listOf()
            )

            data class Subdep(
                val Code: String = """1""",
                val Department: String = """1""",
                val Name: String = """Sales - Institutional""",
                val id: Int = 1
            )
        }

        data class DeliveryIDs(
            val BuyBack: String = "",
            val CancelStatus: String = """tNo""",
            val CardCode: String = """C0000574""",
            val CardName: String = """DIYOS HOSPITAL""",
            val Comments: String = """Based On Sales Orders 20.""",
            val ContactPersonCode: String = """1066""",
            val CreateDate: String = """2024-09-11""",
            val CreateTime: String = "",
            val CreationDate: String = """2024-09-11""",
            val DeliveryStatus: String = """Inspected""",
            val DepositedID: String = "",
            val DiscountPercent: String = """0.0""",
            val DocCurrency: String = """INR""",
            val DocDate: String = """2024-09-11""",
            val DocDueDate: String = """2024-09-11""",
            val DocEntry: String = """20320""",
            val DocNum: String = """21""",
            val DocTotal: Double = 182965.02,
            val DocumentSerialNo: String = "",
            val DocumentStatus: String = """O""",
            val OrderID: String = """2""",
            val PayToCode: String = """NEW DELHI""",
            val Printed: String = "",
            val SalesPersonCode: String = """-1""",
            val ShipToCode: String = """NEW DELHI""",
            val ShippingAndHandling: String = "",
            val ShippingAndHandlingTax: String = "",
            val TaxDate: String = """2024-09-11""",
            val TransporterID: String = "",
            val TransporterName: String = "",
            val U_COURIERNAME: String = "",
            val U_DISPDATE: String = "",
            val U_DOCDT: String = "",
            val U_DOCKETNO: String = "",
            val U_MATDISPDT: String = "",
            val U_MR_NO: String = "",
            val U_RCPTDATE: String = "",
            val U_TMODE: String = "",
            val U_VNO: String = "",
            val U_VTYPE: String = "",
            val U_VehicleNo: String = "",
            val UpdateDate: String = "",
            val UpdateTime: String = """15:22:02""",
            val VatSum: String = """8712.62""",
            val id: Int = 1
        )

        data class OrderIDs(
            val CancelStatus: String = """csNo""",
            val CardCode: String = """C0000574""",
            val CardName: String = """DIYOS HOSPITAL""",
            val CreateDate: String = """2024-09-11""",
            val CreateTime: String = """02:56:21 pm""",
            val Doctor: ArrayList<Doctors> = arrayListOf(),
            val Employee: ArrayList<Employees> = arrayListOf(),
            val NoOfCSRRequired: String = """1""",
            val OrderInformation: String = """DISPOSABLE B/L TKR""",
            val Remarks: String = "",
            val SapOrderId: String = """9863""",
            val Stages: ArrayList<List<Stage>> = arrayListOf(),
            val Status: String = "",
            val SurgeryDate: String = """2024-09-12""",
            val SurgeryName: String = """DISPOSABLE B/L TKR""",
            val SurgeryTime: String = """09:00 am""",
            val UpdateDate: String = """2024-09-11""",
            val UpdateTime: String = """02:56:21 pm""",
            val id: Int = 2,
            val isDepProofUp: Boolean = true,
            val isOrderPrepared: Boolean = true,
            val isTripEnd: Boolean = true,
            val isTripStarted: Boolean = true

        ) {

            data class Doctors(
                val C_Block: String = "",
                val C_BuildingFloorRoom: String = "",
                val C_Country: String = "",
                val C_State: String = "",
                val C_StreetPOBox: String = "",
                val C_Zipcode: String = "",
                val CreateDate: String = """2024-05-10""",
                val CreateTime: String = """11:49:31""",
                val DoctorCode: String = """DR0879""",
                val DoctorFirstName: String = """SAURABH""",
                val DoctorLastName: String = """RAWALL""",
                val DoctorName: String = """SAURABH RAWALL""",
                val EmailID: String = "",
                val H_Block: String = "",
                val H_BuildingFloorRoom: String = "",
                val H_Country: String = "",
                val H_State: String = "",
                val H_StreetPOBox: String = "",
                val H_Zipcode: String = "",
                val MobileNo1: String = "",
                val MobileNo2: String = "",
                val UpdateDate: String = """2024-05-10""",
                val UpdateTime: String = """11:49:31""",
                val id: Int = 866,
                val isActive: Boolean = true
            )

            data class Employees(
                val Active: String = """tYES""",
                val CountryCode: String = """+91""",
                val Email: String = """sureshdiwakar55@gmail.com""",
                val EmployeeCode: String = """258""",
                val EmployeeID: String = """258""",
                val FCM: String = "",
                val Mobile: String = """9582215892""",
                val SalesEmployeeCode: String = """8""",
                val SalesEmployeeName: String = """SURESH DIWAKAR""",
                val branch: String = """1""",
                val companyID: String = "",
                val dep: Dep = Dep(),
                val div: String = """108,109""",
                val firstName: String = """SURESH""",
                val id: Int = 8,
                val lastLoginOn: String = "",
                val lastName: String = """DIWAKAR""",
                val level: Int = 1,
                val logedIn: String = "",
                val middleName: String = """None""",
                val password: String = """123""",
                val passwordUpdatedOn: String = "",
                val position: String = """Key Account Manager""",
                val reportingTo: String = """2""",
                val role: Role = Role(),
                val salesUnit: String = """APD,LED""",
                val subdep: Subdep = Subdep(),
                val timestamp: String = """2022-04-11 19:45:11""",
                val userName: String = """SURESH""",
                val zone: String = """North"""
            ) {

                data class Dep(
                    val Name: String = """Management""",
                    val id: Int = 1
                )

                data class Role(
                    val DiscountPercentage: String = """10""",
                    val Level: Int = 1,
                    val Name: String = """Sales Person""",
                    val Subdepartment: Int = 1,
                    val id: Int = 2,
                    val permissions: List<Int> = listOf()
                )

                data class Subdep(
                    val Code: String = """1""",
                    val Department: String = """1""",
                    val Name: String = """Sales - Institutional""",
                    val id: Int = 1
                )
            }

            data class Stage(
                val Comment: String = "",
                val CreateDate: String = """2024-09-11""",
                val CreateTime: String = """13:05:00""",
                val File: String = "",
                val Name: String = """Info Received""",
                val OrderId: String = """2""",
                val PaymentPercentage: Double = 0.0,
                val SequenceNo: String = """1.0""",
                val Stageno: Double = 1.0,
                val Status: Int = 1,
                val UpdateDate: String = """2024-09-11""",
                val UpdateTime: String = """13:05:00""",
                val id: Int = 9
            )
        }
    }
}