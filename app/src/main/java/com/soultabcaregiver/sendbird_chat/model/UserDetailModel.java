package com.soultabcaregiver.sendbird_chat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.soultabcaregiver.search.models.UserSearchResultResponse;

import java.util.List;

public class UserDetailModel {
	@SerializedName ("status_code")
	@Expose
	private Integer statusCode;
	@SerializedName("status")
	@Expose
	private String status;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("response")
	@Expose
	private List<Response> response = null;
	
	public Integer getStatusCode() {
		return statusCode;
	}
	
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public List<Response> getResponse() {
		return response;
	}
	
	public void setResponse(List<Response> response) {
		this.response = response;
	}
	public class Response {
		
		@SerializedName("id")
		@Expose
		private String id;
		@SerializedName("braintree_user_id")
		@Expose
		private String braintreeUserId;
		@SerializedName("cp_users_id")
		@Expose
		private String cpUsersId;
		@SerializedName("name")
		@Expose
		private String name;
		@SerializedName("lastname")
		@Expose
		private String lastname;
		@SerializedName("dob")
		@Expose
		private String dob;
		@SerializedName("ssn")
		@Expose
		private String ssn;
		@SerializedName("username")
		@Expose
		private String username;
		@SerializedName("city")
		@Expose
		private String city;
		@SerializedName("state")
		@Expose
		private String state;
		@SerializedName("zipcode")
		@Expose
		private String zipcode;
		@SerializedName("country")
		@Expose
		private String country;
		@SerializedName("language")
		@Expose
		private String language;
		@SerializedName("company_school")
		@Expose
		private String companySchool;
		@SerializedName("email")
		@Expose
		private String email;
		@SerializedName("mobile")
		@Expose
		private String mobile;
		@SerializedName("password")
		@Expose
		private String password;
		@SerializedName("check_last_password")
		@Expose
		private Object checkLastPassword;
		@SerializedName("profile_image")
		@Expose
		private String profileImage;
		@SerializedName("device_id")
		@Expose
		private String deviceId;
		@SerializedName("device_type")
		@Expose
		private String deviceType;
		@SerializedName("device_token")
		@Expose
		private String deviceToken;
		@SerializedName("user_role")
		@Expose
		private String userRole;
		@SerializedName("apartment")
		@Expose
		private String apartment;
		@SerializedName("street_name")
		@Expose
		private String streetName;
		@SerializedName("act_link")
		@Expose
		private String actLink;
		@SerializedName("status")
		@Expose
		private String status;
		@SerializedName("conversation_status")
		@Expose
		private String conversationStatus;
		@SerializedName("user_verify_by_app")
		@Expose
		private String userVerifyByApp;
		@SerializedName("device_verify")
		@Expose
		private String deviceVerify;
		@SerializedName("device_verify_by_token")
		@Expose
		private String deviceVerifyByToken;
		@SerializedName("create_user")
		@Expose
		private String createUser;
		@SerializedName("update_user")
		@Expose
		private String updateUser;
		@SerializedName("otp")
		@Expose
		private String otp;
		@SerializedName("otp_expire")
		@Expose
		private String otpExpire;
		@SerializedName("country_code")
		@Expose
		private String countryCode;
		@SerializedName("mobile_with_code")
		@Expose
		private String mobileWithCode;
		@SerializedName("gender")
		@Expose
		private String gender;
		@SerializedName("address")
		@Expose
		private String address;
		@SerializedName("user_id")
		@Expose
		private String userId;
		@SerializedName("timezone")
		@Expose
		private Object timezone;
		@SerializedName("snt_txt_msg_alrt")
		@Expose
		private Object sntTxtMsgAlrt;
		@SerializedName("snt_eml_alrt")
		@Expose
		private Object sntEmlAlrt;
		@SerializedName("call_me_alrt")
		@Expose
		private Object callMeAlrt;
		@SerializedName("snt_daily_eml_daily_rutin_alrt")
		@Expose
		private Object sntDailyEmlDailyRutinAlrt;
		@SerializedName("snt_eml_no_activity_in_app_alrt")
		@Expose
		private Object sntEmlNoActivityInAppAlrt;
		@SerializedName("snt_eml_alrm_is_missed")
		@Expose
		private String sntEmlAlrmIsMissed;
		@SerializedName("notify_time")
		@Expose
		private Object notifyTime;
		@SerializedName("my_doctor_list")
		@Expose
		private Object myDoctorList;
		@SerializedName("invitation")
		@Expose
		private Object invitation;
		@SerializedName("is_40plus_user")
		@Expose
		private String is40plusUser;
		@SerializedName("40plus_userId")
		@Expose
		private Object _40plusUserId;
		@SerializedName("is_sendbird_user")
		@Expose
		private String isSendbirdUser;
		@SerializedName("is_companion")
		@Expose
		private String isCompanion;
		@SerializedName("isMyFriend")
		@Expose
		private String isMyFriend;
		@SerializedName("is_temporary_password")
		@Expose
		private String isTemporaryPassword;
		@SerializedName("tablet_always_on")
		@Expose
		private String tabletAlwaysOn;
		@SerializedName("sub_menu_hide")
		@Expose
		private Object subMenuHide;
		@SerializedName("sub_menu_edit")
		@Expose
		private String subMenuEdit;
		@SerializedName("sub_menu_view")
		@Expose
		private Object subMenuView;
		@SerializedName("main_menu")
		@Expose
		private Object mainMenu;
		@SerializedName("plan_id")
		@Expose
		private String planId;
		@SerializedName("endDate")
		@Expose
		private String endDate;
		@SerializedName("hourly_rate")
		@Expose
		private Object hourlyRate;
		@SerializedName("is_vaccinated")
		@Expose
		private Object isVaccinated;
		@SerializedName("checkr_user_id")
		@Expose
		private Object checkrUserId;
		@SerializedName("driving_license")
		@Expose
		private String drivingLicense;
		@SerializedName("dl_expired_date")
		@Expose
		private String dlExpiredDate;
		@SerializedName("insurance_number")
		@Expose
		private String insuranceNumber;
		@SerializedName("insurance_expired_date")
		@Expose
		private String insuranceExpiredDate;
		@SerializedName("driving_license_image")
		@Expose
		private Object drivingLicenseImage;
		@SerializedName("insurance_image")
		@Expose
		private Object insuranceImage;
		@SerializedName("isConnected")
		@Expose
		private String isConnected;
		@SerializedName("isCaregiver")
		@Expose
		private String isCaregiver;
		
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getBraintreeUserId() {
			return braintreeUserId;
		}
		
		public void setBraintreeUserId(String braintreeUserId) {
			this.braintreeUserId = braintreeUserId;
		}
		
		public String getCpUsersId() {
			return cpUsersId;
		}
		
		public void setCpUsersId(String cpUsersId) {
			this.cpUsersId = cpUsersId;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public String getLastname() {
			return lastname;
		}
		
		public void setLastname(String lastname) {
			this.lastname = lastname;
		}
		
		public String getDob() {
			return dob;
		}
		
		public void setDob(String dob) {
			this.dob = dob;
		}
		
		public String getSsn() {
			return ssn;
		}
		
		public void setSsn(String ssn) {
			this.ssn = ssn;
		}
		
		public String getUsername() {
			return username;
		}
		
		public void setUsername(String username) {
			this.username = username;
		}
		
		public String getCity() {
			return city;
		}
		
		public void setCity(String city) {
			this.city = city;
		}
		
		public String getState() {
			return state;
		}
		
		public void setState(String state) {
			this.state = state;
		}
		
		public String getZipcode() {
			return zipcode;
		}
		
		public void setZipcode(String zipcode) {
			this.zipcode = zipcode;
		}
		
		public String getCountry() {
			return country;
		}
		
		public void setCountry(String country) {
			this.country = country;
		}
		
		public String getLanguage() {
			return language;
		}
		
		public void setLanguage(String language) {
			this.language = language;
		}
		
		public String getCompanySchool() {
			return companySchool;
		}
		
		public void setCompanySchool(String companySchool) {
			this.companySchool = companySchool;
		}
		
		public String getEmail() {
			return email;
		}
		
		public void setEmail(String email) {
			this.email = email;
		}
		
		public String getMobile() {
			return mobile;
		}
		
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		
		public String getPassword() {
			return password;
		}
		
		public void setPassword(String password) {
			this.password = password;
		}
		
		public Object getCheckLastPassword() {
			return checkLastPassword;
		}
		
		public void setCheckLastPassword(Object checkLastPassword) {
			this.checkLastPassword = checkLastPassword;
		}
		
		public String getProfileImage() {
			return profileImage;
		}
		
		public void setProfileImage(String profileImage) {
			this.profileImage = profileImage;
		}
		
		public String getDeviceId() {
			return deviceId;
		}
		
		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}
		
		public String getDeviceType() {
			return deviceType;
		}
		
		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}
		
		public String getDeviceToken() {
			return deviceToken;
		}
		
		public void setDeviceToken(String deviceToken) {
			this.deviceToken = deviceToken;
		}
		
		public String getUserRole() {
			return userRole;
		}
		
		public void setUserRole(String userRole) {
			this.userRole = userRole;
		}
		
		public String getApartment() {
			return apartment;
		}
		
		public void setApartment(String apartment) {
			this.apartment = apartment;
		}
		
		public String getStreetName() {
			return streetName;
		}
		
		public void setStreetName(String streetName) {
			this.streetName = streetName;
		}
		
		public String getActLink() {
			return actLink;
		}
		
		public void setActLink(String actLink) {
			this.actLink = actLink;
		}
		
		public String getStatus() {
			return status;
		}
		
		public void setStatus(String status) {
			this.status = status;
		}
		
		public String getConversationStatus() {
			return conversationStatus;
		}
		
		public void setConversationStatus(String conversationStatus) {
			this.conversationStatus = conversationStatus;
		}
		
		public String getUserVerifyByApp() {
			return userVerifyByApp;
		}
		
		public void setUserVerifyByApp(String userVerifyByApp) {
			this.userVerifyByApp = userVerifyByApp;
		}
		
		public String getDeviceVerify() {
			return deviceVerify;
		}
		
		public void setDeviceVerify(String deviceVerify) {
			this.deviceVerify = deviceVerify;
		}
		
		public String getDeviceVerifyByToken() {
			return deviceVerifyByToken;
		}
		
		public void setDeviceVerifyByToken(String deviceVerifyByToken) {
			this.deviceVerifyByToken = deviceVerifyByToken;
		}
		
		public String getCreateUser() {
			return createUser;
		}
		
		public void setCreateUser(String createUser) {
			this.createUser = createUser;
		}
		
		public String getUpdateUser() {
			return updateUser;
		}
		
		public void setUpdateUser(String updateUser) {
			this.updateUser = updateUser;
		}
		
		public String getOtp() {
			return otp;
		}
		
		public void setOtp(String otp) {
			this.otp = otp;
		}
		
		public String getOtpExpire() {
			return otpExpire;
		}
		
		public void setOtpExpire(String otpExpire) {
			this.otpExpire = otpExpire;
		}
		
		public String getCountryCode() {
			return countryCode;
		}
		
		public void setCountryCode(String countryCode) {
			this.countryCode = countryCode;
		}
		
		public String getMobileWithCode() {
			return mobileWithCode;
		}
		
		public void setMobileWithCode(String mobileWithCode) {
			this.mobileWithCode = mobileWithCode;
		}
		
		public String getGender() {
			return gender;
		}
		
		public void setGender(String gender) {
			this.gender = gender;
		}
		
		public String getAddress() {
			return address;
		}
		
		public void setAddress(String address) {
			this.address = address;
		}
		
		public String getUserId() {
			return userId;
		}
		
		public void setUserId(String userId) {
			this.userId = userId;
		}
		
		public Object getTimezone() {
			return timezone;
		}
		
		public void setTimezone(Object timezone) {
			this.timezone = timezone;
		}
		
		public Object getSntTxtMsgAlrt() {
			return sntTxtMsgAlrt;
		}
		
		public void setSntTxtMsgAlrt(Object sntTxtMsgAlrt) {
			this.sntTxtMsgAlrt = sntTxtMsgAlrt;
		}
		
		public Object getSntEmlAlrt() {
			return sntEmlAlrt;
		}
		
		public void setSntEmlAlrt(Object sntEmlAlrt) {
			this.sntEmlAlrt = sntEmlAlrt;
		}
		
		public Object getCallMeAlrt() {
			return callMeAlrt;
		}
		
		public void setCallMeAlrt(Object callMeAlrt) {
			this.callMeAlrt = callMeAlrt;
		}
		
		public Object getSntDailyEmlDailyRutinAlrt() {
			return sntDailyEmlDailyRutinAlrt;
		}
		
		public void setSntDailyEmlDailyRutinAlrt(Object sntDailyEmlDailyRutinAlrt) {
			this.sntDailyEmlDailyRutinAlrt = sntDailyEmlDailyRutinAlrt;
		}
		
		public Object getSntEmlNoActivityInAppAlrt() {
			return sntEmlNoActivityInAppAlrt;
		}
		
		public void setSntEmlNoActivityInAppAlrt(Object sntEmlNoActivityInAppAlrt) {
			this.sntEmlNoActivityInAppAlrt = sntEmlNoActivityInAppAlrt;
		}
		
		public String getSntEmlAlrmIsMissed() {
			return sntEmlAlrmIsMissed;
		}
		
		public void setSntEmlAlrmIsMissed(String sntEmlAlrmIsMissed) {
			this.sntEmlAlrmIsMissed = sntEmlAlrmIsMissed;
		}
		
		public Object getNotifyTime() {
			return notifyTime;
		}
		
		public void setNotifyTime(Object notifyTime) {
			this.notifyTime = notifyTime;
		}
		
		public Object getMyDoctorList() {
			return myDoctorList;
		}
		
		public void setMyDoctorList(Object myDoctorList) {
			this.myDoctorList = myDoctorList;
		}
		
		public Object getInvitation() {
			return invitation;
		}
		
		public void setInvitation(Object invitation) {
			this.invitation = invitation;
		}
		
		public String getIs40plusUser() {
			return is40plusUser;
		}
		
		public void setIs40plusUser(String is40plusUser) {
			this.is40plusUser = is40plusUser;
		}
		
		public Object get40plusUserId() {
			return _40plusUserId;
		}
		
		public void set40plusUserId(Object _40plusUserId) {
			this._40plusUserId = _40plusUserId;
		}
		
		public String getIsSendbirdUser() {
			return isSendbirdUser;
		}
		
		public void setIsSendbirdUser(String isSendbirdUser) {
			this.isSendbirdUser = isSendbirdUser;
		}
		
		public String getIsCompanion() {
			return isCompanion;
		}
		
		public void setIsCompanion(String isCompanion) {
			this.isCompanion = isCompanion;
		}
		
		public String getIsMyFriend() {
			return isMyFriend;
		}
		
		public void setIsMyFriend(String isMyFriend) {
			this.isMyFriend = isMyFriend;
		}
		
		public String getIsTemporaryPassword() {
			return isTemporaryPassword;
		}
		
		public void setIsTemporaryPassword(String isTemporaryPassword) {
			this.isTemporaryPassword = isTemporaryPassword;
		}
		
		public String getTabletAlwaysOn() {
			return tabletAlwaysOn;
		}
		
		public void setTabletAlwaysOn(String tabletAlwaysOn) {
			this.tabletAlwaysOn = tabletAlwaysOn;
		}
		
		public Object getSubMenuHide() {
			return subMenuHide;
		}
		
		public void setSubMenuHide(Object subMenuHide) {
			this.subMenuHide = subMenuHide;
		}
		
		public String getSubMenuEdit() {
			return subMenuEdit;
		}
		
		public void setSubMenuEdit(String subMenuEdit) {
			this.subMenuEdit = subMenuEdit;
		}
		
		public Object getSubMenuView() {
			return subMenuView;
		}
		
		public void setSubMenuView(Object subMenuView) {
			this.subMenuView = subMenuView;
		}
		
		public Object getMainMenu() {
			return mainMenu;
		}
		
		public void setMainMenu(Object mainMenu) {
			this.mainMenu = mainMenu;
		}
		
		public String getPlanId() {
			return planId;
		}
		
		public void setPlanId(String planId) {
			this.planId = planId;
		}
		
		public String getEndDate() {
			return endDate;
		}
		
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		
		public Object getHourlyRate() {
			return hourlyRate;
		}
		
		public void setHourlyRate(Object hourlyRate) {
			this.hourlyRate = hourlyRate;
		}
		
		public Object getIsVaccinated() {
			return isVaccinated;
		}
		
		public void setIsVaccinated(Object isVaccinated) {
			this.isVaccinated = isVaccinated;
		}
		
		public Object getCheckrUserId() {
			return checkrUserId;
		}
		
		public void setCheckrUserId(Object checkrUserId) {
			this.checkrUserId = checkrUserId;
		}
		
		public String getDrivingLicense() {
			return drivingLicense;
		}
		
		public void setDrivingLicense(String drivingLicense) {
			this.drivingLicense = drivingLicense;
		}
		
		public String getDlExpiredDate() {
			return dlExpiredDate;
		}
		
		public void setDlExpiredDate(String dlExpiredDate) {
			this.dlExpiredDate = dlExpiredDate;
		}
		
		public String getInsuranceNumber() {
			return insuranceNumber;
		}
		
		public void setInsuranceNumber(String insuranceNumber) {
			this.insuranceNumber = insuranceNumber;
		}
		
		public String getInsuranceExpiredDate() {
			return insuranceExpiredDate;
		}
		
		public void setInsuranceExpiredDate(String insuranceExpiredDate) {
			this.insuranceExpiredDate = insuranceExpiredDate;
		}
		
		public Object getDrivingLicenseImage() {
			return drivingLicenseImage;
		}
		
		public void setDrivingLicenseImage(Object drivingLicenseImage) {
			this.drivingLicenseImage = drivingLicenseImage;
		}
		
		public Object getInsuranceImage() {
			return insuranceImage;
		}
		
		public void setInsuranceImage(Object insuranceImage) {
			this.insuranceImage = insuranceImage;
		}
		
		public String getIsConnected() {
			return isConnected;
		}
		
		public void setIsConnected(String isConnected) {
			this.isConnected = isConnected;
		}
		
		public String getIsCaregiver() {
			return isCaregiver;
		}
		
		public void setIsCaregiver(String isCaregiver) {
			this.isCaregiver = isCaregiver;
		}
		
		public UserSearchResultResponse.UserSearchResultModel copyToUserDetailModel() {
			UserSearchResultResponse.UserSearchResultModel response = new UserSearchResultResponse.UserSearchResultModel();
			response.setId(getId());
			response.setName(getName());
			response.setProfileImage(getProfileImage());
			response.setIsSendbirdUser(getIsSendbirdUser());
			response.setConnected(getIsConnected());
			if (getIsCompanion().equals("1")){
				response.setCompanion(true);
			}else {
				response.setCompanion(false);
			}
			return response;
			
		}
	}
}
