package com.ahuja.sons.model

class ScopOfWorkResponseModel (
    val message: String,
    val status: Int,
    val data: List<Daum>,
){
    data class Daum(
        val id: String,
        
        val Type: String,
        
        val Stageno: String,
        
        val PriotiyId: String,
        
        val CreateAt: String,
        
        val UpdateAt: String,
    )
}