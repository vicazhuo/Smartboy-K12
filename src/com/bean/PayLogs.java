package com.bean;

import java.util.Date;

public class PayLogs {
	private String payId;
	private Client payClient;
	private String payType;
	private double payMoney;
	private Date payDate;
	private String payUser;
	public String getPayId() {
		return payId;
	}
	public void setPayId(String payId) {
		this.payId = payId;
	}
	public Client getPayClient() {
		return payClient;
	}
	public void setPayClient(Client payClient) {
		this.payClient = payClient;
	}
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	public double getPayMoney() {
		return payMoney;
	}
	public void setPayMoney(double payMoney) {
		this.payMoney = payMoney;
	}
	public Date getPayDate() {
		return payDate;
	}
	public void setPayDate(Date payDate) {
		this.payDate = payDate;
	}
	public String getPayUser() {
		return payUser;
	}
	public void setPayUser(String payUser) {
		this.payUser = payUser;
	}
	
}
