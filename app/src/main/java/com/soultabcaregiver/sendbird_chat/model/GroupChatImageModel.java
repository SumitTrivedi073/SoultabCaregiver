package com.soultabcaregiver.sendbird_chat.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GroupChatImageModel {
	
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
	
	public class Response {
		
		@SerializedName ("image") @Expose private String image;
		
		public String getImage() {
			return image;
		}
		
		public void setImage(String image) {
			this.image = image;
		}
		
	}
}
