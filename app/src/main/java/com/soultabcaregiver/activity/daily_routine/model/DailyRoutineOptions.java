package com.soultabcaregiver.activity.daily_routine.model;

public class DailyRoutineOptions {
	
	private String optionName;
	
	private int image;
	
	private int id;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public DailyRoutineOptions(int id, String optionName, int image) {
		this.optionName = optionName;
		this.id = id;
		this.image = image;
	}
	
	public String getOptionName() {
		return optionName;
	}
	
	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}
	
	public int getImage() {
		return image;
	}
	
	public void setImage(int image) {
		this.image = image;
	}
}
