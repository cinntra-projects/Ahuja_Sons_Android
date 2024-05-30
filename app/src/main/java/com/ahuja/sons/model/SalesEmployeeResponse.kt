package com.ahuja.sons.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SalesEmployeeResponse  : Serializable {

    @SerializedName("message")
    @Expose
    private var message: String? = ""

    @SerializedName("status")
    @Expose
    private var status: Int = 0

    @SerializedName("data")
    @Expose
    private var data: List<EmployeeData> = emptyList()
    private val serialVersionUID = -6065000172635184482L





    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getStatus(): Int {
        return status
    }

    fun setStatus(status: Int) {
        this.status = status
    }

    fun getData(): List<EmployeeData> {
        return data
    }

    fun setData(data: List<EmployeeData>) {
        this.data = data
    }

}
