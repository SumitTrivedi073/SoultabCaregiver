package com.soultabcaregiver.activity.todotask.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TaskCountModel {
	
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
		
		@SerializedName ("todoCount") @Expose private Integer todoCount;
		
		@SerializedName ("inprogressCount") @Expose private Integer inprogressCount;
		
		@SerializedName ("doneCount") @Expose private Integer doneCount;
		
		public Integer getTodoCount() {
			return todoCount;
		}
		
		public void setTodoCount(Integer todoCount) {
			this.todoCount = todoCount;
		}
		
		public Integer getInprogressCount() {
			return inprogressCount;
		}
		
		public void setInprogressCount(Integer inprogressCount) {
			this.inprogressCount = inprogressCount;
		}
		
		public Integer getDoneCount() {
			return doneCount;
		}
		
		public void setDoneCount(Integer doneCount) {
			this.doneCount = doneCount;
		}
		
	}
	
	public static class ToDoFilterModel {
		
		private String tagName;
		
		private String statusNameForFilter;
		
		public String getStatusNameForFilter() {
			return statusNameForFilter;
		}
		
		public void setStatusNameForFilter(String statusNameForFilter) {
			this.statusNameForFilter = statusNameForFilter;
		}
		
		private Integer count;
		
		public ToDoFilterModel(String tagName, String statusNameForFilter, Integer count) {
			this.tagName = tagName;
			this.statusNameForFilter = statusNameForFilter;
			this.count = count;
		}
		
		public String getTagName() {
			return tagName;
		}
		
		public void setTagName(String tagName) {
			this.tagName = tagName;
		}
		
		public Integer getCount() {
			return count;
		}
		
		public void setCount(Integer count) {
			this.count = count;
		}
		
	}
	
}
