package com.data;

import org.json.JSONObject;

import com.constant.Constant;
import com.net.AsyncRequest;
import com.net.RequestParameter;
/**
 * 获取付费资源接口数据
 * 请求参数：
 * <productNumber> : 产品序列号
 * <clientPassword> : 用户密码
 * 
 * @author lenovo
 * @date 2012-12-7
 */
public final class BuyResourceData extends BaseData {

	@Override
	public void startParse(RequestParameter parameter) throws Exception {
		final String rs = AsyncRequest.openUrl(Constant.SERVER_URL + "business/services!getBuyResource.action", 
				AsyncRequest.HTTP_GET, parameter); 
		if(rs == null)
			throw new NullPointerException("数据获取为空");
		
		JSONObject json = new JSONObject(rs);
		this.mStatus = json.getInt("status");
		if(mStatus != 1)
			throw new Exception(getErrorMessage(mStatus));
		
		// TODO:该处还没有数据
	}

}
