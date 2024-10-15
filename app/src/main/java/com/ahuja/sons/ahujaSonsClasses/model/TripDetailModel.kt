package com.ahuja.sons.ahujaSonsClasses.model

import com.ahuja.sons.ahujaSonsClasses.model.image_get_model.UploadedPictureModel

data class TripDetailModel(
    var `data`: ArrayList<Data> = ArrayList(),
    var errors: String = "",
    var message: String = "",
    val proof_data: ArrayList<UploadedPictureModel.Data> = ArrayList(),
    var status: Int = 0
) {
    data class Data(
        var StartAt: String = "",
        var EndAt: String = "",
        var StartLocation: String = "",
        var EndLocation: String = "",
        var DepositedBy: DepositeClass? = null,
        var CreateDate: String = "",
        var CreateTime: String = "",
        var UpdateDate: String = "",
        var UpdateTime: String = "",
        val Deliveryassigned: ArrayList<DeliveryassignedClass>,

        )

    data class DeliveryassignedClass(
        val id: Int,
        
        val DeliveryPerson1: String,
        
        val DeliveryPerson2: String,
        
        val DeliveryPerson3: String,
        
//        val OrderID: String,
        
        val DeliveryNote: String,
        
        val VechicleNo: String,
    )

    data class DepositeClass(
        val id: Int,

        val companyID: String,

        val EmployeeCode: String,

        val SalesEmployeeCode: String,

        val SalesEmployeeName: String,

        val EmployeeID: String,
        val userName: String,
        val password: String,
        val firstName: String,
        val middleName: String,
        val lastName: String,

        val Email: String,

        val Mobile: String,

        val CountryCode: String,
        val position: String,
        val branch: String,

        val Active: String,
        val salesUnit: String,
        val passwordUpdatedOn: String,
        val lastLoginOn: String,
        val logedIn: String,
        val reportingTo: String,

        val FCM: String,
        val div: String,
        val level: Long,
        val timestamp: String,
        val zone: String,
        val role: Role,
        val dep: Dep,
        val subdep: Subdep,
    )

    data class Role(
        val id: Int,

        val Name: String,

        val Level: Int,

        val DiscountPercentage: String,

        val Subdepartment: Long,
        val permissions: List<Long>,
    )

    data class Dep(
        val id: Int,

        val Name: String,
    )

    data class Subdep(
        val id: Int,

        val Name: String,

        val Code: String,

        val Department: String,
    )


    data class ProofDaum(
        val id: Int,
        
        val OrderID: String,
        
        val DeliveryNote: String,
        
        val Attachment: String,
        
        val UploadBy: String,
        
        val Status: String,
        
        val CreateDate: String,
        
        val CreateTime: String,
        
        val UpdateDate: String,
        
        val UpdateTime: String,
        
        val is_return: Boolean,
    )

}
