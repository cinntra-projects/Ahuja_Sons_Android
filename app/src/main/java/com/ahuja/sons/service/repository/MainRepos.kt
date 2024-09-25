package com.ahuja.sons.service.repository

import com.ahuja.sons.ahujaSonsClasses.model.DoctorNameListModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.AllOrderListModel
import com.ahuja.sons.ahujaSonsClasses.model.orderModel.OrderOneResponseModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllItemsForOrderModel
import com.ahuja.sons.ahujaSonsClasses.model.workQueue.AllWorkQueueResponseModel
import com.google.gson.JsonObject
import com.ahuja.sons.apibody.BodyForIssueSubCategory
import com.ahuja.sons.apihelper.Resource
import com.ahuja.sons.model.*
import com.ahuja.sons.model.BPLID
import com.ahuja.sons.model.DocumentLine
import com.ahuja.sons.newapimodel.*
import com.ahuja.sons.receiver.ResponseEmployeeAllList
import okhttp3.MultipartBody
import retrofit2.http.Body
import java.util.HashMap

interface MainRepos {


    suspend fun loginUser(data: HashMap<String, String>): Resource<ResponseEmployeeAtLogin>
    suspend fun logoutEmployeeNew(data: HashMap<String, String>): Resource<ResponseAddTicket>
    suspend fun getAssignerList(data: HashMap<String, String>): Resource<ResponseAssignedTo>
    suspend fun TestloginUser(data: HashMap<String, String>): Resource<String>
    suspend fun addTicket(bodyAddTicketData: AddTicketRequestModel): Resource<ResponseAddTicket>
    suspend fun updateTicket(data: AddTicketRequestModel): Resource<ResponseAddTicket>
    suspend fun getTicketRemarksUpdate(jsonObject: JsonObject): Resource<ResponseTicket>
    suspend fun updateAssigner(data: HashMap<String, Any>): Resource<ResponseAddTicket>
    suspend fun getTicketTypeLogsInDetails(data: HashMap<String, Any>): Resource<ResponseTicketLogForTicketDetails>
    suspend fun getManTrapLog(data: HashMap<String, Any>): Resource<ResponseManTrapLog>
    suspend fun getDashboardCounterNew(): Resource<ResponseDashBoardCounter>
    suspend fun getDashboardNotifictaionCount(): Resource<NotificationCountResponseModel>
    suspend fun getQualityIssueCategory(): Resource<ResponseQualityIssueCategory>
    suspend fun getDropDownManRescue(): Resource<ResponseDropDownManRescue>
    suspend fun getQualityIssueSubCategory(data: BodyForIssueSubCategory): Resource<ResponseQualityIssueSubCategory>
    suspend fun addQualityIssueInspection(data: HashMap<String, String>): Resource<ResponseAddTicket>
    suspend fun getTodayTicket(data: HashMap<String, Any>): Resource<ResponseTicket>
    suspend fun getTicketOne(data: HashMap<String, String>): Resource<ResponseTicket>

    suspend fun getCustomerMvvmOrderAllOrderList(data: HashMap<String, String>): Resource<ResponseParticularCustomerOrder>
    suspend fun getOrderOne(data: HashMap<String, String>): Resource<ResponseOrderOne>
    suspend fun getTicketSubType(data: HashMap<String, String>): Resource<ResponseSubType>
    suspend fun getTicketType(): Resource<ResponseTypeTickets>
    suspend fun getAllEmployeeList(): Resource<ResponseEmployeeAllList>
    suspend fun getTicketCheckList(data: HashMap<String, String>): Resource<ResponseCheckListTicket>
    suspend fun getUpdateTicketCheckList(data: BodyUpdateCheckListItem): Resource<ResponseCheckListTicket>
    suspend fun getQualityInspectionList(data: PayLoadForInspectionList): Resource<ResponseQualityInspection>
    suspend fun updateManRescueLog(data: HashMap<String, String>): Resource<ResponseAddTicket>

    suspend fun getCustomerForContact(): Resource<ResponseCustomerListForContact>
    suspend fun getContactNameList(jsonObject: JsonObject): Resource<ContactNameListResponseModel>
    suspend fun getBranchAllList(jsonObject: JsonObject): Resource<BranchAllListResponseModel>
    suspend fun getItemAllList(jsonObject: JsonObject): Resource<ItemAllListResponseModel>
    suspend fun getItemsByTicket(jsonObject: JsonObject): Resource<ItemAllListResponseModel>
    suspend fun getScopeWorkList(): Resource<ScopOfWorkResponseModel>

    suspend fun getAnnouncementList(): Resource<ResponseAnnouncement>

    /***Chanchal***/

    suspend fun getAllBPList(): Resource<AccountBPResponse>

    suspend fun getAccountitemList(data: AccountBpData): Resource<AccountItemResponse>


    suspend fun allattachment(tickethistory: HashMap<String, Any>): Resource<AllAttachmentResponse>

    suspend fun addnewCustomer(businessPartnerDataNew: BusinessPartnerDataNew): Resource<AccountBPResponse>


    suspend fun getPaymentTerm(): Resource<BPBranchResponse>

    suspend fun getIndustryList(): Resource<BPBranchResponse>

    suspend fun getAllBranchList(): Resource<BPBranchResponse>

    suspend fun getAllZoneList(): Resource<BPBranchResponse>

    suspend fun getTicketDetails(data: TicketDetailsData): Resource<TicketDetailsModel>

    suspend fun getbpwiseTicket(data: ContactEmployee): Resource<DashboardTicketCounterResponse>

    suspend fun getDepartMent(): Resource<DepartMentDetail>

    suspend fun getRole(): Resource<DepartMentDetail>

    suspend fun createcontact(createContact: CreateContactData): Resource<ResponseAddTicket>

    suspend fun getCountryList(): Resource<BPBranchResponse>

    suspend fun getStateList(stateData: BPLID): Resource<BPBranchResponse>

    suspend fun getSalesEmplyeeList(data: NewLoginData): Resource<SalesEmployeeResponse>

    suspend fun getSalesEmployeeAllList(): Resource<SalesEmployeeResponse>

    suspend fun getEmployeeRoleList(): Resource<EmployeeRoleResponseModel>

    suspend fun getEmployeeSubDepList(): Resource<EmployeeSubDepResponseModel>

    suspend fun getallcontact(): Resource<ContactResponse>

    suspend fun getItemlist(data: DocumentLine): Resource<AccountItemResponse>

    suspend fun getTicketHistory(data: HashMap<String, Int>): Resource<TicketHistoryResponse>

    suspend fun getFollowUpList(data: HashMap<String, Any>): Resource<ResponseFollowUp>

    suspend fun getticketchecklist(data: TicketHistoryData): Resource<TicketChecklistResponse>

    suspend fun startstoptimer(data: HashMap<String, Any>): Resource<TicketChecklistResponse>

    suspend fun resettimer(data: HashMap<String, Any>): Resource<TicketChecklistResponse>

    suspend fun getTicketConversation(data: HashMap<String, Int>): Resource<TicketHistoryResponse>

    suspend fun createTicketConversation(data: TicketHistoryData): Resource<TicketHistoryResponse>

    suspend fun getitemwiseticket(data: TicketDataModel): Resource<ResponseTicket>

    suspend fun getAllCategory(data: NewLoginData): Resource<ItemCategoryResponse>

    suspend fun getAllCategoryList(): Resource<ItemCategoryResponse>

    suspend fun particularTicketDetails(data: HashMap<String, Int>): Resource<ResponseTicket>

    suspend fun createPartRequest(data: CreatePartrequestpayload): Resource<CreatePartRequestResponse>

    suspend fun getAllpartrequest(data: HashMap<String, Any>): Resource<ResponseAllPart>

    suspend fun getAllPartRequestItem(TicketId: HashMap<String, Any>): Resource<ResponsePartOne>

    suspend fun acceptRejectTicket(logInDetail: NewLoginData): Resource<LogInResponse>

    suspend fun signandconfirm(data: MultipartBody): Resource<TicketDetailsModel>

    suspend fun imageupload(requestBody: MultipartBody): Resource<TicketDataResponse>

    suspend fun multiplfileupload(requestBody: MultipartBody): Resource<TicketDataResponse>

    suspend fun prUpload(requestBody: MultipartBody): Resource<TicketDataResponse>

    suspend fun createTicketTypeItems(requestBody: MultipartBody): Resource<TicketDataResponse>

    suspend fun addAttachment(requestBody: MultipartBody): Resource<TicketDataResponse>

    suspend fun updateTicketTypeItems(requestBody: JsonObject): Resource<TicketDataResponse>

    suspend fun ticketMaintenanceOneApi(requestBody: JsonObject): Resource<TicketPreventiveMaintainanceResponse>

    suspend fun ticketInstallationOneApi(requestBody: JsonObject): Resource<InstallationTicketOneResponse>

    suspend fun ticketSiteSurveyOneApi(requestBody: JsonObject): Resource<SiteSurveyTicketResponse>

    suspend fun getIssueCategoryList(): Resource<IssueCategoryListResponseModel>

    suspend fun getSolutionList(jsonObject: SolutionRequestModel): Resource<SolutionListResponseModel>

    suspend fun createIssue(@Body jsonObject: JsonObject): Resource<SolutionListResponseModel>

    suspend fun ServiceContractOneApi(@Body body: JsonObject): Resource<ServiceContractListResponseModel>

    suspend fun createServiceContract(body: CreateServiceContractRequestModel): Resource<ServiceContractListResponseModel>

    suspend fun getProductOneDetailApi(body: JsonObject): Resource<ProductResponseModel>

    suspend fun createEmployee(body: EmployeeCreateRequestModel): Resource<ProductResponseModel>

    suspend fun updateEmployee(body: EmployeeCreateRequestModel): Resource<ProductResponseModel>

    suspend fun callEmployeeOneApi(body: JsonObject): Resource<EmployeeOneModel>

    suspend fun createProduct(data: AddProductRequestModel): Resource<ProductResponseModel>

    suspend fun updateProduct(data: AddProductRequestModel): Resource<ProductResponseModel>

    suspend fun attachmentDelete(tickethistory: HashMap<String, Any>): Resource<AllAttachmentResponse>


    /**** AHUJA Sons***/
    suspend fun getOrderOneDetail(data: JsonObject): Resource<OrderOneResponseModel>

    suspend fun getBPList(): Resource<AccountBPResponse>

    suspend fun getDoctorNameList(): Resource<DoctorNameListModel>
    suspend fun createdOrderRequest(data: MultipartBody): Resource<OrderOneResponseModel>
    suspend fun callWorkQueueDetailApi(data: JsonObject): Resource<AllWorkQueueResponseModel>
    suspend fun callDeliveryDetailApi(data: JsonObject): Resource<AllWorkQueueResponseModel>
    suspend fun callOrderRequestOneApi(data: JsonObject): Resource<OrderOneResponseModel>
    suspend fun completeOrderApi(data: JsonObject): Resource<AllWorkQueueResponseModel>
    suspend fun submitInspectionProof(data: MultipartBody): Resource<AllWorkQueueResponseModel>
    suspend fun orderInspectionComplete(data: JsonObject): Resource<AllWorkQueueResponseModel>
    suspend fun getDeliveryPersonComplete(data: JsonObject): Resource<AllWorkQueueResponseModel>


}