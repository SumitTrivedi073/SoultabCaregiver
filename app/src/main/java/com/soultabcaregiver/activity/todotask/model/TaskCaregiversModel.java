package com.soultabcaregiver.activity.todotask.model;

public class TaskCaregiversModel {
	
	private String id;
	
	private String name;
	
	private String lastname;
	
	private String profileImage;
	
	private int isSelected = 0;
	
	public TaskCaregiversModel(String id, String name, String lastname, String profileImage,
	                           int isSelected) {
		this.id = id;
		this.name = name;
		this.lastname = lastname;
		this.profileImage = profileImage;
		this.isSelected = isSelected;
	}
	
	public TaskCaregiversModel() {
	
	}
	
	public int getIsSelected() {
		return isSelected;
	}
	
	public void setIsSelected(int isSelected) {
		this.isSelected = isSelected;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getLastname() {
		return lastname;
	}
	
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String getProfileImage() {
		return profileImage;
	}
	
	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}
}
