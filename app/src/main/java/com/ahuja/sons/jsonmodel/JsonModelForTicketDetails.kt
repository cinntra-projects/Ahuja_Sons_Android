package com.ahuja.sons.jsonmodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class JsonModelForTicketDetails(
var Status :String="",
var CorrectIssueType :String="",
var ScheduledVisitDate :String="",
var CorrectiveActions :String="",
var RepairRequestNeeded :String="",
var MaterialUsed :String="",
):Parcelable
