package com.data;

import org.json.JSONObject;

import com.constant.Constant;
import com.net.AsyncRequest;
import com.net.RequestParameter;
/**
 * 修改密码
 * 请求参数：
 * <productNumber> : 产品序列号
 * <clientPassword>	： 用户密码
 * <newPassword> ： 新密码
 * 
 * @author lenovo
 * @date 2012-12-7
 */
public final class UpdatePwdData extends BaseData {
	private String mMessage;
	@Override
	public void startParse(RequestParameter parameter) throws Exception {
		final String rs = AsyncRequest.openUrl(Constant.SERVER_URL + "business/services!updateClientPassword.action", 
				AsyncRequest.HTTP_GET, parameter);
		if(rs == null)
			throw new NullPointerException("服务器获取数据为空");
		
		JSONObject json = new JSONObject(rs);
		this.mStatus = json.getInt("status");
		if(mStatus != 1)
			throw new Exception(getErrorMessage(mStatus));
		
		this.mMessage = json.getString("message");
	}

	public String getMessage() {
		return this.mMessage;
	}
}
