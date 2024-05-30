package com.ahuja.sons.model

data class AllAttachmentResponse(
	val data: List<DataItem>,
	val message: String,
	val status: Int
)
