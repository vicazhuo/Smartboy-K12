package com.kingpad;

import android.app.Application;

public final class KingPadApp extends Application {

//	private String mProductPassword = "06308317";
//	private String mProductNumber = "123"; 
	private String mProductPassword = "";
	private String mProductNumber = "";
	
	 
	@Override
	public void onCreate() {
		super.onCreate();
	}


	public String getProductPassword() {
		return mProductPassword;
	}

	public void setProductPassword(String productPassword) {
		this.mProductPassword = productPassword;
	}
	
	public String getProductNumber() {
		return this.mProductNumber;
	}
	
	public void setProductNumber(String productNumber) {
		this.mProductNumber = productNumber;
	}

}
