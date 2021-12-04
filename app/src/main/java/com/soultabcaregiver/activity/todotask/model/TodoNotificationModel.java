package com.soultabcaregiver.activity.todotask.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class TodoNotificationModel {
	
	@SerializedName ("status_code") private Integer statusCode;
	
	@SerializedName ("status") private String status;
	
	@SerializedName ("message") private String message;
	
	@SerializedName ("response") private Response response;
	
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
		
		@SerializedName ("task_notificatio_list") private List<TaskNotification>
				taskNotificationList = null;
		
		public List<TaskNotification> getTaskNotificationList() {
			return taskNotificationList;
		}
		
		public void setTaskNotificatioList(List<TaskNotification> taskNotificationList) {
			this.taskNotificationList = taskNotificationList;
		}
		
	}
	
	public class TaskNotification {
		
		@SerializedName ("id") private String id;
		
		@SerializedName ("sender_id") private String senderId;
		
		@SerializedName ("reciver_id") private String reciverId;
		
		@SerializedName ("msg") private String msg;
		
		@SerializedName ("is_read") private String isRead;
		
		@SerializedName ("created_at") private String createdAt;
		
		@SerializedName ("task_id") private String taskId;
		
		@SerializedName ("user_name") private String user_name;
		
		@SerializedName ("profile_image") private String profile_image;
		
		public String getUser_name() {
			return user_name;
		}
		
		public void setUser_name(String user_name) {
			this.user_name = user_name;
		}
		
		public String getProfile_image() {
			return profile_image;
		}
		
		public void setProfile_image(String profile_image) {
			this.profile_image = profile_image;
		}
		
		public String getId() {
			return id;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public String getSenderId() {
			return senderId;
		}
		
		public void setSenderId(String senderId) {
			this.senderId = senderId;
		}
		
		public String getReciverId() {
			return reciverId;
		}
		
		public void setReciverId(String reciverId) {
			this.reciverId = reciverId;
		}
		
		public String getMsg() {
			return msg;
		}
		
		public void setMsg(String msg) {
			this.msg = msg;
		}
		
		public String getIsRead() {
			return isRead;
		}
		
		public void setIsRead(String isRead) {
			this.isRead = isRead;
		}
		
		public String getCreatedAt() {
			return createdAt;
		}
		
		public void setCreatedAt(String createdAt) {
			this.createdAt = createdAt;
		}
		
		public String getTaskId() {
			return taskId;
		}
		
		public void setTaskId(String taskId) {
			this.taskId = taskId;
		}
		
	}
	
}
