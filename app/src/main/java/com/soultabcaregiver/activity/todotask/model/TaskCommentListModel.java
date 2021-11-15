package com.soultabcaregiver.activity.todotask.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class TaskCommentListModel {
	
	@SerializedName ("status_code")
	
	private Integer statusCode;
	
	@SerializedName ("status")
	
	private String status;
	
	@SerializedName ("message")
	
	private String message;
	
	@SerializedName ("response")
	
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
	
	public static class Response {
		
		@SerializedName ("id")
		
		private String id;
		
		@SerializedName ("user_id")
		
		private String userId;
		
		@SerializedName ("task_id")
		
		private String taskId;
		
		@SerializedName ("comment")
		
		private String comment;
		
		@SerializedName ("created_at")
		
		private String createdAt;
		
		@SerializedName ("created_by")
		
		private String createdBy;
		
		@SerializedName ("updated_at")
		
		private String updatedAt;
		
		@SerializedName ("updated_by")
		
		private String updatedBy;
		
		@SerializedName ("updated_by_name")
		
		private String updated_by_name;
		
		@SerializedName ("created_by_name")
		
		private String created_by_name;
		
		public String getUpdated_by_name() {
			return updated_by_name;
		}
		
		public void setUpdated_by_name(String updated_by_name) {
			this.updated_by_name = updated_by_name;
		}
		
		public String getCreated_by_name() {
			return created_by_name;
		}
		
		public void setCreated_by_name(String created_by_name) {
			this.created_by_name = created_by_name;
		}
		
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
		
		public String getComment() {
			return comment;
		}
		
		public void setComment(String comment) {
			this.comment = comment;
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
