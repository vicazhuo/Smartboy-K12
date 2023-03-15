package com.data;

import org.json.JSONObject;

import android.util.Log;

import com.constant.Constant;
import com.net.AsyncRequest;
import com.net.RequestParameter;
/**
 * 产品激活接口
 * @author lenovo
 * @date 2012-12-7
 * 
 * 请求参数：
 * <productNumber> : 产品序列号
 * <clientPassword> : 客户密码
 * 
 */
public final class UserRegisterData extends BaseData {
	/**
	 * 返回消息
	 */
	private String mMessage;
	
	public void startParse(RequestParameter parameter) throws Exception {
		String urlString = Constant.SERVER_URL + "business/services!clientRegister.action";
		System.out.println("请求的URL="+urlString);
		final String rs = AsyncRequest.openUrl(Constant.SERVER_URL + 
				"business/services!clientRegister.action", AsyncRequest.HTTP_POST, parameter);
		if(rs == null)
			throw new NullPointerException("网络数据请求为空");
//		System.out.println("rs==");
//		System.out.println(rs);
		JSONObject json = new JSONObject(rs);
		mStatus = json.getInt("status");
//		System.out.println("在StartParse中的状态是："+mStatus);
		mMessage = json.getString("message");
//		System.out.println("在StartParse中的消息是："+mMessage);
	} 
	
	public String getMessage() {
		return this.mMessage;
	}
}
