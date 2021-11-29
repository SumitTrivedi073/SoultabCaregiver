package com.soultabcaregiver.activity.todotask.model;

import com.google.gson.annotations.SerializedName;


public class DeleteCommentModel {
	
	@SerializedName ("status_code")
	
	private Integer statusCode;
	
	@SerializedName ("status")
	
	private String status;
	
	@SerializedName ("message")
	
	private String message;
	
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
	
}
