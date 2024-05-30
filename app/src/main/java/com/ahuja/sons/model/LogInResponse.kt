package com.ahuja.sons.model

import android.os.Parcel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class LogInResponse  : Serializable {

    @SerializedName("message")
    @Expose
    private var message: String = ""

    @SerializedName("status")
    @Expose
    private var status: Int = 0

    @SerializedName("data")
    @Expose
    private var LogInDetail: List<NewLoginData> = emptyList()




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
    fun NewLogINResponse(message: String, status: Int, LogInDetail: List<NewLoginData>) {

        this.message = message
        this.status = status
        this.LogInDetail = LogInDetail
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getStatus(): Int {
        return status
    }

    fun setStatus(status: Int) {
        this.status = status
    }

    fun getLogInDetail(): List<NewLoginData> {
        return LogInDetail
    }

    fun setLogInDetail(LogInDetail:  List<NewLoginData>) {
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
