package com.bean;

import java.util.Date;

public class Product {
	private String productNumber;
	private String productName;
	private int productIsactivate = 0;
	private Date productStarDate;
	public String getProductNumber() {
		return productNumber;
	}
	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public int getProductIsactivate() {
		return productIsactivate;
	}
	public void setProductIsactivate(int productIsactivate) {
		this.productIsactivate = productIsactivate;
	}
	public Date getProductStarDate() {
		return productStarDate;
	}
	public void setProductStarDate(Date productStarDate) {
		this.productStarDate = productStarDate;
	}
}
