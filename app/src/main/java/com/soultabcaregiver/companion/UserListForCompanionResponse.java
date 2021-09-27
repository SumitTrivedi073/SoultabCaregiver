package com.soultabcaregiver.companion;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserListForCompanionResponse {
	
	@SerializedName ("status_code") private int statusCode;
	
	@SerializedName ("response") private List<UserListForCompanionModel> response;
	
	@SerializedName ("message") private String message;
	
	@SerializedName ("status") private String status;
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public List<UserListForCompanionModel> getResponse() {
		return response;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getStatus() {
		return status;
	}
}