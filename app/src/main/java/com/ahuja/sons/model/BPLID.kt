package com.ahuja.sons.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class BPLID  : Serializable {
    @SerializedName("id")
    @Expose
    private var id: Int=0

    @SerializedName("BPLId")
    @Expose
    private var BPLId: String=""

    @SerializedName("BPLName")
    @Expose
    private var BPLName: String=""

    @SerializedName("Address")
    @Expose
    private var Address: String=""

    @SerializedName("Building")
    @Expose
    private var Building: String=""

    @SerializedName("City")
    @Expose
    private var City: String=""

    @SerializedName("Country")
    @Expose
    private var Country: String=""

    @SerializedName("DflWhs")
    @Expose
    private var DflWhs: String=""

    @SerializedName("Disabled")
    @Expose
    private var Disabled: String=""

    @SerializedName("MainBPL")
    @Expose
    private var MainBPL: String=""

    @SerializedName("State")
    @Expose
    private var State: String=""

    @SerializedName("StreetNo")
    @Expose
    private var StreetNo: String=""

    @SerializedName("TaxIdNum")
    @Expose
    private var TaxIdNum: String=""

    @SerializedName("UpdateDate")
    @Expose
    private var UpdateDate: String=""

    @SerializedName("UserSign2")
    @Expose
    private var UserSign2: String=""

    @SerializedName("ZipCode")
    @Expose
    private var ZipCode: String=""

    @SerializedName("Zone")
    @Expose
    private var Zone: String=""



    @SerializedName("Code")
    @Expose
    private var code: String=""

    @SerializedName("Name")
    @Expose
    private var name: String=""


    @SerializedName("Type")
    @Expose
    private var type: String=""

    @SerializedName("Priority")
    @Expose
    private var Priority: String=""



    @SerializedName("GroupNumber")
    @Expose
    private var groupNumber: String=""

    @SerializedName("PaymentTermsGroupName")
    @Expose
    private var paymentTermsGroupName: String=""

    @SerializedName("IndustryName")
    @Expose
    private var IndustryName: String=""

    @SerializedName("IndustryCode")
    @Expose
    private var IndustryCode: String=""

    @SerializedName("IndustryDescription")
    @Expose
    private var IndustryDescription: String=""

    private var selected = false

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getBPLId(): String {
        return BPLId
    }

    fun setBPLId(BPLId: String) {
        this.BPLId = BPLId
    }

    fun getBPLName(): String {
        return BPLName
    }

    fun setBPLName(BPLName: String) {
        this.BPLName = BPLName
    }

    fun getAddress(): String {
        return Address
    }

    fun setAddress(address: String) {
        Address = address
    }

    fun getBuilding(): String {
        return Building
    }

    fun setBuilding(building: String) {
        Building = building
    }

    fun getCity(): String {
        return City
    }

    fun setCity(city: String) {
        City = city
    }

    fun getCountry(): String {
        return Country
    }

    fun setCountry(country: String) {
        Country = country
    }

    fun getDflWhs(): String {
        return DflWhs
    }

    fun setDflWhs(dflWhs: String) {
        DflWhs = dflWhs
    }

    fun getDisabled(): String {
        return Disabled
    }

    fun setDisabled(disabled: String) {
        Disabled = disabled
    }

    fun getMainBPL(): String {
        return MainBPL
    }

    fun setMainBPL(mainBPL: String) {
        MainBPL = mainBPL
    }

    fun getState(): String {
        return State
    }

    fun setState(state: String) {
        State = state
    }

    fun getStreetNo(): String {
        return StreetNo
    }

    fun setStreetNo(streetNo: String) {
        StreetNo = streetNo
    }

    fun getTaxIdNum(): String {
        return TaxIdNum
    }

    fun setTaxIdNum(taxIdNum: String) {
        TaxIdNum = taxIdNum
    }

    fun getUpdateDate(): String {
        return UpdateDate
    }

    fun setUpdateDate(updateDate: String) {
        UpdateDate = updateDate
    }

    fun getUserSign2(): String {
        return UserSign2
    }

    fun setUserSign2(userSign2: String) {
        UserSign2 = userSign2
    }

    fun getZipCode(): String {
        return ZipCode
    }

    fun setZipCode(zipCode: String) {
        ZipCode = zipCode
    }

    fun isSelected(): Boolean {
        return selected
    }

    fun setSelected(selected: Boolean) {
        this.selected = selected
    }

    fun getZone(): String {
        return Zone
    }

    fun setZone(zone: String) {
        Zone = zone
    }
    fun getCode(): String {
        return code
    }

    fun setCode(code: String) {
        this.code = code
    }

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }

    fun getType(): String {
        return type
    }

    fun setType(type: String) {
        this.type = type
    }


 fun getPriority(): String {
        return Priority
    }

    fun setPriority(Priority: String) {
        this.Priority = Priority
    }


    fun getGroupNumber(): String {
        return groupNumber
    }

    fun setGroupNumber(groupNumber: String) {
        this.groupNumber = groupNumber
    }

    fun getPaymentTermsGroupName(): String {
        return paymentTermsGroupName
    }

    fun setPaymentTermsGroupName(paymentTermsGroupName: String) {
        this.paymentTermsGroupName = paymentTermsGroupName
    }

    fun getIndustryName(): String {
        return IndustryName
    }

    fun setIndustryName(industryName: String) {
        this.IndustryName = industryName
    }

    fun getIndustryCode(): String {
        return IndustryCode
    }

    fun setIndustryCode(industryCode: String) {
        this.IndustryCode = industryCode
    }

    fun getIndustryDescription(): String {
        return IndustryDescription
    }

    fun setIndustryDescription(industryDescription: String) {
        this.IndustryDescription = industryDescription
    }
}
