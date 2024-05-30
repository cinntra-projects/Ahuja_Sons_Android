package com.ahuja.sons.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class EmployeeData {

    @SerializedName("lastName")
    private var lastName: String? = ""

    @SerializedName("SalesEmployeeCode")
    private var salesEmployeeCode: String? = ""

    @SerializedName("Email")
    private var email: String? = ""

    @SerializedName("role")
    private var role: String? = ""

    @SerializedName("lastLoginOn")
    private var lastLoginOn: String? = ""

    @SerializedName("SalesEmployeeName")
    private var salesEmployeeName: String? = ""

    @SerializedName("userName")
    private var userName: String? = ""

    @SerializedName("Mobile")
    private var mobile: String? = ""

    @SerializedName("branch")
    private var branch: String? = ""

    @SerializedName("logedIn")
    private var logedIn: String? = ""

    @SerializedName("firstName")
    private var firstName: String? = ""

    @SerializedName("companyID")
    private var companyID: String? = ""

    @SerializedName("password")
    private var password: String? = ""

    @SerializedName("Active")
    private var active: String? = ""

    @SerializedName("middleName")
    private var middleName: String? = ""

    @SerializedName("id")
    private var id = 0

    @SerializedName("position")
    private var position: String? = ""

    @SerializedName("EmployeeID")
    private var employeeID: String? = ""

    @SerializedName("passwordUpdatedOn")
    private var passwordUpdatedOn: String? = ""

    @SerializedName("reportingTo")
    private var reportingTo: String? = ""

    @SerializedName("timestamp")
    private var timestamp: String? = ""

    @SerializedName("reportingName")
    private var reportingName: String? = ""

    @SerializedName("discount")
    private var discount = 0f


    @SerializedName("FCM")
    @Expose
    private var fcm: String? = ""

    fun getLastName(): String? {
        return lastName
    }

    fun getSalesEmployeeCode(): String? {
        return salesEmployeeCode
    }

    fun getEmail(): String? {
        return email
    }

    fun getRole(): String? {
        return role
    }

    fun getLastLoginOn(): String? {
        return lastLoginOn
    }

    fun getSalesEmployeeName(): String? {
        return salesEmployeeName
    }

    fun getUserName(): String? {
        return userName
    }

    fun getMobile(): String? {
        return mobile
    }

    fun getBranch(): String? {
        return branch
    }

    fun getLogedIn(): String? {
        return logedIn
    }

    fun getFirstName(): String? {
        return firstName
    }

    fun getCompanyID(): String? {
        return companyID
    }

    fun getPassword(): String? {
        return password
    }

    fun getActive(): String? {
        return active
    }

    fun getMiddleName(): String? {
        return middleName
    }

    fun getId(): Int {
        return id
    }

    fun getPosition(): String? {
        return position
    }

    fun getEmployeeID(): String? {
        return employeeID
    }

    fun getPasswordUpdatedOn(): String? {
        return passwordUpdatedOn
    }

    fun getReportingTo(): String? {
        return reportingTo
    }

    fun getTimestamp(): String? {
        return timestamp
    }

    fun getReportingName(): String? {
        return reportingName
    }

    fun setReportingName(reportingName: String?) {
        this.reportingName = reportingName
    }

    fun setLastName(lastName: String?) {
        this.lastName = lastName
    }

    fun setSalesEmployeeCode(salesEmployeeCode: String?) {
        this.salesEmployeeCode = salesEmployeeCode
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun setRole(role: String?) {
        this.role = role
    }

    fun setLastLoginOn(lastLoginOn: String?) {
        this.lastLoginOn = lastLoginOn
    }

    fun setSalesEmployeeName(salesEmployeeName: String?) {
        this.salesEmployeeName = salesEmployeeName
    }

    fun setUserName(userName: String?) {
        this.userName = userName
    }

    fun setMobile(mobile: String?) {
        this.mobile = mobile
    }

    fun setBranch(branch: String?) {
        this.branch = branch
    }

    fun setLogedIn(logedIn: String?) {
        this.logedIn = logedIn
    }

    fun setFirstName(firstName: String?) {
        this.firstName = firstName
    }

    fun setCompanyID(companyID: String?) {
        this.companyID = companyID
    }

    fun setPassword(password: String?) {
        this.password = password
    }

    fun setActive(active: String?) {
        this.active = active
    }

    fun setMiddleName(middleName: String?) {
        this.middleName = middleName
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun setPosition(position: String?) {
        this.position = position
    }

    fun setEmployeeID(employeeID: String?) {
        this.employeeID = employeeID
    }

    fun setPasswordUpdatedOn(passwordUpdatedOn: String?) {
        this.passwordUpdatedOn = passwordUpdatedOn
    }

    fun setReportingTo(reportingTo: String?) {
        this.reportingTo = reportingTo
    }

    fun setTimestamp(timestamp: String?) {
        this.timestamp = timestamp
    }

    fun getDiscount(): Float {
        return discount
    }

    fun setDiscount(discount: Float) {
        this.discount = discount
    }

     fun setFcm(fcm: String) {
        this.fcm = fcm
    }

    fun getFcm():String?{
        return fcm
    }


}