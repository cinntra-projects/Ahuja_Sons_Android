package com.ahuja.sons.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import com.ahuja.sons.apibody.BodyForIssueSubCategory
import com.ahuja.sons.apihelper.Event
import com.ahuja.sons.apihelper.Resource
import com.ahuja.sons.apiservice.Apis
import com.ahuja.sons.model.*
import com.ahuja.sons.model.BPLID
import com.ahuja.sons.model.DocumentLine
import com.ahuja.sons.newapimodel.*
import com.ahuja.sons.receiver.ResponseEmployeeAllList
import com.ahuja.sons.service.repository.MainRepos
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.util.HashMap

class MainViewModel(
    val app: Application,
    private val repos: MainRepos,
    private val dispatchers: CoroutineDispatcher = Dispatchers.Main,
    val fanxApi: Apis
) : AndroidViewModel(app) {

    private val _userStatus = MutableLiveData<Event<Resource<ResponseEmployeeAtLogin>>>()
    val userStatus: LiveData<Event<Resource<ResponseEmployeeAtLogin>>> = _userStatus

    private val _userlogout = MutableLiveData<Event<Resource<ResponseAddTicket>>>()
    val userlogout: LiveData<Event<Resource<ResponseAddTicket>>> = _userlogout

    private val _assignUserListStatus = MutableLiveData<Event<Resource<ResponseAssignedTo>>>()
    val assignUserListStatus: LiveData<Event<Resource<ResponseAssignedTo>>> = _assignUserListStatus

    private val _logticketTyelist =
        MutableLiveData<Event<Resource<ResponseTicketLogForTicketDetails>>>()
    val logticketTyelist: LiveData<Event<Resource<ResponseTicketLogForTicketDetails>>> =
        _logticketTyelist

    private val _manTrapLog = MutableLiveData<Event<Resource<ResponseManTrapLog>>>()
    val manTrapLog: LiveData<Event<Resource<ResponseManTrapLog>>> = _manTrapLog


    private val _dropDownManRescue = MutableLiveData<Event<Resource<ResponseDropDownManRescue>>>()
    val dropDownManRescue: LiveData<Event<Resource<ResponseDropDownManRescue>>> = _dropDownManRescue

    private val _userTestStatus = MutableLiveData<Event<Resource<String>>>()
    val userTestStatus: LiveData<Event<Resource<String>>> = _userTestStatus


    private val _dashboardCounter = MutableLiveData<Event<Resource<ResponseDashBoardCounter>>>()
    val dashboardCounter: LiveData<Event<Resource<ResponseDashBoardCounter>>> = _dashboardCounter

    private val _notificationCount = MutableLiveData<Event<Resource<NotificationCountResponseModel>>>()
    val notificationCount: LiveData<Event<Resource<NotificationCountResponseModel>>> = _notificationCount

    private val _todaysTicket = MutableLiveData<Event<Resource<ResponseTicket>>>()
    val todaysTicket: LiveData<Event<Resource<ResponseTicket>>> = _todaysTicket


    private val _particularTicket = MutableLiveData<Event<Resource<ResponseTicket>>>()
    val particularTicket: LiveData<Event<Resource<ResponseTicket>>> = _particularTicket

    private val _particularTicketViewPager = MutableLiveData<Event<Resource<ResponseTicket>>>()
    val particularTicketViewPager: LiveData<Event<Resource<ResponseTicket>>> =
        _particularTicketViewPager

    private val _checkList = MutableLiveData<Event<Resource<ResponseCheckListTicket>>>()
    val checkList: LiveData<Event<Resource<ResponseCheckListTicket>>> = _checkList
    private val _updatecheckList = MutableLiveData<Event<Resource<ResponseCheckListTicket>>>()
    val updatecheckList: LiveData<Event<Resource<ResponseCheckListTicket>>> = _updatecheckList

    private val _getCustomerParticularOrder =
        MutableLiveData<Event<Resource<ResponseParticularCustomerOrder>>>()
    val getCustomerParticularOrder: LiveData<Event<Resource<ResponseParticularCustomerOrder>>> =
        _getCustomerParticularOrder

    private val _getAnnouncementist = MutableLiveData<Event<Resource<ResponseAnnouncement>>>()
    val getAnnouncementist: LiveData<Event<Resource<ResponseAnnouncement>>> = _getAnnouncementist


    private val _orderOne = MutableLiveData<Event<Resource<ResponseOrderOne>>>()
    val orderOne: LiveData<Event<Resource<ResponseOrderOne>>> = _orderOne

    private val _typeTicket = MutableLiveData<Event<Resource<ResponseTypeTickets>>>()
    val typeTicket: LiveData<Event<Resource<ResponseTypeTickets>>> = _typeTicket

    private val _employeesAll = MutableLiveData<Event<Resource<ResponseEmployeeAllList>>>()
    val employeesAll: LiveData<Event<Resource<ResponseEmployeeAllList>>> = _employeesAll

    private val _subTypeTicket = MutableLiveData<Event<Resource<ResponseSubType>>>()
    val subTypeTicket: LiveData<Event<Resource<ResponseSubType>>> = _subTypeTicket

    private val _createTicket = MutableLiveData<Event<Resource<ResponseAddTicket>>>()
    val createTicket: LiveData<Event<Resource<ResponseAddTicket>>> = _createTicket

    private val _updateAssigner = MutableLiveData<Event<Resource<ResponseAddTicket>>>()
    val updateAssigner: LiveData<Event<Resource<ResponseAddTicket>>> = _updateAssigner

    private val _customerListContact =
        MutableLiveData<Event<Resource<ResponseCustomerListForContact>>>()
    val customerListContact: LiveData<Event<Resource<ResponseCustomerListForContact>>> =
        _customerListContact

    private val _contactNameList = MutableLiveData<Event<Resource<ContactNameListResponseModel>>>()
    val contactNameList: LiveData<Event<Resource<ContactNameListResponseModel>>> = _contactNameList

    private val _branchAllList = MutableLiveData<Event<Resource<BranchAllListResponseModel>>>()
    val branchAllList: LiveData<Event<Resource<BranchAllListResponseModel>>> = _branchAllList

    private val _itemAllList = MutableLiveData<Event<Resource<ItemAllListResponseModel>>>()
    val itemAllList: LiveData<Event<Resource<ItemAllListResponseModel>>> = _itemAllList

    private val _scopeOfWorkData = MutableLiveData<Event<Resource<ScopOfWorkResponseModel>>>()
    val scopeOfWorkData: LiveData<Event<Resource<ScopOfWorkResponseModel>>> = _scopeOfWorkData


    private val _inspectionIssueCategory =
        MutableLiveData<Event<Resource<ResponseQualityIssueCategory>>>()
    val inspectionIssueCategory: LiveData<Event<Resource<ResponseQualityIssueCategory>>> =
        _inspectionIssueCategory

    private val _inspectionIssueSubCategory =
        MutableLiveData<Event<Resource<ResponseQualityIssueSubCategory>>>()
    val inspectionIssueSubCategory: LiveData<Event<Resource<ResponseQualityIssueSubCategory>>> =
        _inspectionIssueSubCategory

    private val _inspectionList = MutableLiveData<Event<Resource<ResponseQualityInspection>>>()
    val inspectionList: LiveData<Event<Resource<ResponseQualityInspection>>> = _inspectionList

    private val _addQuality = MutableLiveData<Event<Resource<ResponseAddTicket>>>()
    val addQuality: LiveData<Event<Resource<ResponseAddTicket>>> = _addQuality

    private val _updateManRescue = MutableLiveData<Event<Resource<ResponseAddTicket>>>()
    val updateManRescue: LiveData<Event<Resource<ResponseAddTicket>>> = _updateManRescue

    /****CHANCHAL**/
    private val _businessPartnerList = MutableLiveData<Event<Resource<AccountBPResponse>>>()
    val businessPartnerList: LiveData<Event<Resource<AccountBPResponse>>> = _businessPartnerList

    private val _equipmentList = MutableLiveData<Event<Resource<AccountItemResponse>>>()
    val equipmentList: LiveData<Event<Resource<AccountItemResponse>>> = _equipmentList

    private val _allAttachmentList = MutableLiveData<Event<Resource<AllAttachmentResponse>>>()
    val allAttachmentList: LiveData<Event<Resource<AllAttachmentResponse>>> = _allAttachmentList

    private val _addNewCustomer = MutableLiveData<Event<Resource<AccountBPResponse>>>()
    val addNewCustomer: LiveData<Event<Resource<AccountBPResponse>>> = _addNewCustomer

    private val _searchTicketsItem = MutableLiveData<Event<Resource<ResponseTicket>>>()
    val searchTicketsItem: LiveData<Event<Resource<ResponseTicket>>> = _searchTicketsItem

    private val _getAllData = MutableLiveData<Event<Resource<BPBranchResponse>>>()
    val getAllData: LiveData<Event<Resource<BPBranchResponse>>> = _getAllData

    private val _getTicketDetails = MutableLiveData<Event<Resource<TicketDetailsModel>>>()
    val getTicketDetails: LiveData<Event<Resource<TicketDetailsModel>>> = _getTicketDetails

    private val _getBPWiseTicket = MutableLiveData<Event<Resource<DashboardTicketCounterResponse>>>()
    val getBPWiseTicket: LiveData<Event<Resource<DashboardTicketCounterResponse>>> = _getBPWiseTicket

    private val _getDepartment = MutableLiveData<Event<Resource<DepartMentDetail>>>()
    val getDepartment: LiveData<Event<Resource<DepartMentDetail>>> = _getDepartment

    private val _createContact = MutableLiveData<Event<Resource<ResponseAddTicket>>>()
    val createContact: LiveData<Event<Resource<ResponseAddTicket>>> = _createContact

    private val _salesEmployeeResponse = MutableLiveData<Event<Resource<SalesEmployeeResponse>>>()
    val salesEmployeeResponse: LiveData<Event<Resource<SalesEmployeeResponse>>> =
        _salesEmployeeResponse

    private val _employeeRoleList = MutableLiveData<Event<Resource<EmployeeRoleResponseModel>>>()
    val employeeRoleList: LiveData<Event<Resource<EmployeeRoleResponseModel>>> = _employeeRoleList

    private val _employeeSubDepList =
        MutableLiveData<Event<Resource<EmployeeSubDepResponseModel>>>()
    val employeeSubDepList: LiveData<Event<Resource<EmployeeSubDepResponseModel>>> =
        _employeeSubDepList

    private val _contactResponse = MutableLiveData<Event<Resource<ContactResponse>>>()
    val contactResponse: LiveData<Event<Resource<ContactResponse>>> = _contactResponse

    private val _getAllItemList = MutableLiveData<Event<Resource<AccountItemResponse>>>()
    val getAllItemList: LiveData<Event<Resource<AccountItemResponse>>> = _getAllItemList

    private val _ticketAllHistory = MutableLiveData<Event<Resource<TicketHistoryResponse>>>()
    val ticketAllHistory: LiveData<Event<Resource<TicketHistoryResponse>>> = _ticketAllHistory

    private val _allFollowUp = MutableLiveData<Event<Resource<ResponseFollowUp>>>()
    val allFollowUp: LiveData<Event<Resource<ResponseFollowUp>>> = _allFollowUp

    private val _ticketCheckList = MutableLiveData<Event<Resource<TicketChecklistResponse>>>()
    val ticketCheckList: LiveData<Event<Resource<TicketChecklistResponse>>> = _ticketCheckList

    private val _allItemWiseTicket = MutableLiveData<Event<Resource<ResponseTicket>>>()
    val allItemWiseTicket: LiveData<Event<Resource<ResponseTicket>>> = _allItemWiseTicket

    private val _itemCategoryList = MutableLiveData<Event<Resource<ItemCategoryResponse>>>()
    val itemCategoryList: LiveData<Event<Resource<ItemCategoryResponse>>> = _itemCategoryList

    private val _createPartRequest = MutableLiveData<Event<Resource<CreatePartRequestResponse>>>()
    val createPartRequest: LiveData<Event<Resource<CreatePartRequestResponse>>> = _createPartRequest

    private val _partFilterAllResponse = MutableLiveData<Event<Resource<ResponseAllPart>>>()
    val partFilterAllResponse: LiveData<Event<Resource<ResponseAllPart>>> = _partFilterAllResponse

    private val _partRequestOneResponse = MutableLiveData<Event<Resource<ResponsePartOne>>>()
    val partRequestOneResponse: LiveData<Event<Resource<ResponsePartOne>>> = _partRequestOneResponse

    private val _ticketAcceptRejectResponse = MutableLiveData<Event<Resource<LogInResponse>>>()
    val ticketAcceptRejectResponse: LiveData<Event<Resource<LogInResponse>>> =
        _ticketAcceptRejectResponse


    private val _ticketSignnConfirmResponse = MutableLiveData<Event<Resource<TicketDetailsModel>>>()
    val ticketSignnConfirmResponse: LiveData<Event<Resource<TicketDetailsModel>>> =
        _ticketSignnConfirmResponse

    private val _customerUpload = MutableLiveData<Event<Resource<TicketDataResponse>>>()
    val customerUpload: LiveData<Event<Resource<TicketDataResponse>>> = _customerUpload

    private val _preventiveMaintainanceOneData =
        MutableLiveData<Event<Resource<TicketPreventiveMaintainanceResponse>>>()
    val preventiveMaintainanceOneData: LiveData<Event<Resource<TicketPreventiveMaintainanceResponse>>> =
        _preventiveMaintainanceOneData

    private val _installationOneData =
        MutableLiveData<Event<Resource<InstallationTicketOneResponse>>>()
    val installationOneData: LiveData<Event<Resource<InstallationTicketOneResponse>>> =
        _installationOneData

    private val _siteSurveyOneData = MutableLiveData<Event<Resource<SiteSurveyTicketResponse>>>()
    val siteSurveyOneData: LiveData<Event<Resource<SiteSurveyTicketResponse>>> = _siteSurveyOneData

    private val _allSparePartApiList = MutableLiveData<Event<Resource<SpareItemListApiModel>>>()
    val allSparePartApiList: LiveData<Event<Resource<SpareItemListApiModel>>> = _allSparePartApiList

    private val _IssueCategoryList =
        MutableLiveData<Event<Resource<IssueCategoryListResponseModel>>>()
    val IssueCategoryList: LiveData<Event<Resource<IssueCategoryListResponseModel>>> =
        _IssueCategoryList

    private val _solutionList = MutableLiveData<Event<Resource<SolutionListResponseModel>>>()
    val solutionList: LiveData<Event<Resource<SolutionListResponseModel>>> = _solutionList

    private val _serviceContractList =
        MutableLiveData<Event<Resource<ServiceContractListResponseModel>>>()
    val serviceContractList: LiveData<Event<Resource<ServiceContractListResponseModel>>> =
        _serviceContractList

    private val _productOneDetailData = MutableLiveData<Event<Resource<ProductResponseModel>>>()
    val productOneDetailData: LiveData<Event<Resource<ProductResponseModel>>> =
        _productOneDetailData

    private val _employeeOneDetail = MutableLiveData<Event<Resource<EmployeeOneModel>>>()
    val employeeOneDetail: LiveData<Event<Resource<EmployeeOneModel>>> = _employeeOneDetail

    private val _orderOneDetail = MutableLiveData<Event<Resource<OrderOneResponseModel>>>()
    val orderOneDetail: LiveData<Event<Resource<OrderOneResponseModel>>> = _orderOneDetail


    fun getLoginUser(data: HashMap<String, String>) {
        _userStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.loginUser(data)
            _userStatus.postValue(Event(result))
        }
    }

    fun logoutEmployeeNew(data: HashMap<String, String>) {
        _userlogout.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.logoutEmployeeNew(data)
            _userlogout.postValue(Event(result))
        }
    }

    fun getAssignerList(data: HashMap<String, String>) {
        _assignUserListStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getAssignerList(data)
            _assignUserListStatus.postValue(Event(result))
        }
    }


    fun loginTestUser(data: HashMap<String, String>) {
        _userTestStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.TestloginUser(data)
            _userTestStatus.postValue(Event(result))
        }
    }

    fun createTicket(addTicketData: AddTicketRequestModel) {
        _createTicket.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.addTicket(addTicketData)
            _createTicket.postValue(Event(result))
        }
    }


    fun updateParticularTicket(data: AddTicketRequestModel) {
        _createTicket.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.updateTicket(data)
            _createTicket.postValue(Event(result))
        }
    }

    fun getTicketRemarksUpdate(jsonObject: JsonObject) {
        _todaysTicket.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getTicketRemarksUpdate(jsonObject)
            _todaysTicket.postValue(Event(result))
        }
    }

    fun updateManRescueLog(data: HashMap<String, String>) {
        _updateManRescue.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.updateManRescueLog(data)
            _updateManRescue.postValue(Event(result))
        }
    }

    fun addQualityInspection(data: HashMap<String, String>) {
        _addQuality.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.addQualityIssueInspection(data)
            _addQuality.postValue(Event(result))
        }
    }


    fun getDashboardCounter() {
        Log.e(TAG, "getDashboardCounter: ")
        _dashboardCounter.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getDashboardCounterNew()
            _dashboardCounter.postValue(Event(result))
        }
    }


    fun getDashboardNotifictaionCount() {
        Log.e(TAG, "getDashboardCounter: ")
        _notificationCount.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getDashboardNotifictaionCount()
            _notificationCount.postValue(Event(result))
        }
    }


    fun getDropDownManRescue() {
        Log.e(TAG, "getDropDownManRescue: ")
        _dropDownManRescue.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getDropDownManRescue()
            _dropDownManRescue.postValue(Event(result))
        }
    }


    fun getTodayTicket(data: HashMap<String, Any>) {
        Log.e(TAG, "getTodayTicket: ")
        _todaysTicket.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getTodayTicket(data)
            _todaysTicket.postValue(Event(result))
        }
    }


    fun getTicketOne(data: HashMap<String, String>) {
        Log.e(TAG, "getTicketOne: ")
        _particularTicket.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getTicketOne(data)
            _particularTicket.postValue(Event(result))
        }
    }

    fun getTicketOneViewPager(data: HashMap<String, String>) {
        Log.e(TAG, "getTicketOneViewPager: ")
        _particularTicketViewPager.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getTicketOne(data)
            _particularTicketViewPager.postValue(Event(result))
        }
    }


    fun getCheckListOfTicket(data: HashMap<String, String>) {
        Log.e(TAG, "getCheckListOfTicket: ")
        _checkList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getTicketCheckList(data)
            _checkList.postValue(Event(result))
        }
    }

    fun updateAssigner(data: HashMap<String, Any>) {
        Log.e(TAG, "updateAssigner: ")
        _updateAssigner.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.updateAssigner(data)
            _updateAssigner.postValue(Event(result))
        }
    }

    fun getTicketTypeLogsInDetails(data: HashMap<String, Any>) {
        Log.e(TAG, "getTicketTypeLogsInDetails: ")
        _logticketTyelist.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getTicketTypeLogsInDetails(data)
            _logticketTyelist.postValue(Event(result))
        }
    }

    fun getManTrapLog(data: HashMap<String, Any>) {
        Log.e(TAG, "getManTrapLog: ")
        _manTrapLog.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getManTrapLog(data)
            _manTrapLog.postValue(Event(result))
        }
    }

    fun getCustomerListForContact() {
        Log.e(TAG, "getCustomerListForContact: ")
        _customerListContact.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getCustomerForContact()
            _customerListContact.postValue(Event(result))
        }
    }


    fun getContactNameList(jsonObject: JsonObject) {
        Log.e(TAG, "getContactNameList: ")
        _contactNameList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getContactNameList(jsonObject)
            _contactNameList.postValue(Event(result))
        }
    }


    fun getBranchAllList(jsonObject: JsonObject) {
        Log.e(TAG, "getBranchList: ")
        _branchAllList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getBranchAllList(jsonObject)
            _branchAllList.postValue(Event(result))
        }
    }

    fun getItemAllList(jsonObject: JsonObject) {
        Log.e(TAG, "getItemAllList: ")
        _itemAllList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getItemAllList(jsonObject)
            _itemAllList.postValue(Event(result))
        }
    }


    fun getItemsByTicket(jsonObject: JsonObject) {
        Log.e(TAG, "getItemsByTicket: ")
        _itemAllList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getItemsByTicket(jsonObject)
            _itemAllList.postValue(Event(result))
        }
    }


    fun getScopeWorkList() {
        Log.e(TAG, "getScopeWorkList: ")
        _scopeOfWorkData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getScopeWorkList()
            _scopeOfWorkData.postValue(Event(result))
        }
    }


    fun getQualityIssueCategory() {
        Log.e(TAG, "getQualityIssueCategory: ")
        _inspectionIssueCategory.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getQualityIssueCategory()
            _inspectionIssueCategory.postValue(Event(result))
        }
    }

    fun getQualityIssueSubCategory(data: BodyForIssueSubCategory) {
        Log.e(TAG, "getQualityIssueSubCategory: ")
        _inspectionIssueSubCategory.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getQualityIssueSubCategory(data)
            _inspectionIssueSubCategory.postValue(Event(result))
        }
    }


    fun getQualityInspectionList(data: PayLoadForInspectionList) {
        Log.e(TAG, "getQualityInspectionList: ")
        _inspectionList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getQualityInspectionList(data)
            _inspectionList.postValue(Event(result))
        }
    }


    fun getUpdateTicketCheckList(data: BodyUpdateCheckListItem) {
        Log.e(TAG, "getUpdateTicketCheckList: ")
        _updatecheckList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getUpdateTicketCheckList(data)
            _updatecheckList.postValue(Event(result))
        }
    }


    fun getCustomerMvvmOrderAllOrderList(data: HashMap<String, String>) {
        Log.e(TAG, "getCustomerMvvmOrderAllOrderList: ")
        _getCustomerParticularOrder.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getCustomerMvvmOrderAllOrderList(data)
            _getCustomerParticularOrder.postValue(Event(result))
        }
    }


    fun getOrderOne(data: HashMap<String, String>) {
        Log.e(TAG, "getOrderOne: ")
        _orderOne.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getOrderOne(data)
            _orderOne.postValue(Event(result))
        }
    }

    fun getSubType(data: HashMap<String, String>) {
        Log.e(TAG, "getOrderOne: ")
        _subTypeTicket.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getTicketSubType(data)
            _subTypeTicket.postValue(Event(result))
        }
    }

    fun getTypeTicket() {
        Log.e(TAG, "getTypeTicket: ")
        _typeTicket.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getTicketType()
            _typeTicket.postValue(Event(result))
        }
    }


    fun getAllEmployeeList() {
        Log.e(TAG, "getAllEmployeeList: ")
        _employeesAll.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getAllEmployeeList()
            _employeesAll.postValue(Event(result))
        }
    }


    fun getAnnouncementList() {
        Log.e(TAG, "getAnnouncementList: ")
        _getAnnouncementist.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getAnnouncementList()
            _getAnnouncementist.postValue(Event(result))
        }
    }

    private fun hasInternetConnection(): Boolean { // you can check anywhere by this method
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetworkState = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetworkState) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }


        }
        connectivityManager.activeNetworkInfo?.run {
            return when (type) {
                TYPE_WIFI -> true
                TYPE_MOBILE -> true
                TYPE_ETHERNET -> true
                else -> false

            }


        }
        return false
    }

    /****CHANCHAL****/

    fun getBPList() {
        Log.e(TAG, "getBPList: ")
        _businessPartnerList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getBPList()
            _businessPartnerList.postValue(Event(result))
        }
    }

    fun getAllBPList() {
        Log.e(TAG, "getBPList: ")
        _businessPartnerList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getAllBPList()
            _businessPartnerList.postValue(Event(result))
        }
    }

    fun getAccountitemList(data: AccountBpData) {
        _equipmentList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getAccountitemList(data)
            _equipmentList.postValue(Event(result))
        }
    }

    fun allattachment(tickethistory: HashMap<String, Any>) {
        _allAttachmentList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.allattachment(tickethistory)
            _allAttachmentList.postValue(Event(result))
        }
    }

    fun addnewCustomer(businessPartnerDataNew: BusinessPartnerDataNew) {
        _addNewCustomer.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.addnewCustomer(businessPartnerDataNew)
            _addNewCustomer.postValue(Event(result))
        }
    }


    fun getPaymentTerm() {
        _getAllData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getPaymentTerm()
            _getAllData.postValue(Event(result))
        }
    }

    fun getIndustryList() {
        _getAllData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getIndustryList()
            _getAllData.postValue(Event(result))
        }
    }

    fun getAllBranchList() {
        _getAllData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getAllBranchList()
            _getAllData.postValue(Event(result))
        }
    }

    fun getAllZoneList() {
        _getAllData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getAllZoneList()
            _getAllData.postValue(Event(result))
        }
    }

    fun getTicketDetails(data: TicketDetailsData) {
        _getTicketDetails.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getTicketDetails(data)
            _getTicketDetails.postValue(Event(result))
        }
    }


    fun getbpwiseTicket(data: ContactEmployee) {
        _getBPWiseTicket.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getbpwiseTicket(data)
            _getBPWiseTicket.postValue(Event(result))
        }
    }

    fun getDepartMent() {
        _getDepartment.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getDepartMent()
            _getDepartment.postValue(Event(result))
        }
    }

    fun getRole() {
        _getDepartment.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getRole()
            _getDepartment.postValue(Event(result))
        }
    }

    fun createcontact(createContact: CreateContactData) {
        _createContact.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.createcontact(createContact)
            _createContact.postValue(Event(result))
        }
    }


    fun getCountryList() {
        _getAllData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getCountryList()
            _getAllData.postValue(Event(result))
        }
    }

    fun getStateList(stateData: BPLID) {
        _getAllData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getStateList(stateData)
            _getAllData.postValue(Event(result))
        }
    }

    fun getSalesEmplyeeList(data: NewLoginData) {
        _salesEmployeeResponse.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getSalesEmplyeeList(data)
            _salesEmployeeResponse.postValue(Event(result))
        }
    }


    fun getSalesEmployeeAllList() {
        _salesEmployeeResponse.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getSalesEmployeeAllList()
            _salesEmployeeResponse.postValue(Event(result))
        }
    }

    fun getEmployeeRoleList() {
        _employeeRoleList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getEmployeeRoleList()
            _employeeRoleList.postValue(Event(result))
        }
    }

    fun getEmployeeSubDepList() {
        _employeeSubDepList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getEmployeeSubDepList()
            _employeeSubDepList.postValue(Event(result))
        }
    }

    fun getallcontact() {
        _contactResponse.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getallcontact()
            _contactResponse.postValue(Event(result))
        }
    }

    fun getItemlist(data: DocumentLine) {
        _getAllItemList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getItemlist(data)
            _getAllItemList.postValue(Event(result))
        }
    }

    fun getTicketHistory(data: HashMap<String, Int>) {
        _ticketAllHistory.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getTicketHistory(data)
            _ticketAllHistory.postValue(Event(result))
        }
    }

    fun getFollowUpList(data: HashMap<String, Any>) {
        _allFollowUp.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getFollowUpList(data)
            _allFollowUp.postValue(Event(result))
        }
    }

    fun getticketchecklist(data: TicketHistoryData) {
        _ticketCheckList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getticketchecklist(data)
            _ticketCheckList.postValue(Event(result))
        }
    }

    fun startstoptimer(data: HashMap<String, Any>) {
        _ticketCheckList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.startstoptimer(data)
            _ticketCheckList.postValue(Event(result))
        }
    }

    fun resettimer(data: HashMap<String, Any>) {
        _ticketCheckList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.resettimer(data)
            _ticketCheckList.postValue(Event(result))
        }
    }

    fun getTicketConversation(data: HashMap<String, Int>) {
        _ticketAllHistory.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getTicketConversation(data)
            _ticketAllHistory.postValue(Event(result))
        }
    }

    fun createTicketConversation(data: TicketHistoryData) {
        _ticketAllHistory.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.createTicketConversation(data)
            _ticketAllHistory.postValue(Event(result))
        }
    }

    fun getitemwiseticket(data: TicketDataModel) {
        _allItemWiseTicket.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getitemwiseticket(data)
            _allItemWiseTicket.postValue(Event(result))
        }
    }

    fun getAllCategory(data: NewLoginData) {
        _itemCategoryList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getAllCategory(data)
            _itemCategoryList.postValue(Event(result))
        }
    }

    fun getAllCategoryList() {
        _itemCategoryList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getAllCategoryList()
            _itemCategoryList.postValue(Event(result))
        }
    }

    fun particularTicketDetails(data: HashMap<String, Int>) {
        _allItemWiseTicket.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.particularTicketDetails(data)
            _allItemWiseTicket.postValue(Event(result))
        }
    }

    fun createPartRequest(data: CreatePartrequestpayload) {
        _createPartRequest.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.createPartRequest(data)
            _createPartRequest.postValue(Event(result))
        }
    }

    fun getAllpartrequest(data: HashMap<String, Any>) {
        _partFilterAllResponse.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getAllpartrequest(data)
            _partFilterAllResponse.postValue(Event((result)))
        }
    }

    fun getAllPartRequestItem(data: HashMap<String, Any>) {
        _partRequestOneResponse.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getAllPartRequestItem(data)
            _partRequestOneResponse.postValue(Event(result))
        }
    }

    fun acceptRejectTicket(data: NewLoginData) {
        _ticketAcceptRejectResponse.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.acceptRejectTicket(data)
            _ticketAcceptRejectResponse.postValue(Event(result))
        }
    }


    //todo Multipart body..

    fun signandconfirm(data: MultipartBody) {
        _ticketSignnConfirmResponse.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.signandconfirm(data)
            _ticketSignnConfirmResponse.postValue(Event(result))
        }
    }

    fun imageupload(data: MultipartBody) {
        _customerUpload.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.imageupload(data)
            _customerUpload.postValue(Event(result))
        }
    }

    fun multiplfileupload(data: MultipartBody) {
        _customerUpload.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.multiplfileupload(data)
            _customerUpload.postValue(Event(result))
        }
    }

    fun prUpload(data: MultipartBody) {
        _customerUpload.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.prUpload(data)
            _customerUpload.postValue(Event(result))
        }
    }

    fun createTicketTypeItems(data: MultipartBody) {
        _customerUpload.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.createTicketTypeItems(data)
            _customerUpload.postValue(Event(result))
        }
    }

    fun addAttachment(data: MultipartBody) {
        _customerUpload.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.addAttachment(data)
            _customerUpload.postValue(Event(result))
        }
    }

    fun updateTicketTypeItems(data: JsonObject) {
        _customerUpload.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.updateTicketTypeItems(data)
            _customerUpload.postValue(Event(result))
        }
    }

    fun ticketMaintenanceOneApi(data: JsonObject) {
        _preventiveMaintainanceOneData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.ticketMaintenanceOneApi(data)
            _preventiveMaintainanceOneData.postValue(Event(result))
        }
    }

    fun ticketInstallationOneApi(data: JsonObject) {
        _installationOneData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.ticketInstallationOneApi(data)
            _installationOneData.postValue(Event(result))
        }
    }

    fun ticketSiteSurveyOneApi(data: JsonObject) {
        _siteSurveyOneData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.ticketSiteSurveyOneApi(data)
            _siteSurveyOneData.postValue(Event(result))
        }
    }


    fun getIssueCategoryList() {
        _IssueCategoryList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getIssueCategoryList()
            _IssueCategoryList.postValue(Event(result))
        }
    }

    fun getSolutionList(jsonObject: SolutionRequestModel) {
        _solutionList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getSolutionList(jsonObject)
            _solutionList.postValue(Event(result))
        }
    }

    fun createIssue(jsonObject: JsonObject) {
        _solutionList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.createIssue(jsonObject)
            _solutionList.postValue(Event(result))
        }
    }

    fun ServiceContractOneApi(jsonObject: JsonObject) {
        _serviceContractList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.ServiceContractOneApi(jsonObject)
            _serviceContractList.postValue(Event(result))
        }
    }

    fun createServiceContract(jsonObject: CreateServiceContractRequestModel) {
        _serviceContractList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.createServiceContract(jsonObject)
            _serviceContractList.postValue(Event(result))
        }
    }

    fun getProductOneDetailApi(jsonObject: JsonObject) {
        _productOneDetailData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getProductOneDetailApi(jsonObject)
            _productOneDetailData.postValue(Event(result))
        }
    }

    fun createEmployee(jsonObject: EmployeeCreateRequestModel) {
        _productOneDetailData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.createEmployee(jsonObject)
            _productOneDetailData.postValue(Event(result))
        }
    }

    fun updateEmployee(jsonObject: EmployeeCreateRequestModel) {
        _productOneDetailData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.updateEmployee(jsonObject)
            _productOneDetailData.postValue(Event(result))
        }
    }

    fun callEmployeeOneApi(jsonObject: JsonObject) {
        _employeeOneDetail.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.callEmployeeOneApi(jsonObject)
            _employeeOneDetail.postValue(Event(result))
        }
    }


    fun createProduct(jsonObject: AddProductRequestModel) {
        _productOneDetailData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.createProduct(jsonObject)
            _productOneDetailData.postValue(Event(result))
        }
    }

    fun updateProduct(jsonObject: AddProductRequestModel) {
        _productOneDetailData.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.updateProduct(jsonObject)
            _productOneDetailData.postValue(Event(result))
        }
    }

    fun attachmentDelete(tickethistory: HashMap<String, Any>) {
        _allAttachmentList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.attachmentDelete(tickethistory)
            _allAttachmentList.postValue(Event(result))
        }
    }


    /**** AHUJA Sons ***/


    fun getOrderOneDetail(jsonObject: JsonObject) {
        _orderOneDetail.postValue(Event(Resource.Loading()))
        viewModelScope.launch(Dispatchers.Main) {
            val result = repos.getOrderOneDetail(jsonObject)
            _orderOneDetail.postValue(Event(result))
        }
    }


}


private const val TAG = "MainViewModel"


