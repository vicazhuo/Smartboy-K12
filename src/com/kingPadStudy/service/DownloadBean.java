package com.kingPadStudy.service;


import android.os.Parcel;
import android.os.Parcelable;

public class DownloadBean implements Parcelable{
	private static final long serialVersionUID = -3019423378215641308L;
	private String savePath;
	private String course;
	private String grade;
	private String status = DownloadService.ACTION_PASUE;
	private String key;
	private String resourceName;
	private String resrouceId;
	private int progress;
	private String resourceUrl;
	private int ispackage = 0;
	private String fileName;
	private String resourceNumber;

	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeString(savePath);
		parcel.writeString(resourceName);
		parcel.writeString(resrouceId);
		parcel.writeString(key);
		parcel.writeString(grade);
		parcel.writeString(course);
		parcel.writeString(status);
		parcel.writeString(resourceUrl);
		parcel.writeString(fileName);
		parcel.writeString(resourceNumber);
		parcel.writeInt(ispackage);
		
	}
	
	public static final Parcelable.Creator<DownloadBean> CREATOR = new Creator<DownloadBean>() {  
        public DownloadBean createFromParcel( Parcel source ) { 
        	DownloadBean mContent = new DownloadBean();
            mContent.savePath = source.readString();
            mContent.resourceName = source.readString();
            mContent.resrouceId = source.readString();
            mContent.key = source.readString();
            mContent.grade = source.readString();
            mContent.course = source.readString();
            mContent.status = source.readString();
            mContent.resourceUrl = source.readString();
            mContent.fileName = source.readString();
            mContent.resourceNumber = source.readString();
            mContent.ispackage = source.readInt();
            return mContent;  
        }  
        
        public DownloadBean[] newArray( int size ) {  
        	
            return new DownloadBean[size];  
            
        }  
    };
	

	
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	public String getResrouceId() {
		return resrouceId;
	}
	public void setResrouceId(String resrouceId) {
		this.resrouceId = resrouceId;
	}
	
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getProgress() {
		return progress;
	}
	public void setProgress(int progress) {
		this.progress = progress;
	}
	public String getResourceUrl() {
		return resourceUrl;
	}
	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}
	public int getIspackage() {
		return ispackage;
	}
	public void setIspackage(int ispackage) {
		this.ispackage = ispackage;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getResourceNumber() {
		return resourceNumber;
	}
	public void setResourceNumber(String resourceNumber) {
		this.resourceNumber = resourceNumber;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	


	public String getSavePath() {
		return savePath;
	}
	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}


}
