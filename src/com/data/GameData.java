package com.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.bean.GameBean;
import com.constant.Constant;
import com.net.AsyncRequest;
import com.net.RequestParameter;

public final class GameData extends BaseData {

	private ArrayList<GameBean> list;
	@Override
	public void startParse(RequestParameter parameter) throws Exception {
		final String rs = AsyncRequest.openUrl(Constant.SERVER_URL + 
				"business/services!getRsProduct.action", AsyncRequest.HTTP_GET, parameter);
		if(rs == null)
			throw new NullPointerException("网络数据请求为空");
		
		JSONObject json = new JSONObject(rs);
		mStatus = json.getInt("status");
		if(mStatus != 1)
			throw new Exception(getErrorMessage(mStatus));
		json = json.getJSONObject("pager");
		JSONArray array = json.getJSONArray("list");
		if(array.length() == 0)
			return;
		list = new ArrayList<GameBean>();
		for(int i = 0, len = array.length(); i < len; i++) {
			GameBean bean = new GameBean();
			JSONObject obj = array.optJSONObject(i);
			bean.download = obj.getInt("download");
			bean.id = obj.getString("id");
			bean.title = obj.getString("name");
			String pic = obj.getString("picture");
			if(pic != null && !"".equals(pic))
				bean.image = Constant.SERVER_URL + obj.getString("picture");
			String url = obj.getString("url");
			if(url != null && !"".equals(url))
				bean.url = Constant.SERVER_URL + url;
			bean.profile = obj.getString("remark");
			list.add(bean);
		}
	}

	public ArrayList<GameBean> getGameList() {
		return list;
	}
	
	public void printData() {
		if(list == null)
			return;
		for(int i = 0; i < list.size(); i++) {
			GameBean bean = list.get(i);
			System.out.println(bean.title);
			System.out.println(bean.id);
			System.out.println(bean.image);
			System.out.println(bean.url);
			System.out.println(bean.download);
		}
	}
}
