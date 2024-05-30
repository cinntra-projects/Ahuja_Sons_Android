package com.ahuja.sons.model

import android.os.Parcel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class EmployeeResponse {

    @SerializedName("message")
    @Expose
    private var message: String? = ""

    @SerializedName("status")
    @Expose
    private var status: Int? = 0

    @SerializedName("data")
    @Expose
    private var LogInDetail: EmployeeData? =null




    /**
     * No args constructor for use in serialization
     *
     */
    fun NewLogINResponse() {}

    /**
     *
     * @param LogInDetail
     * @param message
     * @param status
     */
    fun NewLogINResponse(message: String?, status: Int?, LogInDetail: EmployeeData) {

        this.message = message
        this.status = status
        this.LogInDetail = LogInDetail
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getStatus(): Int? {
        return status
    }

    fun setStatus(status: Int?) {
        this.status = status
    }

    fun getLogInDetail(): EmployeeData? {
        return LogInDetail
    }

    fun setLogInDetail(LogInDetail:  EmployeeData) {
        this.LogInDetail = LogInDetail
    }



    fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(message)
        dest.writeValue(status)
        dest.writeValue(LogInDetail)
    }

    fun describeContents(): Int {
        return 0
    }

}
