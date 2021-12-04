package com.soultabcaregiver.WebService;

import com.soultabcaregiver.BuildConfig;


public class APIS {
	//Live Server Base URL
	
	public static final String BASEURL = BuildConfig.baseUrl;
	
	public static final String CaregiverImageURL = BuildConfig.caregiverImageUrl;
	
	public static final String BASEURL40Plus = BuildConfig.base40plusUrl;
	
	public static final String SENDBIRD_APP_ID = BuildConfig.sendBirdAppID;// prod
	
	public static final String ShoppingAuthorizationKey = BuildConfig.Authorization;
	
	//40 plus API
	public static final String plus40Signup = "users";
	
	public static final String isplus40userexist = "users/?search=";
	
	public static final String ShoppingProductCateogry_list = "Users/ProductCateogry_list";
	
	public static final String HEADERKEY = "soultab";
	
	public static final String HEADERVALUE = "123456";
	
	public static final String HEADERKEY1 = "Content-Type";
	
	public static final String HEADERVALUE1 = "application/json";
	
	public static final String HEADERKEY2 = "auth";
	
	public static final String APITokenKEY = "Authorization";
	
	public static final String APITokenValue = "APITokenValue";
	
	public static final String RefressTokenValue = "RefressTokenValue";
	
	public static final String APITokenErrorCode = "401";
	
	public static final String APITokenErrorCode2 = "402";
	
	public static final String LOGINAPI = "Caregiver/login";
	
	public static final String FORGOTAPI = "Users/forgot_password";
	
	public static final String VERIFYOTPAPI = "Caregiver/verifyotp";
	
	public static final String ChangePasswordAPI = "Caregiver/resetpassword";
	
	public static final String DailyRoutine = "Caregiver/daily_routine";
	
	public static final String GetDailyRoutineAPI = "Caregiver/fetch_dailyRoutine";
	
	public static final String update_caregiver_flags = "Caregiver/update_caregiver_flags";
	
	public static final String GETDOCCATAPI = "Caregiver/doctorCategory_list";
	
	public static final String GETDOCLISTAPI = "Caregiver/doctor_list";
	
	public static final String GETMYDOCLISTAPI = "Caregiver/my_doctor_list";
	
	public static final String GETMYFAVDOCAPI = "Caregiver/addmy_doctor_list";
	
	public static final String DOC_APPOIN_API = "Caregiver/add_appointment";
	
	public static final String ALLAPPOINTED_DOC_API = "Caregiver/list_appointments";
	
	public static final String DOC_UPDATE_APPOIN_API = "Caregiver/update_appointment";
	
	public static final String DOC_Cancel_APPOIN_API = "Caregiver/cancel_appointment";
	
	public static final String DELETE_DOC_APPOIN_API = "Caregiver/appointmentdelete";
	
	public static final String DOC_APPOIN_DETAILS_API = "Users/show_appointments";
	
	public static final String Add_Doctor_API = "Users/add_doctor";
	
	public static final String EVENTLIST = "Users/activity_details";
	
	public static final String LineChartAPI = "caregiver/lineChart";
	
	public static final String AlertListAPI = "caregiver/get_alert_list";
	
	public static final String DELETEREMINDERAPI = "Users/reminderdelete";
	
	public static final String CaregiverListAPI = "Caregiver/getCaregiverDetailsforCaregiver";
	
	public static final String CaregiverListAPIForCreateGroup =
			"Caregiver/getCaregiverDetailsforCaregiverwithUser";
	
	public static final String QuickAlery = "Users/quik_alert";
	
	public static final String AlertCount = "Users/get_unread_alert_count";
	
	public static final String AlertCountUpdate = "Users/update_unread_alert";
	
	public static final String TwilioAccessToken = "Users/generate_twillio_accessToken";
	
	///////Need to develop for caregiver
	public static final String UPDATEREMINDERAPI = "Users/updatereminder";
	
	public static final String ADDREMINDERAPI = "Users/reminder_add";
	
	public static final String DoctorSendFaxAPI = "Users_milan/sendFax";
	
	public static final String GroupChatProfileImage = "Users/groupChatImage";
	
	//tabs hide show API
	public static final String caregiver_permissionsAPI = "Caregiver/caregiver_permissions";
	
	//Companion APP API
	public static final String GetCompanionDetail = "Users/getUser";
	
	public static final String UpdateCompanionProfile = "Webservices/updateProfile";
	
	public static final String getUsersListForCompanion = "Users/userListForCompanionOfOrder";
	
	public static final String refreshToken = "Users/refreshToken";
	//User Detail
	
	public static final String is_companion = "is_companion";
	
	public static final String user_id = "user_id";
	
	public static final String caregiver_id = "caregiver_id";
	
	public static final String EncodeUser_id = "EncodeUser_id";
	
	public static final String Caregiver_name = "Caregiver_name";
	
	public static final String Caregiver_lastname = "Caregiver_lastname";
	
	public static final String Caregiver_email = "Caregiver_email";
	
	public static final String user_email = "user_email";
	
	public static final String user_name = "user_name";
	
	public static final String Caregiver_mobile = "mobile";
	
	public static final String profile_image = "profile_image";
	
	public static final String save_email = "save_email";
	
	public static final String is_40plus_user = "is_40plus_user";
	
	public static final String is_40plus_userID = "is_40plus_userID";
	
	public static final String Caregiver_username = "Caregiver_username";
	
	public static final String Caregiver_countrycode = "Caregiver_countrycode";
	
	public static final String BadgeCount = "BadgeCount";
	
	public static final String dashbooard_hide_Show = "dashbooard_hide_Show";
	
	public static final String doctor_hide_show = "doctor_hide_show";
	
	public static final String calender_hideshow = "calender_hideshow";
	
	public static final String dailyroutine_hideshow = "dailyroutine_hideshow";
	
	public static final String Hide = "1";
	
	public static final String View = "2";
	
	public static final String Edit = "0";
	
	//Key's
	public static final String DocItemListItem = "DocItemListItem";
	
	public static final String DocListItem = "DocListItem";
	
	public static final String ReminderModel = "ReminderModel";
	
	public static final String Update_reminder = "Update_reminder";
	
	public static final String Send_alarmData = "Send_alarmData";
	
	public static final String TODO_TASK_STATUS_COUNTS = "Caregiver/getTaskStatusCount";
	
	public static final String TODO_TASK_LIST = "Caregiver/getTaskList";
	
	public static final String CREATE_TODO_TASK_LIST = "Caregiver/createTodoTask";
	
	public static final String GET_TODO_COMMENT_LIST = "Caregiver/getCommentList";
	
	public static final String GET_TODO_ACTIVITY_LIST = "Caregiver/getActivityList";
	
	public static final String ADD_NEW_TASK_COMMENT = "Caregiver/createTaskComment";
	
	public static final String DELETE_TASK_COMMENT = "Caregiver/deleteComment";
	
	public static final String TODO_TASK_NOTIFICATION = "Caregiver/task_notification_list";
	
	public static String notifyGroupCallApi = "Users/notifyGroupUser";
	
	public static final String INTENT_FILTER_REFRESH_TASK_LIST = "refresh_task_list";
	
}
