package com.soultabcaregiver.activity.todotask.model;

import com.google.gson.annotations.SerializedName;


public class AddTaskCommentModel {
	
	@SerializedName ("status_code")
	
	private Integer statusCode;
	
	@SerializedName ("status")
	
	private String status;
	
	@SerializedName ("message")
	
	private String message;
	
	@SerializedName ("response") TaskCommentListModel.Response response;
	
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
	
	public TaskCommentListModel.Response getResponse() {
		return response;
	}
	
	public void setResponse(TaskCommentListModel.Response response) {
		this.response = response;
	}
}
