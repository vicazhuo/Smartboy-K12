package com.data;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bean.PayLogs;
import com.constant.Constant;
import com.net.AsyncRequest;
import com.net.RequestParameter;
/**
 * 获取充值记录
 * @author <a href="mailto:udmjh0508@126.com">莫建华</a>
 * @created 2013-1-12
 */
public class PayLogData extends BaseData {
	
	private ArrayList<PayLogs> mPayList;
	private int nPageNumber;
	
	@Override
	public void startParse(RequestParameter parameter) throws Exception {
		final String rs = AsyncRequest.openUrl(Constant.SERVER_URL + 
				"business/services!getPayLogs.action", AsyncRequest.HTTP_GET, parameter);
		if(rs == null)
			throw new NullPointerException("网络数据请求为空");
		
		JSONObject json = new JSONObject(rs);
		mStatus = json.getInt("status");
		if(mStatus != 1)
			throw new Exception(getErrorMessage(mStatus));
		
		JSONObject page = json.getJSONObject("pager");
		this.nPageNumber = page.getInt("pageNumber");
		
		
		
		JSONArray array = page.getJSONArray("list");
		int length = array.length();
		if(length == 0)
			return;
		
		mPayList = new ArrayList<PayLogs>();
		for(int i = 0; i < length; i++) {
			JSONObject object = array.getJSONObject(i);
			PayLogs payLogs = new PayLogs();
			payLogs.setPayId(object.getString("payId"));
			payLogs.setPayType(object.getString("payType"));
			payLogs.setPayMoney(object.getDouble("payMoney"));
			payLogs.setPayDate(new Date(object.getJSONObject("payDate").getLong("time")));
			payLogs.setPayUser(object.getString("payUser"));
			
			mPayList.add(payLogs);
		}// for
	} // startParse
	
	public ArrayList<PayLogs> getList() {
		return this.mPayList;
	}
	
	public int getTotalPage() {
		return this.nPageNumber;
	}

}
