package com.ahuja.sons.newapimodel

data class SolutionRequestModel(
    val fields: ArrayList<String>,
    val filter: FilterX
){

    data class FilterX(
        val IssueCategory: Int
    )
}