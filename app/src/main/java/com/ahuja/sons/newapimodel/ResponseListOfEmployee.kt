package com.ahuja.sons.newapimodel

data class ResponseListOfEmployee(
    val SAP: SAPDataNew,
    val data: List<EmployeeAtLoginData>,
    val message: String,
    val status: Int

)
