package com.ahuja.sons.apiservice

import com.ahuja.sons.ahujaSonsClasses.model.*
import com.ahuja.sons.ahujaSonsClasses.model.image_get_model.UploadedPictureModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.*
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllItemsForOrderModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.DeliveryDetailItemListModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.WorkQueueRequestModel
import com.google.gson.JsonObject
import com.ahuja.sons.apibody.BodyForIssueSubCategory
import com.ahuja.sons.model.*
import com.ahuja.sons.model.BPLID
import com.ahuja.sons.model.DocumentLine
import com.ahuja.sons.newapimodel.*
import com.ahuja.sons.receiver.ResponseEmployeeAllList
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface Apis {

    /*****NEW API RESPONSE WITH RESOURCE CLASSES****//*** Chanchal ***/

    @POST("employee/login_support")
    suspend fun loginTest(@Body data: HashMap<String, String>): Response<String>

    @POST("employee/login")
    suspend fun loginEmployeeNew(@Body data: HashMap<String, String>): Response<ResponseEmployeeAtLogin>

    @POST("employee/logout")
    suspend fun logoutEmployeeNew(@Body data: HashMap<String, String>): Response<ResponseAddTicket>


    @POST("tickets/tickets_dashboard")
    suspend fun getDashboardCounterNew(@Body data: HashMap<String, String>): Response<ResponseDashBoardCounter>

    @POST("employee/dashboard")
    suspend fun getDashboardNotifictaionCount(@Body data: HashMap<String, String>): Response<NotificationCountResponseModel>



    @POST("notification/all_notification_filter")
    fun getAllNotificationList(@Body data: JsonObject): Call<NotificationListResponseModel>

    @POST("tickets/filterbykey")
    suspend fun getTicketsByFilterNew(@Body data: HashMap<String, Any>): Response<ResponseTicket>

    @POST("tickets/filterbykey")
    suspend fun getTicketByPagination(@Body data: HashMap<String, Any>): Response<ResponseTicket>

    @POST("order/all_bybp")
    suspend fun getCustomerMvvmOrderAllOrderList(@Body data: HashMap<String, String>): Response<ResponseParticularCustomerOrder>

    @POST("order/one")
    suspend fun getParticularCustomerMvvmOrderOne(@Body data: HashMap<String, String>): Response<ResponseOrderOne>

    @POST("tickets/dropdowns/subtype")
    suspend fun getTicketSubType(@Body data: HashMap<String, String>): Response<ResponseSubType>

    @POST("employee/reportingto_lower")
    suspend fun getAllEmployeeList(@Body data: HashMap<String, String>): Response<ResponseEmployeeAllList>

    @GET("tickets/dropdowns/type")
    suspend fun getTicketType(): Response<ResponseTypeTickets>

    @POST("tickets/create")
    suspend fun createNewTicketMVVM(@Body tdm: AddTicketRequestModel): Response<ResponseAddTicket> //BodyAddTicketData

    @POST("tickets/one")
    suspend fun getTicketOne(@Body data: HashMap<String, String>): Response<ResponseTicket>

    @POST("tickets/ticketchecklist/filter_all")
    suspend fun getTicketCheckList(@Body data: HashMap<String, String>): Response<ResponseCheckListTicket>

    @POST("tickets/ticketchecklist/update")
    suspend fun getUpdateTicketCheckList(@Body data: BodyUpdateCheckListItem): Response<ResponseCheckListTicket>

    @POST("tickets/update")
    suspend fun updateParticularTicket(@Body data: AddTicketRequestModel): Response<ResponseAddTicket>

    @POST("tickets/item_remarks_update")
    suspend fun getTicketRemarksUpdate(@Body jsonObject: JsonObject): Response<ResponseTicket>


    @GET("businesspartner/all_bp")
    suspend fun getCustomerForContact(): Response<ResponseCustomerListForContact>

    @POST("businesspartner/employee/all")
    suspend fun getContactNameList(@Body jsonObject: JsonObject): Response<ContactNameListResponseModel>

    @POST("businesspartner/branch/all")
    suspend fun getBranchAllList(@Body jsonObject: JsonObject): Response<BranchAllListResponseModel>

    @POST("businesspartner/item/all_filter")
    suspend fun getItemAllList(@Body jsonObject: JsonObject): Response<ItemAllListResponseModel>

    @POST("tickets/itembyticket")
    suspend fun getItemsByTicket(@Body jsonObject: JsonObject): Response<ItemAllListResponseModel>

    @GET("tickets/dropdowns/type")
    suspend fun getScopeWorkList(): Response<ScopOfWorkResponseModel>

    @GET("announcement/campset/all")
    suspend fun getAnnouncementList(): Response<ResponseAnnouncement>

    @GET("inspection/issuecategory/all")
    suspend fun getQualityIssueCategory(): Response<ResponseQualityIssueCategory>

    @GET("dropdown/static/all?DropDownName=RescueStatus")
    suspend fun getDropDownManRescue(): Response<ResponseDropDownManRescue>

    @POST("inspection/issue/filter")
    suspend fun getQualityIssueSubCategory(@Body data: BodyForIssueSubCategory): Response<ResponseQualityIssueSubCategory>

    @POST("inspection/create")
    suspend fun addQualityIssueInspection(@Body data: HashMap<String, String>): Response<ResponseAddTicket>


    @POST("employee/get_help")
    suspend fun getAssignerList(@Body data: HashMap<String, String>): Response<ResponseAssignedTo>

    @POST("tickets/reassign")
    suspend fun updateAssigner(@Body data: HashMap<String, Any>): Response<ResponseAddTicket>

    @POST("tickets/history/filter_all_type")
    suspend fun getTicketTypeLogsInDetails(@Body data: HashMap<String, Any>): Response<ResponseTicketLogForTicketDetails>




    @POST("tickets/rescue/all")
    suspend fun getManTrapLog(@Body data: HashMap<String, Any>): Response<ResponseManTrapLog>

    @POST("inspection/filter")
    suspend fun getQualityInspectionList(@Body data: PayLoadForInspectionList): Response<ResponseQualityInspection>

    @POST("tickets/rescue/create")
    suspend fun updateManRescueLog(@Body data: HashMap<String, String>): Response<ResponseAddTicket>

    @POST("order/all_bybp")
    suspend fun getProductName(@Body data: HashMap<String, String>): Response<ResponseAddTicket>


    @GET("order/bp_list_byorder")
    suspend fun getAllBPList(): Response<AccountBPResponse>

    @POST("businesspartner/all_filter_page")
    fun getCustomerAllList(@Body request : CustomerRequestModel): Call<AccountBPResponse>


    @POST("notification/read")
    fun readNotificationAPi(@Body data: JsonObject): Call<NotificationListResponseModel>

    @POST("businesspartner/one")
    fun getCustomerOneApi(@Body request : JsonObject): Call<AccountBPResponse>


    @POST("delivery/bp_wise_items")
    suspend fun getAccountitemList(@Body data: AccountBpData): Response<AccountItemResponse>

    @POST("tickets/filterbykey")
    fun getfilterbyTickethashmap(@Body data: HashMap<String, Any>): Call<ResponseTicket>

    @POST("tickets/all_filter_page")
    fun getAllTicketList(@Body data: AllTicketRequestModel): Call<ResponseTicket>

    @POST("tickets/itembyticket")
    fun getItemsByTicket2(@Body jsonObject: JsonObject): Call<ItemAllListResponseModel>


    @POST("attachment/all")
    suspend fun allattachment(@Body tickethistory: HashMap<String, Any>): Response<AllAttachmentResponse>


    @POST("businesspartner/create")
    suspend fun addnewCustomer(@Body businessPartnerDataNew: BusinessPartnerDataNew): Response<AccountBPResponse>


    @POST("tickets/searchintickets")
    fun searchApi(@Body data: HashMap<String, Any>): Call<ResponseTicket>


    @GET("paymenttermstypes/all")
    suspend fun getPaymentTerm(): Response<BPBranchResponse>

    @GET("industries/all")
    suspend fun getIndustryList(): Response<BPBranchResponse>


    @GET("company/branches")
    suspend fun getAllBranchList(): Response<BPBranchResponse>

    @GET("tickets/dropdowns/zone")
    suspend fun getAllZoneList(): Response<BPBranchResponse>

    @POST("delivery/itemdetails")
    suspend fun getTicketDetails(@Body data: TicketDetailsData): Response<TicketDetailsModel>

    @POST("tickets/tickets_bp_dashboard")
    suspend fun getbpwiseTicket(@Body data: ContactEmployee): Response<DashboardTicketCounterResponse>

    @GET("businesspartner/department/all")
    suspend fun getDepartMent(): Response<DepartMentDetail>

    @GET("businesspartner/position/all")
    suspend fun getRole(): Response<DepartMentDetail>

    @POST("businesspartner/employee/create")
    suspend fun createcontact(@Body createContact: CreateContactData): Response<ResponseAddTicket>

    @GET("countries/all")
    suspend fun getCountryList(): Response<BPBranchResponse>

    @POST("states/all")
    suspend fun getStateList(@Body stateData: BPLID): Response<BPBranchResponse>

    @POST("employee/all_filter")
    suspend fun getSalesEmplyeeList(@Body employeeValue: NewLoginData): Response<SalesEmployeeResponse>


    @GET("employee/all")
    suspend fun getSalesEmployeeAllList(): Response<SalesEmployeeResponse>

    @GET("employee/role")
    suspend fun getEmployeeRoleList(): Response<EmployeeRoleResponseModel>

    @GET("employee/subdepartment")
    suspend fun getEmployeeSubDepList(): Response<EmployeeSubDepResponseModel>


    @GET("delivery/bp_contact_list")
    suspend fun getallcontact(): Response<ContactResponse>

    @POST("item/all")
    suspend fun getItemlist(@Body data: DocumentLine): Response<AccountItemResponse>


    @POST("activity/chatter_all")
    suspend fun getFollowUpList(@Body data: HashMap<String, Any>): Response<ResponseFollowUp>


    @POST("tickets/servicechecklist/filter_all")
    suspend fun getticketchecklist(@Body data: TicketHistoryData): Response<TicketChecklistResponse>

    @POST("tickets/ticketstartend")
    suspend fun startstoptimer(@Body data: HashMap<String, Any>): Response<TicketChecklistResponse>

    @POST("tickets/ticketreset")
    suspend fun resettimer(@Body data: HashMap<String, Any>): Response<TicketChecklistResponse>

    @POST("tickets/conversation/filter_all")
    suspend fun getTicketConversation(@Body data: HashMap<String, Int>): Response<TicketHistoryResponse>


    @POST("tickets/history/filter_all")
    suspend fun getTicketHistory(@Body data: HashMap<String, Int>): Response<TicketHistoryResponse>


    @POST("tickets/conversation/filter_all")
    fun getConversationList(@Body data: HashMap<String, Int>): Call<TicketHistoryResponse>

    @POST("tickets/conversation/create")
    suspend fun createTicketConversation(@Body data: TicketHistoryData): Response<TicketHistoryResponse>

    @POST("tickets/item_wise_tickets")
    suspend fun getitemwiseticket(@Body id: TicketDataModel): Response<ResponseTicket>


    @POST("category/all_filter")
    suspend fun getAllCategory(@Body id: NewLoginData): Response<ItemCategoryResponse>

    @GET("category/all")
    suspend fun getAllCategoryList(): Response<ItemCategoryResponse>

    @POST("tickets/one")
    suspend fun particularTicketDetails(@Body id: HashMap<String, Int>): Response<ResponseTicket>

    @POST("tickets/parts/create")
    suspend fun createPartRequest(@Body data: CreatePartrequestpayload): Response<CreatePartRequestResponse>

    @POST("tickets/parts/filter_all")
    suspend fun getAllpartrequest(@Body TicketId: HashMap<String, Any>): Response<ResponseAllPart>

    @POST("tickets/parts/one")
    suspend fun getAllPartRequestItem(@Body TicketId: HashMap<String, Any>): Response<ResponsePartOne>

    @POST("tickets/accept_reject")
    suspend fun acceptRejectTicket(@Body logInDetail: NewLoginData): Response<LogInResponse>

    //todo Multipart api's..

    @POST("tickets/ticketsigninconfirm")
    suspend fun signandconfirm(@Body requestBody: MultipartBody): Response<TicketDetailsModel>

    @POST("tickets/customerpirupload")
    suspend fun imageupload(@Body requestBody: MultipartBody): Response<TicketDataResponse>

    @POST("attachment/createmany")
    suspend fun multiplfileupload(@Body requestBody: MultipartBody): Response<TicketDataResponse>

    @POST("tickets/parts/pr_attachments_upload")
    suspend fun prUpload(@Body requestBody: MultipartBody): Response<TicketDataResponse>

    @POST("tickets/create_report")
    //@Headers("Content-Type: application/json")
    suspend fun createTicketTypeItems(@Body requestBody: MultipartBody): Response<TicketDataResponse>

    @POST("tickets/add_report_attach")
    suspend fun addAttachment(@Body requestBody: MultipartBody): Response<TicketDataResponse>

    @POST("tickets/update_report")
    suspend fun updateTicketTypeItems(@Body requestBody: JsonObject): Response<TicketDataResponse>

    @POST("tickets/item_report_detail")
    suspend fun ticketMaintenanceOneApi(@Body jsonObject: JsonObject): Response<TicketPreventiveMaintainanceResponse>

    @POST("tickets/item_report_detail")
    suspend fun ticketInstallationOneApi(@Body jsonObject: JsonObject): Response<InstallationTicketOneResponse>

    @POST("tickets/item_report_detail")
    suspend fun ticketSiteSurveyOneApi(@Body jsonObject: JsonObject): Response<SiteSurveyTicketResponse>

    @GET("item/all_spare_part")
    fun allSparePartApiList(): Call<SpareItemListApiModel>

    @GET("inspection/issuecategory/all")
    suspend fun getIssueCategoryList(): Response<IssueCategoryListResponseModel>

    @POST("inspection/issue/filter")
    suspend fun getSolutionList(@Body jsonObject: SolutionRequestModel): Response<SolutionListResponseModel>

    @POST("inspection/create")
    suspend fun createIssue(@Body jsonObject: JsonObject): Response<SolutionListResponseModel>

    @POST("servicecontract/one")
    suspend fun ServiceContractOneApi(@Body jsonObject: JsonObject): Response<ServiceContractListResponseModel>


    @POST("servicecontract/create")
    suspend fun createServiceContract(@Body data: CreateServiceContractRequestModel): Response<ServiceContractListResponseModel>


    @POST("businesspartner/item/one")
    suspend fun getProductOneDetailApi(@Body data: JsonObject): Response<ProductResponseModel>

    @POST("businesspartner/item/create")
    suspend fun createProduct(@Body data: AddProductRequestModel): Response<ProductResponseModel>

    @POST("businesspartner/item/update")
    suspend fun updateProduct(@Body data: AddProductRequestModel): Response<ProductResponseModel>

    @POST("employee/create")
    suspend fun createEmployee(@Body data: EmployeeCreateRequestModel): Response<ProductResponseModel>

    @POST("employee/one")
    suspend fun callEmployeeOneApi(@Body data: JsonObject): Response<EmployeeOneModel>

    @POST("employee/update")
    suspend fun updateEmployee(@Body data: EmployeeCreateRequestModel): Response<ProductResponseModel>



    @POST("tickets/ticketchecklist/filter_all")
    fun getCheckAllList(@Body data: HashMap<String, String>): Response<ResponseCheckListTicket>

    @POST("tickets/filter_escalation")
    fun getEscallationList(@Body data: JsonObject): Call<EscallationResponseModel>

    @POST("tickets/history/filter_all")
    fun getHistoryList(@Body data: HashMap<String, Int>): Call<TicketHistoryResponse>


    @POST("tickets/conversation/filter_all")
    fun getTicketConversationAll(@Body data: HashMap<String, Int>): Call<TicketHistoryResponse>

    @POST("activity/chatter_all")
    fun getFollowUpAllList(@Body data: HashMap<String, Any>): Call<ResponseFollowUp>

    @POST("activity/chatter")
    fun addFollowUp(@Body data: HashMap<String, Any>): Call<ResponseFollowUp>

    @POST("tickets/ticketchecklist/filter_all")
    fun getAllCheckList(@Body data: HashMap<String, String>): Call<ResponseCheckListTicket>

    @POST("inspection/all_filter_page")
    fun getAllIssueList(@Body data: JsonObject): Call<IssueListResponseModel>

    @POST("servicecontract/all_filter_page")
    fun getServiceProductList(@Body body: ServiceContractListRequest): Call<ServiceContractListResponseModel>

    @POST("businesspartner/item/all_filter_page")
    fun getProductAllList(@Body body: ServiceContractListRequest): Call<ProductResponseModel>

    @POST("employee/all_filter_page")
    fun getEmployeeList(@Body body: EmployeeRequestModel): Call<EmployeeResponseModel>

    @POST("order/all_filter_page")
    fun getOrderList(@Body body: OrderListRequestModel): Call<OrderListResponseModel>


    /*** UNUSED API's ***/
    @GET("tickets/dropdowns/type")
    fun getAllTypeList(): Call<BPBranchResponse>

    @GET("tickets/dropdowns/priority")
    fun getAllPriorityList(): Call<BPBranchResponse>

    @POST("employee/all_filter ")
    fun getEmployeefilterlist(@Body data: NewLoginData): Call<AccountBPResponse>

    @POST("delivery/bp_wise")
    fun getAllorderList(@Body data: AccountBpData): Call<OrderDataResponse>

    @POST("order/all_bybp")
    fun getCustomerOrderAllOrderList(@Body data: AccountBpData): Call<OrderDataResponse>

    @POST("employee/login_support")
    fun loginEmployee(@Body logInDetail: NewLoginData): Call<ResponseEmployeeAtLogin>

    @POST("employee/service_employee_list")
    fun allemployeelist(@Body postin: HashMap<String, String>): Call<LogInResponse>

    @POST("delivery/one")
    fun getparticularorder(@Body id: TicketHistoryData): Call<OrderDataResponse>

    //todo api call in adapter

    @POST("tickets/assign")
    fun assignticketemployee(@Body assignticket: NewLoginData): Call<LogInResponse>

    @POST("tickets/servicechecklist/update")
    fun updatechecklist(@Body data: TicketChecklistData): Call<TicketChecklistResponse>

    //todo calling in work manager

    @POST("activity/maps")
    @Headers("Content-Type: application/json;charset=UTF-8")
    fun sendMaplatlong(@Body mapData: MapData): Call<ResponseAddTicket>

    @POST("tickets/ticket_closed")
    fun closedTicketApi(@Body mapData: JsonObject): Call<LogInResponse>


    @GET("tickets/request_type_all")
    fun getAllComplainDetail(): Call<ComplainDetailResponseModel>

    @GET("tickets/defect_found_all")
    fun getAllDefectFoundList(): Call<ComplainDetailResponseModel>

    @GET("tickets/reason_defect_found_all")
    fun getAllReasonDefectFoundList(): Call<ComplainDetailResponseModel>

    @GET("tickets/remdial_action_all")
    fun getAllRemedialActionList(): Call<ComplainDetailResponseModel>

    @POST("tickets/create_ticket_addon")
    fun createAddOnTimeApi(@Body json: JsonObject): Call<ComplainDetailResponseModel>

    @POST("attachment/delete")
    suspend fun attachmentDelete(@Body tickethistory: HashMap<String, Any>): Response<AllAttachmentResponse>

    @POST("attachment/all")
    fun allAttachment(@Body tickethistory: HashMap<String, Any>): Call<AllAttachmentResponse>


    /***** AHUJA SONS API'S *****/

    @POST("order_request/all_filter_page")
    fun callOrderListApi(@Body data: AllOrderRequestModel): Call<AllOrderListModel>

    @POST("order/one")
    suspend fun getOrderOneDetail(@Body data: JsonObject): Response<OrderOneResponseModel>

    @POST("work_queue/all_filter_page")
    fun callAllWorkQueueApi(@Body data: WorkQueueRequestModel): Call<AllWorkQueueResponseModel>


    @GET("businesspartner/all")
    suspend fun getBPList(): Response<AccountBPResponse>
    @GET("doctor_master/all")
    suspend fun getDoctorNameList(): Response<DoctorNameListModel>

    @POST("order_request/create")
    suspend fun createdOrderRequest(@Body data: MultipartBody): Response<OrderOneResponseModel>

    @POST("order_request/create")
    fun createdOrderRequestMVC(@Body data: MultipartBody): Call<OrderOneResponseModel>

    @POST("order_request/update")
    fun updateOrderRequestMVC(@Body data: MultipartBody): Call<OrderOneResponseModel>

    @POST("work_queue/workQueue_details")
    suspend fun callWorkQueueDetailApi(@Body data: JsonObject): Response<AllWorkQueueResponseModel>

    @POST("delivery/delivery_details")
    suspend fun callDeliveryDetailApi(@Body data: JsonObject): Response<AllWorkQueueResponseModel>

    @POST("order_request/one")
    suspend fun callOrderRequestOneApi(@Body data: JsonObject): Response<OrderOneResponseModel>

    @POST("work_queue/workQueue_details")
    fun callWorkQueueDetailApiMVC(@Body data: JsonObject): Call<AllWorkQueueResponseModel>

    @POST("delivery/all_items_with_status")
    fun callAllOrderItemList(@Body data: JsonObject): Call<AllItemsForOrderModel>

    @POST("delivery/items_by_delivery")
    fun callDeliveryItem(@Body data: JsonObject): Call<DeliveryDetailItemListModel>


    @POST("order_request/get_order_dependency")
    fun getDependencyList(@Body data: JsonObject): Call<AllDependencyAndErrandsListModel>

    @POST("order_request/get_order_errands")
    fun getErrandsList(@Body data: JsonObject): Call<AllErrandsListModel>

    @POST("order_request/link_sap_order")
    fun getSAPLinkOrderApi(@Body data: JsonObject): Call<AllDependencyAndErrandsListModel>

    @POST("order_request/unlink_sap_order")
    fun getSAPUnLinkOrderApi(@Body data: JsonObject): Call<AllDependencyAndErrandsListModel>


    @POST("order_request/get_order_items")
    fun getAllItemListApi(@Body data: JsonObject): Call<AllItemListResponseModel>

    @POST("order_request/create_dependency")
    fun createDependency(@Body data: CreateDependencyRequestModel): Call<AllItemListResponseModel>

    @POST("order_request/create_errands")
    fun createErrands(@Body jsonObject: JsonObject): Call<AllItemListResponseModel>

    @POST("order_request/update_errands")
    fun updateErrands(@Body jsonObject: JsonObject): Call<AllItemListResponseModel>

    @GET("master_apis/nature_errands_all")
    fun getNatureErrands(): Call<NatureErrandsResponseModel>

    @POST("order_request/ord_coordinator_task_complete")
    suspend fun completeOrderApi(@Body jsonObject: JsonObject): Response<AllWorkQueueResponseModel>

    @POST("order_request/order_prepration")
    fun OrderPreparedForCounter(@Body jsonObject: JsonObject): Call<AllWorkQueueResponseModel>

    @POST("order_request/submit_inspection_proof")
    suspend fun submitInspectionProof(@Body data: MultipartBody ): Response<AllWorkQueueResponseModel>

    @POST("order_request/submit_inspection_proof")
    fun submitInspectionProofMVC(@Body data: MultipartBody ): Call<AllWorkQueueResponseModel>

    @POST("order_request/order_inspection")
    suspend fun orderInspectionComplete(@Body data: JsonObject ): Response<AllWorkQueueResponseModel>

    @POST("order_request/get_inspection_proof")
    fun getInspectionImages(@Body data: JsonObject ): Call<UploadedPictureModel>

    @POST("delivery/delivery_assigned")
    fun createAssign(@Body data: JsonObject): Call<AllWorkQueueResponseModel>

    @POST("delivery/delivery_assigned_update")
    fun updateAssign(@Body data: JsonObject): Call<AllWorkQueueResponseModel>


    @POST("delivery/get_order_delivery")
    fun getOrderDeliveryItems(@Body data: JsonObject): Call<DeliveryItemListModel>

    @GET("employee/get_all_delivery_employee")
    fun getDeliveryPerson(): Call<DeliveryPersonEmployeeModel>

    @POST("delivery/delivery_route")
    fun getRouteList(@Body jsonObject: JsonObject): Call<RouteListModel>



}
