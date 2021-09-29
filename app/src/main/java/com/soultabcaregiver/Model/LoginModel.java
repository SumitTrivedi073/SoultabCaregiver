package com.soultabcaregiver.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginModel {
	
	@SerializedName ("status_code") @Expose private Integer statusCode;
	
	@SerializedName ("status") @Expose private String status;
	
	@SerializedName ("message") @Expose private String message;
	
	@SerializedName ("response") @Expose private Response response;
	
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
	
	public Response getResponse() {
		return response;
	}
	
	public void setResponse(Response response) {
		this.response = response;
	}
	
	public static class Response {
		
		@SerializedName ("id") @Expose private String id;
		
		@SerializedName ("name") @Expose private String name;
		
		@SerializedName ("caregiver_id") @Expose private String caregiver_id;
		
		@SerializedName ("lastname") @Expose private String lastname;
		
		@SerializedName ("dob") @Expose private String dob;
		
		@SerializedName ("email") @Expose private String email;
		
		@SerializedName ("user_email_address") @Expose private String user_email_address;
		
		@SerializedName ("Otp_verification") @Expose private String otpVerification;
		
		@SerializedName ("mobile") @Expose private String mobile;
		
		@SerializedName ("device_type") @Expose private String deviceType;
		
		@SerializedName ("user_verification") @Expose private String userVerification;
		
		@SerializedName ("token_verification") @Expose private String tokenVerification;
		
		@SerializedName ("profile_image") @Expose private String profileImage;
		
		@SerializedName ("parent_user") @Expose private String parent_user;
		
		@SerializedName ("is_40plus_user") @Expose private String is_40plus_user;
		
		@SerializedName ("40plus_userId") @Expose private String plus40_userId;
		
		@SerializedName ("is_sendbird_user") @Expose private String isSendBirdUser;
		
		@SerializedName ("countrycode") @Expose private String countrycode;
		
		@SerializedName ("username") @Expose private String caregiver_username;

		@SerializedName ("is_companion") @Expose private String is_companion;

		@SerializedName("permission")
		@Expose
		private Permission permission;
		
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getCaregiver_id() {
			return caregiver_id;
		}
		
		public void setCaregiver_id(String caregiver_id) {
			this.caregiver_id = caregiver_id;
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
		
		public String getEmail() {
			return email;
		}
		
		public void setEmail(String email) {
			this.email = email;
		}
		
		public String getUser_email_address() {
			return user_email_address;
		}
		
		public void setUser_email_address(String user_email_address) {
			this.user_email_address = user_email_address;
		}
		
		public String getOtpVerification() {
			return otpVerification;
		}
		
		public void setOtpVerification(String otpVerification) {
			this.otpVerification = otpVerification;
		}
		
		public String getMobile() {
			return mobile;
		}
		
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		
		public String getDeviceType() {
			return deviceType;
		}
		
		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}
		
		public String getUserVerification() {
			return userVerification;
		}
		
		public void setUserVerification(String userVerification) {
			this.userVerification = userVerification;
		}
		
		public String getTokenVerification() {
			return tokenVerification;
		}
		
		public void setTokenVerification(String tokenVerification) {
			this.tokenVerification = tokenVerification;
		}
		
		public String getProfileImage() {
			return profileImage;
		}
		
		public void setProfileImage(String profileImage) {
			this.profileImage = profileImage;
		}
		
		public String getParent_user() {
			return parent_user;
		}
		
		public void setParent_user(String parent_user) {
			this.parent_user = parent_user;
		}
		
		public String getIs_40plus_user() {
			return is_40plus_user;
		}
		
		public void setIs_40plus_user(String is_40plus_user) {
			this.is_40plus_user = is_40plus_user;
		}
		
		public String getPlus40_userId() {
			return plus40_userId;
		}
		
		public void setPlus40_userId(String plus40_userId) {
			this.plus40_userId = plus40_userId;
		}
		
		public String getCountrycode() {
			return countrycode;
		}
		
		public void setCountrycode(String countrycode) {
			this.countrycode = countrycode;
		}
		
		public String getCaregiver_username() {
			return caregiver_username;
		}
		
		public void setCaregiver_username(String caregiver_username) {
			this.caregiver_username = caregiver_username;
		}
		
		public String getIsSendBirdUser() {return isSendBirdUser;}
		
		public void setIsSendBirdUser(String isSendBirdUser) {
			this.isSendBirdUser = isSendBirdUser;
		}


		public String getIs_companion() {
			return is_companion;
		}

		public void setIs_companion(String is_companion) {
			this.is_companion = is_companion;
		}

		public Permission getPermission() {
			return permission;
		}

		public void setPermission(Permission permission) {
			this.permission = permission;
		}

		public class Permission {

			@SerializedName("social_dashboard")
			@Expose
			private String socialDashboard;
			@SerializedName("transportation_dashboard")
			@Expose
			private String transportationDashboard;
			@SerializedName("email_dashboard")
			@Expose
			private String emailDashboard;
			@SerializedName("yoga")
			@Expose
			private String yoga;
			@SerializedName("yoga_news")
			@Expose
			private String yogaNews;
			@SerializedName("spirituality")
			@Expose
			private String spirituality;
			@SerializedName("spirituality_news")
			@Expose
			private String spiritualityNews;
			@SerializedName("show_activities")
			@Expose
			private String showActivities;
			@SerializedName("dailyroutine")
			@Expose
			private String dailyroutine;
			@SerializedName("dashboard_new")
			@Expose
			private String dashboardNew;
			@SerializedName("talk")
			@Expose
			private String talk;
			@SerializedName("notes")
			@Expose
			private String notes;
			@SerializedName("appointment_list")
			@Expose
			private String appointmentList;
			@SerializedName("vital_signs")
			@Expose
			private String vitalSigns;
			@SerializedName("testreport_list")
			@Expose
			private String testreportList;
			@SerializedName("medicine_list")
			@Expose
			private String medicineList;
			@SerializedName("pharmacy_dashboard")
			@Expose
			private String pharmacyDashboard;
			@SerializedName("product_category")
			@Expose
			private String productCategory;
			@SerializedName("weather")
			@Expose
			private String weather;
			@SerializedName("music")
			@Expose
			private String music;
			@SerializedName("dashboard_game")
			@Expose
			private String dashboardGame;
			@SerializedName("user_photos")
			@Expose
			private String userPhotos;
			@SerializedName("camera")
			@Expose
			private String camera;
			@SerializedName("movie")
			@Expose
			private String movie;
			@SerializedName("internet")
			@Expose
			private String internet;
			@SerializedName("news")
			@Expose
			private String news;

			public String getSocialDashboard() {
				return socialDashboard;
			}

			public void setSocialDashboard(String socialDashboard) {
				this.socialDashboard = socialDashboard;
			}

			public String getTransportationDashboard() {
				return transportationDashboard;
			}

			public void setTransportationDashboard(String transportationDashboard) {
				this.transportationDashboard = transportationDashboard;
			}

			public String getEmailDashboard() {
				return emailDashboard;
			}

			public void setEmailDashboard(String emailDashboard) {
				this.emailDashboard = emailDashboard;
			}

			public String getYoga() {
				return yoga;
			}

			public void setYoga(String yoga) {
				this.yoga = yoga;
			}

			public String getYogaNews() {
				return yogaNews;
			}

			public void setYogaNews(String yogaNews) {
				this.yogaNews = yogaNews;
			}

			public String getSpirituality() {
				return spirituality;
			}

			public void setSpirituality(String spirituality) {
				this.spirituality = spirituality;
			}

			public String getSpiritualityNews() {
				return spiritualityNews;
			}

			public void setSpiritualityNews(String spiritualityNews) {
				this.spiritualityNews = spiritualityNews;
			}

			public String getShowActivities() {
				return showActivities;
			}

			public void setShowActivities(String showActivities) {
				this.showActivities = showActivities;
			}

			public String getDailyroutine() {
				return dailyroutine;
			}

			public void setDailyroutine(String dailyroutine) {
				this.dailyroutine = dailyroutine;
			}

			public String getDashboardNew() {
				return dashboardNew;
			}

			public void setDashboardNew(String dashboardNew) {
				this.dashboardNew = dashboardNew;
			}

			public String getTalk() {
				return talk;
			}

			public void setTalk(String talk) {
				this.talk = talk;
			}

			public String getNotes() {
				return notes;
			}

			public void setNotes(String notes) {
				this.notes = notes;
			}

			public String getAppointmentList() {
				return appointmentList;
			}

			public void setAppointmentList(String appointmentList) {
				this.appointmentList = appointmentList;
			}

			public String getVitalSigns() {
				return vitalSigns;
			}

			public void setVitalSigns(String vitalSigns) {
				this.vitalSigns = vitalSigns;
			}

			public String getTestreportList() {
				return testreportList;
			}

			public void setTestreportList(String testreportList) {
				this.testreportList = testreportList;
			}

			public String getMedicineList() {
				return medicineList;
			}

			public void setMedicineList(String medicineList) {
				this.medicineList = medicineList;
			}

			public String getPharmacyDashboard() {
				return pharmacyDashboard;
			}

			public void setPharmacyDashboard(String pharmacyDashboard) {
				this.pharmacyDashboard = pharmacyDashboard;
			}

			public String getProductCategory() {
				return productCategory;
			}

			public void setProductCategory(String productCategory) {
				this.productCategory = productCategory;
			}

			public String getWeather() {
				return weather;
			}

			public void setWeather(String weather) {
				this.weather = weather;
			}

			public String getMusic() {
				return music;
			}

			public void setMusic(String music) {
				this.music = music;
			}

			public String getDashboardGame() {
				return dashboardGame;
			}

			public void setDashboardGame(String dashboardGame) {
				this.dashboardGame = dashboardGame;
			}

			public String getUserPhotos() {
				return userPhotos;
			}

			public void setUserPhotos(String userPhotos) {
				this.userPhotos = userPhotos;
			}

			public String getCamera() {
				return camera;
			}

			public void setCamera(String camera) {
				this.camera = camera;
			}

			public String getMovie() {
				return movie;
			}

			public void setMovie(String movie) {
				this.movie = movie;
			}

			public String getInternet() {
				return internet;
			}

			public void setInternet(String internet) {
				this.internet = internet;
			}

			public String getNews() {
				return news;
			}

			public void setNews(String news) {
				this.news = news;
			}

		}
	}
}
