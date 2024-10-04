package com.ahuja.sons.ahujaSonsClasses.model

data class DeliveryPersonEmployeeModel(
    val `data`: ArrayList<Data> = arrayListOf(),
    val message: String = "",
    val status: Int = 200
) {
    data class Data(
        val Active: String = "",
        val CountryCode: String = "",
        val Email: String = "",
        val EmployeeCode: String = "",
        val EmployeeID: String = "",
        val FCM: String = "",
        val Mobile: String = "",
        val SalesEmployeeCode: String = "",
        val SalesEmployeeName: String = "",
        val branch: String = "",
        val companyID: String = "",
        val dep: Dep = Dep(),
        val div: String = "",
        val firstName: String = "",
        val id: Int = 0,
        val lastLoginOn: String = "",
        val lastName: String = "",
        val level: Int = 0,
        val logedIn: String = "",
        val middleName: String = "",
        val password: String = "",
        val passwordUpdatedOn: String = "",
        val position: String = "",
        val reportingTo: String = "",
        val role: Role = Role(),
        val salesUnit: String = "",
        val subdep: Subdep = Subdep(),
        val timestamp: String = "",
        val userName: String = "",
        val zone: String = ""
    ) {

        data class Dep(
            val Name: String = "",
            val id: Int = 0
        )

        data class Role(
            val DiscountPercentage: String = "",
            val Level: Int = 0,
            val Name: String = "",
            val Subdepartment: Int = 0,
            val id: Int = 0,
            val permissions: ArrayList<Int> = arrayListOf()
        )

        data class Subdep(
            val Code: String = "",
            val Department: String = "",
            val Name: String = "",
            val id: Int = 0
        )

    }

}