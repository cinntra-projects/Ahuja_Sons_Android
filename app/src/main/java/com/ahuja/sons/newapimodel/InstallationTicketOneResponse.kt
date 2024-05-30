package com.ahuja.sons.newapimodel

import com.ahuja.sons.adapter.ticketItemAdapter.SparePart

data class InstallationTicketOneResponse(
    val `data`: List<DataXXX>,
    val message: String = "",
    val status: Int = 0
){
    data class DataXXX(
        val ColdWater: String = "",
        val CustomerDesignation: String = "",
        val CustomerName: String = "",
        val CustomerNumber: String = "",
        val EmployeeInArea: String = "",
        val EngineerName: String = "",
        val EngineerRemark: String = "",
        val Files: ArrayList<File> = ArrayList(),
        val HotWater: String = "",
        val InstallDate: String = "",
        val ItemCode: String = "",
        val ItemSerialNo: String = "",
        val MachineLocArea: String = "",
        val MachineLocFloor: String = "",
        val Membrane: String = "",
        val PowerAvailableRemark: String = "",
        val ROPump: String = "",
        val Rejected: String = "",
        val Remark: String = "",
        val TDSInput: String = "",
        val TDSOutput: String = "",
        val TicketId: String = "",
        val VentillationRemark: String = "",
        val WaterPressureRemark: String = "",
        val id: String ="",
        val is_PowerAvailable: Boolean = false,
        val is_Ventillation: Boolean= false,
        val is_WaterPressure: Boolean = false,
        val is_PartMissing: String = "",
        val is_DamagedPart: String= "",
        val PartMissingRemark: String = "",
        val DamagedPartRemark: String = "",
        val modelType: String = "",
        var SparePart: List<SparePart> = ArrayList(),
    )

}