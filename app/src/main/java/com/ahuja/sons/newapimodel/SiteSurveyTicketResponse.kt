package com.ahuja.sons.newapimodel

data class SiteSurveyTicketResponse(
    var `data`: List<DataXXX>,
    var message: String = "",
    var status: Int = 0
){
    data class DataXXX(
        var Area: List<Area> = ArrayList(),
        var Availability: List<Availability> = ArrayList(),
        var BuildingFloorCount: String = "",
        var ChlorinatedWater: String = "",
        var CustomerName: String = "",
        var CustomerNumber: String = "",
        var CustomerRemark: String = "",
        var ElectricityType: String = "",
        var EngineerName: String = "",
        var EngineerRemark: String = "",
        var ExistingBottlesMake: String = "",
        var ExistingDispensersMake: String = "",
        var Files: List<File> = ArrayList(),
        var Floor: String = "",
        var InstalledDispensers: String = "",
        var ItemCode: String = "",
        var ItemSerialNo: String = "",
        var PHLevel: String = "",
        var PantriesOnFloor: String = "",
        var PowerBackupCapacity: String = "",
        var ShiftNo: String = "",
        var ShiftTiming: String = "",
        var TDSRawWater: String = "",
        var TankCleaning: String = "",
        var TankFillingFrequency: String = "",
        var TankHeight: String = "",
        var TankVolume: String = "",
        var TicketId: String = "",
        var TotalEmployee: String = "",
        var TotalVisitors: String = "",
        var WaterBottelsUses: String = "",
        var WaterBottlesCapacity: String = "",
        var WaterPressure: String = "",
        var WaterSource: String = "",
        var id: Int = 0
    )
    data class Area(
        var Location: String = "",
        var Length: String = "",
        var Width: String = "",
        var Height: String = "",
        var ItemCode: String = "",
        var ItemSerialNo: String = "",
        var SiteSurveyId: String = "",
        var TicketId: String = "",
        var id: String = ""
    )

    data class Availability(
        var ItemName: String = "",
        var ItemQty: String = "",
        var ItemDistance: String = "",
        var ItemCode: String = "",
        var ItemSerialNo: String = "",
        var SiteSurveyId: String = "",
        var TicketId: String = "",
        var id: String = ""
    )

}