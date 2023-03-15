package com.data;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;

import com.bean.DownloadLogs;
import com.constant.Constant;
import com.net.AsyncRequest;
import com.net.RequestParameter;

/**
 * 获取资源下载记录接口
 * @author <a href="mailto:udmjh0508@126.com">莫建华</a>
 * @created 2013-1-21
 * @version V-1.0
 */
public final class DownloadLogData extends BaseData {

	private int mPageNumber;
	private ArrayList<DownloadLogs> mList;
	
	@Override
	public void startParse(RequestParameter parameter) throws Exception {
		
		final String rs = AsyncRequest.openUrl(Constant.SERVER_URL + 
				"business/services!getDownloadLogOfProduct.action", AsyncRequest.HTTP_GET, parameter);
		if(rs == null)
			throw new NullPointerException("网络数据请求为空");
		
		JSONObject json = new JSONObject(rs);
		this.mStatus = json.getInt("status");
		if(this.mStatus != 1)
			throw new Exception(getErrorMessage(mStatus));
		
		json = json.getJSONObject("pager");
		mPageNumber = json.getInt("pageNumber");
		JSONArray array = json.getJSONArray("list");
		
		mList = new ArrayList<DownloadLogs>();
		for(int i = 0, len = array.length(); i < len; i++) {
			DownloadLogs logs = new DownloadLogs();
			JSONObject object = array.getJSONObject(i);
			logs.setDownloadCost(object.getDouble("downloadCost"));
			logs.setDownloadDate(new Date(object.getJSONObject("downloadDate").getLong("time")));
			logs.setDownloadId(object.getString("downloadId"));
			logs.setMostCost(object.getDouble("mostCost"));
			logs.setResourcePrice(object.getDouble("resourcePrice"));
			object = object.getJSONObject("downloadResource");
			String text = object.getString("fileName");
			logs.setResourceName(text.substring(0, text.lastIndexOf('.')));
			object = object.getJSONObject("resourceCourse");
			logs.setCourseName(object.getString("courseName"));
			mList.add(logs);
		}
		
	}
	
	public int getPageNumber() {
		return this.mPageNumber;
	}
	
	public ArrayList<DownloadLogs> getDownloadLogsList() {
		return this.mList;
	}
	
	/*public void printData() {
		for (DownloadLogs list : mList) {
			System.out.println(list.getDownloadCost());
			System.out.println(list.getDownloadId());
			System.out.println(list.getMostCost());
			System.out.println(list.getResourceName());
			System.out.println(list.getCourseName());
		}
	}*/

}
