package de.gimik.apps.gpstracker.backend.util;

public class Constants {
    // ~ Static fields/initializers
    // =============================================

    /**
     * The name of the ResourceBundle used in this application
     */
    public static final String BUNDLE_KEY = "ApplicationResources";

    public static final String DATA_CACHE_CONFIG_KEY = "configKey";

    /**
     * New User
     */
    public static final long NEW_USER = 1;

    /**
     * Old User
     */
    public static final long OLD_USER = 0;

    /**
     * Days for password expire
     */
    public static final String MAXIMUM_PASSWORD_AGE = "MAXIMUM_PASSWORD_AGE";

    /**
     * Days for warning
     */
    public static final String PASSWORD_ALERT_THRESHOLD = "PASSWORD_ALERT_THRESHOLD";

    /**
     * The name of the ADMIN role
     */
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ADMIN = "admin";
    public static final String ROLE_CUSTOMER = "ROLE_CUSTOMER";
    public static final String ROLE_EMPLOYEES = "ROLE_EMPLOYEES";
    public static final String ROLE_VISITOR = "ROLE_VISITOR";
    public static final String ROLE_GUEST = "ROLE_GUEST";
    public static final String ROLE_PILOT = "ROLE_PILOT";
    

    public static final String ANONYMOUS = "ANONYMOUS";
    public static final String ROOMPICTURE = "room_picture";

    public static final int MAX_RESULT = 10;

    public static final String DATE_FORMAT = "yyyy/MM/dd";

    public static final String DATE_FORMAT_FOR_VALIDATION = "yyyyMMdd";

    public static final int MIN_PASWORD_LENGTH = 4;

    public static final int MAX_PASWORD_LENGTH = 20;

    public static final int PASSWORD_EXPIRED_FLAG = 2;

    public static final String MESSAGE_CODE_TEMPLATE = "%1$s(%2$d)";

    public static final String MSG_CODE_TEMPLATE_IDENTICAL = "%1$s";

    public static final String NA = "label.na";

    public static final long IMAGE_UPLOAD_FILE_MAX_SIZE = 500 * 1024 * 1024;

    public static final String ENGLISH = "English";
    public static final String GERMAN = "German";
    public static final String GERMANCODE = "de";
    public static final Integer ERROR = 1;
    public static final Integer OK = 200;
    public static final String SUCCESS = "Success";
    public static final String PUSH_ERROR = "push_error";
    public static final String MAX_TIME = "23:59:59";
    public static final String NONE = "NONE";
    public interface ErrorCode {
        public static final String NOT_AVAILABLE_FUNCTION = "ERR_0001";
        public static final String UNKNOWN_ERROR = "ERR_0002";
        public static final String IMAGE_UPLOAD_ERROR = "ERR_0003";
        public static final String CENTER_NOT_SELECTED = "ERR_0004";
        public static final String FROM_DATE_MUST_BEFORE_TO_DATE = "ERR_0005";

        public static final String USERNAME_DUPLICATED = "ERR_1001";
        public static final String USER_ROLE_NOT_SET = "ERR_1002";
        public static final String USERNAME_NOT_EXIST = "ERR_1003";
        public static final String PASSWORD_INVALID = "ERR_1004";
        public static final String ABBREVIATION_DUPLICATED = "ERR_1005";
        public static final Integer BAD_TOKEN = 10001;
        public static final Integer USERNAME_EXIST = 10002;
        public static final Integer USER_NOT_EXIST = 10003;
        public static final Integer PASSWORD_INCORRECT = 10004;
        public static final Integer ROLE_CHANGED = 10005;
        public static final Integer TOKEN_TIMEOUT = 10006;
        public static final Integer APP_IS_EXPRIED = 10007;
        public static final Integer WRONG_USER_NAME_OR_PASS = 10008;
        public static final Integer WRONG_ROLE_LOGIN_THIS_APP = 10009;
        public static final String NAME_DUPLICATE = "ERR_1101";
        public static final String ROOM_INFO_DUPLICATE = "ERR_1102";
        public static final String PROJECT_NUMBER_EXIST = "ERR_1103";
        public static final String NAME_WETTPORTAL_DUPLICATE = "name Wettportal duplicate";
        public static final String NAME_FLASHSCORE_DUPLICATE = "name Flashscore duplicate";
        public static final String TEAMS_NOT_EXIST = "teams not exist";
        public static final String LOGIN_FAILURE = "Wrong username or bad password";
        public static final Integer HOME_SCREEN_NOT_EXIST = 10000;
        public static final Integer APPLICATION_NOT_EXIST = 20000;
        public static final Integer CODE_EXIST = 20001;
        public static final Integer GENERAL_DATA_NOT_EXIST = 3000;
        public static final Integer GENERAL_DATA_EXIST = 30001;
        public static final Integer CONTAINER_NOT_EXIST = 40001;
        public static final Integer PAGE_NOT_EXIST = 50001;
        public static final Integer PAGE_NAME_ALREADY_EXIST = 50002;
        public static final Integer BEACON_NOT_EXIST = 60000;
        public static final Integer MAC_EXIST_IN_BEACON = 60001;
        public static final Integer MAJOR_MINOR_DUPLICATE = 60002;
        public static final Integer ELEMENT_NOT_EXIST = 70000;
        public static final Integer LBS_NOT_EXIST = 80000;
        public static final Integer POINT_NOT_EXIST = 90000;
        public static final Integer DOCUMENT_NOT_EXIST = 100000;
        public static final Integer ROOMPLAN_NOT_EXIST = 110000;
        public static final Integer PUSH_NOT_EXIST = 120000;
        public static final Integer PICTURE_NOT_EXIST = 130000;
        public static final Integer SENSOR_POINT_EXIST_BEACON = 140000;
        public static final Integer REPORT_NO_DATA = 210001;
    }

    public interface ImageFolder {

    }

    public interface ExprotType {
        public static final String PDF = "pdf";
    }

    public interface Object {
        public static final String USER = "User";
        public static final String ROLE = "Role";
        public static final String PROFILE = "Profile";
        public static final String CUSTOMER = "customer";
        public static final String EMPLOYEES = "employees";
        public static final String WINGO = "wingo";
        public static final String HOME_SCREEN = "home_screen";
        public static final String APP_MANAGER = "app_manager";
        public static final String GENERAL_DATA = "general_data";
        public static final String REPORT = "report";
        public static final String CONTAINER = "container";
        public static final String ELEMENT = "element";
        public static final String PAGE_MANAGE = "page_manage";
        public static final String PAGE_BANNER = "page_banner";
        public static final String BEACON = "beacon";
        public static final String QR_CODE = "qr_code";
        public static final String LBS = "lbs";
        public static final String LBS_BEACON = "lbs_beacon";
        public static final String POINT = "point";
        public static final String BEACON_POINT = "beacon_station";
        public static final String PICTURE = "picture";
        public static final String DOCUMENT = "document";
        public static final String ROOMPLAN = "room_plan";
        public static final String ROOMPLAN_DETAIL = "room_plan_detail";
        public static final String STATION = "station";
        public static final String PUSH_MANAGE = "push_manage";
        public static final String PUSH_DETAIL = "push_detail";
        public static final String DEVICE = "device";
        public static final String SENSORDATA = "sensordata";
        public static final String SENSOR_POINT = "sensor_point";
    }

    public interface Action {
        public static final String ADD = "Add";
        public static final String DELETE = "Delete";
        public static final String UPDATE = "Update";
        public static final String ACTIVATE = "Activate";
        public static final String DEACITIVATE = "Deactivate";
        public static final String CHANGE_PASSWORD = "Change Password";
        public static final String DELETE_All = "Delete all";
        public static final String SCHEDULE = "schedule";
//		public static final String SEND_EMAIL = "Send Email";
		public static final String RESET_PASSWORD = null;
    }

    public interface CategoryType {
        public static final int FAMILY = 1;
        public static final int SOCIAL = 2;
        public static final int AGE = 3;
        public static final int TYPE = 4;
        public static final int GENDER = 5;
    }

    public interface Role {

        public static final Long USER = 1L;

        public static final Long ADMIN = 2L;

        public static final Long PHYSICIAN = 3L;

    }

    public interface Sex {
        public static final int MALE = 1;
        public static final int FEMALE = 2;
    }

    /**
     * Error message key
     */
    public interface ERROR_MESSAGE {
    	public static final String CAN_NOT_DELETE = "Can not delete record";
        public static final String KEY_INPUT_REQUIRED = "errors.required";
        public static final String KEY_INPUT_INVALID = "errors.invalid";
        public static final String NOT_EXCEL_FILE = "Please import xlsx file";
        public static final String BAD_USERNAME_AND_PASSWORD = "wrong username or password invalid";
        public static final String ClaimDirectFzgTyp_not_exist = "ClaimDirectFzgTyp not exist";
        public static final String ClaimDirectStvsDekra_not_exist = "ClaimDirectStvsDekra not exist";
        public static final String KEY_INPUT_REQUIRED_MESSAGE = "Some data input rerquired";
        public static final String HOME_SCREEN_NOT_EXIST = "home screen not exist";
        public static final String APPLICATION_NOT_EXIST = "application not exist";
        public static final String CODE_EXIST = "code already  exist in system";
        public static final String GENERAL_DATA_EXIST = "code of this type already  exist in system";
        public static final String GENERAL_DATA_NOT_EXIST = "data not exist";
        public static final String CONTAINER_NOT_EXIST = "container not exist";
        public static final String PAGE_NOT_EXIST = "page not exist";
        public static final String BAD_TOKEN = "bad token!!";
        public static final String gpstracker_NOT_EXIST = "gpstracker not exist";
        public static final String BEACON_NOT_EXIST = "beacon not exist";
        public static final String ELEMENT_NOT_EXIST = "element not exist";
        public static final String LBS_NOT_EXIST = "lbs not exist";
        public static final String PAGE_NAME_ALREADY_EXIST = "page name already exist in system";
        public static final String USERNAME_EXIST = "username already  exist in system";
        public static final String USER_NOT_EXIST = "user not exist in system";
        public static final String PASSWORD_INCORRECT = "password incorrect";
        public static final String POINT_NOT_EXIST = "point not exist";
        public static final String DOCUMENT_NOT_EXIST = "document not exist";
        public static final String ROOMPLAN_NOT_EXIST = "roomplan not exist";
        public static final String PUSH_NOT_EXIST = "push not exist";
        public static final String ROLE_CHANGED = "role changed";
        public static final String TOKEN_TIMEOUT = "session time out";
        public static final String APP_IS_EXPRIED = "app is expried";
        public static final String PICTURE_NOT_EXIST = "picture not exist";
        public static final String WRONG_ROLE_LOGIN_THIS_APP = "you have not role to login this app";
        public static final String MAC_EXIST_IN_BEACON = "Mac already exist in system!!";
        public static final String SENSOR_POINT_EXIST_BEACON = "beacon exit in other sensorpoint";
        public static final String MAJOR_MINOR_DUPLICATE = "Major and minor already exist in system!";
        public static final String REPORT_NO_DATA = "report not data";
        
        
    }

    /**
     * Error argument key
     */

    public interface ERROR_ARGUMENT {

        public static final String USERNAME = "user.username";
        public static final String PASSWORD = "user.password";


    }

    /**
     * Max length of username attribute.
     */
    public static final int USERNAME_MAXLENGTH = 50;

    /**
     * Min length of username attribute.
     */
    public static final int USERNAME_MINLENGTH = 2;

    /**
     * Max length of loginName attribute.
     */
    public static final int LOGINNAME_MAXLENGTH = 20;

    /**
     * Min length of loginName attribute.
     */
    public static final int LOGINNAME_MINLENGTH = 1;


    /**
     * Max length of password attribute.
     */
    public static final int PASSWORD_MAXLENGTH = 20;

    /**
     * Min length of password attribute.
     */
    public static final int PASSWORD_MINLENGTH = 4;

    /**
     * default value of Character variable
     */
    public static final Character DEFAULT_CHAR_VALUE = '0';

    /**
     * empty string
     */
    public static final String DEFAULT_STRING_VALUE = "";

    /**
     * Commas character.
     */
    public static final char COMMA = ',';

    /**
     * Commas character.
     */
    public static final char APOSTROPHE = '\'';

    /**
     * Commas character.
     */
    public static final char SPACE = ' ';
    public static final char DASH = '-';

    /**
     * quotation marks
     */
    public static final char QUOTATION_MARKS = '\"';

    public static final String PHONE_NUMBER_SEPARATOR = " / ";
    
    
    public interface type{
    	public static final String SPATIAL_USE ="spatial_use";
    	public static final String TEST_METHOD ="test_method";
    	public static final String PROTECTIVE_CLASS ="protective_class";
    	public static final String CATEGORIES_LEVEL1 ="categories_level1";
    	public static final String CATEGORIES_LEVEL2 ="categories_level2";
    	
    	public static final String BUILDING ="building";
    	public static final String FLOOR ="floor";
    	public static final String MAIN_VIEW ="MAIN_VIEW";
    	public static final String BANNER ="BANNER";
    	public static final String MENU ="MENU";
    	public static final String FOOTER ="FOOTER";
    	public static final String FULL ="FULL";
    	public static final String SQUARES_SQUARES ="SQUARES-SQUARES";
    	public static final String SQUARES_RECTANGLE ="SQUARES-RECTANGLE";
    	public static final String RECTANGLE_SQUARES ="RECTANGLE-SQUARES";
    	public static final String LEFT ="LEFT";
    	public static final String RIGHT ="RIGHT";
    	public static final String FULL_ELEMENT ="FULL";
    	public static final String BEACON_INFO ="BEACONINFO";
    }
    public static final Integer ROOM_STATUS_NOT_START = 0;
    public static final Integer ROOM_STATUS_NOT_INPROGRESS = 1;
    public static final Integer ROOM_STATUS_DONE = 2;
    public static final Integer ROOM_STATUS_CROSSCHECK = 4;
    public static final Integer ROOM_STATUS_HANDOVER = 5;
    public static final Integer TEST_METHOD_STATUS_DONE = 3;
    public static final Integer COUNT_EMPTY = 0;
    public interface Header {
    	public static final String Schadeninformation ="Schadeninformation";
    	public static final String Schadenzusatzkosten ="Schadenzusatzkosten";
    	public static final String Schadenhergang ="Schadenhergang";
    	public static final String Schadensteuerung ="Schadensteuerung";
    	public static final String Datenumfang ="Datenumfang";
    	public static final String Zahlschaden ="Laufleistung zu Schadenhöhe";
    	public static final String Betrug="Betrug";
    	
    }
    public interface folder {
    	public static final String DOCUMENT ="document";
    	public static final String ELEMENT ="element";
    	public static final String VIDEOORAUDIOR ="videooraudio";
    	public static final String ICON ="icon";
    	public static final String SPLASH ="splash";
    	public static final String MENU_LOGO ="menulogo";
    	public static final String HOME ="home";
    	public static final String BASE_PIUCTURE ="basepicture";
    	public static final String ROOMPLAN ="roomplan";
    	public static final String REPORT_REQUEST ="reportrequest";
    }
    public interface Push {
    	public static final String SENT = "Sent";
        public static final String TYPE_ALL = "TOALL";
        public static final String TYPE_USER = "TOUSER";
        public static final String TYPE_ALL_USER = "TOALLUSER";
        public static final String FREQUENCY_ONE_TIME = "ONETIME";
        public static final String FREQUENCY_ONE_EVERYDAY = "EVERYDAY";
    }
    public interface Token {
        public static final String SESSION_TYPE_ONCE = "once";
        public static final String SESSION_TYPE_ONCE_DAY = "everydayonce";
        public static final String SESSION_TYPE_SPECIFIED = "specifiedtime";
    }
}
