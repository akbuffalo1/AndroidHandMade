package com.uae.tra_smart_services.global;

/**
 * Created by mobimaks on 30.07.2015.
 */
public final class ServerConstants {

    private ServerConstants() {
    }

    public static final int TIMEOUT = 20;//seconds
    public static final String HTTP_SCHEME = "http://";
    public static final String HTTPS_SCHEME = "https://";
    public static final String JSON_TYPE = "Content-Type: application/json";

    public static final String BASE_URL1 = HTTP_SCHEME + "mobws.tra.gov.ae";
    public static final String BASE_URL2 = HTTP_SCHEME + "185.54.19.249:80";
    public static final String BASE_URL3 = HTTP_SCHEME + "192.168.120.40:80";
    public static final String BASE_URL4 = HTTP_SCHEME + "projects.thinkmobiles.com:7791";
    public static final String BASE_URL5 = HTTPS_SCHEME + "mobsrv.tra.gov.ae";
    public static String BASE_URL = BASE_URL5;

    public static final String APP_KEY_HEADER_NAME = "appkey";
    public static final String APP_KEY_HEADER_VALUE = "testAppKey";

    public static final String AUTH_URL = "/auth";
    public static final String ACCESS_TOKEN = "access_token";
    //Domain check service.
    public static final String DNS_LOOKUP_URL = "/dnslookup";

    //Approved devices
    public static final String BREND_NAME = "brendName";
    public static final String SEARCH_MODEL = "searchModelName";
    public static final String BASE_APPROVED_DEVICES_URL = "/getdevicebrandmodel/%1$s/%2$s";
    public static final String APPROVED_DEVICES_URL = BASE_APPROVED_DEVICES_URL + "/{" + BREND_NAME + "}/" + SEARCH_MODEL;
    public static final String ALL_APPROVED_DEVICES_URL = BASE_APPROVED_DEVICES_URL + "/all" + "/{" + SEARCH_MODEL + "}";

    //Check mobile verification
    public static final String IS_FAKE_DEVICE_URL = "/isfakedevice";

    //Report poor coverage
    public static final String SIGNAL_COVERAGE_URL = "/signalcoverage";

    //Help Salim
    public static final String BLOCK_WEBSITE_URL = "/blockwebsite";

    //Rating
    public static final String RATING_SERVICE_URL = "/feedback";

    //SMS spam
    public static final String SMS_SPAM_URL = "/insertsmsspam";

    //Search
    public static final String SEARCH_STRING = "searchString";
    public static final String SEARCH_URL = "/taggedsearch/{" + SEARCH_STRING + "}";

    public static final String PATH_HOLDER = "path";

    //!!!!!!!!!!!!! NEW API !!!!!!!!!!!!!!!!!!!!!!!!
    public static final String CHECK_WHO_IS_URL = "/checkWhois";
    public static final String CHECK_WHO_IS_AVAILABLE_URL = "/checkWhoisAvailable";
    public static final String SEARCH_DEVICE_BY_IMEI_URL = "/searchMobile";
    public static final String SEARCH_DEVICE_BY_BRAND_NAME_URL = "/searchMobileBrand";
    public static final String NEW_RATING_SERVICE_URL = "/feedback";
    public static final String SMS_SPAM_REPORT_URL = "/crm/complainSmsSpam";
    public static final String SMS_SPAM_BLOCK_URL = "/complainSmsBlock";
    public static final String HELP_SALIM_URL = "/sendHelpSalim";
    public static final String COMPLAIN_ABOUT_SERVICE_PROVIDER_URL = "/crm/complainServiceProvider";
    public static final String COMPLAIN_ABOUT_TRA_SERVICE_URL = "/crm/complainTRAService";
    public static final String COMPLAIN_ENQUIRIES_SERVICE_URL = "/crm/complainEnquiries";
    public static final String SEND_SUGGESTION_URL = "/crm/sendSuggestion";
    public static final String POOR_COVERAGE_URL = "/sendPoorCoverage";
    public static final String REGISTER_URL = "/crm/register";
    public static final String LOGIN_URL = "/crm/signIn";
    public static final String RESTORE_PASS_URL = "/crm/forgotPass";
    public static final String LOGOUT_URL = "/crm/signOut";
    public static final String USER_PROFILE = "/crm/profile";
    public static final String CHANGE_PASSWORD = "/crm/changePass";
    public static final String GET_TRANSACTIONS = "/crm/transactions";
    public static final String GET_ANNOUNCEMENTS = "/announcement";
    public static final String POST_INNOVATION = "/innovation";
    public static final String SERVICE_INFO = "/service/about";
    public static final String DYNAMIC_SERVICE_LIST = "/service";
    public static final String UPLOAD_ATTACHMENT = "/attachment";
    public static final String CONTACT_US = "/contact";

    //!!!!!!!!!!!! API PARAMETERS !!!!!!!!!!!!!!!!!!
    public static final String PARAMETER_CHECK_URL = "checkUrl";
    public static final String PARAMETER_IMEI = "imei";
    public static final String PARAMETER_DEVICE_BRAND = "brand";
    public static final String PARAMETER_OFFSET = "offset";
    public static final String PARAMETER_LIMIT = "limit";
    public static final String PARAMETER_START_OFFSET = "start";
    public static final String PARAMETER_END_LIMIT = "end";
    public static final String PARAMETER_SERVICE_NAME = "name";
    public static final String PARAMETER_LANGUAGE = "lang";
    public static final String PARAMETER_PAGE = "page";
    public static final String PARAMETER_COUNT = "count";
    public static final String PARAMETER_SEARCH = "search";

    //!!!!!!!!!!!! SERVER RESPONSES !!!!!!!!!!!!!!!!!
    public static final String AVAILABLE = "Available";
    public static final String NOT_AVAILABLE = "Not Available";
    public static final String NO_DATA_FOUND = "No Data Found\r\n";
}
