package com.ahuja.sons.newapimodel

data class ResponseManTrapLog(
    val `data`: MutableList<DataManTrapLogo>,
    val message: String,
    val status: Int
)