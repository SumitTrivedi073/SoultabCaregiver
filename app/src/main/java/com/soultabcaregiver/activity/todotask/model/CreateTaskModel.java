package com.soultabcaregiver.activity.todotask.model;

import com.google.gson.annotations.SerializedName;


public class CreateTaskModel {
	
	@SerializedName ("status_code")
	
	private Integer statusCode;
	
	@SerializedName ("status")
	
	private String status;
	
	@SerializedName ("message")
	
	private String message;
	
	@SerializedName ("response")
	
	private Response response;
	
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
	
	private class Response {
		
		@SerializedName ("id")
		
		private String id;
		
		@SerializedName ("user_id")
		
		private String userId;
		
		@SerializedName ("title")
		
		private String title;
		
		@SerializedName ("start_date")
		
		private String startDate;
		
		@SerializedName ("end_date")
		
		private String endDate;
		
		@SerializedName ("description")
		
		private String description;
		
		@SerializedName ("assign_to")
		
		private String assignTo;
		
		@SerializedName ("attachments")
		
		private String attachments;
		
		@SerializedName ("task_status")
		
		private String taskStatus;
		
		@SerializedName ("comments")
		
		private String comments;
		
		@SerializedName ("is_caregiver_created")
		
		private String isCaregiverCreated;
		
		@SerializedName ("created_at")
		
		private String createdAt;
		
		@SerializedName ("created_by")
		
		private String createdBy;
		
		@SerializedName ("updated_at")
		
		private String updatedAt;
		
		@SerializedName ("updated_by")
		
		private String updatedBy;
		
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
		
		public String getAssignTo() {
			return assignTo;
		}
		
		public void setAssignTo(String assignTo) {
			this.assignTo = assignTo;
		}
		
		public String getAttachments() {
			return attachments;
		}
		
		public void setAttachments(String attachments) {
			this.attachments = attachments;
		}
		
		public String getTaskStatus() {
			return taskStatus;
		}
		
		public void setTaskStatus(String taskStatus) {
			this.taskStatus = taskStatus;
		}
		
		public String getComments() {
			return comments;
		}
		
		public void setComments(String comments) {
			this.comments = comments;
		}
		
		public String getIsCaregiverCreated() {
			return isCaregiverCreated;
		}
		
		public void setIsCaregiverCreated(String isCaregiverCreated) {
			this.isCaregiverCreated = isCaregiverCreated;
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
		
	}
}
