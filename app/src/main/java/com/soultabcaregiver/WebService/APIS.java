package com.soultabcaregiver.WebService;

public class APIS {

    //Live Server Base URL
    public static final String BASEURL1 = "https://mysoultab.com/api/";
    public static final String CaregiverImageURL1 = "https://mysoultab.com/uploads/profile_images/";


    //  Development Server Base URL
    public static final String BASEURL = "https://dev.mysoultab.com/api/";
    public static final String CaregiverImageURL = "https://dev.mysoultab.com/uploads/profile_images/";


    //Streaging ke liye Base URL
    public static final String BASEURL2 = "https://stage.mysoultab.com/api/";
    public static final String CaregiverImageURL2 = "https://stage.mysoultab.com/uploads/profile_images/";


    public static final String HEADERKEY = "soultab";
    public static final String HEADERVALUE = "123456";
    public static final String HEADERKEY1 = "Content-Type";
    public static final String HEADERVALUE1 = "application/json";
    public static final String HEADERKEY2 = "auth";

    public static final String LOGINAPI = "Caregiver/login";
    public static final String FORGOTAPI = "Users/forgot_password";
    public static final String VERIFYOTPAPI  ="Caregiver/verifyotp";
    public static final String ChangePasswordAPI  ="Caregiver/resetpassword";

    public static final String DailyRoutine = "Caregiver/daily_routine";
    public static final String GetDailyRoutineAPI = "Caregiver/fetch_dailyRoutine";

    public static final String GETDOCCATAPI = "Caregiver/doctorCategory_list";
    public static final String GETDOCLISTAPI = "Caregiver/doctor_list";
    public static final String GETMYDOCLISTAPI ="Caregiver/my_doctor_list";
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
    public static final String QuickAlery = "Users/quik_alert";
    public static final String AlertCount = "Users/get_unread_alert_count";
    public static final String AlertCountUpdate = "Users/update_unread_alert";
    public static final String VideoCallNotificationAPI = "Users/notifyUser";




    ///////Need to develop for caregiver
    public static final String UPDATEREMINDERAPI = "Users/updatereminder";
    public static final String ADDREMINDERAPI = "Users/reminder_add";
    public static final String DoctorSendFaxAPI = "Users_milan/sendFax";



    //User Detail
    public static final String user_id = "user_id";
    public static final String caregiver_id = "caregiver_id";
    public static final String EncodeUser_id ="EncodeUser_id";
    public static final String Caregiver_name = "name";
    public static final String Caregiver_lastname = "lastname";
    public static final String Caregiver_email = "email";
    public static final String user_email = "user_email";
    public static final String user_name = "user_name";

    public static final String Caregiver_mobile = "mobile";
    public static final String profile_image = "profile_image";
    public static final String save_email = "save_email";


    //Key's
    public static final String DocItemListItem = "DocItemListItem";
    public static final String DocListItem = "DocListItem";
    public static final String ReminderModel = "ReminderModel";
    public static final String Update_reminder = "Update_reminder";
    public static final String Send_alarmData = "Send_alarmData";



    // public static final String LineChartAPI = "caregiver/lineChart";


    /*public static final String Add_Doctor_API = "Users/add_doctor";
    public static final String EVENTLIST = "Users/activity_details";
    public static final String DELETEREMINDERAPI = "Users/reminderdelete";
    public static final String QuickAlery = "Users/quik_alert";
    public static final String AlertCount = "Users/get_unread_alert_count";
    public static final String AlertCountUpdate = "Users/update_unread_alert";




    ///////Need to develop for caregiver
    public static final String UPDATEREMINDERAPI = "Users/updatereminder";
    public static final String ADDREMINDERAPI = "Users/reminder_add";
    public static final String DoctorSendFaxAPI = "Users_milan/sendFax";
*/

}
