package com.ahuja.sons.model

import com.google.gson.annotations.SerializedName

data class MapData(


    @SerializedName("Lat")
    var Lat: String? = null,
    @SerializedName("Long")
    var Long: String? = null,

    @SerializedName("Emp_Id")
    var Emp_Id: String? = null,

    @SerializedName("Emp_Name")
    var Emp_Name: String? = null,

    @SerializedName("UpdateDate")
    var UpdateDate: String? = null,

    @SerializedName("UpdateTime")
    var UpdateTime: String? = null,

    @SerializedName("Address")
    var Address: String? = null,

    @SerializedName("type")
    var type: String? = null,

    @SerializedName("shape")
    var shape: String? = null,

    @SerializedName("remark")
    var remark: String? = null,

    @SerializedName("ResourceId")
    var ResourceId: String? = null,

    @SerializedName("SourceType")
    var SourceType: String? = null,

    @SerializedName("ContactPerson")
    var ContactPerson: String? = null,

    @SerializedName("ExpenseCost")
    var ExpenseCost: String? = null,

    @SerializedName("ExpenseDistance")
    var ExpenseDistance: String? = null,

    @SerializedName("ExpenseType")
    var ExpenseType: String? = null,

    @SerializedName("ExpenseAttach")
    var ExpenseAttach: String? = null,

    @SerializedName("ExpenseRemark")
    var ExpenseRemark: String? = null,

    @SerializedName("Attach")
    var Attach: String? = null,






)
