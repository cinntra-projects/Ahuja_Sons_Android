package com.ahuja.sons.ahujaSonsClasses.model.image_get_model

data class UploadedPictureModel(
    var `data`: ArrayList<Data> = ArrayList(),
    var errors: String = "",
    var message: String = "",
    var status: Int = 0
){
    data class Data(
        var Attachment: String = "",
        var CreateDate: String = "",
        var CreateTime: String = "",
        var CreatedBy: Int = 0,
        var DeliveryId: String = "",
        var InspectionStatus: String = "",
        var OrderRequestID: Int = 0,
        var Remark: String = "",
        var UpdateDate: String = "",
        var UpdateTime: String = "",
        var WorkQueue: String = "",
        var id: Int = 0
    )
}