package com.ahuja.sons.ahujaSonsClasses.model

data class DoctorNameListModel(
    val `data`: ArrayList<Data>,
    val message: String,
    val status: Int
){

    data class Data(
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
}