package com.bean;

import java.util.ArrayList;
import java.util.Date;

public class BResource {
	/**
	 * 资源id号，在下载资源时使用，在该arraylist中唯一
	 */
	private long id;
	private String resourceId;
	private String resourceUrl;
	private double resourceSpace;
	private Course resourceCourse;
	private Grade resourceGrade;
	private int ispackage = 0;
	private Date updateDate;
	private double resourcePrice;
	private int resourceisactivate = 0;
	private String resourceRemark;
	private String resourceName;
	private String fileName;
	private String resourceNumber;
	private int resourceIsold = 0;
	private int isDownload;
	
	public void setId(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
	
	public String getResourceId() {
		return resourceId;
	}
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	public String getResourceUrl() {
		return resourceUrl;
	}
	public void setResourceUrl(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}
	public double getResourceSpace() {
		return resourceSpace;
	}
	public void setResourceSpace(double resourceSpace) {
		this.resourceSpace = resourceSpace;
	}
	public Course getResourceCourse() {
		return resourceCourse;
	}
	public void setResourceCourse(Course resourceCourse) {
		this.resourceCourse = resourceCourse;
	}
	public Grade getResourceGrade() {
		return resourceGrade;
	}
	public void setResourceGrade(Grade resourceGrade) {
		this.resourceGrade = resourceGrade;
	}
	public int getIspackage() {
		return ispackage;
	}
	public void setIspackage(int ispackage) {
		this.ispackage = ispackage;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public double getResourcePrice() {
		return resourcePrice;
	}
	public void setResourcePrice(double resourcePrice) {
		this.resourcePrice = resourcePrice;
	}
	public int getResourceisactivate() {
		return resourceisactivate;
	}
	public void setResourceisactivate(int resourceisactivate) {
		this.resourceisactivate = resourceisactivate;
	}
	public String getResourceRemark() {
		return resourceRemark;
	}
	public void setResourceRemark(String resourceRemark) {
		this.resourceRemark = resourceRemark;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
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
	public int getResourceIsold() {
		return resourceIsold;
	}
	public void setResourceIsold(int resourceIsold) {
		this.resourceIsold = resourceIsold;
	}
	public int getDownload() {
		return isDownload;
	}

	public void setDownload(int isDownload) {
		this.isDownload = isDownload;
	}
}
