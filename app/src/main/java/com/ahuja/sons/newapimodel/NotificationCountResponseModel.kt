package com.ahuja.sons.newapimodel

class NotificationCountResponseModel(
    val message: String,
    val status: Int,
    val data: List<Daum>,
) {
    data class Daum(
        val notification: Int,
        val amount: Int,
        val sale: Int,
        
        val sale_diff: Int,
        
        val Opportunity: Int,
        
        val Quotation: Int,
        
        val Order: Int,
        
        val Invoice: Int,
        
        val Tender: Int,
        
        val Customer: Int,
        
        val Leads: Int,
        
        val Leads_Product: Int,
        
        val Leads_Project: Int,
        
        val Over: Int,
        
        val Open: Int,
        
        val Close: Int,
        
        val CampaignSet: Int,
    )
}