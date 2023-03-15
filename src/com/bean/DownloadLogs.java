package com.bean;

import java.util.Date;


public class DownloadLogs { 
	private String downloadId;
	private String courseName;
	private String resourceName;
	private double downloadCost;
	private double resourcePrice;
	private double mostCost;
	private Date downloadDate;
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getDownloadId() {
		return downloadId;
	}
	public void setDownloadId(String downloadId) {
		this.downloadId = downloadId;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
	
	public double getDownloadCost() {
		return downloadCost;
	}
	public void setDownloadCost(double downloadCost) {
		this.downloadCost = downloadCost;
	}
	public double getResourcePrice() {
		return resourcePrice;
	}
	public void setResourcePrice(double resourcePrice) {
		this.resourcePrice = resourcePrice;
	}
	public double getMostCost() {
		return mostCost;
	}
	public void setMostCost(double mostCost) {
		this.mostCost = mostCost;
	}
	public Date getDownloadDate() {
		return downloadDate;
	}
	public void setDownloadDate(Date downloadDate) {
		this.downloadDate = downloadDate;
	}
}
