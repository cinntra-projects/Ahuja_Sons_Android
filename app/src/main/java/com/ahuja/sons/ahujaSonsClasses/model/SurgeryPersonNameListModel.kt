package com.ahuja.sons.ahujaSonsClasses.model

data class SurgeryPersonNameListModel(
    val `data`: ArrayList<Data> = arrayListOf(),
    val errors: String = "",
    val message: String = "",
    val status: Int = 200
) {
    data class Data(
        val CreateDate: String = """2024-09-21""",
        val CreateTime: String = """14:45:29""",
        val CreatedBy: String = """14""",
        val OrderID: String = """139""",
        val SurgeryPersonCode: String = "",
        var SurgeryPersonsName: String = "",
        val UpdateDate: String = """2024-09-21""",
        val UpdateTime: String = """14:45:29""",
        val id: Int = 0,
        val NoOfCSRRequired: String = "",
        val StartAt: String = "",
        val EndAt: String = "",
        var isSurgeryStarted: Boolean = false,
        var isSurgeryEnd: Boolean = false,
        var isSurgeryProofUp: Boolean = false
    )
}