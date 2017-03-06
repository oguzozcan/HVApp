package com.armut.armuthv.utils;

/**
 * Created by oguzemreozcan on 16/05/16.
 */
public class Constants {
    public final static boolean EMULATOR_RUN = false;
    //public final static String IMAGE_BASE_URL_REAL = "http://cdn.armut.com/mnresize";
    public final static String IMAGE_BASE_URL_REAL = "https://cdn2.armut.com/s3/picsanddocs";
    //public final static String IMAGE_BASE_URL_REAL ="http://178.211.57.11"; //test cdn'i
    public final static String AUTH_URL = "https://auth.armut.com/";
    public final static String TEST_AUTH_URL ="http://176.53.48.74/";
    public final static String BASE_URL_KEY = "BASE_URL";
    public final static String BASE_AUTH_URL_KEY = "BASE_AUTH_URL";

    public final static String BASE_LIVE_URL = "https://api.armut.com/v1.3/";
    public final static String BASE_TEST_URL = "http://testapi.armut.com/v1.3/";
    public final static String BASE_STAGE_URL = "https://stageapi.armut.com/v1.3/";

    public final static String BASE_PARITUS_URL = "https://api1.paritus.com/services/rest/";

    public final static int THUMBNAIL_SIZE = 140;
    public final static int GALLERY_THUMBNAIL_SIZE = 280;
    //public final static int FULL_SIZE = 600;
    public final static int NO_CONNECTION = -99;

    public final static String CLIENT_ID ="b69cfc73ce9842218e89a45c28c2b0c1";

    //PERMISSIONS
    public static final int PERMISSIONS_REQUEST_LOCATION = 1;
    public static final int PERMISSIONS_READ_CONTACTS = 2;
    public static final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 3;
    public static final int PERMISSIONS_CALL = 4;

    public static final String PERMISSION_KEY_REQUEST_LOCATION = "LOCATION_PERMISSION";
    public static final String PERMISSION_KEY_REQUEST_LOCATION_IS_ASKED = "LOCATION_PERMISSION_ASKED";
    public static final String PERMISSION_KEY_READ_CONTACTS = "CONTACTS_PERMISSION";
    public static final String PERMISSION_KEY_WRITE_EXTERNAL_STORAGE = "WRITE_STORAGE_PERMISSON";
    public static final String PERMISSION_KEY_CALL = "CALL_PERMISSON";
    public final static String NO_SHOW_RATE_US_DIALOG_KEY = "OPEN_RATE_US_DIALOG";

    //public final static String PROFILES_POSTFIX = "profiles";
    public final static String PROFILES_POSTFIX = "users/profiles";
    public final static String USERS_POSTFIX = "users";
    public final static String PHOTO_UPLOAD_POSTFIX ="users/profile_images";
    public final static String USER_DETAILS_POSTFIX = "users/details";
    public final static String USER_ADDRESS_POSTFIX = "users/addresses";
    public final static String USER_REVIEWS_POSTFIX = "users/reviews";
    public final static String RATINGS_POSTFIX = "ratings";
    public final static String USER_ACCOUNT_POSTFIX = "users/account";
    public final static String USER_ASSIGN_PHONE_NUMBER_POSTFIX = "users/assignPhoneNumber";

    public final static String JOB_DEALS_POSTFIX = "profiles/job_deals";
    public final static String JOB_QUOTES_POSTFIX = "profiles/job_quotes";
    public final static String JOB_OPPORTUNITIES_POSTFIX = "profiles/job_opportunities";
    public final static String JOB_DETAILS_POSTFIX = "jobs/";
    public final static String JOB_PHOTO_UPLOAD_POSTFIX ="jobs/image";
    public final static String JOB_QUOTES_LEAD_POSTFIX = "job_quotes/service_lead/";
    public final static String JOB_OPPORTUNITIES_REJECT_POSTFIX = "profiles/job_opportunities/{job_id}/nothanks";
    public final static String JOB_OPPORTUNITIES_REJECT_REASONS_POSTFIX = "profiles/job_opportunities/nothanksreasons";
    public final static String JOB_QUOTES_COMMISSION_RATE = "job_quotes/commission_rate/";

    public final static String CALENDAR_POSTFIX = "profiles/calendar";
    public final static String COORDINATE_POSTFIX = "profiles/coordinate";
    public final static String COORDINATES_POSTFIX = "profiles/coordinates";

    public final static String MESSAGES_POSTFIX ="messages";

    public final static String STATES_POSTFIX = "states/";
    public final static String CITIES_POSTFIX = "cities/";
    public final static String DISTRICTS_POSTFIX = "/districts";

    public final static String OTHER_PICS_POSTFIX = "/ProfilePics/";
    public final static String SERVICE_IMAGES_POSTFIX = "/JobRequestPics/";//"/ServiceImages/"; // JobRequestPics
    public final static String PROFILE_PICS_POSTFIX = "/UserPics/";//"/ProfileLogo/"; //

    public final static String DEVICES_POSTFIX ="devices";

    public final static String FORGET_PASSWORD_URL = "http://armut.com/mobileapp/forgetpassword.aspx";
    public final static String USER_AGREEMENT_URL = "http://armut.com/kullanici-sozlesmesi";

    public final static String REGISTER_HV_URL = "https://armut.com/uyeol_hesap/profil";

    public final static String PARITUS_VERIFY = "verifyaddress";

    public final static String CALL_ARMUT = "08503332200";
    public final static String MAIL_TO_ARMUT = "destek@armut.com";

    public final static String SEPERATOR = "*";
    public final static String VISUAL_SEPERATOR = " / ";

    public final static int UPDATE_JOBS_PAGE = 103;
    public final static int CHANGE_OPPORTUNITY_TO_QUOTE_PAGE = 107;
    public final static int NEW_LOGIN_UPDATE_ALL = 102;
    public final static int LOCATION_AVAILABLE = 104;
    public final static int UPDATE_ADDRESS_REQUEST_CODE = 99;
    public final static int UPDATE_CREDIT_CARD_REQUEST_CODE = 100;
    public final static int UPDATE_USER_INFO_REQUEST_CODE = 101;
    public final static int UPDATE_USER_PROFILE_PAGE = 102;
    public final static int UPDATE_NOTIFICATIONS_PAGE = 106;

    public final static int REQUEST_CHECK_SETTINGS = 108;

    public final static int DO_NOTHING = 0;

    public final static int BUSINESS_MODEL_REQUEST = 1;
    public final static int BUSINESS_MODEL_RESERVATION = 2;

    public final static int OPPORTUNITY_SECTION = 0;
    public final static int QUOTE_SECTION = 1;
    public final static int DEALS_SECTION = 2;

    public final static int OPPORTUNITY_SECTION_INDEX = 1;
    public final static int QUOTE_SECTION_INDEX = 2;
    public final static int DEALS_SECTION_INDEX = 3;

    public final static int JOB_START_DATE_TYPE_SPECIFIC_TIME = 4;
    public final static int JOB_START_DATE_TYPE_LOOKING_PRICE = 3;
    public final static int JOB_START_DATE_TYPE_SIX_MONTHS = 2;
    public final static int JOB_START_DATE_TYPE_TWO_MONTHS = 1;

    public final static int STATE_FIELD = 0;
    public final static int CITY_FIELD = 1;
    public final static int DISTRICT_FIELD = 2;

    public final static String ACCESS_TOKEN_KEY = "ACCESS_TOKEN";
    //public final static String VISITOR_ACCESS_TOKEN_KEY = "VISITOR_ACCESS_TOKEN";
    public final static String USERNAME = "USERNAME";
    public final static String USERID = "USER_ID";
    public final static String BUSINESS_MODEL_ID = "BUSINESS_MODEL_ID";
    public final static String DEVICE_TOKEN = "DEVICE_TOKEN";

    public final static String PARITUS_VERIFIED_ADDRESS_LIST = "PARITUS_VERIFIED_ADDRESS_LIST";

    public final static int CALL_PREF_NO_CALL = 0;
    public final static int CALL_PREF_CAN_CALL = 1;
    public final static int CALL_PREF_SECRET_NUMBER = 2;
}
