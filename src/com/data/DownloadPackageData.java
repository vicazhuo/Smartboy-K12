package com.data;

import java.util.Date;

import org.json.JSONObject;

import com.bean.BResource;
import com.constant.Constant;
import com.net.AsyncRequest;
import com.net.RequestParameter;
/**
 * 解析package数据实体
 * @author <a href="mailto:udmjh0508@126.com">莫建华</a>
 * @created 2013-1-21
 * @version V-1.0
 */
public class DownloadPackageData extends BaseData {

	private BResource mResource;
	
	@Override
	public void startParse(RequestParameter parameter) throws Exception {
		final String rs = AsyncRequest.openUrl(Constant.SERVER_URL + 
				"business/services!getPackageOfCourse.action", AsyncRequest.HTTP_GET, parameter);
		if(rs == null)
			throw new NullPointerException("网络数据请求为空");
		JSONObject json = new JSONObject(rs);
		mStatus = json.getInt("status");
		if(mStatus != 1)
			throw new Exception(getErrorMessage(mStatus));
		
		mResource = new BResource();
		JSONObject object = json.getJSONObject("bean");
		mResource.setResourceId(object.getString("resourceId"));
		mResource.setResourceUrl(Constant.SERVER_URL + object.getString("resourceUrl"));
		mResource.setResourceSpace(object.getDouble("resourceSpace"));
		mResource.setIspackage(object.getInt("ispackage"));
		mResource.setUpdateDate(new Date(object.getJSONObject("updateDate").getLong("time")));
		mResource.setResourcePrice(object.getDouble("resourcePrice"));
		mResource.setResourceisactivate(object.getInt("resourceisactivate"));
		mResource.setResourceRemark(object.getString("resourceRemark"));
		mResource.setResourceName(object.getString("resourceName"));
		mResource.setFileName(object.getString("fileName"));
		mResource.setResourceNumber(object.getString("resourceNumber"));
		mResource.setResourceIsold(object.getInt("resourceIsold"));
	}
	
	public BResource getResource() {
		return mResource;
	}

}
