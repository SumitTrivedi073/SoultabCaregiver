package com.soultabcaregiver.search.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserSearchResultResponse {
	
	@SerializedName ("status_code") private Integer statusCode;
	
	@SerializedName ("response") private Response response;
	
	@SerializedName ("message") private String message;
	
	@SerializedName ("status") private String status;
	
	public Integer getStatusCode() {
		return statusCode;
	}
	
	public Response getResponse() {
		return response;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getStatus() {
		return status;
	}
	
	public static class UserSearchResultModel implements Parcelable {
		
		public static final Creator<UserSearchResultModel> CREATOR =
				new Creator<UserSearchResultModel>() {
					@Override
					public UserSearchResultModel createFromParcel(Parcel in) {
						return new UserSearchResultModel(in);
					}
					
					@Override
					public UserSearchResultModel[] newArray(int size) {
						return new UserSearchResultModel[size];
					}
				};
		
		@SerializedName ("is_sendbird_user") private String isSendbirdUser;
		
		@SerializedName ("status") private String connected;
		
		@SerializedName ("profile_image") private String profileImage;
		
		@SerializedName ("name") private String name;
		
		@SerializedName ("id") private String id;
		
		boolean isCompanion;
		
		public UserSearchResultModel() {
		
		}
		
		public boolean isCompanion() {
			return isCompanion;
		}
		
		public void setCompanion(boolean companion) {
			isCompanion = companion;
		}
		
		public void setIsSendbirdUser(String isSendbirdUser) {
			this.isSendbirdUser = isSendbirdUser;
		}
		
		public void setProfileImage(String profileImage) {
			this.profileImage = profileImage;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public void setId(String id) {
			this.id = id;
		}
		
		public UserSearchResultModel(Parcel in) {
			isSendbirdUser = in.readString();
			connected = in.readString();
			profileImage = in.readString();
			name = in.readString();
			id = in.readString();
		}
		
		@Override
		public int describeContents() {
			return 0;
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeString(isSendbirdUser);
			dest.writeString(connected);
			dest.writeString(profileImage);
			dest.writeString(name);
			dest.writeString(id);
		}
		
		public String getIsSendbirdUser() {
			return isSendbirdUser;
		}
		
		public String getConnected() {
			return connected;
		}
		
		public void setConnected(String connected) {
			this.connected = connected;
		}
		
		public String getProfileImage() {
			return profileImage;
		}
		
		public String getName() {
			return name;
		}
		
		public String getId() {
			return id;
		}
	}
	
	public class Response {
		
		@SerializedName ("result_data") private List<UserSearchResultModel> resultData;
		
		public List<UserSearchResultModel> getResultData() {
			return resultData;
		}
	}
}