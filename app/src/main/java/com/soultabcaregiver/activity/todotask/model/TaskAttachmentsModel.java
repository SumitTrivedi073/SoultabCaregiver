package com.soultabcaregiver.activity.todotask.model;

public class TaskAttachmentsModel {
	
	private String filePath;
	private String fileName;
	private String fileExtension;
	private String mimeType;
	private int isFromGallery = 0;
	
	public int getIsFromGallery() {
		return isFromGallery;
	}
	
	public void setIsFromGallery(int isFromGallery) {
		this.isFromGallery = isFromGallery;
	}
	
	public TaskAttachmentsModel(String filePath, String fileName, String fileExtension,
	                            String mimeType) {
		this.filePath = filePath;
		this.fileName = fileName;
		this.fileExtension = fileExtension;
		this.mimeType = mimeType;
	}
	
	public TaskAttachmentsModel() {
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFileExtension() {
		return fileExtension;
	}
	
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	
}
