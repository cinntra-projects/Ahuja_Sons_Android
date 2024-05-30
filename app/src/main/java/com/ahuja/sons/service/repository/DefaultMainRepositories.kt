package com.ahuja.sons.service.repository

import com.google.gson.JsonObject
import com.ahuja.sons.apibody.BodyForIssueSubCategory
import com.ahuja.sons.apihelper.Resource
import com.ahuja.sons.apihelper.safeCall
import com.ahuja.sons.apiservice.ApiClient
import com.ahuja.sons.globals.ApiPayloadKeys
import com.ahuja.sons.globals.Global
import com.pixplicity.easyprefs.library.Prefs
import com.ahuja.sons.model.*
import com.ahuja.sons.model.BPLID
import com.ahuja.sons.model.DocumentLine
import com.ahuja.sons.newapimodel.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody


class DefaultMainRepositories : MainRepos {

    override suspend fun loginUser(data: java.util.HashMap<String, String>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.loginEmployeeNew(data)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun logoutEmployeeNew(data: java.util.HashMap<String, String>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.logoutEmployeeNew(data)

                Resource.Success(response.body()!!)
            }
        }


    override suspend fun getAssignerList(data: java.util.HashMap<String, String>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getAssignerList(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun TestloginUser(data: java.util.HashMap<String, String>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.loginTest(data)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun addTicket(addTicketData: AddTicketRequestModel) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.createNewTicketMVVM(addTicketData)
            Resource.Success(response.body()!!)
        }
    }


    override suspend fun updateTicket(data: AddTicketRequestModel) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.updateParticularTicket(data)
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getTicketRemarksUpdate(jsonObject: JsonObject) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getTicketRemarksUpdate(jsonObject)
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun updateAssigner(data: HashMap<String, Any>) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.updateAssigner(data)
            Resource.Success(response.body()!!)
        }
    }


    override suspend fun getTicketTypeLogsInDetails(data: HashMap<String, Any>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getTicketTypeLogsInDetails(data)

                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getManTrapLog(data: HashMap<String, Any>) = withContext(Dispatchers.IO) {
        safeCall {
            val response =

                ApiClient().service.getManTrapLog(data)

            Resource.Success(response.body()!!)
        }
    }


    override suspend fun getDashboardCounterNew() = withContext(Dispatchers.IO) {
        safeCall {
            var hashMap = HashMap<String, String>()
            hashMap.put(ApiPayloadKeys.EMPLOYEE_ID, Prefs.getString(Global.Employee_Code))
            val response =


                ApiClient().service.getDashboardCounterNew(hashMap)

            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getDashboardNotifictaionCount() = withContext(Dispatchers.IO) {
        safeCall {
            var hashMap = HashMap<String, String>()
            hashMap.put("SalesEmployeeCode", Prefs.getString(Global.Employee_Code))
            val response = ApiClient().service.getDashboardNotifictaionCount(hashMap)

            Resource.Success(response.body()!!)
        }
    }


    override suspend fun getQualityIssueCategory() = withContext(Dispatchers.IO) {
        safeCall {
//            var hashMap = HashMap<String, String>()
//            hashMap.put(ApiPayloadKeys.EMPLOYEE_ID, Prefs.getString(Global.Employee_Code))
            val response =


                ApiClient().service.getQualityIssueCategory()

            Resource.Success(response.body()!!)
        }
    }


    override suspend fun getDropDownManRescue() = withContext(Dispatchers.IO) {
        safeCall {
//            var hashMap = HashMap<String, String>()
//            hashMap.put(ApiPayloadKeys.EMPLOYEE_ID, Prefs.getString(Global.Employee_Code))
            val response =


                ApiClient().service.getDropDownManRescue()

            Resource.Success(response.body()!!)
        }
    }


    override suspend fun getQualityIssueSubCategory(data: BodyForIssueSubCategory) =
        withContext(Dispatchers.IO) {
            safeCall {
//            var hashMap = HashMap<String, String>()
//            hashMap.put(ApiPayloadKeys.EMPLOYEE_ID, Prefs.getString(Global.Employee_Code))
                val response =


                    ApiClient().service.getQualityIssueSubCategory(data)

                Resource.Success(response.body()!!)
            }
        }


    override suspend fun addQualityIssueInspection(data: HashMap<String, String>) =
        withContext(Dispatchers.IO) {
            safeCall {
//            var hashMap = HashMap<String, String>()
//            hashMap.put(ApiPayloadKeys.EMPLOYEE_ID, Prefs.getString(Global.Employee_Code))
                val response =


                    ApiClient().service.addQualityIssueInspection(data)

                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getTodayTicket(data: java.util.HashMap<String, Any>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getTicketsByFilterNew(data)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun getCustomerMvvmOrderAllOrderList(data: java.util.HashMap<String, String>) =
        withContext(Dispatchers.IO) {
            safeCall {

                val response =


                    ApiClient().service.getCustomerMvvmOrderAllOrderList(data)

                Resource.Success(response.body()!!)
            }
        }


    override suspend fun getTicketType() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getTicketType()
            Resource.Success(response.body()!!)
        }
    }


    override suspend fun getAllEmployeeList() = withContext(Dispatchers.IO) {
        safeCall {
            var hash = HashMap<String, String>()
            hash["SalesPerson"] = Prefs.getString(Global.Employee_Code)
            hash["Team"] = "SERVICE" //Operation
            val response = ApiClient().service.getAllEmployeeList(hash)
            Resource.Success(response.body()!!)
        }
    }


    override suspend fun getTicketSubType(data: java.util.HashMap<String, String>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getTicketSubType(data)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun getOrderOne(data: java.util.HashMap<String, String>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getParticularCustomerMvvmOrderOne(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getTicketOne(data: java.util.HashMap<String, String>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getTicketOne(data)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun getTicketCheckList(data: java.util.HashMap<String, String>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getTicketCheckList(data)

                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getUpdateTicketCheckList(data: BodyUpdateCheckListItem) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getUpdateTicketCheckList(data)

                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getQualityInspectionList(data: PayLoadForInspectionList) =
        withContext(Dispatchers.IO) {
            safeCall {

                val response =


                    ApiClient().service.getQualityInspectionList(data)

                Resource.Success(response.body()!!)
            }
        }

    override suspend fun updateManRescueLog(data: java.util.HashMap<String, String>) =
        withContext(Dispatchers.IO) {
            safeCall {

                val response =


                    ApiClient().service.updateManRescueLog(data)

                Resource.Success(response.body()!!)
            }
        }


    override suspend fun getCustomerForContact() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getCustomerForContact()

            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getContactNameList(jsonObject: JsonObject) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getContactNameList(jsonObject)

            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getBranchAllList(jsonObject: JsonObject) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getBranchAllList(jsonObject)

            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getItemAllList(jsonObject: JsonObject) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getItemAllList(jsonObject)

            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getItemsByTicket(jsonObject: JsonObject) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getItemsByTicket(jsonObject)

            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getScopeWorkList() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getScopeWorkList()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getAnnouncementList() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getAnnouncementList()

            Resource.Success(response.body()!!)
        }
    }


    /****Chanchal***/

    override suspend fun getBPList() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getBPList()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getAllBPList() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getAllBPList()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getAccountitemList(data: AccountBpData) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getAccountitemList(data)
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun allattachment(tickethistory: HashMap<String, Any>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.allattachment(tickethistory)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun addnewCustomer(businessPartnerDataNew: BusinessPartnerDataNew) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.addnewCustomer(businessPartnerDataNew)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getPaymentTerm() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getPaymentTerm()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getIndustryList() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getIndustryList()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getAllBranchList() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getAllBranchList()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getAllZoneList() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getAllZoneList()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getTicketDetails(data: TicketDetailsData) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getTicketDetails(data)
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getbpwiseTicket(data: ContactEmployee) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getbpwiseTicket(data)
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getDepartMent() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getDepartMent()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getRole() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getRole()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun createcontact(createContact: CreateContactData) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.createcontact(createContact)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getCountryList() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getCountryList()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getStateList(stateData: BPLID) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getStateList(stateData)
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getSalesEmplyeeList(data: NewLoginData) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getSalesEmplyeeList(data)
            Resource.Success(response.body()!!)
        }
    }

  override suspend fun getSalesEmployeeAllList() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getSalesEmployeeAllList()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getEmployeeRoleList() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getEmployeeRoleList()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getEmployeeSubDepList() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getEmployeeSubDepList()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getallcontact() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getallcontact()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getItemlist(data: DocumentLine) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getItemlist(data)
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getTicketHistory(data: HashMap<String, Int>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getTicketHistory(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getFollowUpList(data: HashMap<String, Any>) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getFollowUpList(data)
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getticketchecklist(data: TicketHistoryData) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getticketchecklist(data)
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun startstoptimer(data: HashMap<String, Any>) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.startstoptimer(data)
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun resettimer(data: HashMap<String, Any>) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.resettimer(data)
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getTicketConversation(data: HashMap<String, Int>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getTicketConversation(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun createTicketConversation(data: TicketHistoryData) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.createTicketConversation(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getitemwiseticket(data: TicketDataModel) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getitemwiseticket(data)
            Resource.Success(response.body()!!)
        }
    }


    override suspend fun getAllCategory(data: NewLoginData) = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getAllCategory(data)
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun getAllCategoryList() = withContext(Dispatchers.IO) {
        safeCall {
            val response = ApiClient().service.getAllCategoryList()
            Resource.Success(response.body()!!)
        }
    }

    override suspend fun particularTicketDetails(data: HashMap<String, Int>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.particularTicketDetails(data)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun createPartRequest(data: CreatePartrequestpayload) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.createPartRequest(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getAllpartrequest(data: HashMap<String, Any>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getAllpartrequest(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getAllPartRequestItem(data: HashMap<String, Any>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getAllPartRequestItem(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun acceptRejectTicket(logInDetail: NewLoginData) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.acceptRejectTicket(logInDetail)
                Resource.Success(response.body()!!)
            }
        }


    //todo Multipart body..

    override suspend fun signandconfirm(data: MultipartBody) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.signandconfirm(data)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun imageupload(requestBody: MultipartBody) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.imageupload(requestBody)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun multiplfileupload(requestBody: MultipartBody) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.multiplfileupload(requestBody)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun prUpload(requestBody: MultipartBody)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.prUpload(requestBody)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun createTicketTypeItems(requestBody: MultipartBody)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.createTicketTypeItems(requestBody)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun addAttachment(requestBody: MultipartBody)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.addAttachment(requestBody)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun updateTicketTypeItems(requestBody: JsonObject)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.updateTicketTypeItems(requestBody)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun ticketMaintenanceOneApi(requestBody: JsonObject)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.ticketMaintenanceOneApi(requestBody)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun ticketInstallationOneApi(requestBody: JsonObject)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.ticketInstallationOneApi(requestBody)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun ticketSiteSurveyOneApi(requestBody: JsonObject)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.ticketSiteSurveyOneApi(requestBody)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun getIssueCategoryList()=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getIssueCategoryList()
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun getSolutionList(jsonObject: SolutionRequestModel)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getSolutionList(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun createIssue(jsonObject: JsonObject)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.createIssue(jsonObject)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun ServiceContractOneApi(body: JsonObject)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.ServiceContractOneApi(body)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun createServiceContract(body: CreateServiceContractRequestModel)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.createServiceContract(body)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun getProductOneDetailApi(body: JsonObject)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getProductOneDetailApi(body)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun createEmployee(body: EmployeeCreateRequestModel)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.createEmployee(body)
                Resource.Success(response.body()!!)
            }
        }

    override suspend fun updateEmployee(body: EmployeeCreateRequestModel)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.updateEmployee(body)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun callEmployeeOneApi(body: JsonObject)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.callEmployeeOneApi(body)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun createProduct(body: AddProductRequestModel)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.createProduct(body)
                Resource.Success(response.body()!!)
            }
        }


    override suspend fun updateProduct(body: AddProductRequestModel)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.updateProduct(body)
                Resource.Success(response.body()!!)
            }
        }




    override suspend fun attachmentDelete(tickethistory: HashMap<String, Any>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.attachmentDelete(tickethistory)
                Resource.Success(response.body()!!)
            }
        }


    /***AHUJA Sons ***/
    override suspend fun getOrderOneDetail(body: JsonObject)=
        withContext(Dispatchers.IO) {
            safeCall {
                val response = ApiClient().service.getOrderOneDetail(body)
                Resource.Success(response.body()!!)
            }
        }


}