package com.customer.customerapp.globals

import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.ahuja.sons.globals.WorkManagerApplication

object ApiScheduler {
    private const val INTERVAL_MINUTES = 15
    private const val JOB_ID = 1

    fun schedularCall(activity: Activity){
        val componentName = ComponentName(activity, WorkManagerApplication::class.java)
        val jobInfo = JobInfo.Builder(JOB_ID, componentName)
            .setPeriodic(15 * 60 * 1000) // 15 minutes in milliseconds
            .build()

        val jobScheduler = activity.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(jobInfo)
    }

    fun cancelApiCall(context: Context) {
        androidx.work.WorkManager.getInstance(context).cancelUniqueWork("CancelApiCallWork")
    }
}