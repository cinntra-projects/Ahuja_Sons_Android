package com.ahuja.sons.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class BusinessPartnerDataNew : Serializable {

    @SerializedName("id")
    @Expose
    private var id: Int? = null

    @SerializedName("CardCode")
    @Expose
    private var cardCode: String? = null

    @SerializedName("CardName")
    @Expose
    private var cardName: String? = null

    @SerializedName("Industry")
    @Expose
    private var industry: String? = null

    @SerializedName("CardType")
    @Expose
    private var cardType: String? = null

    @SerializedName("Website")
    @Expose
    private var website: String? = null

    @SerializedName("EmailAddress")
    @Expose
    private var emailAddress: String? = null

    @SerializedName("Phone1")
    @Expose
    private var phone1: String? = null

    @SerializedName("DiscountPercent")
    @Expose
    private var discountPercent: String? = null

    @SerializedName("Currency")
    @Expose
    private var currency: String? = null

    @SerializedName("IntrestRatePercent")
    @Expose
    private var intrestRatePercent: String? = null

    @SerializedName("CommissionPercent")
    @Expose
    private var commissionPercent: String? = null

    @SerializedName("Notes")
    @Expose
    private var notes: String? = null

    @SerializedName("PayTermsGrpCode")
    @Expose
    private var payTermsGrpCode: String? = null

    @SerializedName("CreditLimit")
    @Expose
    private var creditLimit: String? = null

    @SerializedName("AttachmentEntry")
    @Expose
    private var attachmentEntry: String? = null

    @SerializedName("SalesPersonCode")
    @Expose
    private var salesPersonCode: String? = null

    @SerializedName("ContactPerson")
    @Expose
    private var contactPerson: String? = null

    @SerializedName("U_PARENTACC")
    @Expose
    private var uParentacc: String? = null

    @SerializedName("U_BPGRP")
    @Expose
    private var uBpgrp: String? = null

    @SerializedName("U_CONTOWNR")
    @Expose
    private var uContownr: String? = null

    @SerializedName("U_RATING")
    @Expose
    private var uRating: String? = null

    @SerializedName("U_TYPE")
    @Expose
    private var uType: String? = null

    @SerializedName("U_ANLRVN")
    @Expose
    private var uAnlrvn: String? = null

    @SerializedName("U_CURBAL")
    @Expose
    private var uCurbal: String? = null

    @SerializedName("U_ACCNT")
    @Expose
    private var uAccnt: String? = null

    @SerializedName("U_INVNO")
    @Expose
    private var uInvno: String? = null

    @SerializedName("CreateDate")
    @Expose
    private var createDate: String? = null

    @SerializedName("CreateTime")
    @Expose
    private var createTime: String? = null

    @SerializedName("UpdateDate")
    @Expose
    private var updateDate: String? = null

    @SerializedName("UpdateTime")
    @Expose
    private var updateTime: String? = null

    @SerializedName("U_LAT")
    @Expose
    private var uLat: String? = null

    @SerializedName("U_LONG")
    @Expose
    private var uLong: String? = null


    @SerializedName("U_LEADID")
    @Expose
    private var U_LEADID: String? = null

    @SerializedName("U_LEADNM")
    @Expose
    private var U_LEADNM: String? = null

    @SerializedName("Zone")
    @Expose
    private var Zone: String? = null

    @SerializedName("BPAddresses")
    @Expose
    private var bPAddresses: List<BPAddresse>? = null

    @SerializedName("ContactEmployees")
    @Expose
    private var contactEmployees: List<ContactEmployee>? = null


    @SerializedName("BPLID")
    @Expose
    private var BPL_IDAssignedToInvoice= arrayOf<String>()


    private val serialVersionUID = 3537533647679235231L



    /**
     *
     * @param uRating
     * @param updateDate
     * @param notes
     * @param cardName
     * @param bPAddresses
     * @param contactPerson
     * @param industry
     * @param uParentacc
     * @param phone1
     * @param uCurbal
     * @param emailAddress
     * @param payTermsGrpCode
     * @param uType
     * @param creditLimit
     * @param currency
     * @param id
     * @param uBpgrp
     * @param createDate
     * @param website
     * @param uInvno
     * @param discountPercent
     * @param uAnlrvn
     * @param uContownr
     * @param cardCode
     * @param cardType
     * @param intrestRatePercent
     * @param updateTime
     * @param createTime
     * @param commissionPercent
     * @param uAccnt
     * @param attachmentEntry
     * @param salesPersonCode
     */

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getCardCode(): String? {
        return cardCode
    }

    fun setCardCode(cardCode: String?) {
        this.cardCode = cardCode
    }

    fun getCardName(): String? {
        return cardName
    }

    fun setCardName(cardName: String?) {
        this.cardName = cardName
    }

    fun getIndustry(): String? {
        return industry
    }

    fun setIndustry(industry: String?) {
        this.industry = industry
    }

    fun getCardType(): String? {
        return cardType
    }

    fun setCardType(cardType: String?) {
        this.cardType = cardType
    }

    fun getWebsite(): String? {
        return website
    }

    fun setWebsite(website: String?) {
        this.website = website
    }

    fun getEmailAddress(): String? {
        return emailAddress
    }

    fun setEmailAddress(emailAddress: String?) {
        this.emailAddress = emailAddress
    }

    fun getPhone1(): String? {
        return phone1
    }

    fun setPhone1(phone1: String?) {
        this.phone1 = phone1
    }

    fun getDiscountPercent(): String? {
        return discountPercent
    }

    fun setDiscountPercent(discountPercent: String?) {
        this.discountPercent = discountPercent
    }

    fun getCurrency(): String? {
        return currency
    }

    fun setCurrency(currency: String?) {
        this.currency = currency
    }

    fun getIntrestRatePercent(): String? {
        return intrestRatePercent
    }

    fun setIntrestRatePercent(intrestRatePercent: String?) {
        this.intrestRatePercent = intrestRatePercent
    }

    fun getCommissionPercent(): String? {
        return commissionPercent
    }

    fun setCommissionPercent(commissionPercent: String?) {
        this.commissionPercent = commissionPercent
    }

    fun getNotes(): String? {
        return notes
    }

    fun setNotes(notes: String?) {
        this.notes = notes
    }

    fun getPayTermsGrpCode(): String? {
        return payTermsGrpCode
    }

    fun setPayTermsGrpCode(payTermsGrpCode: String?) {
        this.payTermsGrpCode = payTermsGrpCode
    }

    fun getCreditLimit(): String? {
        return creditLimit
    }

    fun setCreditLimit(creditLimit: String?) {
        this.creditLimit = creditLimit
    }

    fun getAttachmentEntry(): String? {
        return attachmentEntry
    }

    fun setAttachmentEntry(attachmentEntry: String?) {
        this.attachmentEntry = attachmentEntry
    }

    fun getSalesPersonCode(): String? {
        return salesPersonCode
    }

    fun setSalesPersonCode(salesPersonCode: String?) {
        this.salesPersonCode = salesPersonCode
    }

    fun getContactPerson(): String? {
        return contactPerson
    }

    fun setContactPerson(contactPerson: String?) {
        this.contactPerson = contactPerson
    }

    fun getUParentacc(): String? {
        return uParentacc
    }

    fun setUParentacc(uParentacc: String?) {
        this.uParentacc = uParentacc
    }

    fun getUBpgrp(): String? {
        return uBpgrp
    }

    fun setUBpgrp(uBpgrp: String?) {
        this.uBpgrp = uBpgrp
    }

    fun getUContownr(): String? {
        return uContownr
    }

    fun setUContownr(uContownr: String?) {
        this.uContownr = uContownr
    }

    fun getURating(): String? {
        return uRating
    }

    fun setURating(uRating: String?) {
        this.uRating = uRating
    }

    fun getUType(): String? {
        return uType
    }

    fun setUType(uType: String?) {
        this.uType = uType
    }

    fun getUAnlrvn(): String? {
        return uAnlrvn
    }

    fun setUAnlrvn(uAnlrvn: String?) {
        this.uAnlrvn = uAnlrvn
    }

    fun getUCurbal(): String? {
        return uCurbal
    }

    fun setUCurbal(uCurbal: String?) {
        this.uCurbal = uCurbal
    }

    fun getUAccnt(): String? {
        return uAccnt
    }

    fun setUAccnt(uAccnt: String?) {
        this.uAccnt = uAccnt
    }

    fun getUInvno(): String? {
        return uInvno
    }

    fun setUInvno(uInvno: String?) {
        this.uInvno = uInvno
    }

    fun getCreateDate(): String? {
        return createDate
    }

    fun setCreateDate(createDate: String?) {
        this.createDate = createDate
    }

    fun getCreateTime(): String? {
        return createTime
    }

    fun setCreateTime(createTime: String?) {
        this.createTime = createTime
    }

    fun getUpdateDate(): String? {
        return updateDate
    }

    fun setUpdateDate(updateDate: String?) {
        this.updateDate = updateDate
    }

    fun getUpdateTime(): String? {
        return updateTime
    }

    fun setUpdateTime(updateTime: String?) {
        this.updateTime = updateTime
    }


    fun getuLat(): String? {
        return uLat
    }

    fun setuLat(uLat: String?) {
        this.uLat = uLat
    }

    fun getuLong(): String? {
        return uLong
    }

    fun setuLong(uLong: String?) {
        this.uLong = uLong
    }

    fun getBPAddresses(): List<BPAddresse>? {
        return bPAddresses
    }

    fun setBPAddresses(bPAddresses: List<BPAddresse>?) {
        this.bPAddresses = bPAddresses
    }

    fun getContactEmployees(): List<ContactEmployee>? {
        return contactEmployees
    }

    fun setContactEmployees(contactEmployees: List<ContactEmployee>?) {
        this.contactEmployees = contactEmployees
    }


    fun getBPL_IDAssignedToInvoice(): Array<String>? {
        return BPL_IDAssignedToInvoice
    }

    fun setBPL_IDAssignedToInvoice(BPL_IDAssignedToInvoice: Array<String>) {
        this.BPL_IDAssignedToInvoice = BPL_IDAssignedToInvoice
    }

    fun getU_LEADID(): String? {
        return U_LEADID
    }

    fun setU_LEADID(u_LEADID: String?) {
        U_LEADID = u_LEADID
    }

    fun getU_LEADNM(): String? {
        return U_LEADNM
    }

    fun setU_LEADNM(u_LEADNM: String?) {
        U_LEADNM = u_LEADNM
    }

    fun getZone(): String? {
        return Zone
    }

    fun setZone(zone: String?) {
        Zone = zone
    }
}

