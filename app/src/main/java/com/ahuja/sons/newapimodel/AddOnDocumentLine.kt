package com.ahuja.sons.newapimodel

data class AddOnDocumentLine(
    val ItemCode: String,
    val ItemDescription: String,
    val LineNum: Int,
    val OrderID: String,
    val ParentItemCode: String,
    val Quantity: Int,
    val UnitPrice: Int,
    val id: Int
)