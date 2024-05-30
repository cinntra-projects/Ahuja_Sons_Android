package com.ahuja.sons.newapimodel

data class ResponseAnnouncement(
    val data: MutableList<ResponseAnnouncementData>,
    val message: String,
    val status: Int
)