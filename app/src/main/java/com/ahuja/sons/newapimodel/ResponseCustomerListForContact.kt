package com.ahuja.sons.newapimodel

data class ResponseCustomerListForContact(
    val `data`: ArrayList<DataCustomerListForContact>,
    val message: String,
    val status: Int
)