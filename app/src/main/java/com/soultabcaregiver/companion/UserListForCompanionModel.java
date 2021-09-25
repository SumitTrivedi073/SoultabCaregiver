package com.soultabcaregiver.companion;

import com.google.gson.annotations.SerializedName;

public class UserListForCompanionModel {
	
	@SerializedName ("user_id") private String userId;
	
	@SerializedName ("user_image") private String userImage;
	
	@SerializedName ("user_name") private String userName;
	
	@SerializedName ("user_sendbird_user") private String userSendbirdUser;
	
	@SerializedName ("username") private String username;
	
	public String getUserId() {
		return userId;
	}
	
	public String getUserImage() {
		return userImage;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getUserSendbirdUser() {
		return userSendbirdUser;
	}
	
	public String getUsername() {
		return username;
	}
}