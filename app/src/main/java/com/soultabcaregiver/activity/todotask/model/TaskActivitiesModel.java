package com.soultabcaregiver.activity.todotask.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class TaskActivitiesModel {
	
	@SerializedName ("status_code") private Integer statusCode;
	
	@SerializedName ("status") private String status;
	
	@SerializedName ("message") private String message;
	
	@SerializedName ("response") private List<Response> response = null;
	
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
	
	public static class Response {
		
		@SerializedName ("id") private String id;
		
		@SerializedName ("user_id") private String userId;
		
		@SerializedName ("task_id") private String taskId;
		
		@SerializedName ("title") private String title;
		
		@SerializedName ("start_date") private String startDate;
		
		@SerializedName ("end_date") private String endDate;
		
		@SerializedName ("description") private String description;
		
		@SerializedName ("task_status") private String taskStatus;
		
		@SerializedName ("created_at") private String createdAt;
		
		@SerializedName ("created_by") private String createdBy;
		
		@SerializedName ("updated_at") private String updatedAt;
		
		@SerializedName ("updated_by") private String updatedBy;
		
		@SerializedName ("created_by_name") private String createdByName;
		
		@SerializedName ("profile_image") private String profile_image;
		
		public String getProfile_image() {
			return profile_image;
		}
		
		public void setProfile_image(String profile_image) {
			this.profile_image = profile_image;
		}
		
		@SerializedName ("updated_by_name") private String updatedByName;
		
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getUserId() {
			return userId;
		}
		
		public void setUserId(String userId) {
			this.userId = userId;
		}
		
		public String getTaskId() {
			return taskId;
		}
		
		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}
		
		public String getTitle() {
			return title;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public String getStartDate() {
			return startDate;
		}
		
		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}
		
		public String getEndDate() {
			return endDate;
		}
		
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		
		public String getDescription() {
			return description;
		}
		
		public void setDescription(String description) {
			this.description = description;
		}
		
		public String getTaskStatus() {
			return taskStatus;
		}
		
		public void setTaskStatus(String taskStatus) {
			this.taskStatus = taskStatus;
		}
		
		public String getCreatedAt() {
			return createdAt;
		}
		
		public void setCreatedAt(String createdAt) {
			this.createdAt = createdAt;
		}
		
		public String getCreatedBy() {
			return createdBy;
		}
		
		public void setCreatedBy(String createdBy) {
			this.createdBy = createdBy;
		}
		
		public String getUpdatedAt() {
			return updatedAt;
		}
		
		public void setUpdatedAt(String updatedAt) {
			this.updatedAt = updatedAt;
		}
		
		public String getUpdatedBy() {
			return updatedBy;
		}
		
		public void setUpdatedBy(String updatedBy) {
			this.updatedBy = updatedBy;
		}
		
		public String getCreatedByName() {
			return createdByName;
		}
		
		public void setCreatedByName(String createdByName) {
			this.createdByName = createdByName;
		}
		
		public String getUpdatedByName() {
			return updatedByName;
		}
		
		public void setUpdatedByName(String updatedByName) {
			this.updatedByName = updatedByName;
		}
		
	}
	
}
