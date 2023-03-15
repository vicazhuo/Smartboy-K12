package com.data;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bean.Client;
import com.bean.Product;
import com.constant.Constant;
import com.net.AsyncRequest;
import com.net.RequestParameter;

public class ClientData extends BaseData {
	/**
	 * 客户信息
	 */
	private Client mClient;
	
	private ArrayList<Product> mList;
	
	@Override
	public void startParse(RequestParameter parameter) throws Exception {
		final String rs = AsyncRequest.openUrl(Constant.SERVER_URL + 
				"business/services!getClientInfo.action", AsyncRequest.HTTP_GET, parameter);
		if(rs == null)
			throw new NullPointerException("网络数据请求为空");
		JSONObject json = new JSONObject(rs);
		
		mStatus = json.getInt("status");
		if(mStatus != 1)
			throw new Exception(getErrorMessage(mStatus));
		
		json = json.getJSONObject("bean");
		mClient = new Client();
		mClient.setClientId(json.getString("clientId"));
		mClient.setClientName(json.getString("clientName"));
		mClient.setClientTelnumber(json.getString("clientTelnumber"));
		mClient.setClientAddress(json.getString("clientAddress"));
		mClient.setAvailablEmoney(json.getDouble("availablEmoney"));
		mClient.setClientEmail(json.getString("clientEmail"));
//		JSONObject object = json.getJSONObject("clientCreatedate");
		mClient.setClientCreatedate(new Date(json.getJSONObject("clientCreatedate").getLong("time")));
		mClient.setClientRemark(json.getString("clientRemark"));
		mClient.setClientIsactivate(json.getInt("clientIsactivate"));
		mClient.setClientPassword(json.getString("clientPassword"));
		
		JSONArray array = json.getJSONArray("products");
		mList = new ArrayList<Product>();
		analyzeProduct(array);
	} // startParse
	
	private void analyzeProduct(JSONArray json) throws JSONException{
		for(int i = 0, len = json.length(); i< len; i++) {
			JSONObject object = json.getJSONObject(i);
			Product product = new Product();
			product.setProductNumber(object.getString("productNumber"));
			product.setProductName(object.getString("productName"));
			product.setProductIsactivate(object.getInt("productIsactivate"));
			product.setProductStarDate(new Date(object.getJSONObject("productCreatedate").getLong("time")));
			//product.setProductCreatedate(new Date(object.getJSONObject("productCreatedate").getLong("time")));
			
			mList.add(product);
		}
	}

	public Client getClient() {
		return this.mClient;
	}
	
	public ArrayList<Product> getProductList() {
		return this.mList;
	}
}
