package com.ahuja.sons.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import com.ahuja.sons.`interface`.SmsListener


class SmsReceiver :  BroadcastReceiver(){
    private var mListener: SmsListener? = null
    var b: Boolean = true
    var abcd: String? = null
    var xyz:kotlin.String? = null
    fun bindListener(listener: SmsListener) {
        mListener = listener
    }

    override fun onReceive(p0: Context?, p1: Intent?) {
        val data: Bundle? = p1?.extras
        val pdus = data?.get("pdus") as Array<Any>?
        for (i in 0..pdus!!.size) {
            val smsMessage: SmsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray)
            val sender: String = smsMessage.displayOriginatingAddress
            val messageBody = smsMessage.messageBody
            abcd = messageBody.replace("[^0-9]".toRegex(), "")
            if (b) {
                mListener!!.messageReceived(abcd); // attach value to interface

            } else {}
        }
    }
}