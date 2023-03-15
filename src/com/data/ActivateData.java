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
public final class ActivateData extends BaseData {
	/**
	 * 返回消息
	 */
	private String mMessage;
	
	public void startParse(RequestParameter parameter) throws Exception {
		
		final String rs = AsyncRequest.openUrl(Constant.SERVER_URL + 
				"business/services!productActivate.action", AsyncRequest.HTTP_GET, parameter);
		if(rs == null)
			throw new NullPointerException("网络数据请求为空");
		
		JSONObject json = new JSONObject(rs);
		
		mStatus = json.getInt("status");
		if(mStatus != 1)
			throw new Exception(getErrorMessage(mStatus));
		
		mMessage = json.getString("message");
		
	} // startParse
	
	public String getMessage() {
		return this.mMessage;
	}
	
	/*public void printData() {
		String tag = "tag";
		Log.e(tag, mStatus + "");
		Log.e(tag, mMessage + "");
	}*/
}
