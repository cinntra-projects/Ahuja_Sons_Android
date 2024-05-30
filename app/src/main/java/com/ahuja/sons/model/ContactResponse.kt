package com.ahuja.sons.model


data class ContactResponse (


     var message: String? = "",


     var status: Int? = 0,

     var data: List<ContactEmployee> = emptyList())
