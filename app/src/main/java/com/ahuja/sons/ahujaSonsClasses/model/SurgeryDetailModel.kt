package com.ahuja.sons.ahujaSonsClasses.model

data class SurgeryDetailModel(
    val `data`: ArrayList<Data> = arrayListOf(),
    val errors: String = "",
    val message: String = """Success""",
    val status: Int = 200
) {
    data class Data(
        val CreateDate: String = """2024-09-23""",
        val CreateTime: String = """13:19:45""",
        val CreatedBy: String = """14""",
        val OrderID: String = """35""",
        val Order_Detail: ArrayList<OrderDetail> = arrayListOf(),
        val SurgeryPersonCode: String = "",
        val SurgeryPersonsName: String = """abcd""",
        val UpdateDate: String = """2024-09-23""",
        val UpdateTime: String = """13:19:45""",
        val id: Int = 7
    ) {
        data class OrderDetail(
            val CancelStatus: String = """csNo""",
            val CardCode: String = """C0000133""",
            val CardName: String = """ALL INDIA INSTITUTE OF MEDICAL SCIENCE""",
            val CreateDate: String = """2024-09-23""",
            val CreateTime: String = """11:04:58 am""",
            val Doctor: Int = 850,
            val Employee: Int = 8,
            val NoOfCSRRequired: String = """1""",
            val OrderInformation: String = """B/L Attune""",
            val Remarks: String = "",
            val ReturnTypeStatus: String = "",
            val SapOrderId: String = """9924""",
            val Status: String = """Assigned""",
            val SurgeryDate: String = """2024-09-24""",
            val SurgeryName: String = """B/L Attune""",
            val SurgeryTime: String = """11:00 am""",
            val UpdateDate: String = """2024-09-23""",
            val UpdateTime: String = """11:04:58 am""",
            val id: Int = 35,
            val isDepProofUp: Boolean = false,
            val isOrderPrepared: Boolean = true,
            val isSurgeryEnd: Boolean = false,
            val isSurgeryProofUp: Boolean = false,
            val isSurgeryStarted: Boolean = false,
            val isTripEnd: Boolean = true,
            val isTripStarted: Boolean = true
        )
    }
}