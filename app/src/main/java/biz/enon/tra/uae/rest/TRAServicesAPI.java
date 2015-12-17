package biz.enon.tra.uae.rest;

import biz.enon.tra.uae.entities.dynamic_service.DynamicService;
import biz.enon.tra.uae.rest.model.request.ChangePasswordModel;
import biz.enon.tra.uae.rest.model.request.ComplainServiceProviderModel;
import biz.enon.tra.uae.rest.model.request.ComplainTRAServiceModel;
import biz.enon.tra.uae.rest.model.request.HelpSalimModel;
import biz.enon.tra.uae.rest.model.request.LoginModel;
import biz.enon.tra.uae.rest.model.request.LoginQuestionRequestModel;
import biz.enon.tra.uae.rest.model.request.LogoutRequestModel;
import biz.enon.tra.uae.rest.model.request.PoorCoverageRequestModel;
import biz.enon.tra.uae.rest.model.request.PostInnovationRequestModel;
import biz.enon.tra.uae.rest.model.request.RatingServiceRequestModel;
import biz.enon.tra.uae.rest.model.request.RegisterModel;
import biz.enon.tra.uae.rest.model.request.RestorePasswordRequestModel;
import biz.enon.tra.uae.rest.model.request.SmsBlockRequestModel;
import biz.enon.tra.uae.rest.model.request.SmsReportRequestModel;
import biz.enon.tra.uae.rest.model.request.UserNameModel;
import biz.enon.tra.uae.rest.model.response.ContactUsResponse;
import biz.enon.tra.uae.rest.model.response.DomainAvailabilityCheckResponseModel;
import biz.enon.tra.uae.rest.model.response.DomainInfoCheckResponseModel;
import biz.enon.tra.uae.rest.model.response.DynamicServiceInfoResponseModel;
import biz.enon.tra.uae.rest.model.response.GetAnnouncementsResponseModel;
import biz.enon.tra.uae.rest.model.response.TransactionModel;
import biz.enon.tra.uae.rest.model.response.RatingServiceResponseModel;
import biz.enon.tra.uae.rest.model.response.SearchDeviceResponseModel;
import biz.enon.tra.uae.rest.model.response.SecurityQuestionResponse;
import biz.enon.tra.uae.rest.model.response.ServiceInfoResponse;
import biz.enon.tra.uae.rest.model.response.SmsSpamResponseModel;
import biz.enon.tra.uae.rest.model.response.UserProfileResponseModel;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

import static biz.enon.tra.uae.global.ServerConstants.CHANGE_PASSWORD;
import static biz.enon.tra.uae.global.ServerConstants.CHECK_WHO_IS_AVAILABLE_URL;
import static biz.enon.tra.uae.global.ServerConstants.CHECK_WHO_IS_URL;
import static biz.enon.tra.uae.global.ServerConstants.COMPLAIN_ABOUT_SERVICE_PROVIDER_URL;
import static biz.enon.tra.uae.global.ServerConstants.COMPLAIN_ABOUT_TRA_SERVICE_URL;
import static biz.enon.tra.uae.global.ServerConstants.COMPLAIN_ENQUIRIES_SERVICE_URL;
import static biz.enon.tra.uae.global.ServerConstants.CONTACT_US;
import static biz.enon.tra.uae.global.ServerConstants.DYNAMIC_SERVICE_LIST;
import static biz.enon.tra.uae.global.ServerConstants.GET_ANNOUNCEMENTS;
import static biz.enon.tra.uae.global.ServerConstants.GET_TRANSACTIONS;
import static biz.enon.tra.uae.global.ServerConstants.PUT_TRANSACTIONS;
import static biz.enon.tra.uae.global.ServerConstants.HELP_SALIM_URL;
import static biz.enon.tra.uae.global.ServerConstants.JSON_TYPE;
import static biz.enon.tra.uae.global.ServerConstants.LOGIN_URL;
import static biz.enon.tra.uae.global.ServerConstants.LOGOUT_URL;
import static biz.enon.tra.uae.global.ServerConstants.PARAMETER_CHECK_URL;
import static biz.enon.tra.uae.global.ServerConstants.PARAMETER_COUNT;
import static biz.enon.tra.uae.global.ServerConstants.PARAMETER_DEVICE_BRAND;
import static biz.enon.tra.uae.global.ServerConstants.PARAMETER_END_LIMIT;
import static biz.enon.tra.uae.global.ServerConstants.PARAMETER_IMEI;
import static biz.enon.tra.uae.global.ServerConstants.PARAMETER_LANGUAGE;
import static biz.enon.tra.uae.global.ServerConstants.PARAMETER_LIMIT;
import static biz.enon.tra.uae.global.ServerConstants.PARAMETER_OFFSET;
import static biz.enon.tra.uae.global.ServerConstants.PARAMETER_PAGE;
import static biz.enon.tra.uae.global.ServerConstants.PARAMETER_SEARCH;
import static biz.enon.tra.uae.global.ServerConstants.PARAMETER_SERVICE_NAME;
import static biz.enon.tra.uae.global.ServerConstants.PARAMETER_START_OFFSET;
import static biz.enon.tra.uae.global.ServerConstants.PATH_HOLDER;
import static biz.enon.tra.uae.global.ServerConstants.POOR_COVERAGE_URL;
import static biz.enon.tra.uae.global.ServerConstants.POST_INNOVATION;
import static biz.enon.tra.uae.global.ServerConstants.RATING_SERVICE_URL;
import static biz.enon.tra.uae.global.ServerConstants.REGISTER_URL;
import static biz.enon.tra.uae.global.ServerConstants.RESTORE_PASS_URL;
import static biz.enon.tra.uae.global.ServerConstants.SEARCH_DEVICE_BY_BRAND_NAME_URL;
import static biz.enon.tra.uae.global.ServerConstants.SEARCH_DEVICE_BY_IMEI_URL;
import static biz.enon.tra.uae.global.ServerConstants.SECRET_QUESTIONS;
import static biz.enon.tra.uae.global.ServerConstants.SEND_SUGGESTION_URL;
import static biz.enon.tra.uae.global.ServerConstants.SERVICE_INFO;
import static biz.enon.tra.uae.global.ServerConstants.SMS_SPAM_BLOCK_URL;
import static biz.enon.tra.uae.global.ServerConstants.SMS_SPAM_REPORT_URL;
import static biz.enon.tra.uae.global.ServerConstants.USER_PROFILE;

/**
 * Created by Mikazme on 13/08/2015.
 */
public interface TRAServicesAPI {

    @GET(CHECK_WHO_IS_URL)
    DomainInfoCheckResponseModel getDomainData(@Query(PARAMETER_CHECK_URL) String _checkUrl);

    @GET(CHECK_WHO_IS_AVAILABLE_URL)
    DomainAvailabilityCheckResponseModel checkDomainAvailability(@Query(PARAMETER_CHECK_URL) String _checkUrl);

    @GET(SEARCH_DEVICE_BY_IMEI_URL)
    SearchDeviceResponseModel.List searchDeviceByImei(@Query(PARAMETER_IMEI) String _imei);

    @GET(SEARCH_DEVICE_BY_BRAND_NAME_URL)
    SearchDeviceResponseModel.List searchDeviceByBrandName(@Query(PARAMETER_DEVICE_BRAND) String _brand,
                                                           @Query(PARAMETER_START_OFFSET) Integer _start,
                                                           @Query(PARAMETER_END_LIMIT) Integer _end);

    @POST(RATING_SERVICE_URL)
    RatingServiceResponseModel ratingService(@Body RatingServiceRequestModel _ratingServiceModel);

    @POST(SMS_SPAM_REPORT_URL)
    SmsSpamResponseModel reportSmsSpam(@Body SmsReportRequestModel _smsSpamModel);


    @POST(SMS_SPAM_BLOCK_URL)
    SmsSpamResponseModel blockSmsSpam(@Body SmsBlockRequestModel _smsSpamModel);

    @POST(POOR_COVERAGE_URL)
    Response poorCoverage(@Body PoorCoverageRequestModel _smsSpamModel);

    @POST(HELP_SALIM_URL)
    Response sendHelpSalim(@Body HelpSalimModel _helpSalimModel);

    @POST(COMPLAIN_ABOUT_SERVICE_PROVIDER_URL)
    Response complainServiceProvider(@Body ComplainServiceProviderModel _complainServiceProviderModel);

    @POST(COMPLAIN_ABOUT_TRA_SERVICE_URL)
    Response complainTraServiceProvider(@Body ComplainTRAServiceModel _complainTRAServiceModel);

    @Headers(JSON_TYPE)
    @POST(COMPLAIN_ENQUIRIES_SERVICE_URL)
    Response complainEnquiries(@Body ComplainTRAServiceModel _complainTRAServiceModel);

    @POST(SEND_SUGGESTION_URL)
    Response sendSuggestion(@Body ComplainTRAServiceModel _complainTRAServiceModel);

    @POST(REGISTER_URL)
    Response register(@Body RegisterModel _registerModel);

    @POST(LOGIN_URL)
    Response login(@Body LoginModel _loginModel);

    @POST(LOGIN_URL)
    Response loginWithQuestion(@Body LoginQuestionRequestModel _loginModel);

    @POST(RESTORE_PASS_URL)
    Response restorePassword(@Body RestorePasswordRequestModel _loginModel);

    @POST(LOGOUT_URL)
    Response logout(@Body LogoutRequestModel _req);

    @GET(USER_PROFILE)
    UserProfileResponseModel getUserProfile();

    @PUT(USER_PROFILE)
    UserProfileResponseModel editUserProfile(@Body UserNameModel _userName);

    @PUT(CHANGE_PASSWORD)
    Response changePassword(@Body ChangePasswordModel _changePasswordModel);

    @POST(POST_INNOVATION)
    Response postInnovation(@Body PostInnovationRequestModel _model);

    @GET(GET_TRANSACTIONS)
    TransactionModel.List getTransactions(@Query(PARAMETER_PAGE) final int _page,
                                                     @Query(PARAMETER_COUNT) final int _count);

    @GET(GET_TRANSACTIONS)
    TransactionModel.List searchTransactions(@Query(PARAMETER_PAGE) final int _page,
                                                        @Query(PARAMETER_COUNT) final int _count,
                                                        @Query(PARAMETER_SEARCH) final String _query);

    @PUT(PUT_TRANSACTIONS + "/{path}")
    Response putTransactions(@Path("path") String _path, @Body TransactionModel _transactionModel);

    @GET(GET_ANNOUNCEMENTS)
    GetAnnouncementsResponseModel getAnnouncements(
            @Query(PARAMETER_OFFSET) final int _offset,
            @Query(PARAMETER_LIMIT) final int _limit,
            @Query(PARAMETER_LANGUAGE) final String _lang);

    @GET(GET_ANNOUNCEMENTS)
    GetAnnouncementsResponseModel searchAnnouncements(
            @Query(PARAMETER_OFFSET) final int _offset,
            @Query(PARAMETER_LIMIT) final int _limit,
            @Query(PARAMETER_SEARCH) final String _query,
            @Query(PARAMETER_LANGUAGE) final String _lang);

    @GET(SERVICE_INFO)
    ServiceInfoResponse getServiceInfo(@Query(PARAMETER_SERVICE_NAME) String _serviceName,
                                       @Query(PARAMETER_LANGUAGE) String _language);

    @GET(DYNAMIC_SERVICE_LIST)
    DynamicServiceInfoResponseModel.List getDynamicServiceList();

    @GET(DYNAMIC_SERVICE_LIST + "/{" + PATH_HOLDER + "}")
    DynamicService getDynamicServiceDetails(@Path(PATH_HOLDER) String _id);

    @GET(CONTACT_US)
    ContactUsResponse.List getContactUsInfo(@Query(PARAMETER_LANGUAGE) final String _lang);

    @GET(SECRET_QUESTIONS)
    SecurityQuestionResponse.List getSecurityQuestions();

}
